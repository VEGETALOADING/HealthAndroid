package com.tyut.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tyut.activity.ActivityActivity;
import com.tyut.R;
import com.tyut.vo.Reply;

import java.util.List;

public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private Boolean showAll;

    private List<Reply> mList;
    public ReplyListAdapter(Context context, List<Reply> list, OnItemClickListener listener, Boolean showAll){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
        this.showAll = showAll;
    }



    //子线程主线程通讯
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    break;
                case 1:
                    break;

            }
        }
    };
    @NonNull
    @Override
    public ReplyListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReplyListAdapter.LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_reply, null));

    }

    @Override
    public void onBindViewHolder(@NonNull final ReplyListAdapter.LinearViewHolder holder, final int position) {
        int showCount = 3;
        if(showAll){
            showCount = mList.size();
        }
        if(showCount > position && mList.size() > position){

            if(mList.get(position).getCategory() == 1){
                holder.twoList_ll.setVisibility(View.GONE);
                holder.oneList_ll.setVisibility(View.VISIBLE);
                holder.userName_ol.setText(mList.get(position).getUserName() + ":");
                holder.content_ol.setText(mList.get(position).getContent());
            }else if(mList.get(position).getCategory() == 2){
                holder.oneList_ll.setVisibility(View.GONE);
                holder.twoList_ll.setVisibility(View.VISIBLE);
                holder.userName_tl.setText(mList.get(position).getUserName() + ":");
                holder.parentName_tl.setText("@"+mList.get(position).getParentName()+":");
                holder.content_tl.setText(mList.get(position).getContent());
            }
            View.OnClickListener clickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActivityActivity.class);
                    intent.putExtra("username", mList.get(position).getUserName());
                    mContext.startActivity(intent);
                }
            };
            holder.userName_ol.setOnClickListener(clickListener);
            holder.userName_tl.setOnClickListener(clickListener);
            holder.parentName_tl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActivityActivity.class);
                    intent.putExtra("username", mList.get(position).getParentName());
                    mContext.startActivity(intent);
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(mList.get(position));
                }
            });

        }



    }

    @Override
    public int getItemCount() {
        if(showAll){
            return mList.size();
        }else {
            if (mList.size() <= 3){
                return mList.size();
            }
        }
            return 3;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout oneList_ll;
        private LinearLayout twoList_ll;
        private TextView userName_ol;
        private TextView content_ol;
        private TextView userName_tl;
        private TextView content_tl;
        private TextView parentName_tl;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            oneList_ll = itemView.findViewById(R.id.oneList_ll);
            twoList_ll = itemView.findViewById(R.id.twoList_ll);
            userName_ol = itemView.findViewById(R.id.username_ol);
            userName_tl = itemView.findViewById(R.id.username_tl);
            content_ol = itemView.findViewById(R.id.content_ol);
            content_tl = itemView.findViewById(R.id.content_tl);
            parentName_tl = itemView.findViewById(R.id.parentname_tl);

        }
    }

    public interface OnItemClickListener{
        void onClick(Reply reply);
    }



}
