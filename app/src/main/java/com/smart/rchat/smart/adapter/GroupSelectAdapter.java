package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;

import java.util.BitSet;

/**
 * Created by nishant on 15.02.17.
 */

public class GroupSelectAdapter extends ContactsAdapter {

    private BitSet selectedItems;

    public GroupSelectAdapter(Context context, Cursor cursor){
        super(context,cursor);
        selectedItems = new BitSet(cursor.getCount());
    }

    @Override
    public void bindView(View itemView, Context context, Cursor cursor) {
        super.bindView(itemView, context, cursor);
        itemView.setBackgroundColor(getSelection(cursor.getPosition())? Color.GRAY:Color.TRANSPARENT);
    }

    public void updateSelectedSet(int pos){
        selectedItems.flip(pos);
    }

    public boolean getSelection(int pos){
        return  selectedItems.get(pos);
    }
}
