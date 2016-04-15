/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.common.LoansAdapter;
import thomasc.loananalyzer.db.LoanReaderContract;
import thomasc.loananalyzer.db.LoanReaderOpenHelper;
import thomasc.loananalyzer.loans.Loan;

public class LoansFragment extends Fragment implements
        LoansAdapter.OnLoanActionListener {

    @SuppressWarnings("unused")
    private static final String TAG = "LoansFragment";
    private OnLoanActionListener listener = null;
    private LoansAdapter adapter = null;
    private SQLiteDatabase database = null;

    public LoansFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loans, container, false);

        database = new LoanReaderOpenHelper(getContext()).getReadableDatabase();
        adapter = new LoansAdapter(getContext(), this);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Cursor cursor = database.query(
                LoanReaderContract.LoanEntry.TABLE_NAME,
                LoanReaderContract.columns,
                null, null, null, null, null, null);

        adapter.changeCursor(cursor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        adapter.changeCursor(null);
        database.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnLoanActionListener) context;
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
    public void onView(Loan loan) {
        if (listener != null) {
            listener.onView(loan);
        }
    }

    public interface OnLoanActionListener {
        void onView(Loan loan);
    }
}
