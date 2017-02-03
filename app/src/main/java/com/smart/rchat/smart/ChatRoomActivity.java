package com.smart.rchat.smart;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.rchat.smart.adapter.ChatRoomAdapter;
import com.smart.rchat.smart.database.RChatContract;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nishant on 28.01.17.
 */

public class ChatRoomActivity extends  BaseActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.toolbar3)
    public Toolbar toolbar;

    @BindView(R.id.btSendMessage)
    public ImageView send;

    @BindView(R.id.edMessageBox)
    public EditText edMessageBox;

    @BindView(R.id.lvChatRoom)
    public ListView listView;

    public static  final int TYPE_MESSAGE = 1;
    public static  final int TYPE_IMAGE = 2;

    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private String friendUserId;
    private String name;

    private ChatRoomAdapter chatRoomAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat_room);
        friendUserId = getIntent().getStringExtra("friend_user_id");
        name = getIntent().getStringExtra("name");
        ButterKnife.bind(this);
        send.setOnClickListener(this);
        chatRoomAdapter =  new ChatRoomAdapter(this,null);
        listView.setAdapter(chatRoomAdapter);
        listView.setDivider(null);
        listView.setStackFromBottom(true);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        getLoaderManager().initLoader(0,null,this);

        TextView tvName = (TextView) toolbar.findViewById(R.id.tbName);
        tvName.setText(name);

        final  TextView tvLastSeen = (TextView) toolbar.findViewById(R.id.tbLastSeen);
        FirebaseDatabase.getInstance().getReference().child("Users").child(friendUserId).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    tvLastSeen.setText(dataSnapshot.getValue().toString());
                }else{
                    tvLastSeen.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tvLastSeen.setText("");
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("Online");
    }

    @Override
    public void onClick(View v) {
        if(edMessageBox.getText().toString().equals("")){
            return;
        }
        String message = edMessageBox.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("/Messages");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("from",currUser.getUid());
        hashMap.put("to",friendUserId);
        hashMap.put("message",message);
        hashMap.put("type",TYPE_MESSAGE);
        ContentValues cv = new ContentValues();
        cv.put(RChatContract.MESSAGE_TABLE.to,friendUserId);
        cv.put(RChatContract.MESSAGE_TABLE.message,hashMap.get("message").toString());
        cv.put(RChatContract.MESSAGE_TABLE.time,System.currentTimeMillis());
        cv.put(RChatContract.MESSAGE_TABLE.from,currUser.getUid());
        cv.put(RChatContract.MESSAGE_TABLE.type,TYPE_MESSAGE);
        getContentResolver().insert(RChatContract.MESSAGE_TABLE.CONTENT_URI,cv);
        ref.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                makeToast("success sending");
            }
        });
        edMessageBox.getText().clear();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,RChatContract.MESSAGE_TABLE.CONTENT_URI,null, RChatContract.MESSAGE_TABLE.from
             +" =? OR "+ RChatContract.MESSAGE_TABLE.to + " =? ",new String[]{friendUserId,friendUserId},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        chatRoomAdapter.swapCursor(data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//Fixme
        NavUtils.shouldUpRecreateTask(this,new Intent(this,HomeActivity.class));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        chatRoomAdapter.swapCursor(null);
    }
}
