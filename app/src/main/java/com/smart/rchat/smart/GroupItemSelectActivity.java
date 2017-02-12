package com.smart.rchat.smart;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;

import com.smart.rchat.smart.adapter.ContactsAdapter;
import com.smart.rchat.smart.database.RChatContract;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by nishant on 08.02.17.
 */

public class GroupItemSelectActivity extends ContactActivity implements View.OnClickListener {

    private  Cursor cursor;

    @BindView(R.id.fab)
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select contact");
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    protected void onCursorLoaded(Cursor cursor) {
        this.cursor = cursor;
        ContactsAdapter contactsAdapter = new ContactsAdapter(GroupItemSelectActivity.this,cursor);//fix me
        getListView().setAdapter(contactsAdapter);
    }

    @Override
    public void onClick(View v) {
        SparseBooleanArray sparseBooleanArray =  getListView().getCheckedItemPositions();
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<sparseBooleanArray.size();i++){

            if(sparseBooleanArray.valueAt(i)){
                cursor.moveToPosition(sparseBooleanArray.keyAt(i));
                list.add(cursor.getString(cursor.getColumnIndex(RChatContract.USER_TABLE.USER_ID)));
            }
        }

        String [] users = new String[list.size()];
        for(int i=0;i<list.size();i++){
            users[i] = list.get(i);
        }

        Intent intent = new Intent(this,GroupCreateActivity.class);
        intent.putExtra("users",users);
        startActivity(intent);
    }

}
