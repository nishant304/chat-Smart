package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.rchat.smart.R;
import com.smart.rchat.smart.models.User;
import com.smart.rchat.smart.network.NetworkClient;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;

/**
 * Created by nishant on 16.02.17.
 */

public class GroupMemberAdapter extends GroupItemSelectAdapter{

    public GroupMemberAdapter(Context context, ArrayList<User> list){
        super(context,list);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        super.onBindViewHolder(holder,position);
        if(list.get(position).getName().equals("")){
            FirebaseDatabase.getInstance().getReference().child("Users").child(list.get(position).getUserId()).child("phone").
                    addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String phone = (String) dataSnapshot.getValue();
                    holder.tvName.setText(phone);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
