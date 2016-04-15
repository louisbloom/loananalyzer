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
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.common.LoanLoader;
import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;
import thomasc.loananalyzer.loans.Loan;
import thomasc.loananalyzer.loans.Payment;

public class DetailsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Loan> {
    private long loanId = -1;
    private Loan loan = null;

    @SuppressWarnings("unused")
    private OnFragmentInteractionListener listener = null;

    public static DetailsFragment newInstance(long loanId) {
        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putLong(LoanEntry._ID, loanId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Required empty Fragment constructor.
     */
    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
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

    public void setPayment(Payment payment) {
        Resources res = getResources();

        if (this.loan == null) {
            return;
        }

        View view = getView();
        if (view == null) {
            return;
        }

        TextView tv1 = (TextView) getView().findViewById(R.id.text1);
        TextView tv2 = (TextView) getView().findViewById(R.id.text2);

        switch (loan.getLoanType()) {
            case RULE_OF_78:
                tv1.setText(res.getString(R.string.rebate_fmt, loan.getRebate(payment.getNo())));
                tv2.setText(res.getString(R.string.savings_fmt, loan.getSavings(payment.getNo())));
                break;

            default:
                tv2.setText(res.getString(R.string.savings_fmt, loan.getSavings(payment.getNo())));
                break;
        }

        TextView tv = (TextView) getView().findViewById(R.id.position);
        tv.setText(res.getString(R.string.duration, payment.getNo(), loan.getIntervals()));
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
    public Loader<Loan> onCreateLoader(int id, Bundle args) {
        return new LoanLoader(getActivity(), loanId);
    }

    @Override
    public void onLoadFinished(Loader<Loan> loader, Loan loan) {
        this.loan = loan;
    }

    @Override
    public void onLoaderReset(Loader<Loan> loader) {

    }

    public interface OnFragmentInteractionListener {
    }

}
