package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.smart.rchat.smart.R;
import com.smart.rchat.smart.util.AppUtil;

import java.util.HashMap;

/**
 * Created by nishant on 19.02.17.
 */

public class GroupChatRoomAdapter extends ChatRoomAdapter {

    public GroupChatRoomAdapter(Context context, Cursor cursor, String groupUserId){
        super(context,cursor,groupUserId);
    }

    @Override
    protected void handleContactView(View view, Context context, Cursor cursor, String from) {
        super.handleContactView(view, context, cursor, from);
        setNameForGroupMember(view,from);
    }

    @Override
    protected void handleImageView(View view, Context context, Cursor cursor, String from) {
        super.handleImageView(view, context, cursor, from);
        setNameForGroupMember(view,from);
    }

    @Override
    protected void handleMessageView(View view, Context context, Cursor cursor, String from) {
        super.handleMessageView(view, context, cursor, from);
        setNameForGroupMember(view,from);
    }

    private void setNameForGroupMember(View view,String from){
        if (!from.equals(AppUtil.getUserId())) {
            HashMap<String,String> hashMap = getUserIdMapping();
            TextView tvMemberName = (TextView) view.findViewById(R.id.tvGrpMember);
            if(hashMap == null){
                return;
            }
            tvMemberName.setVisibility(View.VISIBLE);
            if(hashMap.get(from) == null){
                tvMemberName.setText("Unknown");
                return;
            }
            tvMemberName.setText(hashMap.get(from));
        }
    }

}
