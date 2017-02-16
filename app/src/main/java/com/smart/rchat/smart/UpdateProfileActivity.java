package com.smart.rchat.smart;

import android.content.ContentValues;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.util.AppData;
import com.smart.rchat.smart.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by nishant on 13.02.17.
 */

public class UpdateProfileActivity extends BaseActivity  implements View.OnClickListener{

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.profile_image)
    public ImageView profileImage;

    @BindView(R.id.ivCamera)
    public ImageView cameraView;

    private static final int PICK_IMAGE = 1;

    private  static  final  int REQUEST_IMAGE_CAPTURE =2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        String id = getIntent().getStringExtra("id");
        getNetworkClient().loadBitMap(this,id,profileImage,1);
        cameraView.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btChangePic)
    public void  changePic(View view){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        getIntent.putExtra("return-data",true);
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        pickIntent.putExtra("return-data",true);
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onClick(View v) {
        Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profileImage.setImageBitmap(imageBitmap);
                updateProfile(imageBitmap);
            }

            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = AppUtil.getBitmapFromUri(data.getData(),this);
                profileImage.setImageBitmap(imageBitmap);
                updateProfile(imageBitmap);
            }
    }

    private void updateProfile(Bitmap imageBitmap){
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

}
