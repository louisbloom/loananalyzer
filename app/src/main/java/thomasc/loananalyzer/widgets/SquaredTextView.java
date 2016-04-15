/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import thomasc.loananalyzer.R;

public class SquaredTextView extends AppCompatTextView {

    @SuppressWarnings("unused")
    private static final String TAG = "SquaredTextView";

    public SquaredTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public SquaredTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SquaredTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SquaredTextView, defStyle, 0);
        int color = a.getColor(R.styleable.SquaredTextView_circle, Color.TRANSPARENT);
        a.recycle();

        setColor(color);
    }

    public void setColor(int color) {
        GradientDrawable drawable = (GradientDrawable) getBackground();
        drawable.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        // Works around an API 16 measure overflow bug.
        // http://developer.android.com/reference/android/view/View.MeasureSpec.html#makeMeasureSpec(int, int)
        if (h > 1000) {
            h = 0;
        }
        if (w > 1000) {
            w = 0;
        }

        int size = Math.max(w, h);
        setMeasuredDimension(size, size);
    }
}
