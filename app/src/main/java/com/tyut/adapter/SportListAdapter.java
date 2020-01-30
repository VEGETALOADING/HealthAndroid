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
import com.tyut.vo.SportVO;

import java.util.List;

public class SportListAdapter extends RecyclerView.Adapter<SportListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private List<SportVO> mList;
    public SportListAdapter(Context context, List<SportVO> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
    }
    @NonNull
    @Override
    public SportListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.sport_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SportListAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.sportname_tv.setText(mList.get(position).getName());
            holder.calories_tv.setText(mList.get(position).getCalories()+"");
            holder.quantity_tv.setText(mList.get(position).getQuantity()+"");
            holder.unit_tv.setText(mList.get(position).getUnit());
            Glide.with(mContext).load("http://192.168.1.10:8080/sportpic/" + mList.get(position).getPic()).into(holder.sport_pic);

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

        private TextView sportname_tv;
        private TextView calories_tv;
        private TextView quantity_tv;
        private TextView unit_tv;
        private ImageView sport_pic;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            sportname_tv = itemView.findViewById(R.id.sportname_tv);
            calories_tv = itemView.findViewById(R.id.calories_tv);
            quantity_tv = itemView.findViewById(R.id.quantity_tv);
            unit_tv = itemView.findViewById(R.id.unit_tv);
            sport_pic = itemView.findViewById(R.id.sport_pic);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
