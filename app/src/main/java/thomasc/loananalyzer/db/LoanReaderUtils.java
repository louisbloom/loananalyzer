/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;
import thomasc.loananalyzer.loans.BaseLoan;
import thomasc.loananalyzer.loans.Loan;

public class LoanReaderUtils {

    private final LoanReaderOpenHelper helper;

    public LoanReaderUtils(Context context) {
        helper = new LoanReaderOpenHelper(context);
    }


    public void saveLoan(Loan loan) {
        ContentValues values = new ContentValues();

        values.put(LoanEntry.COLUMN_TYPE, loan.getLoanType().ordinal());
        values.put(LoanEntry.COLUMN_NAME, loan.getName());
        values.put(LoanEntry.COLUMN_DESCRIPTION, loan.getDescription());
        values.put(LoanEntry.COLUMN_CREATED, loan.getCreated().getTime());
        values.put(LoanEntry.COLUMN_FIRST_PAYMENT, loan.getFirstPayment().getTime());
        values.put(LoanEntry.COLUMN_PRINCIPAL, loan.getPrincipal());
        values.put(LoanEntry.COLUMN_INTERVAL_TYPE, loan.getIntervalType().ordinal());
        values.put(LoanEntry.COLUMN_INTERVAL_TYPE_TIMES, loan.getIntervalTypeTimes());
        values.put(LoanEntry.COLUMN_INTERVALS, loan.getIntervals());
        values.put(LoanEntry.COLUMN_PAYMENT_TYPE, loan.getPaymentType().ordinal());
        values.put(LoanEntry.COLUMN_AMOUNT, loan.getAmount());
        values.put(LoanEntry.COLUMN_RATE, loan.getAnnualRate());
        values.put(LoanEntry.COLUMN_PRATE, loan.getPeriodicRate());
        values.put(LoanEntry.COLUMN_PERIODIC_FEE, loan.getPeriodicFee());

        SQLiteDatabase database = helper.getWritableDatabase();

        if (loan.getId() > -1) {
            values.put(LoanEntry._ID, loan.getId());
            database.update(LoanEntry.TABLE_NAME,
                    values,
                    LoanEntry._ID + " = ?",
                    new String[]{String.valueOf(loan.getId())}
            );
        } else {
            long id = database.insert(LoanEntry.TABLE_NAME, null, values);
            loan.setId(id);
        }

        database.close();
    }

    public void deleteLoan(long loanId) {
        SQLiteDatabase database = helper.getWritableDatabase();

        database.delete(
                LoanEntry.TABLE_NAME,
                LoanEntry._ID + " = ?",
                new String[]{Long.toString(loanId)}
        );

        database.close();
    }

    public Loan loadLoan(long loanId) {
        Loan loan = null;

        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.query(LoanEntry.TABLE_NAME,
                LoanReaderContract.columns,
                LoanEntry._ID + " = ?",
                new String[] { Long.toString(loanId) },
                null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            loan = BaseLoan.valueOf(cursor);
        }

        cursor.close();

        database.close();

        return loan;
    }
}
