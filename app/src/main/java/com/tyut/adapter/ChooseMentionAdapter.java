package com.tyut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.vo.SportVO;
import com.tyut.vo.UserVO;

import java.util.HashMap;
import java.util.List;

public class ChooseMentionAdapter extends RecyclerView.Adapter {

    private List<UserVO> datas;
    public static HashMap<Integer, Boolean> isSelected;
    private Context mContext;

    public ChooseMentionAdapter(Context context, List<UserVO> datas) {
        this.mContext = context;
        this.datas = datas;
        init();
    }

    // 初始化 设置所有item都为未选择
    public void init() {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < datas.size(); i++) {
            isSelected.put(i, false);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choosemention, parent, false);

        return new MultiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MultiViewHolder){
            final MultiViewHolder viewHolder = (MultiViewHolder) holder;

            viewHolder.userName_tv.setText(datas.get(position).getUsername());
            Glide.with(mContext).load("http://"+mContext.getString(R.string.localhost)+"/userpic/" + datas.get(position).getUserpic()).into(((MultiViewHolder) holder).userPic_iv);

            viewHolder.mCheckBox.setChecked(isSelected.get(position));
            viewHolder.itemView.setSelected(isSelected.get(position));

            if (mOnItemClickListener != null)
            {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, viewHolder.getAdapterPosition());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MultiViewHolder extends RecyclerView.ViewHolder{
        TextView userName_tv;
        ImageView userPic_iv;
        CheckBox mCheckBox;

        public MultiViewHolder(View itemView) {
            super(itemView);
            userName_tv = itemView.findViewById(R.id.username_tv);
            userPic_iv = itemView.findViewById(R.id.userpic_iv);
            mCheckBox = itemView.findViewById(R.id.choose_cb);
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener{
         void onItemClick(View view, int position);
    }
}
