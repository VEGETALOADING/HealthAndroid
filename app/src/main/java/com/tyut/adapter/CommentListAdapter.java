package com.tyut.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.ActivityDetailActivity;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.CommentVO;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private OnUpdateListener updateListener;


    private List<CommentVO> mList;
    public CommentListAdapter(Context context, List<CommentVO> list, OnItemClickListener listener, OnUpdateListener updateListener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
        this.updateListener = updateListener;
    }

    //子线程主线程通讯
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    break;
                case 1:
                    break;

            }
        }
    };
    @NonNull
    @Override
    public CommentListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final CommentListAdapter.LinearViewHolder holder, final int position) {
        final UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(mContext).readObject("user", UserVO.class);
        if(mList.size() > position){

            if(userVO.getId() == mList.get(position).getUserid()){
                holder.delete_iv.setVisibility(View.VISIBLE);
            }else {
                holder.delete_iv.setVisibility(View.GONE);
            }
            holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                            .setMessage("确定要删除吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                //添加"Yes"按钮
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                     OkHttpUtils.get("http://192.168.1.9:8080/portal/comment/delete.do?userid="+userVO.getId()+"&id="+mList.get(position).getId(),
                                             new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, final String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() == 0){
                                        updateListener.onUpdate();
                                    }
                                    Looper.prepare();
                                    Toast.makeText(mContext, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();


                                }
                            }
                    );
                                }
                            })

                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                //添加取消
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).create();
                    alertDialog.show();

                }
            });
            CommentVO vo = mList.get(position);
            holder.username_tv.setText(vo.getUserName());
            Glide.with(mContext).load("http://192.168.1.9:8080/userpic/" + vo.getUserpic()).into(holder.userpic_iv);
            if(vo.getIfLike()){
                holder.like_iv.setImageResource(R.mipmap.icon_like_selected);
                holder.likeCount_tv.setTextColor(mContext.getResources().getColor(R.color.green_light));
            }else{
                holder.like_iv.setImageResource(R.mipmap.icon_like_unselected);
                holder.likeCount_tv.setTextColor(mContext.getResources().getColor(R.color.nav_text_default));
            }
            if(vo.getLikeCount() == 0){
                holder.likeCount_tv.setVisibility(View.GONE);

            }else {
                holder.likeCount_tv.setText(vo.getLikeCount()+"");
            }
            try {
                holder.commentTime_tv.setText(StringUtil.convertSharetime(vo.getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            {
                String str = vo.getContent();
                SpannableStringBuilder style = new SpannableStringBuilder(str);
                Map<Integer, Integer> mentionMap = StringUtil.getMention(str);
                Map<Integer, Integer> topicsMap = StringUtil.getTopics(str);
                Set<Integer> mentionKeys = mentionMap.keySet();
                Set<Integer> topicKeys = topicsMap.keySet();
                //设置部分文字点击事件
                holder.content_tv.setMovementMethod(LinkMovementMethod.getInstance());
                ClickableSpan mentionClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(mContext, "mentionClickableSpan!", Toast.LENGTH_SHORT).show();
                    }
                };
                ClickableSpan topicsClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(mContext, "topicsClickableSpan!", Toast.LENGTH_SHORT).show();
                    }
                };
                for (Integer key : mentionKeys) {

                    style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green_light)), key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(mentionClickableSpan, key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }

                for (Integer key : topicKeys) {

                    style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green_light)), key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(topicsClickableSpan, key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                holder.content_tv.setText(style);
            }

            holder.like_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpUtils.get("http://192.168.1.9:8080/portal/like/addorcancel.do?userid="+userVO.getId()+"&objectid="+mList.get(position).getId()+"&category=1",
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
                                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.like_iv.setImageResource(R.mipmap.icon_like_selected);
                                                    holder.likeCount_tv.setTextColor(mContext.getResources().getColor(R.color.green_light));
                                                    holder.likeCount_tv.setText(serverResponse.getMsg());
                                                    holder.likeCount_tv.setVisibility(View.VISIBLE);
                                                }
                                            });
                                            Looper.prepare();
                                            Toast.makeText(mContext, "已点赞", Toast.LENGTH_LONG).show();
                                            Looper.loop();


                                        }else if((Double) serverResponse.getData() == 58){

                                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(Double.parseDouble(serverResponse.getMsg()) == 0){
                                                        holder.likeCount_tv.setVisibility(View.GONE);
                                                    }else{
                                                        holder.likeCount_tv.setVisibility(View.VISIBLE);
                                                    }
                                                    holder.like_iv.setImageResource(R.mipmap.icon_like_unselected);
                                                    holder.likeCount_tv.setTextColor(mContext.getResources().getColor(R.color.nav_text_default));
                                                    holder.likeCount_tv.setText( Double.parseDouble(serverResponse.getMsg()) == 0 ? "赞" : serverResponse.getMsg());
                                                }
                                            });

                                            Looper.prepare();
                                            Toast.makeText(mContext, "已取消", Toast.LENGTH_LONG).show();
                                            Looper.loop();
                                        }
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(mContext, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }

                                }
                            }
                    );
                }
            });

            if(mList.get(position).getReplyList().size() > 3){
                holder.replyCount_ll.setVisibility(View.VISIBLE);
                holder.replyCount_tv.setText(mList.get(position).getReplyList().size()+"");
            }else{
                holder.replyCount_ll.setVisibility(View.GONE);
            }
            if(mList.get(position).getReplyList().size() != 0) {
                holder.replyRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                //holder.replyRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                holder.replyRv.setAdapter(new ReplyListAdapter(mContext, mList.get(position).getReplyList(), new ReplyListAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        Toast.makeText(mContext, "评论评论", Toast.LENGTH_LONG).show();
                    }
                }));
            }else{
                holder.replyRv.setVisibility(View.GONE);
            }


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

        private TextView username_tv;
        private ImageView userpic_iv;
        private TextView commentTime_tv;
        private ImageView like_iv;
        private ImageView comment_iv;
        private TextView content_tv;
        private TextView likeCount_tv;
        private ImageView delete_iv;

        private LinearLayout replyCount_ll;
        private TextView replyCount_tv;
        private RecyclerView replyRv;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            username_tv = itemView.findViewById(R.id.username_commentItem);
            userpic_iv = itemView.findViewById(R.id.userpic_commentItem);
            commentTime_tv = itemView.findViewById(R.id.commentTime_tv);
            like_iv = itemView.findViewById(R.id.like_comment_iv);
            comment_iv = itemView.findViewById(R.id.comment_comment_iv);
            content_tv = itemView.findViewById(R.id.content_commentItem);
            likeCount_tv = itemView.findViewById(R.id.like_count_tv);
            delete_iv = itemView.findViewById(R.id.delete_comment_iv);
            replyCount_ll = itemView.findViewById(R.id.replycount_ll);
            replyCount_tv = itemView.findViewById(R.id.replycount_tv);
            replyRv = itemView.findViewById(R.id.reply_Rv);


        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    public interface OnUpdateListener{
        void onUpdate();
    }

}