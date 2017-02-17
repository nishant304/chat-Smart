package com.smart.rchat.smart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.rchat.smart.adapter.GroupMemberAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by nishant on 16.02.17.
 */

public class ProfileActivity extends BaseActivity  {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.ivProfile)
    public ImageView ivProfile;

    @BindView(R.id.rvProfile)
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        String id = getIntent().getStringExtra("id");
        int type =  getIntent().getIntExtra("type",0);
        String name = getIntent().getStringExtra("name");
        if(type == 2){
            createUserList(id);
        }
        getNetworkClient().loadBitMap(this,id,ivProfile,type);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
    }

    private  void createUserList(String groupId){
        FirebaseDatabase.getInstance().getReference().child("Group").child(groupId).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> users = (ArrayList<String>) dataSnapshot.getValue();
                GroupMemberAdapter gr = new GroupMemberAdapter(ProfileActivity.this,users);
                recyclerView.setAdapter(gr);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
