/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.common;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CappedArrayAdapter extends ArrayAdapter<CharSequence> {

    private CappedArrayAdapter(Context context, int resource, CharSequence[] objects) {
        super(context, resource, objects);
    }

    public static CappedArrayAdapter createFromResource(Context context, int
            textArrayResId, int textViewResId) {
        String[] array = context.getResources().getStringArray(textArrayResId);
        return new CappedArrayAdapter(context, textViewResId, array);
    }

    @Override
    public CharSequence getItem(int position) {
        return super.getItem(position).toString().toUpperCase();
    }

}
