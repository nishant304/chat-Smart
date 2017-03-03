package com.smart.rchat.smart;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.fragments.ImageSelectFragment;
import com.smart.rchat.smart.interfaces.ResponseListener;
import com.smart.rchat.smart.util.AppData;
import com.smart.rchat.smart.util.AppUtil;
import com.smart.rchat.smart.util.Preferenceutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by nishant on 13.02.17.
 */

public class UpdateProfileActivity extends BaseActivity  implements ImageSelectFragment.BitMapFetchListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.profile_image)
    public ImageView profileImage;

    private ImageSelectFragment imageSelectFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        String id = getIntent().getStringExtra("id");
        loadBitMap(Preferenceutil.getProfileUrl(this),profileImage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        getNetworkClient().getProfileUrlFromId(id,1,new FetchProfileUrl(this));
    }

    @OnClick(R.id.ivCamera)
    public void onClick(View v) {
        imageSelectFragment = new ImageSelectFragment();
        imageSelectFragment.show(getFragmentManager(),ImageSelectFragment.TAG);
    }

    @Override
    public void onBitMapFetched(Bitmap imageBitmap) {
        profileImage.setImageBitmap(imageBitmap);
        final String fileUrl = "images/" + UUID.randomUUID()+".png";
        AppUtil.uploadBitmap(fileUrl, imageBitmap, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.
                        getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                        map.put("profilePic",fileUrl);
                        dataSnapshot.getRef().removeEventListener(this);
                        dataSnapshot.getRef().setValue(map);
                        Preferenceutil.saveProfileUrl(UpdateProfileActivity.this,fileUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private static class FetchProfileUrl implements ResponseListener{

        private WeakReference<UpdateProfileActivity> activityRef;

        private FetchProfileUrl(UpdateProfileActivity updateProfileActivity){
            activityRef = new WeakReference<UpdateProfileActivity>(updateProfileActivity);
        }

        @Override
        public void onSuccess(JSONObject jsonObject) {
            UpdateProfileActivity updateProfileActivity = (UpdateProfileActivity) activityRef.get();
            if(updateProfileActivity == null){
                return;
            }
            try {
                String newurl = jsonObject.getString("url");
                if(newurl == null || newurl.isEmpty()){
                    return;
                }
                if (Preferenceutil.getProfileUrl(updateProfileActivity) != null &&
                        Preferenceutil.getProfileUrl(updateProfileActivity).equals(newurl)) {
                    return;
                }
                updateProfileActivity.loadBitMap(newurl, updateProfileActivity.profileImage);
                Preferenceutil.saveProfileUrl(updateProfileActivity,newurl);
            }catch (JSONException ex){

            }
        }

        @Override
        public void onError(Exception error) {

        }
    }


}
