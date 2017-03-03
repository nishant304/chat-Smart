package com.smart.rchat.smart;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.network.NetworkClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;


/**
 * Created by nishant on 1/24/2017.
 */
public class BaseActivity extends AppCompatActivity  {

    private ProgressDialog progressDialog;

    private ContentObserver contentObserver;

    private NetworkClient networkClient = NetworkClient.getInstance();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected void addFragment(Fragment fragment, int id) {
        getFragmentManager().beginTransaction().add(id, fragment).commit();
    }

    protected void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }


    protected void showDialog() {
        progressDialog = ProgressDialog.show(this,"","Please wait");
    }

    protected void hideDialog() {//ixme
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

    protected  NetworkClient getNetworkClient(){
        return networkClient;
    }

    @Override
    protected void onDestroy() {
        //Glide.with(this).onDestroy(); //Fixme
        //Fixme
        super.onDestroy();
    }

    protected void loadBitMap(String url, ImageView imageView){
        if(url == null || url.isEmpty()){
            return;
        }
        Glide.with(this).using(new FirebaseImageLoader())
                .load(FirebaseStorage.getInstance().getReference(url))
                .into(imageView);
    }

}
