/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.loans.Loan;
import thomasc.loananalyzer.loans.Payment;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter
        .ViewHolder> {

    private Loan loan = null;
    private Payment payments[] = null;
    private int selected = -1;
    private int now = -1;
    private final OnPaymentActionListener listener;

    public ScheduleAdapter(OnPaymentActionListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final TextView principal;
        final TextView interest;
        final TextView balance;
        final View view;

        public ViewHolder(View view) {
            super(view);

            date = (TextView) view.findViewById(R.id.text1);
            principal = (TextView) view.findViewById(R.id.principal);
            interest = (TextView) view.findViewById(R.id.text3);
            balance = (TextView) view.findViewById(R.id.text4);

            this.view = view;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (payments == null) {
            return;
        }

        Payment p = payments[position];

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.US);

        switch (loan.getIntervalType()) {
            case YEARLY:
            case MONTHLY:
                break;

            case WEEKLY:
            case DAILY:
                sdf = new SimpleDateFormat("dd-MM", Locale.US);
                break;
        }

        if (p.getDate().getTime() == 0) {
            holder.date.setText("");
        } else {
            holder.date.setText(sdf.format(p.getDate()));
        }

        holder.principal.setText(String.format("%,.2f", p.getAmount() - p.getInterest()));
        holder.interest.setText(String.format("%,.2f", p.getInterest()));
        holder.balance.setText(String.format("%,.2f", p.getBalance()));

        holder.view.setSelected((position == selected));

        if (position == now) {
            holder.view.setBackgroundResource(R.drawable.background_list_item_now);
        } else {
            holder.view.setBackgroundResource(R.drawable.background_list_item);
        }

        // Bind it for the listener instance.
        final int i = position;

        holder.view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                setSelected(i);
            }
        });
    }

    public void setSelected(int position) {
        if (selected > -1 && selected < getItemCount()) {
            notifyItemChanged(selected);
        }

        selected = position;

        if (selected > -1 && selected < getItemCount()) {
            notifyItemChanged(selected);

            if (listener != null) {
                if (payments != null) {
                    listener.onPaymentSelected(payments[selected]);
                }
            }
        }
    }

    public int getSelected() {
        return selected;
    }

    @Override
    public int getItemCount() {
        if (payments != null) {
            return payments.length;
        } else {
            return 0;
        }
    }

    public void populate(Loan loan, List<Payment> paymentList) {
        this.loan = loan;
        payments = paymentList.toArray(new Payment[paymentList.size()]);
        Loan.IntervalType type = loan.getIntervalType();
        int typeTimes = loan.getIntervalTypeTimes();

        // Use joda-time.
        DateTime today = new DateTime();
        ReadablePeriod period;

        switch (type) {
            case YEARLY:
                period = Years.years(typeTimes);
                break;

            case MONTHLY:
                period = Months.months(typeTimes);
                break;

            case WEEKLY:
                period = Weeks.weeks(typeTimes);
                break;

            case DAILY:
                period = Days.days(typeTimes);
                break;

            default:
                throw new RuntimeException();
        }

        // log(n) search for today.
        int pos = payments.length / 2;
        int upper = payments.length;
        int lower = 0;

        while (pos > 0 && pos < payments.length) {
            DateTime t1 = new DateTime(payments[pos].getDate());
            DateTime t2 = t1.plus(period);

            if (today.isBefore(t1)) {
                upper = pos;
                pos -= Math.max((pos - lower) / 2, 1);
            } else if (today.isAfter(t2)) {
                lower = pos;
                pos += Math.max((upper - pos) / 2, 1);
            } else {
                // Found it.
                break;
            }
        }

        now = pos;

        notifyDataSetChanged();
    }

    public int getNow() {
        return now;
    }

    public interface OnPaymentActionListener {
        void onPaymentSelected(Payment payment);
    }
}
