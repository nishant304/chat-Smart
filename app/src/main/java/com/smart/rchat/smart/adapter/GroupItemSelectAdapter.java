package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.rchat.smart.R;
import com.smart.rchat.smart.models.User;
import com.smart.rchat.smart.network.NetworkClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 14.02.17.
 */

public class GroupItemSelectAdapter extends RecyclerView.Adapter<GroupItemSelectAdapter.Holder> {

    private LayoutInflater inflater;
    protected ArrayList<User> list;

    public GroupItemSelectAdapter(Context context, ArrayList<User> list){
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_view,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        User user = list.get(position);
        NetworkClient.getInstance().loadBitMap(holder.imageView.getContext(),user.getUserId(),holder.imageView,1);
        holder.tvName.setText(user.getName());
        holder.phone.setText(user.getPhone());
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
