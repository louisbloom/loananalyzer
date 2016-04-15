/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.common;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import thomasc.loananalyzer.db.LoanReaderUtils;
import thomasc.loananalyzer.loans.Loan;

public class LoanLoader extends AsyncTaskLoader<Loan> {
    private static final String TAG = "LoanLoader";
    private long loanId;

    public LoanLoader(Context context, long loanId) {
        super(context);
        this.loanId = loanId;
    }

    @Override
    public Loan loadInBackground() {
        Loan loan = new LoanReaderUtils(getContext()).loadLoan(loanId);
        return loan;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
