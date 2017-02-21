package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.smart.rchat.smart.R;
import com.smart.rchat.smart.database.RChatContract;
import com.smart.rchat.smart.network.NetworkClient;
import com.smart.rchat.smart.util.AppUtil;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.HashMap;

/**
 * Created by nishant on 1/25/2017.
 */

public class ContactsAdapter extends CursorAdapter {

    private final Cursor cursor;
    private final LayoutInflater inflater;

    public ContactsAdapter(Context context, Cursor cursor) {
        super(context,cursor,false);
        this.cursor = cursor;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.item_view, parent, false);
    }

    @Override
    public void bindView(View itemView, final Context context, Cursor cursor) {
        int type = cursor.getInt(
                cursor.getColumnIndex(RChatContract.USER_TABLE.type));
        String userId = cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.USER_ID));
        String name = cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.USER_NAME));

        TextView  textview = (TextView) itemView.findViewById(R.id.tvNumber);
        TextView  tvName = (TextView) itemView.findViewById(R.id.tvName);

        final ImageView imv = (ImageView) itemView.findViewById(R.id.profile_image);
        NetworkClient.getInstance().loadBitMap(context, userId,imv,type);

        textview.setTypeface(EasyFonts.robotoBlack(context));
        tvName.setTypeface(EasyFonts.robotoBold(context));

        textview.setText(cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.PHONE)));
        tvName.setText(name);
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

}


