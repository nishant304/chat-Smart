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

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.Holder>{

    private LayoutInflater inflater;
    protected ArrayList<String> list;

    public GroupMemberAdapter(Context context, ArrayList<String> list){
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public GroupMemberAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_view,parent,false);
        return new GroupMemberAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        NetworkClient.getInstance().loadBitMap(holder.imageView.getContext(),list.get(position),holder.imageView,1);
        holder.tvName.setTypeface(EasyFonts.robotoBlack(holder.imageView.getContext()));
        FirebaseDatabase.getInstance().getReference().child("Phone").child(list.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String phone = (String) dataSnapshot.getValue();
                holder.tvName.setText(phone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.phone.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView tvName ;
        TextView phone ;
        ImageView imageView;

        Holder(View view){
            super(view);
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.phone = (TextView) view.findViewById(R.id.tvNumber);
            this.imageView = (ImageView) view.findViewById(R.id.profile_image);
        }

    }
}
