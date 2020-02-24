package com.tyut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tyut.R;

import java.util.List;


/**
 * Created by Idtk on 2017/3/9.
 * Blog : http://www.idtkm.com
 * GitHub : https://github.com/Idtk
 * 描述 :
 */

public class MyNineAdapter extends NinePhotoViewAdapter<MyNineAdapter.MyHolder> {

    private Context mContext;
    private int count;
    private List<String> mList;

    public MyNineAdapter(Context context, List<String> mList) {
        super();
        this.mContext = context;
        this.mList = mList;
        this.count = mList.size();
    }

    @Override
    public MyHolder createView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_oneofnine, parent, false);
        MyHolder viewHolder = new MyHolder(view);
        return viewHolder;
    }

    @Override
    public void displayView(final MyHolder holder, final int position) {
        Glide
                .with(mContext)
                .load("http://192.168.1.9:8080/activitypic/" + mList.get(position))
                .placeholder(R.mipmap.my_unselected)
                .into(holder.mImageView);

        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, position+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    class MyHolder extends NinePhotoViewHolder {

        ImageView mImageView;

        public MyHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.nine_pic);
        }
    }

}
