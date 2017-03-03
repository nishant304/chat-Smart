package com.smart.rchat.smart.dao;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.models.MessageRequest;

/**
 * Created by nishant on 19.02.17.
 */

public final class MessageDao {

    public static Uri insertValue(Context context, MessageRequest request, String key){
        ContentValues cv = new ContentValues();
        cv.put(RChatContract.MESSAGE_TABLE.from, request.getFrom());
        cv.put(RChatContract.MESSAGE_TABLE.type, request.getType());
        cv.put(RChatContract.MESSAGE_TABLE.message, request.getMessage());
        cv.put(RChatContract.MESSAGE_TABLE.time, System.currentTimeMillis());
        cv.put(RChatContract.MESSAGE_TABLE.to, request.getTo());
        cv.put(RChatContract.MESSAGE_TABLE.msg_id, key);
        return context.getContentResolver().insert(RChatContract.MESSAGE_TABLE.CONTENT_URI,cv);
    }

    public static void clearData(Context context){
        context.getContentResolver().delete(RChatContract.MESSAGE_TABLE.CONTENT_URI,null,null);
    }

}
