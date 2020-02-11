package com.tyut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.vo.FollowerVO;

import java.util.List;

public class FollowingListAdapter extends RecyclerView.Adapter<FollowingListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private List<FollowerVO> mList;
    public FollowingListAdapter(Context context, List<FollowerVO> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
    }
    @NonNull
    @Override
    public FollowingListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.following_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull FollowingListAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.username_tv.setText(mList.get(position).getUsername());
            Glide.with(mContext).load("http://192.168.1.4:8080/userpic/" + mList.get(position).getUserpic()).into(holder.userpic_iv);

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toast
            }
        });*/
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });

        }



    }

    @Override
    public int getItemCount() {
       return mList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView username_tv;
        private ImageView userpic_iv;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            username_tv = itemView.findViewById(R.id.username_tv);
            userpic_iv = itemView.findViewById(R.id.userpic_iv);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
