package com.smart.rchat.smart;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.smart.rchat.smart.adapter.ContactsAdapter;
import com.smart.rchat.smart.adapter.GroupSelectAdapter;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.models.User;
import com.smart.rchat.smart.util.AppData;
import com.smart.rchat.smart.util.AppUtil;

import java.util.ArrayList;
import java.util.BitSet;

import butterknife.BindView;

/**
 * Created by nishant on 08.02.17.
 */

public class GroupItemSelectActivity extends ContactActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private  Cursor cursor;

    private GroupSelectAdapter groupSelectAdapter;

    @BindView(R.id.fab)
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        getListView().setOnItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select contact");
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    protected CursorLoader getLoader() {
        return new CursorLoader(this, RChatContract.USER_TABLE.CONTENT_URI,null,
                RChatContract.USER_TABLE.type
                +" =? ",new String[]{"1"},null);
    }

    @Override
    protected void onCursorLoaded(Cursor cursor) {
        this.cursor = cursor;
        groupSelectAdapter = new GroupSelectAdapter(GroupItemSelectActivity.this,cursor);
        getListView().setAdapter(groupSelectAdapter);
    }

    @Override
    public void onClick(View v) {
        SparseBooleanArray sparseBooleanArray =  getListView().getCheckedItemPositions();

        if(sparseBooleanArray == null){
            return;
        }

        if(sparseBooleanArray.size() ==0){
            makeToast("Please select at lease one item");
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        ArrayList<User> userList = new ArrayList<>();
        for(int i=0;i<sparseBooleanArray.size();i++){

            if(sparseBooleanArray.valueAt(i)){
                cursor.moveToPosition(sparseBooleanArray.keyAt(i));
                String id = cursor.getString(cursor.getColumnIndex(RChatContract.USER_TABLE.USER_ID));
                list.add(id);
                String  name = cursor.getString(cursor.getColumnIndex(RChatContract.USER_TABLE.USER_NAME));
                String  number = cursor.getString(cursor.getColumnIndex(RChatContract.USER_TABLE.PHONE));
                String url = cursor.getString(cursor.getColumnIndex(RChatContract.USER_TABLE.PROFILE_PIC));
                userList.add(new User(id,url,number,name));
            }
        }

        String [] users = new String[list.size()];
        for(int i=0;i<list.size();i++){
            users[i] = list.get(i);
        }

        AppData.getInstance().dumpObject(userList);


        Intent intent = new Intent(this,GroupCreateActivity.class);
        intent.putExtra("users",users);
        startActivityForResult(intent,1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        groupSelectAdapter.updateSelectedSet(position);
        view.setBackgroundColor(groupSelectAdapter.getSelection(position)?Color.GRAY:Color.TRANSPARENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            startActivity(data);
            finish();
        }

    }
}
