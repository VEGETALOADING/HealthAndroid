package com.tyut.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.style.ImageSpan;
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
import com.tyut.ActivityActivity;
import com.tyut.ActivityDetailActivity;
import com.tyut.CommentDetailActivity;
import com.tyut.R;
import com.tyut.ReportActivity;
import com.tyut.utils.EmojiUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;

import com.tyut.view.GlideRoundTransform;
import com.tyut.vo.CommentVO;
import com.tyut.vo.Emoji;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.Reply;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tyut.utils.EmojiUtil.decodeSampledBitmapFromResource;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private OnUpdateListener updateListener;
    private OnReplyListener replyListener;
    private OnReportListener reportListener;

    public CommentListAdapter setReportListener(OnReportListener reportListener) {
        this.reportListener = reportListener;
        return this;
    }

    public CommentListAdapter setReplyListener(OnReplyListener replyListener) {
        this.replyListener = replyListener;
        return this;
    }


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
        final UserVO userVO = (UserVO) SPSingleton.get(mContext, SPSingleton.USERINFO).readObject("user", UserVO.class);
        if(mList.size() > position){

            if(userVO.getId() == mList.get(position).getUserid()){
                holder.delete_iv.setVisibility(View.VISIBLE);
                holder.report_iv.setVisibility(View.GONE);
            }else {
                holder.delete_iv.setVisibility(View.GONE);
                holder.report_iv.setVisibility(View.VISIBLE);
                holder.report_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OkHttpUtils.get("http://"+mContext.getString(R.string.url)+":8080//portal/report/select.do?userid="
                                        +userVO.getId()
                                        +"&objectid="+mList.get(position).getId()
                                        +"&category=1",                                                        new OkHttpCallback(){
                                    @Override
                                    public void onFinish(String status, String msg) {
                                        super.onFinish(status, msg);
                                        //解析数据
                                        Gson gson=new Gson();
                                        ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                        if(serverResponse.getStatus() == 0){
                                            Intent intent = new Intent(mContext, ReportActivity.class);
                                            intent.putExtra("category", 1);
                                            intent.putExtra("objectid", mList.get(position).getId());
                                            mContext.startActivity(intent);
                                        }else{
                                            Looper.prepare();
                                            Toast.makeText(mContext, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    }
                                }
                        );
                    }
                });
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
                                     OkHttpUtils.get("http://"+mContext.getString(R.string.url)+":8080/portal/comment/delete.do?userid="+userVO.getId()+"&id="+mList.get(position).getId()+"&category=0",
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
                                    Toast.makeText(mContext, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
            holder.username_tv.setText(mList.get(position).getUserName());
            Glide.with(mContext)
                    .load("http://"+mContext.getString(R.string.url)+":8080/userpic/" + mList.get(position).getUserpic())
                    .transform(new GlideRoundTransform(mContext, 25))
                    .into(holder.userpic_iv);

            holder.userpic_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActivityActivity.class);
                    intent.putExtra("username",mList.get(position).getUserName());
                    mContext.startActivity(intent);
                }
            });
            if(mList.get(position).getIfLike()){
                holder.like_iv.setImageResource(R.mipmap.icon_like_selected);
                holder.likeCount_tv.setTextColor(mContext.getResources().getColor(R.color.green_light));
            }else{
                holder.like_iv.setImageResource(R.mipmap.icon_like_unselected);
                holder.likeCount_tv.setTextColor(mContext.getResources().getColor(R.color.nav_text_default));
            }
            if(mList.get(position).getLikeCount() == 0){
                holder.likeCount_tv.setVisibility(View.GONE);

            }else {
                holder.likeCount_tv.setText(mList.get(position).getLikeCount()+"");
            }
            try {
                holder.commentTime_tv.setText(StringUtil.convertSharetime(mList.get(position).getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            {
                String str = mList.get(position).getContent();
                SpannableStringBuilder style = new SpannableStringBuilder(str);
                Map<Integer, Integer> mentionMap = StringUtil.getMention(str);
                Map<Integer, Integer> topicsMap = StringUtil.getTopics(str);
                Map<Integer, Integer> emojiMap = StringUtil.getEmoji(str);
                Set<Integer> mentionKeys = mentionMap.keySet();
                Set<Integer> topicKeys = topicsMap.keySet();
                Set<Integer> emojiKeys = emojiMap.keySet();
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
                Iterator<Emoji> iterator = EmojiUtil.getEmojiList().iterator();

                for (final Integer key : emojiKeys) {
                    Emoji emoji = null;
                    String tempText = str.substring(key, emojiMap.get(key));
                    while (iterator.hasNext()) {
                        emoji = iterator.next();
                        if (tempText.equals(emoji.getContent())) {
                            //转换为Span并设置Span的大小
                            style.setSpan(new ImageSpan(mContext, decodeSampledBitmapFromResource(mContext.getResources(), emoji.getImageUri()
                                    , EmojiUtil.dip2px(mContext, 18), EmojiUtil.dip2px(mContext, 18))),
                                    key, emojiMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            break;
                        }
                    }
                }
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
                    OkHttpUtils.get("http://"+mContext.getString(R.string.url)+":8080/portal/like/addorcancel.do?userid="+userVO.getId()+"&objectid="+mList.get(position).getId()+"&category=1",
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
                                            Toast.makeText(mContext, "已点赞", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(mContext, "已取消", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(mContext, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
                holder.replyCount_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, CommentDetailActivity.class);
                        intent.putExtra("commentvo", mList.get(position));
                        mContext.startActivity(intent);
                    }
                });
            }else{
                holder.replyCount_ll.setVisibility(View.GONE);
            }
            if(mList.get(position).getReplyList().size() != 0) {
                holder.replyRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                //holder.replyRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                ReplyListAdapter replyListAdapter = new ReplyListAdapter(mContext, mList.get(position).getReplyList(), new ReplyListAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Reply reply) {
                        //弹框询问
                        if(replyListener != null){
                            replyListener.onReply(reply);
                        }
                    }
                }, false);

                holder.replyRv.setAdapter(replyListAdapter);
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
        private ImageView report_iv;

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
            report_iv = itemView.findViewById(R.id.report_comment_iv);


        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    public interface OnUpdateListener{
        void onUpdate();
    }
    public interface OnReplyListener{
        void onReply(Reply reply);
    }
    public interface OnReportListener{
        void onReport(Reply reply);
    }


}
