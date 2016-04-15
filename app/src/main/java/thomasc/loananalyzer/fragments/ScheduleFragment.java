/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.common.LoanLoader;
import thomasc.loananalyzer.common.ScheduleAdapter;
import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;
import thomasc.loananalyzer.loans.Loan;
import thomasc.loananalyzer.loans.Loan.IntervalType;
import thomasc.loananalyzer.loans.Payment;
import thomasc.loananalyzer.widgets.SquaredTextView;

public class ScheduleFragment extends Fragment implements
        ScheduleAdapter.OnPaymentActionListener,
        LoaderManager.LoaderCallbacks<Loan> {

    private OnFragmentInteractionListener listener = null;

    private static final String TAG = "ScheduleFragment";
    private static final String STATE_SELECTED = "selected";

    private long loanId = -1;
    private LinearLayoutManager manager;
    private ScheduleAdapter adapter;
    private SquaredTextView eap;
    private TextView type;
    private TextView principal;
    private TextView term;
    private TextView payment;
    private TextView duration;

    public static ScheduleFragment newInstance(long loanId) {
        ScheduleFragment fragment = new ScheduleFragment();

        Bundle args = new Bundle();
        args.putLong(LoanEntry._ID, loanId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScheduleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            loanId = args.getLong(LoanEntry._ID);
        }

        // Needed to handle appbar items in fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule,
                container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        eap = (SquaredTextView) activity.findViewById(R.id.eap);
        type = (TextView) activity.findViewById(R.id.type);
        principal = (TextView) activity.findViewById(R.id.principal);
        term = (TextView) activity.findViewById(R.id.term);
        payment = (TextView) activity.findViewById(R.id.payment);
        duration = (TextView) activity.findViewById(R.id.duration);

        adapter = new ScheduleAdapter(this);
        manager = new LinearLayoutManager(getContext());

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        return view;
    }

    void setLoan(Loan loan) {
        Resources res = getResources();
        int id;
        IntervalType type = loan.getIntervalType();
        String[] loanTypes = res.getStringArray(R.array.loan_types);

        switch (type) {
            case YEARLY:
                id = R.plurals.plural_n_years;
                break;

            case MONTHLY:
                id = R.plurals.plural_n_months;
                break;

            case WEEKLY:
                id = R.plurals.plural_n_weeks;
                break;

            case DAILY:
                id = R.plurals.plural_n_days;
                break;

            default:
                throw new RuntimeException();
        }

        String term = res.getString(R.string.payment_fmt,
                res.getQuantityString(id,
                        loan.getIntervalTypeTimes(),
                        loan.getIntervalTypeTimes()),
                res.getQuantityString(R.plurals.n_times,
                        loan.getIntervals(),
                        loan.getIntervals()));
        int now = Math.min(adapter.getNow(), loan.getIntervals());
        String duration = res.getString(R.string.duration, now, loan.getIntervals());

        this.eap.setText(String.format("%,.2f", loan.getEap() * 100));
        this.type.setText(loanTypes[loan.getLoanType().ordinal()]);
        this.principal.setText(String.format("%,.2f", loan.getPrincipal()));
        this.term.setText(term);
        this.payment.setText(String.format("%,.2f", loan.getAmount() + loan.getPeriodicFee()));
        this.duration.setText(duration);

        double eap = loan.getEap();
        if (eap <= 0.1) {
            this.eap.setColor(ContextCompat.getColor(
                    getContext(),
                    R.color.eap_good));
        } else if (eap <= 0.2) {
            this.eap.setColor(ContextCompat.getColor(
                    getContext(),
                    R.color.eap_normal));
        } else if (eap <= 0.5) {
            this.eap.setColor(ContextCompat.getColor(
                    getContext(),
                    R.color.eap_ugly));
        } else {
            this.eap.setColor(ContextCompat.getColor(
                    getContext(),
                    R.color.eap_bad));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED, adapter.getSelected());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        if (savedInstanceState != null) {
            adapter.setSelected(savedInstanceState.getInt(STATE_SELECTED));
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goto_today:
                int now = adapter.getNow();
                if (now > -1 && now < adapter.getItemCount()) {
                    manager.scrollToPositionWithOffset(now, 0);
                }
                return true;

            case R.id.delete:
                listener.onDelete(loanId);
                return true;

            case R.id.edit:
                listener.onEdit(loanId);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Loan> onCreateLoader(int id, Bundle args) {
        return new LoanLoader(getActivity(), loanId);
    }

    @Override
    public void onLoadFinished(Loader<Loan> loanLoader, Loan loan) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(loan.getName());
        }

        adapter.populate(loan, loan.getPayments());

        loan.compute();
        setLoan(loan);
    }

    @Override
    public void onLoaderReset(Loader<Loan> loanLoader) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoanActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onPaymentSelected(Payment payment) {
        listener.onPaymentSelected(payment);
    }

    public interface OnFragmentInteractionListener {
        void onPaymentSelected(Payment payment);
        void onEdit(long loanId);
        void onDelete(long loanId);
    }
}
