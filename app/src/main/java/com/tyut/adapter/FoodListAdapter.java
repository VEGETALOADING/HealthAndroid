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
import com.tyut.vo.FoodVO;
import com.tyut.vo.SportVO;

import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;

    private List<FoodVO> mList;
    public FoodListAdapter(Context context, List<FoodVO> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
    }
    @NonNull
    @Override
    public FoodListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.food_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.foodname_tv.setText(mList.get(position).getName());
            holder.foodcalories_tv.setText(mList.get(position).getCalories()+"");
            holder.foodquantity_tv.setText(mList.get(position).getQuantity()+"");
            holder.foodunit_tv.setText(mList.get(position).getUnit());
            Glide.with(mContext).load("http://192.168.1.10:8080/foodpic/" + mList.get(position).getPic()).into(holder.food_pic);


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

        private TextView foodname_tv;
        private TextView foodcalories_tv;
        private TextView foodquantity_tv;
        private TextView foodunit_tv;
        private ImageView food_pic;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            foodname_tv = itemView.findViewById(R.id.foodname_tv);
            foodcalories_tv = itemView.findViewById(R.id.foodcalories_tv);
            foodquantity_tv = itemView.findViewById(R.id.foodquantity_tv);
            foodunit_tv = itemView.findViewById(R.id.foodunit_tv);
            food_pic = itemView.findViewById(R.id.food_pic);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
