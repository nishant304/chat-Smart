package com.smart.rchat.smart.database;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by nishant on 27.01.17.
 */


public final class RChatContract {

    public  static  final  String Authority = "com.smart.rchat.smart";
    public  static  final Uri CONTENT_URI = Uri.parse("content://"+Authority) ;

    public static class USER_TABLE implements BaseColumns{
        public  static  final  String TABLE_NAME=  "User";
        public  static  final Uri  CONTENT_URI = Uri.parse(RChatContract.CONTENT_URI.toString()+"/"+TABLE_NAME);
        public  static  final  String USER_ID=  "user_id";
        public  static  final  String USER_NAME=  "user_name";
        public  static  final  String PROFILE_PIC=  "profile_pic";
        public  static  final  String LAST_MESSAGE =  "last_message";
        public  static  final  String PHONE =  "phone";
        public  static  final  String type =  "type";
        public  static  final  String memebers =  "members";
    }

    public static class MESSAGE_TABLE implements BaseColumns{
        public  static  final  String TABLE_NAME=  "Message";
        public  static  final Uri  CONTENT_URI = Uri.parse(RChatContract.CONTENT_URI.toString()+"/"+TABLE_NAME);
        public  static  final  String message=  "message";
        public  static  final  String type=  "type";
        public  static  final  String from=  "from_user_id";
        public  static  final  String to=  "to_user_id";
        public  static  final  String time =  "time";
        public  static  final  String msg_id =  "messageID";
    }
}