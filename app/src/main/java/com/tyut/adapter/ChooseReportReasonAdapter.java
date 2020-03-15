package com.tyut.adapter;

import android.content.Context;
import android.service.chooser.ChooserTarget;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.view.GlideRoundTransform;
import com.tyut.vo.FoodVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseReportReasonAdapter extends RecyclerView.Adapter<ChooseReportReasonAdapter.LinearViewHolder> {

    private Context mContext;
    private List<String> mList =  Arrays.asList("广告", "色情", "欺诈","骚扰", "侮辱", "侵权","其它");
    private Integer nowPosition=0;
    private OnItemClickListener listener;

    public ChooseReportReasonAdapter setListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }


    public ChooseReportReasonAdapter(Context context){
        this.mContext = context;

    }
    @NonNull
    @Override
    public ChooseReportReasonAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        final LinearViewHolder holder = new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_reportreason, parent, false));

          /*
        添加选中的打勾显示
         */
        holder.item_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将点击的位置传出去
                nowPosition = holder.getAdapterPosition();
                //在点击监听里最好写入setVisibility(View.VISIBLE);这样可以避免效果会闪
                holder.check_iv.setVisibility(View.VISIBLE);
                if(listener != null){
                    listener.onClick(nowPosition);
                }
                Toast.makeText(mContext, nowPosition+"", Toast.LENGTH_SHORT).show();
                //刷新界面 notify 通知Data 数据set设置Changed变化
                //在这里运行notifyDataSetChanged 会导致下面的onBindViewHolder 重新加载一遍
                notifyDataSetChanged();
            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ChooseReportReasonAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.reason_tv.setText(mList.get(position));
            if(position == nowPosition){
                holder.check_iv.setVisibility(View.VISIBLE);
            }else {
                holder.check_iv.setVisibility(View.INVISIBLE);
            }
        }



    }

    @Override
    public int getItemCount() {
       return mList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        TextView reason_tv;
        ImageView check_iv;
        LinearLayout item_ll;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            reason_tv = itemView.findViewById(R.id.reason_tv);
            check_iv = itemView.findViewById(R.id.check_iv);
            item_ll = itemView.findViewById(R.id.item_ll);

        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
