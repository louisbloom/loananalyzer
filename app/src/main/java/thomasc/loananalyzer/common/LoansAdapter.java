/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.common;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.joda.time.Years;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.loans.BaseLoan;
import thomasc.loananalyzer.loans.Loan;
import thomasc.loananalyzer.widgets.SquaredTextView;

public class LoansAdapter extends RecyclerCursorAdapter<LoansAdapter.LoanViewHolder> {

    @SuppressWarnings("unused")
    private final static String TAG = "LoansAdapter";
    private final Context context;
    private final OnLoanActionListener listener;
    private final String[] loanTypes;

    public static class LoanViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public OnLoanActionListener listener;
        public Loan loan;
        final TextView name;
        final TextView type;
        final SquaredTextView eap;
        final TextView principal;
        final TextView payment;
        final TextView term;
        final TextView duration;
        final ProgressBar progress;

        public LoanViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.name);
            type = (TextView) view.findViewById(R.id.type);
            eap = (SquaredTextView) view.findViewById(R.id.eap);
            principal = (TextView) view.findViewById(R.id.principal);
            payment = (TextView) view.findViewById(R.id.payment);
            term = (TextView) view.findViewById(R.id.term);
            duration = (TextView) view.findViewById(R.id.duration);
            progress = (ProgressBar) view.findViewById(R.id.progress);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onView(loan);
        }
    }

    public LoansAdapter(Context context, OnLoanActionListener listener) {
        super();

        this.context = context;
        this.listener = listener;

        Resources res = context.getResources();
        loanTypes = res.getStringArray(R.array.loan_types);
    }

    public LoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_loan,
                parent, false);
        return new LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoanViewHolder holder, Cursor cursor) {
        Loan loan = BaseLoan.valueOf(cursor);
        Loan.IntervalType type = loan.getIntervalType();
        int typeTimes = loan.getIntervalTypeTimes();

        loan.compute();

        Resources res = context.getResources();

        // Construct strings.
        String term;
        String duration;
        int id;

        // Use joda-time.
        DateTime now = new DateTime();
        ReadablePeriod period;

        // log(n) search for today.
        int pos = loan.getIntervals() / 2;
        int upper = loan.getIntervals() - 1;
        int lower = 0;

        while (true) {
            DateTime t1 = new DateTime(loan.getFirstPayment());
            DateTime t2;

            switch (type) {
                case YEARLY:
                    id = R.plurals.plural_n_years;
                    t1 = t1.plus(Years.years(typeTimes * pos));
                    t2 = t1.plus(Years.years(typeTimes));
                    break;

                case MONTHLY:
                    id = R.plurals.plural_n_months;
                    t1 = t1.plus(Months.months(typeTimes * pos));
                    t2 = t1.plus(Months.months(typeTimes));
                    break;

                case WEEKLY:
                    id = R.plurals.plural_n_weeks;
                    t1 = t1.plus(Weeks.weeks(typeTimes * pos));
                    t2 = t1.plus(Weeks.weeks(typeTimes));
                    break;

                case DAILY:
                    id = R.plurals.plural_n_days;
                    t1 = t1.plus(Days.days(typeTimes * pos));
                    t2 = t1.plus(Days.days(typeTimes));
                    break;

                default:
                    throw new RuntimeException();
            }

            if (now.isBefore(t1)) {
                if (pos == 0) {
                    // Today is term not begun.
                    pos = -1;
                    break;
                }
                upper = pos;
                pos -= Math.max((pos - lower) / 2, 1);
            } else if (now.isAfter(t2)) {
                if (pos == loan.getIntervals() - 1) {
                    // Today is term completed.
                    break;
                }
                lower = pos;
                pos += Math.max((upper - pos) / 2, 1);
            } else {
                // Found it.
                break;
            }
        }

        pos++;
        term = res.getString(R.string.payment_fmt,
                res.getQuantityString(id,
                        loan.getIntervalTypeTimes(),
                        loan.getIntervalTypeTimes()),
                res.getQuantityString(R.plurals.n_times,
                        loan.getIntervals(),
                        loan.getIntervals()));
        duration = res.getString(R.string.duration, pos, loan.getIntervals());

        holder.loan = loan;
        holder.listener = listener;
        holder.eap.setText(String.format("%,.2f", loan.getEap() * 100));
        holder.name.setText(loan.getName());
        holder.type.setText(loanTypes[loan.getLoanType().ordinal()]);
        holder.principal.setText(String.format("%,.2f", loan.getPrincipal()));
        holder.term.setText(term);
        holder.payment.setText(String.format("%,.2f", loan.getAmount() + loan.getPeriodicFee()));
        holder.duration.setText(duration);
        holder.progress.setMax(loan.getIntervals());
        holder.progress.setProgress(pos);

        double eap = loan.getEap();
        if (eap <= 0.1) {
            holder.eap.setColor(ContextCompat.getColor(
                    holder.eap.getContext(),
                    R.color.eap_good));
        } else if (eap <= 0.2) {
            holder.eap.setColor(ContextCompat.getColor(
                    holder.eap.getContext(),
                    R.color.eap_normal));
        } else if (eap <= 0.5) {
            holder.eap.setColor(ContextCompat.getColor(
                    holder.eap.getContext(),
                    R.color.eap_ugly));
        } else {
            holder.eap.setColor(ContextCompat.getColor(
                    holder.eap.getContext(),
                    R.color.eap_bad));
        }
    }

    public interface OnLoanActionListener {
        void onView(Loan loan);
    }
}
