package com.tyut.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.ActivityActivity;
import com.tyut.ActivityDetailActivity;
import com.tyut.R;
import com.tyut.ReportActivity;
import com.tyut.TopicActivity;
import com.tyut.UpdateUsernameActivity;
import com.tyut.utils.EmojiUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;

import com.tyut.view.GlideRoundTransform;
import com.tyut.view.NinePhotoView;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.Emoji;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.DeleteActivityPUW;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tyut.utils.EmojiUtil.decodeSampledBitmapFromResource;



public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.RVHolder> {

    private Context context;
    private List<ActivityVO> mList;
    private OnItemClickListener mListener;
    private OnUpdateListener updateListener;
    private OnFlushListener flushListener;

    public ActivityListAdapter setFlushListener(OnFlushListener flushListener) {
        this.flushListener = flushListener;
        return this;
    }

    public ActivityListAdapter(Context context, List<ActivityVO> mList, OnItemClickListener listener, OnUpdateListener updateListener) {
        super();
        this.context = context;
        this.mList = mList;
        this.mListener = listener;
        this.updateListener = updateListener;
    }

    private static final int ADDFAVORITE = 0;
    private static final int CANCELFAVORITE = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){

                case 0:
                    //holder.content_tv.setText("xxx");
                    /*ActivityListAdapter.this.holder.favorite_iv.setImageResource(R.mipmap.icon_favorite_selected);
                    ActivityListAdapter.this.holder.favorite_tv.setTextColor(context.getResources().getColor(R.color.green_light));
                    ActivityListAdapter.this.holder.favorite_tv.setText(msg.obj+"");*/
                    break;
                case 1:


                    break;


            }
        }
    };

    @Override
    public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity,parent,false);
        RVHolder rvHolder = new RVHolder(view);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(final RVHolder holder, final int position) {
        final UserVO userVO = (UserVO) SPSingleton.get(context, SPSingleton.USERINFO).readObject("user", UserVO.class);

        if(position < mList.size()) {
            holder.userName_tv.setText(mList.get(position).getUsername() + "");
            //holder.content_tv.setText(mList.get(position).getContent());
            final String str = mList.get(position).getContent();

            SpannableStringBuilder style=new SpannableStringBuilder(str);
            final Map<Integer, Integer> mentionMap = StringUtil.getMention(str);
            final Map<Integer, Integer> topicsMap = StringUtil.getTopics(str);
            final Map<Integer, Integer> emojiMap = StringUtil.getEmoji(str);
            Set<Integer> mentionKeys = mentionMap.keySet();
            Set<Integer> topicKeys = topicsMap.keySet();
            Set<Integer> emojiKeys = emojiMap.keySet();

            //设置部分文字点击事件
            {
                Iterator<Emoji> iterator = EmojiUtil.getEmojiList().iterator();

                for (final Integer key : emojiKeys) {
                    Emoji emoji = null;
                    String tempText = str.substring(key, emojiMap.get(key));
                    while (iterator.hasNext()) {
                        emoji = iterator.next();
                        if (tempText.equals(emoji.getContent())) {
                            //转换为Span并设置Span的大小
                            style.setSpan(new ImageSpan(context, decodeSampledBitmapFromResource(context.getResources(), emoji.getImageUri()
                                    , EmojiUtil.dip2px(context, 18), EmojiUtil.dip2px(context, 18))),
                                    key, emojiMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            break;
                        }
                    }
                }

                for (final Integer key : mentionKeys) {

                    style.setSpan(new ClickableSpan() {
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            //ds.setColor(Color.parseColor("#FF0090FF"));//设置颜色
                            ds.setUnderlineText(false);//去掉下划线
                        }
                        @Override
                        public void onClick(View widget) {
                            //跳转用户动态
                            Intent intent = new Intent(context, ActivityActivity.class);
                            String sub = str.substring(key, mentionMap.get(key));
                            intent.putExtra("username", sub.substring(1));
                            context.startActivity(intent);

                        }
                    }, key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.green_light)),key,mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }

                for (final Integer key : topicKeys) {

                    style.setSpan(new ClickableSpan() {
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            //ds.setColor(Color.parseColor("#FF0090FF"));//设置颜色
                            ds.setUnderlineText(false);//去掉下划线
                        }
                        @Override
                        public void onClick(View widget) {
                            //跳转话题页面
                            Intent intent = new Intent(context, TopicActivity.class);
                            intent.putExtra("topicname", str.substring(key, topicsMap.get(key)));
                            context.startActivity(intent);
                        }
                    }, key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.green_light)),key,topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }

            }


            holder.content_tv.setText(style);
            holder.content_tv.setMovementMethod(LinkMovementMethod.getInstance());



            Glide.with(context)
                    .load("http://"+context.getString(R.string.url)+":8080/userpic/" + mList.get(position).getUserpic())
                    .transform(new GlideRoundTransform(context, 25))
                    .into(holder.userPic_iv);

            holder.userPic_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityActivity.class);
                    intent.putExtra("username", mList.get(position).getUsername());
                    context.startActivity(intent);
                }
            });

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
                holder.like_iv.setImageResource(R.mipmap.icon_like_unselected);
                holder.like_tv.setTextColor(context.getResources().getColor(R.color.nav_text_default));
            }
            if (mList.get(position).getLikeCount() > 0) {
                holder.like_tv.setText(mList.get(position).getLikeCount() + "");
            } else {
                holder.like_tv.setText("赞");
            }
            if (mList.get(position).getCommentCount() > 0) {
                holder.comment_tv.setText(mList.get(position).getCommentCount() + "");
            } else {
                holder.comment_tv.setText("评论");
            }

            if(mList.get(position).getPic() != null && !"".equals(mList.get(position).getPic())) {
                String picStr = mList.get(position).getPic();
                List<String> picList = Arrays.asList(picStr.split("/"));
                MyNineAdapter adapter = new MyNineAdapter(context, picList);
                holder.mNinePhoto.setAdapter(adapter);
            }

            holder.favorite_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/favorite/addorcancel.do?userid="+userVO.getId()+"&category=1&objectid="+mList.get(position).getId(),
                    new OkHttpCallback(){
                        @Override
                        public void onFinish(String status, final String msg) {
                            super.onFinish(status, msg);
                            //解析数据
                            Gson gson=new Gson();
                            final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                            if(serverResponse.getStatus() == 0){
                                if((Double) serverResponse.getData() == 45){

                                    //子线程更新UI
                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.favorite_iv.setImageResource(R.mipmap.icon_favorite_selected);
                                            holder.favorite_tv.setTextColor(context.getResources().getColor(R.color.green_light));
                                            holder.favorite_tv.setText(serverResponse.getMsg());
                                            Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });



                                }else if((Double) serverResponse.getData() == 47){

                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.favorite_iv.setImageResource(R.mipmap.icon_favorite_unselected);
                                            holder.favorite_tv.setTextColor(context.getResources().getColor(R.color.nav_text_default));
                                            holder.favorite_tv.setText( Double.parseDouble(serverResponse.getMsg()) == 0 ? "收藏" : serverResponse.getMsg());
                                            Toast.makeText(context, "取消收藏", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }else{
                                Looper.prepare();
                                Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        }
                    }
                );
                }
            });
            holder.comment_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ActivityDetailActivity.class);
                    intent.putExtra("activityid", mList.get(position).getId());
                    intent.putExtra("action", 1);
                    context.startActivity(intent);
                }
            });
            holder.like_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/like/addorcancel.do?userid="+userVO.getId()+"&objectid="+mList.get(position).getId()+"&category=0",
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, final String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() == 0){
                                        if((Double) serverResponse.getData() == 56){

                                            //点赞成功
                                            //子线程更新UI
                                            ((Activity)context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.like_iv.setImageResource(R.mipmap.icon_like_selected);
                                                    holder.like_tv.setTextColor(context.getResources().getColor(R.color.green_light));
                                                    holder.like_tv.setText(serverResponse.getMsg());
                                                    Toast.makeText(context, "已点赞", Toast.LENGTH_SHORT).show();

                                                }
                                            });



                                        }else if((Double) serverResponse.getData() == 58){

                                            ((Activity)context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.like_iv.setImageResource(R.mipmap.icon_like_unselected);
                                                    holder.like_tv.setTextColor(context.getResources().getColor(R.color.nav_text_default));
                                                    holder.like_tv.setText( Double.parseDouble(serverResponse.getMsg()) == 0 ? "赞" : serverResponse.getMsg());
                                                    Toast.makeText(context, "已取消", Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                }
                            }
                    );
                }
            });

            holder.more_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flushListener != null){
                        flushListener.onFlush(0);
                    }
                    final DeleteActivityPUW deleteActivityPUW = new DeleteActivityPUW(context, mList.get(position));
                    deleteActivityPUW
                            .setReport(new DeleteActivityPUW.IReportListener() {
                                @Override
                                public void onReport(DeleteActivityPUW deleteActivityPUW) {

                                    OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080//portal/report/select.do?userid="
                                                    +userVO.getId()
                                                    +"&objectid="+mList.get(position).getId()
                                                    +"&category="+0,                                                        new OkHttpCallback(){
                                                @Override
                                                public void onFinish(String status, String msg) {
                                                    super.onFinish(status, msg);
                                                    //解析数据
                                                    Gson gson=new Gson();
                                                    ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                                    if(serverResponse.getStatus() == 0){
                                                        Intent intent = new Intent(context, ReportActivity.class);
                                                        intent.putExtra("category", 0);
                                                        intent.putExtra("objectid", mList.get(position).getId());
                                                        context.startActivity(intent);
                                                    }else{
                                                        Looper.prepare();
                                                        Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                    }
                                                }
                                            }
                                    );
                                }
                            })
                            .setDelete(new DeleteActivityPUW.IDeleteListener() {
                                @Override
                                public void onDelete(DeleteActivityPUW puw) {
                                    OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/activity/delete.do?userid="+userVO.getId()+"&id="+mList.get(position).getId(),
                                            new OkHttpCallback(){
                                                @Override
                                                public void onFinish(String status, final String msg) {
                                                    super.onFinish(status, msg);
                                                    //解析数据
                                                    Gson gson=new Gson();
                                                    final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                                    if(serverResponse.getStatus() == 0){
                                                        ((Activity)context).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                                if(updateListener != null){
                                                                    updateListener.onUpdate(position);
                                                                }
                                                            }
                                                        });

                                                    }else {
                                                        Looper.prepare();
                                                        Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                    }


                                                }
                                            }
                                    );
                                }
                            })
                            .setTOP(new DeleteActivityPUW.ITopListener() {
                                @Override
                                public void onTop(DeleteActivityPUW deleteActivityPUW) {
                                    OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/user/update.do?id="+userVO.getId()+"&topacid="+mList.get(position).getId(),
                                            new OkHttpCallback(){
                                                @Override
                                                public void onFinish(String status, final String msg) {
                                                    super.onFinish(status, msg);
                                                    //解析数据
                                                    Gson gson=new Gson();
                                                    ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                                    if(serverResponse.getStatus() == 0){
                                                        SPSingleton util =  SPSingleton.get(context,SPSingleton.USERINFO);
                                                        util.delete("user");
                                                        util.putString("user", gson.toJson(serverResponse.getData()));
                                                        ((Activity)context).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(context, "置顶成功", Toast.LENGTH_SHORT).show();
                                                                if(updateListener != null){
                                                                    updateListener.onUpdate(position);
                                                                }
                                                            }
                                                        });
                                                    }else{
                                                        Looper.prepare();
                                                        Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                    }
                                                }
                                            }
                                    );
                                }
                            })
                            .setCancelTop(new DeleteActivityPUW.ICancelTopListener() {
                                @Override
                                public void onCancelTop(DeleteActivityPUW deleteActivityPUW) {
                                    OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/user/update.do?id="+userVO.getId()+"&topacid=0",
                                            new OkHttpCallback(){
                                                @Override
                                                public void onFinish(String status, final String msg) {
                                                    super.onFinish(status, msg);
                                                    //解析数据
                                                    Gson gson=new Gson();
                                                    ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                                    if(serverResponse.getStatus() == 0){
                                                        SPSingleton util =  SPSingleton.get(context,SPSingleton.USERINFO);
                                                        util.delete("user");
                                                        util.putString("user", gson.toJson(serverResponse.getData()));
                                                        ((Activity)context).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(context, "取消置顶成功", Toast.LENGTH_SHORT).show();
                                                                if(updateListener != null){
                                                                    updateListener.onUpdate(position);
                                                                }
                                                            }
                                                        });
                                                    }else{
                                                        Looper.prepare();
                                                        Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                    }
                                                }
                                            }
                                    );
                                }
                            })
                            /*
                            .setShare(new DeleteActivityPUW.IShareListener() {
                                @Override
                                public void onShare(DeleteActivityPUW deleteActivityPUW) {
分享待实现
                                }
                            })*/
                            .showFoodPopWindow();
                    deleteActivityPUW.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(flushListener != null){
                                        flushListener.onFlush(1);
                                    }
                                }
                            });

                        }
                    });
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
    public interface OnItemClickListener{
        void onClick(int position);
    }

    public interface OnUpdateListener{
        void onUpdate(int position);
    }

    public interface OnFlushListener{
        void onFlush(int i);
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
