package com.smart.rchat.smart.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ListView;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.smart.rchat.smart.ChatRoomActivity;
import com.smart.rchat.smart.database.RChatContract;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Created by nishant on 31.01.17.
 */

public class AppUtil {

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    public static void uploadBitmap(String url, Bitmap bitmap,
                                    OnCompleteListener<UploadTask.TaskSnapshot> onCompleteListener) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] bytes = baos.toByteArray();
        FirebaseStorage.getInstance().getReference(url).putBytes(bytes).addOnCompleteListener(onCompleteListener);
    }

    public static Bitmap getBitmapFromUri(Uri uri, Context context) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return  image;
        }catch (Exception ex){

        }
        return null;
    }

    public  static HashMap<String,Object>  getMessageRequest (String friendUserId,String message,int type){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("to",friendUserId);
        hashMap.put("message",message);
        hashMap.put("type", type);
        return  hashMap;
    }

    public  static ContentValues getCVforMessafRequest (String friendUserId, String message,int type,String key){
        ContentValues cv = new ContentValues();
        cv.put(RChatContract.MESSAGE_TABLE.to,friendUserId);
        cv.put(RChatContract.MESSAGE_TABLE.msg_id,key);
        cv.put(RChatContract.MESSAGE_TABLE.message,message);
        cv.put(RChatContract.MESSAGE_TABLE.time,System.currentTimeMillis());
        cv.put(RChatContract.MESSAGE_TABLE.from,FirebaseAuth.getInstance().getCurrentUser().getUid());
        cv.put(RChatContract.MESSAGE_TABLE.type,type);
        return  cv;
    }

    public  static  String getUserId(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            return  null ;
        }
        return firebaseUser.getUid();
    }

    public  static  String getSelection(JSONArray jsonArray) throws JSONException{
        if(jsonArray.length() == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<jsonArray.length();i++){
            stringBuilder.append(RChatContract.USER_TABLE.USER_ID);
            stringBuilder.append(" ='"+jsonArray.getString(i)+"' ");
            if(jsonArray.length() != 1 && i != jsonArray.length()-1){
                stringBuilder.append("  OR  ");
            }
        }
        return stringBuilder.toString();
    }

}
