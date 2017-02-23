package com.smart.rchat.smart;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smart.rchat.smart.adapter.GroupItemSelectAdapter;
import com.smart.rchat.smart.dao.UserDao;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.fragments.ImageSelectFragment;
import com.smart.rchat.smart.interfaces.ResponseListener;
import com.smart.rchat.smart.models.User;
import com.smart.rchat.smart.util.AppData;
import com.smart.rchat.smart.util.AppUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by nishant on 09.02.17.
 */

public class GroupCreateActivity extends BaseActivity implements View.OnClickListener, ImageSelectFragment.BitMapFetchListener {

    @BindView(R.id.edGroupName)
    public EmojiconEditText editText;

    @BindView(R.id.smiley)
    public ImageView emoji;

    @BindView(R.id.fab)
    public FloatingActionButton floatingActionButton;

    @BindView(R.id.rvSelected)
    public RecyclerView recyclerView;

    @BindView(R.id.ivGroupImage)
    public ImageView groupImage;

    private int REQUEST_IMAGE_CAPTURE =1;

    private Bitmap imageBitmap;

    private  String [] userid;

    private ImageSelectFragment imageSelectFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_create_activity);

        userid = getIntent().getStringArrayExtra("users");

        View rootView = findViewById(R.id.rootView);
        EmojIconActions emojIcon=new EmojIconActions(this,rootView,editText,emoji);
        emojIcon.ShowEmojIcon();
        floatingActionButton.setOnClickListener(this);

        ArrayList<User> list = (ArrayList<User>) AppData.getInstance().getDumpObject();
        recyclerView.setAdapter(new GroupItemSelectAdapter(this,list));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        final String groupName = editText.getText().toString();
        if(groupName.isEmpty()){
            makeToast("group name should not be empty");
            return;
        }

        getNetworkClient().createGroup(groupName,imageBitmap, userid,new ResponseListener(){
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    UserDao.insertValues(GroupCreateActivity.this,jsonObject.getString("groupId"),groupName,
                            jsonObject.getString("url"),jsonObject.getJSONArray("members").toString(),2);

                    Intent intent = new Intent(GroupCreateActivity.this, ChatRoomActivity.class);
                    intent.putExtra("friend_user_id",jsonObject.getString("groupId"));
                    intent.putExtra("name",groupName);
                    intent.putExtra("type",2);
                    intent.putExtra("members",jsonObject.getJSONArray("members").toString());
                    setResult(RESULT_OK,intent);
                    finish();
                }catch (Exception e){

                }
            }

            @Override
            public void onError(Exception error) {

            }
        });
    }

    @OnClick(R.id.ivGroupImage)
    public void fetchGroupImage(View view){
        imageSelectFragment = new ImageSelectFragment();
        imageSelectFragment.show(getFragmentManager(),ImageSelectFragment.TAG);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBitMapFetched(Bitmap bitmap) {
        groupImage.setImageBitmap(bitmap);
        imageBitmap = bitmap;
    }

}
