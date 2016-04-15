/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends AppCompatDialogFragment {

    private DatePickerDialog.OnDateSetListener listener = null;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        Bundle args = getArguments();

        if (args != null) {
            calendar.clear();

            calendar.set(Calendar.YEAR, args.getInt("year"));
            calendar.set(Calendar.MONTH, args.getInt("month"));
            calendar.set(Calendar.DAY_OF_MONTH, args.getInt("day"));
        }

        return new DatePickerDialog(
                getContext(),
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }
}
