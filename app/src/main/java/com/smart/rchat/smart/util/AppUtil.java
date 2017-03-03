package com.smart.rchat.smart.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.models.MessageRequest;
import com.smart.rchat.smart.network.FireBaseImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by nishant on 31.01.17.
 */

public class AppUtil {

    private static Pattern splChars = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

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

    public static boolean hasSplChars(String number){
        return splChars.matcher(number).find();
    }

}
