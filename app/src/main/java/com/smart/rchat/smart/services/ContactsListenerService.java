package com.smart.rchat.smart.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.rchat.smart.ChatRoomActivity;
import com.smart.rchat.smart.IActivityCallBack;
import com.smart.rchat.smart.IContactListener;
import com.smart.rchat.smart.R;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by nishant on 29.01.17.
 */

public class ContactsListenerService extends Service {

    private IActivityCallBack iActivityCallBack;
    private HashMap<String, String> idToName = new HashMap<>();

    private boolean stopTask = false;

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationContext().getContentResolver().
                registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false, new ContactsObserver());
        stopTask = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseApp.initializeApp(this);
        updateDb();
        listenForMessages();
        return START_STICKY;
    }

    private class ContactsObserver extends ContentObserver {

        public ContactsObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            updateDb();
        }
    }

    private void updateDb() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]
                {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
        List<String> phoneNumbers = new ArrayList<>();
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            number = number.trim().replace(" ", "").replace("-", "").replace("+", "");
            if (p.matcher(number).find()) {
                continue;
            }
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            checkPhone(number, name);
        }
        cursor.close();
    }

    private void checkPhone(final String phoneNo, final String name) {
        FirebaseDatabase.getInstance().getReference().child("/Phone").child(phoneNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = null;
                if (dataSnapshot.exists()) {
                    userID = dataSnapshot.getValue().toString();
                    if (userID != null) {
                        idToName.put(userID, name);
                    }
                }

                if (userID != null) {
                    final String finalUserID = userID;

                    FirebaseDatabase.getInstance().getReference().child("/Users").child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(RChatContract.USER_TABLE.USER_ID, finalUserID);
                            contentValues.put(RChatContract.USER_TABLE.USER_NAME, name);
                            contentValues.put(RChatContract.USER_TABLE.PHONE, phoneNo);
                            contentValues.put(RChatContract.USER_TABLE.PROFILE_PIC, "");
                            getContentResolver().insert(RChatContract.USER_TABLE.CONTENT_URI, contentValues);
                            idToName.put(finalUserID, name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(RChatContract.USER_TABLE.USER_ID, "");
                    contentValues.put(RChatContract.USER_TABLE.PHONE, phoneNo);
                    contentValues.put(RChatContract.USER_TABLE.USER_NAME, name);
                    //getContentResolver().insert(RChatContract.USER_TABLE.CONTENT_URI,contentValues) ;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        if(!stopTask) {

            Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
            restartServiceIntent.setPackage(getPackageName());

            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
        }

        super.onTaskRemoved(rootIntent);
    }


    private void listenForMessages() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Fixme

        if (user != null) {
            FirebaseDatabase.getInstance().getReference().child("/Messages").orderByChild("/to").limitToLast(1).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (map == null) {
                        return;
                    }
                    Set<String> set = map.keySet();
                    for (String s : set) {
                        HashMap<String, Object> hm = (HashMap<String, Object>) map.get(s);
                        ContentValues cv = new ContentValues();
                        cv.put(RChatContract.MESSAGE_TABLE.from, hm.get("from").toString());
                        cv.put(RChatContract.MESSAGE_TABLE.type, Integer.valueOf(hm.get("type").toString()));
                        cv.put(RChatContract.MESSAGE_TABLE.message, hm.get("message").toString());
                        cv.put(RChatContract.MESSAGE_TABLE.time, System.currentTimeMillis());
                        cv.put(RChatContract.MESSAGE_TABLE.to, user.getUid());
                        //cv.put(RChatContract.MESSAGE_TABLE.message_id, s); //Fixme
                        getContentResolver().insert(RChatContract.MESSAGE_TABLE.CONTENT_URI, cv);
                        try {
                            if (iActivityCallBack == null || !iActivityCallBack.getFriendIdInChat().equals(hm.get("from").toString())) {
                                createNotification(hm.get("from").toString(), hm.get("message").toString());
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void createNotification(String friendUserId, String message) {
        Intent resultIntent = new Intent(this, ChatRoomActivity.class);
        resultIntent.putExtra("friend_user_id", friendUserId);
        resultIntent.putExtra("name", idToName.get(friendUserId) != null ? idToName.get(friendUserId) : "");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).
                setContentTitle(idToName.get(friendUserId))
                .setContentText(message)
                .setSmallIcon(R.drawable.zzz_alert)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    IContactListener.Stub binder = new IContactListener.Stub() {
        @Override
        public void registerActivityCallBack(IActivityCallBack callBack) throws RemoteException {
            iActivityCallBack = callBack;
        }

        @Override
        public void stopService() throws RemoteException {

            FirebaseAuth.getInstance().signOut();
            stopTask = true;
            stopSelf();
            stopService();
        }
    };

}
