package com.smart.rchat.smart.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * <Created by nishant on 05.02.17.
 */

public class FireBaseImpl implements IServerEndPoint {

    @Override
    public void sendMessage(MessageRequest messageRequest) {
        FirebaseDatabase.getInstance().getReference().child("/Messages").push().
                setValue(AppUtil.getMessageRequest(messageRequest.getTo(),
                        messageRequest.getMessage(), messageRequest.getType()));
    }

    @Override
    public void createUser(String email, String password, final ResponseListener responseListener) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    responseListener.onSuccess(null);
                } else {
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
    public void loginUser(String email, String password, final ResponseListener responseListener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    responseListener.onSuccess(null);
                } else {
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
                if (!task.isSuccessful()) {
                    Exception ex = task.getException();
                    if (ex instanceof StorageException) {
                        StorageException st = (StorageException) ex;
                        if (st.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            if (bitmap == null) {
                                responseListener.onError(null);
                                return;
                            }
                            AppUtil.uploadBitmap(url, bitmap, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        responseListener.onSuccess(null);
                                    } else {
                                        responseListener.onError(task.getException());
                                    }
                                }
                            });
                        }
                    } else {
                        responseListener.onSuccess(null);
                    }
                } else {
                    responseListener.onError(null);
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

    @Override
    public void createGroup(final String groupName, Bitmap bitmap, final String[] userIDs, final ResponseListener responseListener) {

        if (bitmap == null) {
            try {
                handleCreateGroupRequest(groupName, "", userIDs, responseListener);
            }catch (Exception e){

            }
            return;
        }

        final String fileUrl = "images/" + UUID.randomUUID() + ".png";

        uploadPhoto(fileUrl, bitmap, new ResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    handleCreateGroupRequest(groupName, fileUrl, userIDs, responseListener);
                }catch (Exception e){

                }
            }

            @Override
            public void onError(Exception error) {
                responseListener.onError(new RchatError(""));
            }
        });
    }


    private void handleCreateGroupRequest(final String groupName, final String fileUrl, final String[] userIDs,
                                          final ResponseListener responseListener) throws Exception {

        final ArrayList<String> list= new ArrayList<>();
        for(String us :userIDs){
            list.add(us);
        }

        final HashMap<String,Object> jsonObject = new HashMap<>();
        jsonObject.put("name", groupName);
        jsonObject.put("url", fileUrl);
        jsonObject.put("members", list);

        DatabaseReference newRef=  FirebaseDatabase.getInstance().getReference("/Group").push();
        final String key = newRef.getKey();
        newRef.setValue(jsonObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){
                     //Fixme if flow breaks
                     FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.
                             getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                             ArrayList<String> list = (ArrayList<String>) map.get("groups");
                             if(list == null){
                                 list = new ArrayList<String>();
                             }
                             list.add(key);
                             map.put("groups",list);
                             dataSnapshot.getRef().removeEventListener(this);
                             dataSnapshot.getRef().setValue(map);

                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {

                         }
                     });


                     try {
                         jsonObject.put("groupId", key);
                         JSONObject js = new JSONObject();
                         js.put("groupId",key);
                         js.put("url",fileUrl);
                         js.put("name",groupName);
                         js.put("members",list);
                         responseListener.onSuccess(js);
                         notifyMembers(userIDs,js);
                     }catch (Exception e){

                     }
                 }else{
                     responseListener.onError(task.getException());
                 }
            }
        });
    }

    private void notifyMembers(String [] members,JSONObject jsonObject){
        for(String member:members){
            sendMessage(new MessageRequest(member,FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    jsonObject.toString(), 4));
        }
    }

    @Override
    public void loadBitMap(final Context context,String userId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                        if(map == null){
                            return ;
                        }
                        String url = (String) map.get("profilePic");
                        if(url == null){
                            return;
                        }
                        if(url.equals("")){
                            return;
                        }

                        Glide.with(context).using(new FirebaseImageLoader())
                                .load(FirebaseStorage.getInstance().getReference(url))
                                .into(imageView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
