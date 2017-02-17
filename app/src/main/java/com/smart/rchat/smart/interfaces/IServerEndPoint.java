package com.smart.rchat.smart.interfaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.smart.rchat.smart.models.MessageRequest;

/**
 * Created by nishant on 05.02.17.
 */

public interface IServerEndPoint {

    String sendMessage(MessageRequest messageRequest);

    void createUser(String email,String password,ResponseListener responseListener);

    void loginUser(String email,String password,ResponseListener responseListener);

    void logoutUser();

    void verifyPhoneNo(String phone,ResponseListener responseListener);

    void uploadPhoto(String url,Bitmap bitmap,ResponseListener responseListener);

    void updateUserStatus(String status);

    void updateTypingStatus(String typingTo);

    void updatePhoneNo(String phoneNumber,ResponseListener responseListener);

    void createGroup(String groupName,Bitmap bitmap,String [] userIDs,ResponseListener responseListener);

    void loadBitMap(Context context,String url, ImageView imageView,int type);

}
