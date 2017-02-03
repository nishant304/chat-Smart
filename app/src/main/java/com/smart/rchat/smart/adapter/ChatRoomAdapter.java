package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.smart.rchat.smart.R;
import com.smart.rchat.smart.database.RChatContract;

/**
 * Created by nishant on 28.01.17.
 */

public class ChatRoomAdapter extends CursorAdapter {

    private final  LayoutInflater inflater;
    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ChatRoomAdapter(Context context, Cursor c) {
        super(context, c,false);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.chat_room_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String from  = cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.from));
        TextView left = (TextView) view.findViewById(R.id.tvLeft);
        TextView right = (TextView) view.findViewById(R.id.tvRight);
        View leftView = view.findViewById(R.id.rlLeft);
        View rightView = view.findViewById(R.id.rlRight);

        if(!from.equals(myId)) {
            left.setText(cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message)));
            rightView.setVisibility(View.GONE);
            leftView.setVisibility(View.VISIBLE);
        }else {
            rightView.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.GONE);
            right.setText(cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message)));
        }
    }
}
