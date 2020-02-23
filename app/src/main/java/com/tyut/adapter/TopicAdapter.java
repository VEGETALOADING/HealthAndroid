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

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private List<String> mList;
    public TopicAdapter(Context context, List<String> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
    }
    @NonNull
    @Override
    public TopicAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_topic, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull TopicAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.topic_tv.setText(mList.get(position));
            
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

        private TextView topic_tv;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            topic_tv = itemView.findViewById(R.id.topic_tv);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
