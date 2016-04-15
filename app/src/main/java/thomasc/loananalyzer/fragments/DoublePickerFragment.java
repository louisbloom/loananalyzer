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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.common.LoanUtils;

public class DoublePickerFragment extends AppCompatDialogFragment
        implements DialogInterface.OnClickListener {
    private EditText editText = null;
    private OnDoublePickedListener listener = null;

    public void setListener(OnDoublePickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = "";
        double value = 0;

        if (args != null) {
            title = args.getString("title");
            value = args.getDouble("value");
        }

        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.double_picker,
                (ViewGroup) getView());

        editText = (EditText) view.findViewById(R.id.value);
        if (value != 0) {
            editText.setText(String.format("%1$.2f", value));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .setTitle(title).create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (listener != null) {
            listener.onDoublePicked(LoanUtils.getDouble(editText, 0));
        }
    }

    interface OnDoublePickedListener {
        void onDoublePicked(double value);
    }
}
