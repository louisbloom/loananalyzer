/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;

public class LoanReaderOpenHelper extends SQLiteOpenHelper {

    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "LoanReaderOpenHelper";

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "LoanAnalyzer.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LoanEntry.TABLE_NAME + " (" +
                    LoanEntry._ID + " INTEGER PRIMARY KEY, " +
                    LoanEntry.COLUMN_TYPE + " INTEGER, " +
                    LoanEntry.COLUMN_NAME + " VARCHAR, " +
                    LoanEntry.COLUMN_DESCRIPTION + " VARCHAR, " +
                    LoanEntry.COLUMN_CREATED + " TIMESTAMP, " +
                    LoanEntry.COLUMN_FIRST_PAYMENT + " TIMESTAMP, " +
                    LoanEntry.COLUMN_PRINCIPAL + " DOUBLE, " +
                    LoanEntry.COLUMN_INTERVALS + " INTEGER, " +
                    LoanEntry.COLUMN_INTERVAL_TYPE + " INTEGER, " +
                    LoanEntry.COLUMN_INTERVAL_TYPE_TIMES + " INTEGER, " +
                    LoanEntry.COLUMN_PAYMENT_TYPE + " INTEGER, " +
                    LoanEntry.COLUMN_RATE + " DOUBLE, " +
                    LoanEntry.COLUMN_PRATE + " DOUBLE, " +
                    LoanEntry.COLUMN_AMOUNT + " DOUBLE, " +
                    LoanEntry.COLUMN_PERIODIC_FEE + " DOUBLE" +
                    ")";

    private static final String SQL_TEMP_POSTFIX = "_temp";

    private static final String SQL_LOANS_RENAME_TO_TEMP = "ALTER TABLE " +
            LoanEntry.TABLE_NAME +
            " RENAME TO " +
            LoanEntry.TABLE_NAME + SQL_TEMP_POSTFIX;

    private static final String SQL_LOANS_DROP_TEMP = "DROP TABLE " +
            LoanEntry.TABLE_NAME + SQL_TEMP_POSTFIX;

    private static final String SQL_DROP_LOANS =
            "DROP TABLE IF EXISTS " + LoanEntry.TABLE_NAME;

    // 1 to 2
    private static final String SQL_UPGRADE_1_2 = "UPDATE loans SET " +
            "rate = (rate / 100.0), " +
            "prate = (prate / 100.0);";

    // 2 to 3
    private static final String SQL_COPY_LOAN_2_3 =
            "INSERT INTO " + LoanEntry.TABLE_NAME + "(" +
                    LoanEntry._ID + ", " +
                    LoanEntry.COLUMN_TYPE + ", " +
                    LoanEntry.COLUMN_NAME + ", " +
                    LoanEntry.COLUMN_DESCRIPTION + ", " +
                    LoanEntry.COLUMN_CREATED + ", " +
                    LoanEntry.COLUMN_FIRST_PAYMENT + ", " +
                    LoanEntry.COLUMN_PRINCIPAL + ", " +
                    LoanEntry.COLUMN_INTERVALS + ", " +
                    LoanEntry.COLUMN_INTERVAL_TYPE + ", " +
                    LoanEntry.COLUMN_INTERVAL_TYPE_TIMES + ", " +
                    LoanEntry.COLUMN_PAYMENT_TYPE + ", " +
                    LoanEntry.COLUMN_RATE + ", " +
                    LoanEntry.COLUMN_PRATE + ", " +
                    LoanEntry.COLUMN_AMOUNT +
                    ")" +
            " SELECT " +
                    LoanEntry._ID + ", " +
                    LoanEntry.COLUMN_TYPE + ", " +
                    LoanEntry.COLUMN_NAME + ", " +
                    LoanEntry.COLUMN_DESCRIPTION + ", " +
                    LoanEntry.COLUMN_CREATED + ", " +
                    LoanEntry.COLUMN_FIRST_PAYMENT + ", " +
                    LoanEntry.COLUMN_PRINCIPAL + ", " +
                    LoanEntry.COLUMN_INTERVALS + ", " +
                    LoanEntry.COLUMN_INTERVAL_TYPE + ", " +
                    LoanEntry.COLUMN_INTERVAL_TYPE_TIMES + ", " +
                    LoanEntry.COLUMN_PAYMENT_TYPE + ", " +
                    LoanEntry.COLUMN_RATE + ", " +
                    LoanEntry.COLUMN_PRATE + ", " +
                    LoanEntry.COLUMN_PAYMENT_OLD +
            " FROM " + LoanEntry.TABLE_NAME + SQL_TEMP_POSTFIX;

    // 3 to 4
    private static final String SQL_UPGRADE_3_4 = "ALTER TABLE loans " +
            "ADD COLUMN " + LoanEntry.COLUMN_PERIODIC_FEE + " DOUBLE DEFAULT 0";

    public LoanReaderOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version;

        for (version = oldVersion; version < newVersion; version++) {
            switch (version) {
                case 1:
                    db.execSQL(SQL_UPGRADE_1_2);
                    break;

                case 2:
                    db.execSQL("BEGIN TRANSACTION");
                    db.execSQL(SQL_LOANS_RENAME_TO_TEMP);
                    db.execSQL(SQL_CREATE_ENTRIES);
                    db.execSQL(SQL_COPY_LOAN_2_3);
                    db.execSQL(SQL_LOANS_DROP_TEMP);
                    db.execSQL("COMMIT");
                    break;

                case 3:
                    // On an upgrade from 2 to 4 the previous step will add
                    // the column that is added here, so the exception is
                    // considered an indicator of that.
                    try {
                        db.execSQL(SQL_UPGRADE_3_4);
                    } catch (SQLiteException e) {
                        Log.d(TAG, e.getMessage());
                    }
                    break;

                case 4:
                    // Fix wrongly created tables.
                    Cursor cursor = db.query(LoanEntry.TABLE_NAME,
                            new String[]{ LoanEntry._ID },
                            null, null, null, null, null, null);
                    if (cursor.getCount() == 0) {
                        db.execSQL(SQL_DROP_LOANS);
                        onCreate(db);
                    }
                    cursor.close();
                    break;

                default:
                    return;
            }

            Log.d(TAG, String.format("Upgraded " + LoanEntry.TABLE_NAME +
                    " from %1$d to %2$d", version, version + 1));
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int
            newVersion) {
        db.execSQL(SQL_DROP_LOANS);
        onCreate(db);
        Log.d(TAG, LoanEntry.TABLE_NAME + " dropped");
    }
}
