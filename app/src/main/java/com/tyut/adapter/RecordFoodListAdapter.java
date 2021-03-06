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
import com.tyut.vo.MyfoodVO;

import java.util.List;

public class RecordFoodListAdapter extends RecyclerView.Adapter<RecordFoodListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private List<MyfoodVO> mList;
    public RecordFoodListAdapter(Context context, List<MyfoodVO> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
    }
    @NonNull
    @Override
    public RecordFoodListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecordFoodListAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.recordName_tv.setText(mList.get(position).getName());
            holder.recordHot_tv.setText(mList.get(position).getCal()+"");
            holder.recordQuantity_tv.setText(mList.get(position).getQuantity()+"");
            holder.recordUnit_tv.setText(mList.get(position).getUnit());
            Glide.with(mContext).load("http://"+mContext.getString(R.string.url)+":8080/foodpic/" + mList.get(position).getPic()).into(holder.recordPic_iv);


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

        private TextView recordName_tv;
        private TextView recordQuantity_tv;
        private TextView recordUnit_tv;
        private TextView recordHot_tv;
        private ImageView recordPic_iv;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            recordName_tv = itemView.findViewById(R.id.recordName);
            recordHot_tv = itemView.findViewById(R.id.recordHot);
            recordQuantity_tv = itemView.findViewById(R.id.recordQuantity);
            recordUnit_tv = itemView.findViewById(R.id.recordUnit);
            recordPic_iv = itemView.findViewById(R.id.recordPic);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
