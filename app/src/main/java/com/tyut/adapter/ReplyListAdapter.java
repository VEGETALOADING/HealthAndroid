package com.tyut.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.CommentVO;
import com.tyut.vo.Reply;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;


    private List<Reply> mList;
    public ReplyListAdapter(Context context, List<Reply> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
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
        int i = mList.size();
        if(mList.size() > position){

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
            holder.userName_ol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "跳转<"+mList.get(position).getUserName()+">用户界面", Toast.LENGTH_SHORT).show();
                }
            });
            holder.userName_tl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "跳转<"+mList.get(position).getUserName()+">用户界面", Toast.LENGTH_SHORT).show();
                }
            });
            holder.parentName_tl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "跳转<"+mList.get(position).getUserName()+">用户界面", Toast.LENGTH_SHORT).show();
                }
            });

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
        void onClick(int position);
    }


}
