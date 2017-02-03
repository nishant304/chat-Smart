package com.smart.rchat.smart.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Process;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.rchat.smart.database.RChatContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nishant on 27.01.17.
 */

public class UpdateContactsService  implements Runnable {

    private final List<String> cursor;
    private final Context context;

    public UpdateContactsService(List<String> list, Context context){
        this.cursor = list;
        this.context = context;
    }

    @Override
    public void run() {

        Process.setThreadPriority(5);
        for(String string:cursor){
            checkPhone(string);
        }

    }

    private void checkPhone(final String phoneNo) {
        FirebaseDatabase.getInstance().getReference().child("/Phone").child(phoneNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = null;
                if(dataSnapshot.exists()){
                    userID = dataSnapshot.getValue().toString();
                }

                if(userID!=null){
                    final String finalUserID = userID;
                    FirebaseDatabase.getInstance().getReference().child("/Users").child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(RChatContract.USER_TABLE.USER_ID, finalUserID);
                            contentValues.put(RChatContract.USER_TABLE.USER_NAME, (String) map.get("name"));
                            contentValues.put(RChatContract.USER_TABLE.PHONE, phoneNo);
                            contentValues.put(RChatContract.USER_TABLE.PROFILE_PIC, "");
                            context.getContentResolver().insert(RChatContract.USER_TABLE.CONTENT_URI,contentValues) ;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(RChatContract.USER_TABLE.PHONE, phoneNo);
                    //getContentResolver().insert(RChatContract.USER_TABLE.CONTENT_URI,contentValues) ;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
