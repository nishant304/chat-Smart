package com.smart.rchat.smart.interfaces;

import android.graphics.Bitmap;

import com.smart.rchat.smart.models.MessageRequest;

/**
 * Created by nishant on 05.02.17.
 */

public interface IServerEndPoint {

    void sendMessage(MessageRequest messageRequest);

    void createUser(String email,String password,ResponseListener responseListener);

    void loginUser(String email,String password,ResponseListener responseListener);

    void logoutUser();

    void verifyPhoneNo(String phone,ResponseListener responseListener);

    void uploadPhoto(String url,Bitmap bitmap,ResponseListener responseListener);

    void updateUserStatus(String status);

    void updateTypingStatus(String typingTo);

    void updatePhoneNo(String phoneNumber,ResponseListener responseListener);

}
