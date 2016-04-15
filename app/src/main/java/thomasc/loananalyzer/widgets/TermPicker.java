/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.common.CappedArrayAdapter;
import thomasc.loananalyzer.common.LoanUtils;
import thomasc.loananalyzer.loans.Loan.IntervalType;

public class TermPicker extends LinearLayoutCompat {
    private Spinner intervalTypeCtrl;
    private EditText intervalTypeTimesCtrl;
    private TextView intervalTypePlural;
    private EditText intervalsCtrl;
    private TextView intervalsPlural;
    private OnTermSetListener listener = null;
    private IntervalType intervalType = IntervalType.MONTHLY;
    private int intervalTypeTimes = 1;
    private int intervals = 1;

    public TermPicker(Context context, IntervalType intervalType, int intervalTypeTimes, int intervals) {
        super(context);

        this.intervalType = intervalType;
        this.intervalTypeTimes = intervalTypeTimes;
        this.intervals = intervals;

        init(null);
    }

    public TermPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.term_picker, this);

        intervalTypeCtrl = (Spinner) findViewById(R.id.interval_type);
        intervalTypeTimesCtrl = (EditText) findViewById(R.id.interval_type_times);
        intervalTypePlural = (TextView) findViewById(R.id.interval_type_plural);
        intervalsCtrl = (EditText) findViewById(R.id.intervals);
        intervalsPlural = (TextView) findViewById(R.id.intervals_plural);

        CappedArrayAdapter adapter = CappedArrayAdapter.createFromResource(
                getContext(),
                R.array.interval_types,
                R.layout.custom_spinner_item_inverse);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        intervalTypeCtrl.setAdapter(adapter);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TermPicker,
                0, 0);

        try {
            int i;
            i = a.getInteger(R.styleable.TermPicker_intervalType,
                    intervalType.ordinal());
            intervalTypeCtrl.setSelection(i);

            i = a.getInteger(R.styleable.TermPicker_intervalTypeTimes,
                    intervalTypeTimes);
            intervalTypeTimesCtrl.setText(String.valueOf(i));

            i = a.getInteger(R.styleable.TermPicker_intervals, intervals);
            intervalsCtrl.setText(String.valueOf(i));
        } finally {
            a.recycle();
        }

        intervalTypeCtrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleViewChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                handleViewChange();
            }
        });

        intervalTypeTimesCtrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                handleViewChange();
            }
        });

        intervalsCtrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                handleViewChange();
            }
        });

        this.intervalTypeCtrl.setSelection(intervalType.ordinal());
        this.intervalTypeTimesCtrl.setText(String.valueOf(intervalTypeTimes));
        this.intervalsCtrl.setText(String.valueOf(intervals));

        handleViewChange();
    }

    /**
     * React to changes in views.
     */
    private void handleViewChange() {
        int id = -1;
        int pos = intervalTypeCtrl.getSelectedItemPosition();

        // Update state.
        intervalType = IntervalType.values()[pos];
        intervalTypeTimes = LoanUtils.getInt(this.intervalTypeTimesCtrl);
        intervals = LoanUtils.getInt(this.intervalsCtrl);

        switch (intervalType) {
            case YEARLY:
                id = R.plurals.years;
                break;

            case MONTHLY:
                id = R.plurals.months;
                break;

            case WEEKLY:
                id = R.plurals.weeks;
                break;

            case DAILY:
                id = R.plurals.days;
                break;
        }

        intervalTypePlural.setText(getResources().getQuantityString(
                id,
                intervalTypeTimes));

        intervalsPlural.setText(getResources().getQuantityString(
                R.plurals.times,
                intervals, intervals));

        if (intervalTypeTimes > 0 && intervals > 0) {
            if (listener != null) {
                listener.onTermSet(intervalType, intervalTypeTimes, intervals);
            }
        }
    }

    public IntervalType getIntervalType() {
        return intervalType;
    }

    public int getIntervalTypeTimes() {
        return intervalTypeTimes;
    }

    public int getIntervals() {
        return intervals;
    }

    public void setOnTermChangedListener(OnTermSetListener listener) {
        this.listener = listener;
    }

    public interface OnTermSetListener {
        void onTermSet(IntervalType intervalType, int intervalTypeTimes, int intervals);
    }
}
