package com.smart.rchat.smart.network;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smart.rchat.smart.ChatRoomActivity;
import com.smart.rchat.smart.interfaces.IServerEndPoint;
import com.smart.rchat.smart.interfaces.ResponseListener;
import com.smart.rchat.smart.models.MessageRequest;
import com.smart.rchat.smart.util.AppData;
import com.smart.rchat.smart.util.AppUtil;
import com.smart.rchat.smart.util.RchatError;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by nishant on 05.02.17.
 */

public class FireBaseImpl implements IServerEndPoint {

    @Override
    public void sendMessage(MessageRequest messageRequest) {
        FirebaseDatabase.getInstance().getReference().child("/Messages").push().
                setValue(AppUtil.getMessageRequest(messageRequest.getFrom(),
                        messageRequest.getMessage(),messageRequest.getType()));
    }

    @Override
    public void createUser(String email, String password, final ResponseListener responseListener) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    responseListener.onSuccess(null);
                }else{
                    String code = "something went wrong";
                    if (task.getException() instanceof FirebaseAuthException) {
                        FirebaseAuthException firebaseAuthException = (FirebaseAuthException) task.getException();
                        code = firebaseAuthException.getErrorCode();
                    }
                    responseListener.onError(new RchatError(code));
                }
            }
        });
    }

    @Override
    public void loginUser(String email, String password,final ResponseListener responseListener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    responseListener.onSuccess(null);
                }else{
                    String code = "something went wrong";
                    if (task.getException() instanceof FirebaseAuthException) {
                        FirebaseAuthException firebaseAuthException = (FirebaseAuthException) task.getException();
                        code = firebaseAuthException.getErrorCode();
                    }
                    responseListener.onError(new RchatError(code));
                }
            }
        });
    }

    @Override
    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void updateTypingStatus(String typingTo) {

    }

    @Override
    public void uploadPhoto(final String url, final Bitmap bitmap, final ResponseListener responseListener) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);
        storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                if(!task.isSuccessful()) {
                    Exception ex = task.getException();
                    if (ex instanceof StorageException) {
                        StorageException st = (StorageException) ex;
                        if (st.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            if(bitmap == null){
                                responseListener.onError(null);
                                return;
                            }
                            AppUtil.uploadBitmap(url, bitmap, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        responseListener.onSuccess(null);
                                    }else{
                                        responseListener.onError(task.getException());
                                    }
                                }
                            });
                        }
                    }else{
                        responseListener.onSuccess(null);
                    }
                }else{
                    responseListener.onError(task.getException());
                }
            }
        });
    }

    @Override
    public void updateUserStatus(String status) {
        FirebaseDatabase.getInstance().getReference().child("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue(status);
    }

    @Override
    public void verifyPhoneNo(String phone, ResponseListener responseListener) {

    }

    @Override
    public void updatePhoneNo(String phoneNumber, ResponseListener responseListener) {

    }
}
