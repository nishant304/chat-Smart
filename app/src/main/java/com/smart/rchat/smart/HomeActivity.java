package com.smart.rchat.smart;

import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.smart.rchat.smart.adapter.ContactsAdapter;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.services.ContactsListenerService;
import com.smart.rchat.smart.util.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nishant on 1/23/2017.
 */

public class HomeActivity extends ContactActivity implements View.OnClickListener {

    IContactListener listener;

    private String id = "";
    boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService();
    }

    private void startService() {
        Intent intent = new Intent(HomeActivity.this, ContactsListenerService.class);
        //startService(intent);
        bindService(intent,mConnection,BIND_EXTERNAL_SERVICE);
    }

    @Override
    protected void onCursorLoaded(Cursor cursor) {
        HomeScreenAdapter contactsAdapter = new HomeScreenAdapter(HomeActivity.this,cursor);//fix me
        getListView().setAdapter(contactsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        id = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_signout){
            onSignOutClicked();
            return  true;
        }else if(item.getItemId() == R.id.action_create_group){
            Intent intent = new Intent(this,GroupItemSelectActivity.class);
            startActivity(intent);
            return  true;
        }else if(item.getItemId() == R.id.action_update_profile){
            Intent intent = new Intent(this,UpdateProfileActivity.class);
            intent.putExtra("id",AppUtil.getUserId());
            startActivity(intent);
            return  true;
        }
        return false;
    }

    private void onSignOutClicked(){
        getNetworkClient().updateStatus("last seen at "+AppUtil.getCurrentTime());
        if(bound) {
            unbindService(mConnection);
            bound = false;
        }
        try {
            if (listener != null) {
                listener.stopService();
            }
        }catch(Exception e){

        }

        FirebaseAuth.getInstance().signOut();
        getContentResolver().delete(RChatContract.USER_TABLE.CONTENT_URI,null,null);
        getContentResolver().delete(RChatContract.MESSAGE_TABLE.CONTENT_URI,null,null);

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        ContactsAdapter.NameIdPair nameIdPair= (ContactsAdapter.NameIdPair) v.getTag();
        id = nameIdPair.userId;
        Intent intent = new Intent(HomeActivity.this,ChatRoomActivity.class);
        intent.putExtra("friend_user_id",id);
        intent.putExtra("name",nameIdPair.name);
        startActivity(intent);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            bound = true;
            try {
                listener = IContactListener.Stub.asInterface(service);
                listener.registerActivityCallBack(cl);
            }catch(RemoteException ex){

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound =false;
        }
    };

    private  IActivityCallBack cl =  new  IActivityCallBack.Stub(){

        @Override
        public String getFriendIdInChat() throws RemoteException {
            return id;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bound) {
            unbindService(mConnection);
        }
    }
}
