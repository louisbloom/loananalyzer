/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.common;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import thomasc.loananalyzer.loans.Loan.IntervalType;

public abstract class LoanUtils {

    private static final String TAG = "LoanUtils";

    @SuppressWarnings("unused")
    public static String getShortDate(Date date) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat
                .getDateInstance(DateFormat.MEDIUM, Locale.US);

        Calendar time = Calendar.getInstance();
        time.setTime(date);

        Calendar now = Calendar.getInstance();

        int year = time.get(Calendar.YEAR);
        int day = time.get(Calendar.DAY_OF_MONTH);

        int year_now = now.get(Calendar.YEAR);
        int day_now = now.get(Calendar.DAY_OF_MONTH);

        long unix = time.getTimeInMillis() / 1000;
        long unix_now = now.getTimeInMillis() / 1000;

        long delta = unix_now - unix;

        if (year == year_now) {
            if (day == day_now) {
                if (delta < 3600) {
                    if (delta < 60) {
                        return String.format("%ds", delta);
                    } else {
                        return String.format("%dm", delta / 60);
                    }
                } else {
                    return String.format("%dh", delta / 60 / 60);
                }
            } else {
                String pattern = sdf.toPattern();
                pattern = pattern.replaceAll(
                        "[\\W]*[Yy]+[\\W]*",
                        "");
                return new SimpleDateFormat(pattern, Locale.US).format(date);
            }
        } else {
            return sdf.format(date);
        }
    }

    public static int getInt(EditText text) {
        return getInt(text, -1);
    }

    private static int getInt(EditText editText, int v) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        String text = editText.getText().toString();
        Number number;
        try {
            number = nf.parse(text);
        } catch (ParseException e) {
            number = v;
            Log.e(TAG, e.getMessage());
        }
        return number.intValue();
    }

    public static double getDouble(TextView editText, double v) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        String text = editText.getText().toString();
        Number number;
        try {
            number = nf.parse(text);
        } catch (ParseException e) {
            number = v;
            Log.e(TAG, e.getMessage());
        }
        return number.doubleValue();
    }

    /**
     * Sets a double as text with two decimal fractions. Rounding is performed.
     *
     * @param editText The EditText to alter.
     * @param value A double value to assign to the EditText.
     */
    public static void setDouble(TextView editText, double value) {
        value = Math.round(value * 100) / 100d;
        String text = String.format("%,.2f", value);
        editText.setText(text);
    }

    public static void calendarAdd(Calendar calendar, IntervalType
            intervalType, int intervalTypeNumber) {
        switch (intervalType) {
            case YEARLY:
                calendar.add(Calendar.YEAR, intervalTypeNumber);
                break;

            case MONTHLY:
                calendar.add(Calendar.MONTH, intervalTypeNumber);
                break;

            case WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, intervalTypeNumber);
                break;

            case DAILY:
                calendar.add(Calendar.DAY_OF_YEAR, intervalTypeNumber);
                break;

            default:
                // Do nothing.
                break;
        }
    }

    public static CharSequence capitalizeString(String text) {
        if (text != null && text.length() > 1) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }
}
