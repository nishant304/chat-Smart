package com.smart.rchat.smart.dao;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.VisibleForTesting;

import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.models.User;

/**
 * Created by nishant on 21.02.17.
 */

public final class UserDao {

    public static Uri insertValues(Context context, String  userId, String userName, String profileUrl, String members, int type){
        ContentValues contentValues = new ContentValues();
        contentValues.put(RChatContract.USER_TABLE.USER_ID, userId);
        contentValues.put(RChatContract.USER_TABLE.USER_NAME, userName);
        contentValues.put(RChatContract.USER_TABLE.PROFILE_PIC, profileUrl);
        contentValues.put(RChatContract.USER_TABLE.memebers, members);
        contentValues.put(RChatContract.USER_TABLE.type, type);
        return context.getContentResolver().insert(RChatContract.USER_TABLE.CONTENT_URI,contentValues);
    }

    @VisibleForTesting
    public static Uri insertValues(Context context, User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(RChatContract.USER_TABLE.USER_ID, user.getUserId());
        contentValues.put(RChatContract.USER_TABLE.USER_NAME, user.getName());
        contentValues.put(RChatContract.USER_TABLE.PHONE, user.getPhone());
        contentValues.put(RChatContract.USER_TABLE.PROFILE_PIC, user.getProfilePic());
        contentValues.put(RChatContract.USER_TABLE.type, 1);
        return context.getContentResolver().insert(RChatContract.USER_TABLE.CONTENT_URI, contentValues);
    }

    @VisibleForTesting
    public static void clearData(Context context){
        context.getContentResolver().delete(RChatContract.USER_TABLE.CONTENT_URI,null,null);
    }

    public static void updateUser(Context context, String userId,String newUrl){
        ContentValues contentValues = new ContentValues();
        contentValues.put(RChatContract.USER_TABLE.PROFILE_PIC,newUrl);
        context.getContentResolver().update(RChatContract.USER_TABLE.CONTENT_URI,contentValues,
                RChatContract.USER_TABLE.USER_ID+" =? ",new String[]{userId});
    }

}
