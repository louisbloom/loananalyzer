/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import thomasc.loananalyzer.loans.Loan;
import thomasc.loananalyzer.widgets.TermPicker;

public class TermPickerFragment extends AppCompatDialogFragment {

    private OnTermPickedListener listener = null;

    public void setListener(OnTermPickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Loan.IntervalType intervalType = Loan.IntervalType.MONTHLY;
        int intervalTypeTimes = 1;
        int intervals = 1;

        Bundle args = getArguments();
        if (args != null) {
            int i = args.getInt("intervalType");
            intervalType = Loan.IntervalType.values()[i];
            intervalTypeTimes = args.getInt("intervalTypeTimes");
            intervals = args.getInt("intervals");
        }

        final TermPicker termPicker = new TermPicker(getContext(),
                intervalType,
                intervalTypeTimes,
                intervals);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(termPicker)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onTermPicked(termPicker);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    interface OnTermPickedListener {
        void onTermPicked(TermPicker termPicker);
    }
}
