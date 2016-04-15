/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.common;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerCursorAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private Cursor cursor = null;
    private int idColumn;

    RecyclerCursorAdapter() {
        setHasStableIds(true);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Couldn't move cursor");
        }

        onBindViewHolder(holder, cursor);
    }

    protected abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (hasStableIds() && cursor != null) {
            if (cursor.moveToPosition(position)) {
                return cursor.getLong(idColumn);
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return RecyclerView.NO_ID;
        }
    }

    public void changeCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    private Cursor swapCursor(Cursor cursor) {
        if (cursor == this.cursor) {
            return null;
        }

        Cursor oldCursor = this.cursor;
        this.cursor = cursor;

        if (cursor != null) {
            idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            notifyDataSetChanged();
        } else {
            idColumn = -1;
            notifyItemRangeRemoved(0, getItemCount());
        }

        return oldCursor;
    }
}
