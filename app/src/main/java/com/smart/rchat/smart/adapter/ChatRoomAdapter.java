package com.smart.rchat.smart.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smart.rchat.smart.ChatRoomActivity;
import com.smart.rchat.smart.R;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.interfaces.ResponseListener;
import com.smart.rchat.smart.network.NetworkClient;
import com.smart.rchat.smart.util.AppData;
import com.smart.rchat.smart.util.AppUtil;
import com.smart.rchat.smart.util.RchatError;
import com.vstechlab.easyfonts.EasyFonts;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by nishant on 28.01.17.
 */

public class ChatRoomAdapter extends CursorAdapter {

    private final LayoutInflater inflater;
    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private String friendUserId;

    public ChatRoomAdapter(Context context, Cursor c, String friendUserId) {
        super(context, c, false);
        this.inflater = LayoutInflater.from(context);
        this.friendUserId = friendUserId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int type = cursor.getInt(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.type));
        if(type ==1){
            return inflater.inflate(R.layout.chat_message_layout, parent, false);
        }else if(type == 2){
            return inflater.inflate(R.layout.image_message_layout, parent, false);
        }
        return inflater.inflate(R.layout.contact_message_layout, parent, false);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        int type = cursor.getInt(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.type));
        return type-1 ;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String from = cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.from));
        int type = cursor.getInt(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.type));


        if (type == 1) {
            View leftView = view.findViewById(R.id.rlLeft);
            View rightView = view.findViewById(R.id.rlRight);

            TextView left = (TextView) view.findViewById(R.id.tvLeft);
            TextView right = (TextView) view.findViewById(R.id.tvRight);

            if (!from.equals(myId)) {
                left.setText(cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message)));
                left.setTypeface(EasyFonts.robotoThin(context));
                rightView.setVisibility(View.GONE);
                leftView.setVisibility(View.VISIBLE);
            } else {
                rightView.setVisibility(View.VISIBLE);
                leftView.setVisibility(View.GONE);
                right.setTypeface(EasyFonts.robotoThin(context));
                right.setText(cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message)));
            }
        } else  if(type ==2) {

            View leftViewImg = view.findViewById(R.id.rlImgLeft);
            View rightViewImg = view.findViewById(R.id.rlImgRight);

            if (!from.equals(myId)) {
                leftViewImg.setVisibility(View.VISIBLE);
                rightViewImg.setVisibility(View.GONE);
                ImageView leftImageView = (ImageView) view.findViewById(R.id.ivInImg);

                String url = cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message));
                Glide.with(context).using(new FirebaseImageLoader())
                        .load(FirebaseStorage.getInstance().getReference(url))
                        .into(leftImageView);

            } else {
                rightViewImg.setVisibility(View.VISIBLE);
                leftViewImg.setVisibility(View.GONE);

                final ImageView rightImageView = (ImageView) view.findViewById(R.id.ivOutImg);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pbRight);
                final String url = cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message));
                Bitmap bitmap = AppData.getInstance().getLruCache().get(url);
                if(bitmap!=null){
                    rightImageView.setImageBitmap(bitmap);
                }

                //rightImageView.setImageBitmap(bitmap);
                NetworkClient.getInstance().uploadBitMap(url, new ResponseListener() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Glide.with(context).using(new FirebaseImageLoader())
                                .load(FirebaseStorage.getInstance().getReference(url))
                                .into(rightImageView);
                        progressBar.setVisibility(View.GONE);
                        NetworkClient.getInstance().sendImageRequest(friendUserId,url);
                    }

                    @Override
                    public void onError(Exception error) {

                        Glide.with(context).using(new FirebaseImageLoader())
                                .load(FirebaseStorage.getInstance().getReference(url))
                                .into(rightImageView);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }else{
            View inContactLayout = view.findViewById(R.id.inContactLayout);
            View outContactLayout = view.findViewById(R.id.outContactLayout);


            if (!from.equals(myId)) {
                outContactLayout.setVisibility(View.GONE);
                inContactLayout.setVisibility(View.VISIBLE);

                TextView name = (TextView) inContactLayout.findViewById(R.id.tvContactName);
                TextView number = (TextView)inContactLayout.findViewById(R.id.tvContactNumber);

                name.setTypeface(EasyFonts.robotoBold(context));
                number.setTypeface(EasyFonts.robotoThin(context));

                final String message = cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message));
                try {
                   final  JSONObject json = new JSONObject(message);
                    name.setText(json.getString("name"));
                    number.setText(json.getString("number"));
                    inContactLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                contactIntent
                                        .putExtra(ContactsContract.Intents.Insert.NAME, json.getString("name"))
                                        .putExtra(ContactsContract.Intents.Insert.PHONE, json.getString("number"));
                                context.startActivity(contactIntent);
                            }catch (Exception e){
                                
                            }
                        }
                    });
                }catch(Exception e){

                }

            }else{
                outContactLayout.setVisibility(View.VISIBLE);
                inContactLayout.setVisibility(View.GONE);

                TextView name = (TextView)outContactLayout.findViewById(R.id.tvContactName);
                TextView number = (TextView)outContactLayout.findViewById(R.id.tvContactNumber);
                name.setTypeface(EasyFonts.robotoBold(context));
                number.setTypeface(EasyFonts.robotoThin(context));

                final String message = cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message));
                try {
                    JSONObject json = new JSONObject(message);
                    name.setText(json.getString("name"));
                    number.setText(json.getString("number"));
                }catch(Exception e){

                }
            }
        }
    }

}
