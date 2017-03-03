package com.smart.rchat.smart.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by nishant on 03.03.17.
 */

public class Preferenceutil {

    private static final String userID = "userID";

    private static final String profileUrl = "profileUrl";

    public static void saveUserID(@NonNull Context context){
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sh.edit().putString(userID,AppUtil.getUserId());
    }

    public static String getUserId(@NonNull Context context){
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sh.getString(userID,"");
    }

    public static void saveProfileUrl(@NonNull Context context,String url){
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sh.edit().putString(profileUrl,url);
    }

    public static String getProfileUrl(@NonNull Context context){
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sh.getString(profileUrl,"");
    }

}
