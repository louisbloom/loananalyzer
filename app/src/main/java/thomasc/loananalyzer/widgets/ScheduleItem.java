/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import thomasc.loananalyzer.R;

public class ScheduleItem extends LinearLayoutCompat {

    @SuppressWarnings("unused")
    private static final String TAG = "ScheduleItem";

    public ScheduleItem(Context context) {
        super(context);
        init(null, 0);
    }

    public ScheduleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScheduleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        inflate(getContext(), R.layout.list_item_schedule_custom, this);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ScheduleItem,
                defStyleAttr,
                0);

        try {
            TextView tv;
            String s;
            TypedValue v = new TypedValue();

            tv = (TextView) findViewById(R.id.text1);
            if (tv != null) {
                s = a.getString(R.styleable.ScheduleItem_text1);
                if (s != null) {
                    tv.setText(s);
                }

                if (a.getValue(R.styleable.ScheduleItem_textAppearance1, v)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tv.setTextAppearance(v.resourceId);
                    } else {
                        //noinspection deprecation
                        tv.setTextAppearance(getContext(), v.resourceId);
                    }
                }
            }

            tv = (TextView) findViewById(R.id.principal);
            if (tv != null) {
                s = a.getString(R.styleable.ScheduleItem_text2);
                if (s != null) {
                    tv.setText(s);
                }

                if (a.getValue(R.styleable.ScheduleItem_textAppearance2, v)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tv.setTextAppearance(v.resourceId);
                    } else {
                        //noinspection deprecation
                        tv.setTextAppearance(getContext(), v.resourceId);
                    }
                }
            }

            tv = (TextView) findViewById(R.id.text3);
            if (tv != null) {
                s = a.getString(R.styleable.ScheduleItem_text3);
                if (s != null) {
                    tv.setText(s);
                }

                if (a.getValue(R.styleable.ScheduleItem_textAppearance3, v)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tv.setTextAppearance(v.resourceId);
                    } else {
                        //noinspection deprecation
                        tv.setTextAppearance(getContext(), v.resourceId);
                    }
                }
            }

            tv = (TextView) findViewById(R.id.text4);
            if (tv != null) {
                s = a.getString(R.styleable.ScheduleItem_text4);
                if (s != null) {
                    tv.setText(s);
                }

                if (a.getValue(R.styleable.ScheduleItem_textAppearance4, v)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tv.setTextAppearance(v.resourceId);
                    } else {
                        //noinspection deprecation
                        tv.setTextAppearance(getContext(), v.resourceId);
                    }
                }
            }

        } finally {
            a.recycle();
        }
    }
}
