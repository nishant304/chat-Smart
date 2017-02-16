package com.smart.rchat.smart;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.services.UpdateContactsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nishant on 1/25/2017.
 */

public class PhoneVerifyActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.edPhone)
    public EditText phone;

    @BindView(R.id.btPhoneSubmit)
    public Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verify_screen);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (phone.getText().toString().length() < 8) {
            makeToast("phone number length is less than 8");
            return;
        }
        final String phoneNo = phone.getText().toString().trim().replace(" ","");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Users");
        final FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone.getText().toString());
        map.put("blockedList", new ArrayList<String>());
        map.put("typingTo", "");
        map.put("status", "Online");
        map.put("profilePic", "");
        map.put("name",currUser.getDisplayName());
        ref.child(currUser.getUid()).setValue(map).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    final DatabaseReference phoneRef = FirebaseDatabase.getInstance().getReference("/Phone");
                    phoneRef.child(phoneNo).setValue(currUser.getUid()).addOnCompleteListener(PhoneVerifyActivity.this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        makeToast("phone verification complete");
                                        //FirebaseAuth.getInstance().getCurrentUser().
                                        Intent intent1 = new Intent(PhoneVerifyActivity.this, HomeActivity.class);
                                        startActivity(intent1);
                                        //phoneRef.child(phoneNo);  Fixme
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

}
