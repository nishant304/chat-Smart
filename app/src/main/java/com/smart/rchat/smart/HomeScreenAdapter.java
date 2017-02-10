package com.smart.rchat.smart;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.smart.rchat.smart.adapter.ContactsAdapter;

/**
 * Created by nishant on 08.02.17.
 */

public class HomeScreenAdapter extends ContactsAdapter {

    private View.OnClickListener onClickListener;

    public HomeScreenAdapter(Context context, Cursor cursor){
        super(context,cursor);
        this.onClickListener = (View.OnClickListener) context;
    }

    @Override
    public void bindView(View itemView, Context context, Cursor cursor) {
        super.bindView(itemView, context, cursor);
        itemView.setOnClickListener(onClickListener);
    }

}
