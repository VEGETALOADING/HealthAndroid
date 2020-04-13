package com.tyut.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tyut.activity.BigPicActivity;
import com.tyut.R;

import java.util.ArrayList;
import java.util.List;


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
                .load("http://"+mContext.getString(R.string.url)+":8080/activitypic/" + mList.get(position))
                .placeholder(R.mipmap.my_unselected)
                .into(holder.mImageView);

        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (String s : mList) {
                    arrayList.add(s);
                }

                Intent intent = new Intent(mContext, BigPicActivity.class);
                intent.putStringArrayListExtra("picList", arrayList);
                intent.putExtra("defaultposition", position);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.activityshow, R.anim.activityhidden);
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
