package com.smart.rchat.smart.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.smart.rchat.smart.util.AppData;
import com.smart.rchat.smart.util.AppUtil;

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
        return inflater.inflate(R.layout.chat_room_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String from = cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.from));
        int type = cursor.getInt(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.type));

        View leftView = view.findViewById(R.id.rlLeft);
        View rightView = view.findViewById(R.id.rlRight);
        View leftViewImg = view.findViewById(R.id.rlImgLeft);
        View rightViewImg = view.findViewById(R.id.rlImgRight);

        if (type == 1) {
            leftViewImg.setVisibility(View.GONE);
            rightViewImg.setVisibility(View.GONE);

            TextView left = (TextView) view.findViewById(R.id.tvLeft);
            TextView right = (TextView) view.findViewById(R.id.tvRight);

            if (!from.equals(myId)) {
                left.setText(cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message)));
                rightView.setVisibility(View.GONE);
                leftView.setVisibility(View.VISIBLE);
            } else {
                rightView.setVisibility(View.VISIBLE);
                leftView.setVisibility(View.GONE);
                right.setText(cursor.getString(cursor.getColumnIndex(RChatContract.MESSAGE_TABLE.message)));
            }
        } else {
            rightView.setVisibility(View.GONE);
            leftView.setVisibility(View.GONE);

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

                final StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);
                storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                    @Override
                    public void onComplete(@NonNull Task<StorageMetadata> task) {
                        Exception ex = task.getException();
                        if (ex instanceof StorageException) {
                            StorageException st = (StorageException) ex;
                            if (st.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                Bitmap bitmap = AppData.getInstance()
                                        .getLruCache().get(url);

                                if (bitmap == null) {
                                    progressBar.setVisibility(View.GONE);
                                    return;
                                }
                                rightImageView.setImageBitmap(bitmap);

                                AppUtil.uploadBitmap(url,bitmap, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        progressBar.setVisibility(View.GONE);
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("/Messages");
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        hashMap.put("to", friendUserId);
                                        hashMap.put("message", url);
                                        hashMap.put("type", ChatRoomActivity.TYPE_IMAGE);
                                        ref.push().setValue(hashMap);

                                    }
                                });
                            }
                        }

                        if(ex == null){
                            Glide.with(context).using(new FirebaseImageLoader())
                                    .load(FirebaseStorage.getInstance().getReference(url))
                                    .into(rightImageView);
                        }

                    }
                });
            }

        }
    }
}
