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
import com.smart.rchat.smart.dao.UserDao;
import com.smart.rchat.smart.interfaces.ResponseListener;
import com.smart.rchat.smart.models.User;
import com.smart.rchat.smart.util.AppData;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
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

    private String profileUrl;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userId = getIntent().getStringExtra("id");
        int type =  getIntent().getIntExtra("type",0);
        String name = getIntent().getStringExtra("name");
        profileUrl = getIntent().getStringExtra("url");

        if(type == 2){
            createUserList(userId);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
        getNetworkClient().getProfileUrlFromId(userId,type,new FetchLatestProfileUrl(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBitMap(profileUrl,ivProfile);
    }

    private  void createUserList(String groupId){
        GroupMemberAdapter gr = new GroupMemberAdapter(ProfileActivity.this,(ArrayList<User>) AppData.getInstance().getDumpObject());
        recyclerView.setAdapter(gr);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
    }

    private static class FetchLatestProfileUrl implements ResponseListener{

        private WeakReference<ProfileActivity> profileActivityRef ;

        FetchLatestProfileUrl(ProfileActivity profileActivity){
            profileActivityRef = new WeakReference<ProfileActivity>(profileActivity);
        }

        @Override
        public void onSuccess(JSONObject jsonObject) {
            ProfileActivity profileActivity = (ProfileActivity) profileActivityRef.get();
            if(profileActivity == null){
                return;
            }
            try {
                String newurl = jsonObject.getString("url");
                if(newurl == null || newurl.isEmpty()){
                    return;
                }

                if (profileActivity.profileUrl != null && profileActivity.profileUrl.equals(newurl)) {
                    return;
                }
                profileActivity.loadBitMap(newurl, profileActivity.ivProfile);
                UserDao.updateUser(profileActivity, profileActivity.userId, newurl);
            }catch (JSONException ex){

            }
        }

        @Override
        public void onError(Exception error) {

        }
    }

}
