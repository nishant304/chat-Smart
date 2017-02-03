package com.smart.rchat.smart.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.rchat.smart.R;
import com.smart.rchat.smart.database.RChatContract;

/**
 * Created by nishant on 1/25/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<Holder> {

    private final Cursor cursor;
    private final LayoutInflater inflater;
    private View.OnClickListener onClickListener;

    public ContactsAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = (View.OnClickListener) context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_view, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        cursor.moveToPosition(position);
        String userId = cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.USER_ID));
        String name = cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.USER_NAME));
        holder.view.setTag(new NameIdPair(name,userId));
        holder.view.setOnClickListener(onClickListener);
        holder.textview.setText(cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.PHONE)));
        holder.tvInvite.setVisibility(cursor.getString(
                cursor.getColumnIndex(RChatContract.USER_TABLE.USER_ID)) == null ? View.VISIBLE : View.GONE);
        holder.tvName.setText(name);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }


    public static  class NameIdPair{
        public String name;
        public String userId;
        NameIdPair(String name,String userId){
            this.name = name;
            this.userId = userId;
        }
    }

}

class Holder extends RecyclerView.ViewHolder {

    public final TextView textview;
    public final TextView tvName;
    public final TextView tvInvite;
    final View view;

    public Holder(View itemView) {
        super(itemView);
        this.textview = (TextView) itemView.findViewById(R.id.tvNumber);
        this.tvName = (TextView) itemView.findViewById(R.id.tvName);
        this.tvInvite = (TextView) itemView.findViewById(R.id.tvInvite);
        this.view = itemView;
    }
}

