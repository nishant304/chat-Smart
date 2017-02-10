package com.smart.rchat.smart.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;


/**
 * Created by nishant on 27.01.17.
 */

public class RChatContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int USER_TABLE = 1;

    private static final int MESSAGE_TABLE = 2;

    static {
        uriMatcher.addURI(RChatContract.USER_TABLE.CONTENT_URI.getAuthority(), RChatContract.USER_TABLE.TABLE_NAME, USER_TABLE);
        uriMatcher.addURI(RChatContract.USER_TABLE.CONTENT_URI.getAuthority(),
                RChatContract.MESSAGE_TABLE.TABLE_NAME, MESSAGE_TABLE);
    }

    private SQLiteOpenHelper database;

    @Override
    public boolean onCreate() {
        database = new RChatDataBase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        if (uriMatcher.match(uri) == USER_TABLE) {
            cursor = database.getReadableDatabase().query(RChatContract.USER_TABLE.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        } else {
            cursor = database.getReadableDatabase().query(RChatContract.MESSAGE_TABLE.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        try {
            if (uriMatcher.match(uri) == USER_TABLE) {
                long id = database.getWritableDatabase().insert(RChatContract.USER_TABLE.TABLE_NAME, null, contentValues);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri.withAppendedPath(uri, id + "");
            } else if (uriMatcher.match(uri) == MESSAGE_TABLE) {
                long id = database.getWritableDatabase().insert(RChatContract.MESSAGE_TABLE.TABLE_NAME, null, contentValues);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri.withAppendedPath(uri, id + "");
            }
        }catch (Exception e){

        }

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        if(uriMatcher.match(uri)==1){
            database.getWritableDatabase().delete(RChatContract.USER_TABLE.TABLE_NAME,s,strings);
        }else {
            database.getWritableDatabase().delete(RChatContract.MESSAGE_TABLE.TABLE_NAME,s,strings);
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }


    private class RChatDataBase extends SQLiteOpenHelper {

        private static final String name = "rchatdatabase";
        private static final int vers = 1;

        RChatDataBase(Context context) {
            super(context, name, null, vers);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table " + RChatContract.USER_TABLE.TABLE_NAME
                    + " (  " + " _id INTEGER PRIMARY KEY , " + RChatContract.USER_TABLE.USER_ID + " TEXT , " +
                    RChatContract.USER_TABLE.USER_NAME + " TEXT , " +
                    RChatContract.USER_TABLE.PROFILE_PIC + " TEXT , " +
                    RChatContract.USER_TABLE.PHONE + " TEXT unique , "
                    + RChatContract.USER_TABLE.LAST_MESSAGE + " TEXT );");

            sqLiteDatabase.execSQL("create table " + RChatContract.MESSAGE_TABLE.TABLE_NAME
                    + " (  " + " _id INTEGER PRIMARY KEY , " + RChatContract.MESSAGE_TABLE.from + " TEXT , " +
                    RChatContract.MESSAGE_TABLE.message + " TEXT , " +
                    RChatContract.MESSAGE_TABLE.to + " TEXT , " +
                    RChatContract.MESSAGE_TABLE.type + " INTEGER , "+
                    RChatContract.MESSAGE_TABLE.time + " TEXT );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            dropTables();
            onCreate(sqLiteDatabase);
        }

        private void dropTables() {

        }
    }

}
