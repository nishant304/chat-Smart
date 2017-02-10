package com.smart.rchat.smart;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;

import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.interfaces.ResponseListener;

import org.json.JSONObject;

import butterknife.BindView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by nishant on 09.02.17.
 */

public class GroupCreateActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edGroupName)
    public EmojiconEditText editText;

    @BindView(R.id.smiley)
    public ImageView emoji;

    @BindView(R.id.fab)
    public FloatingActionButton floatingActionButton;

    private  String [] userid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_create_activity);

        userid = getIntent().getStringArrayExtra("userid");

        View rootView = findViewById(R.id.rootView);
        EmojIconActions emojIcon=new EmojIconActions(this,rootView,editText,emoji);
        emojIcon.ShowEmojIcon();

        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String groupName = editText.getText().toString();
        if(groupName.isEmpty()){
            makeToast("group name should not be empty");
            return;
        }

        getNetworkClient().createGroup(groupName,null, userid,new ResponseListener(){
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    ContentValues cv = new ContentValues();
                    cv.put(RChatContract.USER_TABLE.USER_NAME, groupName);
                    cv.put(RChatContract.USER_TABLE.USER_ID, jsonObject.getString("groupId"));
                    cv.put(RChatContract.USER_TABLE.PROFILE_PIC, jsonObject.getString("url"));
                    getContentResolver().insert(RChatContract.USER_TABLE.CONTENT_URI, cv);
                    Intent intent = new Intent(GroupCreateActivity.this, ChatRoomActivity.class);
                    startActivity(intent);
                }catch (Exception e){

                }
            }

            @Override
            public void onError(Exception error) {

            }
        });
    }
}
