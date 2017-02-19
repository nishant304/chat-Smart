package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;

import com.smart.rchat.smart.ChatRoomActivity;
import com.smart.rchat.smart.HomeActivity;
import com.smart.rchat.smart.adapter.ContactsAdapter;
import com.smart.rchat.smart.database.RChatContract;

/**
 * Created by nishant on 08.02.17.
 */

public class HomeScreenAdapter extends ContactsAdapter {

    public HomeScreenAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    @Override
    public void bindView(View itemView, Context context, Cursor cursor) {
        super.bindView(itemView, context, cursor);

        final int type = cursor.getInt(
                cursor.getColumnIndex(RChatContract.USER_TABLE.type));
        final String userId = cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.USER_ID));
        final String name = cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.USER_NAME));
        final String members = cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.memebers));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ChatRoomActivity.class);
                intent.putExtra("friend_user_id",userId);
                intent.putExtra("name",name);
                intent.putExtra("type",type);
                intent.putExtra("members",members);
                v.getContext().startActivity(intent);
            }
        });

    }

}
