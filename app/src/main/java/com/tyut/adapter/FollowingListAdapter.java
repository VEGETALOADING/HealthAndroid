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
import com.tyut.vo.Topic;

import java.util.List;

public class FollowingListAdapter extends RecyclerView.Adapter<FollowingListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private List<FollowerVO> followerList;
    private List<Topic> topicList;
    public FollowingListAdapter(Context context, List<FollowerVO> followerList, List<Topic> topicList,  OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.followerList = followerList;
        this.topicList = topicList;
    }
    @NonNull
    @Override
    public FollowingListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_following, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull FollowingListAdapter.LinearViewHolder holder, final int position) {

        if(followerList != null) {
            if (followerList.size() > position) {
                holder.username_tv.setText(followerList.get(position).getUsername());
                Glide.with(mContext).load("http://192.168.1.9:8080/userpic/" + followerList.get(position).getUserpic()).into(holder.userpic_iv);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(position);
                    }
                });

            }
        }else {
            if (topicList.size() > position) {
                holder.username_tv.setText(topicList.get(position).getName());
                Glide.with(mContext).load("http://192.168.1.9:8080/topicpic/" + (topicList.get(position).getPic() == null ? "default.jpg" : topicList.get(position).getPic())).into(holder.userpic_iv);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(position);
                    }
                });

            }
        }



    }

    @Override
    public int getItemCount() {
        if(topicList!=null) {
            return topicList.size();
        }else{
            return followerList.size();
        }
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
