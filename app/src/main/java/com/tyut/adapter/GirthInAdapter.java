package com.tyut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tyut.R;
import com.tyut.utils.JudgeUtil;
import com.tyut.vo.GirthVO;

import java.util.List;

public class GirthInAdapter extends RecyclerView.Adapter<GirthInAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private List<GirthVO> mList;
    public GirthInAdapter(Context context, List<GirthVO> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
    }
    @NonNull
    @Override
    public GirthInAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_girthin, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull GirthInAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.type_tv.setText(JudgeUtil.getGirthName(mList.get(position).getType()));
            holder.value_tv.setText(mList.get(position).getValue()+"");

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

        private TextView type_tv;
        private TextView value_tv;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            type_tv = itemView.findViewById(R.id.girthItem_type);
            value_tv = itemView.findViewById(R.id.girthItem_value);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
