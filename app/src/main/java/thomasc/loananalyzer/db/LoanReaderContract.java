/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.db;

import android.provider.BaseColumns;

public final class LoanReaderContract {

    public LoanReaderContract() {
    }

    public static abstract class LoanEntry implements BaseColumns {
        public static final String TABLE_NAME = "loans";

        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_CREATED = "created";
        public final static String COLUMN_FIRST_PAYMENT = "first_payment";
        public final static String COLUMN_PRINCIPAL = "principal";
        public final static String COLUMN_INTERVALS = "intervals";
        public final static String COLUMN_INTERVAL_TYPE = "interval_type";
        public final static String COLUMN_INTERVAL_TYPE_TIMES = "interval_type_times";
        public final static String COLUMN_PAYMENT_TYPE = "payment_type";
        public final static String COLUMN_AMOUNT = "amount";
        public final static String COLUMN_RATE = "rate";
        public final static String COLUMN_PRATE = "prate";
        public final static String COLUMN_PERIODIC_FEE = "periodic_fee";

        // Deprecated
        public final static String COLUMN_PAYMENT_OLD = "payment";
    }

    public static final String[] columns = new String[] {
            LoanEntry._ID,
            LoanEntry.COLUMN_TYPE,
            LoanEntry.COLUMN_NAME,
            LoanEntry.COLUMN_DESCRIPTION,
            LoanEntry.COLUMN_CREATED,
            LoanEntry.COLUMN_FIRST_PAYMENT,
            LoanEntry.COLUMN_PRINCIPAL,
            LoanEntry.COLUMN_INTERVAL_TYPE,
            LoanEntry.COLUMN_INTERVAL_TYPE_TIMES,
            LoanEntry.COLUMN_INTERVALS,
            LoanEntry.COLUMN_PAYMENT_TYPE,
            LoanEntry.COLUMN_AMOUNT,
            LoanEntry.COLUMN_RATE,
            LoanEntry.COLUMN_PRATE,
            LoanEntry.COLUMN_PERIODIC_FEE,
    };

    public static abstract class PaymentEntry implements BaseColumns {
        public static final String TABLE_NAME = "payments";

        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_AMOUNT = "amount";
    }

    public static final String[] paymentColumns = new String[] {
            PaymentEntry._ID,
            PaymentEntry.COLUMN_DATE,
            PaymentEntry.COLUMN_AMOUNT,
    };
}
