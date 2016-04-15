/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.common.CappedArrayAdapter;
import thomasc.loananalyzer.common.LoanLoader;
import thomasc.loananalyzer.common.LoanUtils;
import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;
import thomasc.loananalyzer.db.LoanReaderUtils;
import thomasc.loananalyzer.loans.BaseLoan;
import thomasc.loananalyzer.loans.Loan;
import thomasc.loananalyzer.loans.Loan.LoanError;
import thomasc.loananalyzer.loans.Loan.LoanType;
import thomasc.loananalyzer.loans.SimpleLoan;
import thomasc.loananalyzer.widgets.TermPicker;

import static thomasc.loananalyzer.loans.Loan.PaymentType;

public class EditorFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener,
        TermPickerFragment.OnTermPickedListener,
        LoaderManager.LoaderCallbacks<Loan> {

    @SuppressWarnings("unused")
    static final String TAG = "EditorFragment";

    @SuppressWarnings("unused")
    private OnFragmentInteractionListener listener = null;

    private EditText name;
    private Spinner loanType;
    private TextView principal;
    private TextView firstPayment;
    private TextView term;
    private Spinner paymentType;
    private TextView amount;
    private TextView rate;
    private TextView prate;
    private TextView fee;

    // This holds the state of the fragment.
    long loanId;
    private Loan loan = new SimpleLoan();

    private final SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat
            .getDateInstance(DateFormat.FULL);

    public static EditorFragment newInstance(long loanId) {
        EditorFragment fragment = new EditorFragment();

        Bundle args = new Bundle();
        args.putLong(LoanEntry._ID, loanId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_editor,
                container, false);

        name = (EditText) view.findViewById(R.id.name);
        loanType = (Spinner) view.findViewById(R.id.loan_type);
        principal = (TextView) view.findViewById(R.id.principal);
        firstPayment = (TextView) view.findViewById(R.id.text1);
        term = (TextView) view.findViewById(R.id.term);

        paymentType = (Spinner) view.findViewById(R.id.payment_type);
        amount = (TextView) view.findViewById(R.id.amount);
        rate = (TextView) view.findViewById(R.id.rate);
        prate = (TextView) view.findViewById(R.id.prate);
        fee = (TextView) view.findViewById(R.id.fee);

        CappedArrayAdapter adapter;

        adapter = CappedArrayAdapter.createFromResource(
                getContext(),
                R.array.loan_types,
                R.layout.custom_spinner_item);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        loanType.setAdapter(adapter);

        adapter = CappedArrayAdapter.createFromResource(
                getContext(),
                R.array.payment_types,
                R.layout.custom_spinner_item);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        paymentType.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            loanId = args.getLong(LoanEntry._ID);
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private void init(Loan loadedLoan) {
        if (loadedLoan != null) {
            loan = loadedLoan;
        }

        name.setText(loan.getName());
        loanType.setSelection(loan.getLoanType().ordinal());
        LoanUtils.setDouble(principal, loan.getPrincipal());
        paymentType.setSelection(loan.getPaymentType().ordinal());
        LoanUtils.setDouble(amount, loan.getAmount());
        LoanUtils.setDouble(rate, loan.getAnnualRate() * 100);
        LoanUtils.setDouble(prate, loan.getPeriodicRate() * 100);
        LoanUtils.setDouble(fee, loan.getPeriodicFee());

        loanType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = loanType.getSelectedItemPosition();
                loan.setLoanType(LoanType.values()[pos]);
                loan = BaseLoan.retype(loan);
                renderPayment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = paymentType.getSelectedItemPosition();
                EditorFragment.this.loan.setPaymentType(PaymentType.values()[pos]);
                renderPayment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        principal.setOnClickListener(new DoubleClickListener(
                getContext(),
                getResources().getString(R.string.principal),
                getFragmentManager(),
                new DoublePickerFragment.OnDoublePickedListener() {

                    @Override
                    public void onDoublePicked(double value) {
                        loan.setPrincipal(value);
                        LoanUtils.setDouble(principal, loan.getPrincipal());
                        renderPayment();
                    }
                }));

        term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickTerm();
            }
        });

        firstPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickFirstPayment();
            }
        });

        amount.setOnClickListener(new DoubleClickListener(
                getContext(),
                getResources().getString(R.string.amount),
                getFragmentManager(),
                new DoublePickerFragment.OnDoublePickedListener() {

                    @Override
                    public void onDoublePicked(double value) {
                        loan.setAmount(value);
                        LoanUtils.setDouble(amount, loan.getAmount());
                        renderPayment();
                    }
                }));

        rate.setOnClickListener(new DoubleClickListener(
                getContext(),
                getResources().getString(R.string.annual_rate),
                getFragmentManager(),
                new DoublePickerFragment.OnDoublePickedListener() {

                    @Override
                    public void onDoublePicked(double value) {
                        loan.setAnnualRate(value / 100);
                        LoanUtils.setDouble(rate, value);
                        renderPayment();
                    }
                }));

        prate.setOnClickListener(new DoubleClickListener(
                getContext(),
                getResources().getString(R.string.periodic_rate),
                getFragmentManager(),
                new DoublePickerFragment.OnDoublePickedListener() {

                    @Override
                    public void onDoublePicked(double value) {
                        loan.setPeriodicRate(value / 100);
                        LoanUtils.setDouble(prate, value);
                        renderPayment();
                    }
                }));

        fee.setOnClickListener(new DoubleClickListener(
                getContext(),
                getResources().getString(R.string.fee),
                getFragmentManager(),
                new DoublePickerFragment.OnDoublePickedListener() {

                    @Override
                    public void onDoublePicked(double value) {
                        loan.setPeriodicFee(value);
                        LoanUtils.setDouble(fee, value);
                    }
                }));

        View view = getView();
        if (view != null) {
            view.findViewById(R.id.close).setOnClickListener(new View
                    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            view.findViewById(R.id.save).setOnClickListener(new View
                    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSave();
                }
            });
        }

        firstPayment.setText(sdf.format(loan.getFirstPayment()));

        renderTerm();
        renderPayment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(LoanEntry._ID, loan.getId());
        super.onSaveInstanceState(outState);
    }

    private void renderPayment() {
        if (loan.validate() != LoanError.SUCCESS) {
            switch (loan.getPaymentType()) {

                case AMOUNT:
                    loan.setAnnualRate(0);
                    loan.setPeriodicRate(0);
                    break;

                case ANNUAL_RATE:
                    loan.setAmount(0);
                    loan.setPeriodicRate(0);
                    break;

                case PERIODIC_RATE:
                    loan.setAmount(0);
                    loan.setAnnualRate(0);
                    break;
            }
        } else {
            loan.compute();
        }

        switch (loan.getPaymentType()) {

            case AMOUNT:
                amount.setEnabled(true);
                rate.setEnabled(false);
                prate.setEnabled(false);
                break;

            case ANNUAL_RATE:
                amount.setEnabled(false);
                rate.setEnabled(true);
                prate.setEnabled(false);
                break;

            case PERIODIC_RATE:
                amount.setEnabled(false);
                rate.setEnabled(false);
                prate.setEnabled(true);
                break;
        }

        LoanUtils.setDouble(amount, loan.getAmount());
        LoanUtils.setDouble(rate, loan.getAnnualRate() * 100);
        LoanUtils.setDouble(prate, loan.getPeriodicRate() * 100);
    }

    private void renderTerm() {
        Resources res = getResources();
        String term;
        int id = -1;

        switch (loan.getIntervalType()) {
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
        }

        term = res.getString(
                R.string.term_fmt,
                res.getQuantityString(id,
                        loan.getIntervalTypeTimes(),
                        loan.getIntervalTypeTimes()),
                res.getQuantityString(R.plurals.n_times,
                        loan.getIntervals(),
                        loan.getIntervals()));

        this.term.setText(term);
    }

    private void onSave() {
        if (!validateForm()) {
            return;
        }

        new LoanReaderUtils(getContext()).saveLoan(loan);
        getActivity().finish();
    }

    private boolean validateForm() {
        Resources res = getResources();
        int field_id = -1;

        loan.setName(name.getText().toString());

        switch (loan.validate()) {
            case SUCCESS:
                break;

            case NAME:
                field_id = R.string.field_name;
                break;

            case PRINCIPAL:
                field_id = R.string.field_principal;
                break;

            case TERM:
                field_id = R.string.field_term;
                break;

            case AMOUNT:
                field_id = R.string.field_amount;
                break;

            case PERIODIC_RATE:
                field_id = R.string.field_periodic_rate;
                break;

            case ANNUAL_RATE:
                field_id = R.string.field_annual_rate;
                break;

            case FEE:
                field_id = R.string.field_fee;
                break;

            case COMPUTE:
                field_id = R.string.field_computational;
        }

        if (field_id > -1) {
            View view = getView();
            if (view != null) {
                Snackbar.make(view, LoanUtils.capitalizeString(
                                res.getString(R.string.problem_with, res.getString(field_id))),
                        Snackbar.LENGTH_INDEFINITE).show();
            }
            return false;
        } else {
            return true;
        }
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

    private void onPickFirstPayment() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loan.getFirstPayment());

        DatePickerFragment fragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("year", calendar.get(Calendar.YEAR));
        args.putInt("month", calendar.get(Calendar.MONTH));
        args.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));

        fragment.setArguments(args);
        fragment.setListener(this);

        fragment.show(getFragmentManager(), "date_picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.clear();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        loan.setFirstPayment(calendar.getTime());

        firstPayment.setText(sdf.format(loan.getFirstPayment()));
    }

    private void onPickTerm() {
        TermPickerFragment fragment = new TermPickerFragment();
        Bundle args = new Bundle();

        args.putInt("intervalType", loan.getIntervalType().ordinal());
        args.putInt("intervalTypeTimes", loan.getIntervalTypeTimes());
        args.putInt("intervals", loan.getIntervals());

        fragment.setArguments(args);
        fragment.setListener(this);

        fragment.show(getFragmentManager(), "term_picker");
    }

    @Override
    public void onTermPicked(TermPicker termPicker) {
        loan.setIntervalType(termPicker.getIntervalType());
        loan.setIntervalTypeTimes(termPicker.getIntervalTypeTimes());
        loan.setIntervals(termPicker.getIntervals());

        renderTerm();
        renderPayment();
    }

    @Override
    public Loader<Loan> onCreateLoader(int id, Bundle args) {
        return new LoanLoader(getContext(), loanId);
    }

    @Override
    public void onLoadFinished(Loader<Loan> loader, Loan loan) {
        init(loan);
    }

    @Override
    public void onLoaderReset(Loader<Loan> loader) {

    }

    private static class DoubleClickListener implements View.OnClickListener {

        final Context context;
        final String title;
        final FragmentManager manager;
        final DoublePickerFragment.OnDoublePickedListener listener;

        public DoubleClickListener(
                Context context,
                String title,
                FragmentManager manager,
                DoublePickerFragment.OnDoublePickedListener listener) {
            this.context = context;
            this.title = title;
            this.manager = manager;
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            DoublePickerFragment fragment = new DoublePickerFragment();

            Bundle args = new Bundle();
            args.putString("title", title);
            args.putDouble("value", LoanUtils.getDouble((TextView) v, 0));

            fragment.setArguments(args);
            fragment.setListener(listener);

            fragment.show(manager, "double_picker");
        }
    }

    public interface OnFragmentInteractionListener {
    }
}
