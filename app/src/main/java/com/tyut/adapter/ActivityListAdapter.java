package com.tyut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.NinePhotoView;
import com.tyut.vo.ActivityVO;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Idtk on 2017/3/9.
 * Blog : http://www.idtkm.com
 * GitHub : https://github.com/Idtk
 * 描述 :
 */

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.RVHolder> {

    private Context context;
    private List<ActivityVO> mList;



    public ActivityListAdapter(Context context, List<ActivityVO> mList) {
        super();
        this.context = context;
        this.mList = mList;
    }

    @Override
    public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity,parent,false);
        RVHolder rvHolder = new RVHolder(view);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RVHolder holder, int position) {

        if(position < mList.size()) {
            holder.userName_tv.setText(mList.get(position).getUsername() + "");
            holder.content_tv.setText(mList.get(position).getContent());
            Glide.with(context).load("http://192.168.1.9:8080/userpic/" + mList.get(position).getUserpic()).into(holder.userPic_iv);

            try {
                holder.shareTime_tv.setText(StringUtil.convertSharetime(mList.get(position).getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (mList.get(position).getIfFavorite()) {
                holder.favorite_iv.setImageResource(R.mipmap.icon_favorite_selected);
                holder.favorite_tv.setTextColor(context.getResources().getColor(R.color.green_light));
            } else {
                holder.favorite_iv.setImageResource(R.mipmap.icon_favorite_unselected);
                holder.favorite_tv.setTextColor(context.getResources().getColor(R.color.nav_text_default));
            }
            if (mList.get(position).getFavoriteCount() > 0) {
                holder.favorite_tv.setText(mList.get(position).getFavoriteCount() + "");
            } else {
                holder.favorite_tv.setText("收藏");
            }
            if (mList.get(position).getIfLike()) {
                holder.like_iv.setImageResource(R.mipmap.icon_like_selected);
                holder.like_tv.setTextColor(context.getResources().getColor(R.color.green_light));
            } else {
                holder.favorite_iv.setImageResource(R.mipmap.icon_favorite_unselected);
                holder.favorite_tv.setTextColor(context.getResources().getColor(R.color.nav_text_default));
            }
            if (mList.get(position).getLikeCount() > 0) {
                holder.like_tv.setText(mList.get(position).getLikeCount() + "");
            } else {
                holder.like_tv.setText("赞");
            }
            if (mList.get(position).getCommentCount() > 0) {
                holder.like_tv.setText(mList.get(position).getCommentCount() + "");
            } else {
                holder.like_tv.setText("评论");
            }

            if(mList.get(position).getPic() != null && !"".equals(mList.get(position).getPic())) {
                String picStr = mList.get(position).getPic();
                List<String> picList = Arrays.asList(picStr.split("/"));
                MyNineAdapter adapter = new MyNineAdapter(context, picList);
                holder.mNinePhoto.setAdapter(adapter);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class RVHolder extends RecyclerView.ViewHolder{

        NinePhotoView mNinePhoto;
        TextView userName_tv;
        TextView shareTime_tv;
        ImageView userPic_iv;
        ImageView more_iv;
        TextView content_tv;

        LinearLayout like_ll;
        LinearLayout comment_ll;
        LinearLayout favorite_ll;
        ImageView like_iv;
        ImageView comment_iv;
        ImageView favorite_iv;
        TextView like_tv;
        TextView comment_tv;
        TextView favorite_tv;
        public RVHolder(View itemView) {
            super(itemView);
            mNinePhoto = itemView.findViewById(R.id.nine_photo);
            userName_tv = itemView.findViewById(R.id.username_activityItem);
            shareTime_tv = itemView.findViewById(R.id.sharetime_activityItem);
            userPic_iv = itemView.findViewById(R.id.userpic_activityItem);
            more_iv = itemView.findViewById(R.id.more_iv);
            content_tv = itemView.findViewById(R.id.content_activityItem);
            like_ll = itemView.findViewById(R.id.like_ll);
            comment_ll = itemView.findViewById(R.id.comment_ll);
            favorite_ll = itemView.findViewById(R.id.favorite_ll);
            like_iv = itemView.findViewById(R.id.like_iv);
            comment_iv = itemView.findViewById(R.id.comment_iv);
            favorite_iv = itemView.findViewById(R.id.favorite_iv);
            like_tv = itemView.findViewById(R.id.like_tv);
            comment_tv = itemView.findViewById(R.id.comment_tv);
            favorite_tv = itemView.findViewById(R.id.favorite_tv);
        }
    }
}
