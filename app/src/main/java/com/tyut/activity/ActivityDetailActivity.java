package com.tyut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.adapter.CommentListAdapter;
import com.tyut.adapter.MyNineAdapter;
import com.tyut.fragment.FaceFragment;
import com.tyut.utils.EmojiUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SoftKeyBoardListener;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;

import com.tyut.view.GlideRoundTransform;
import com.tyut.view.NinePhotoView;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.CommentVO;
import com.tyut.vo.Emoji;
import com.tyut.vo.Reply;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.ChooseMentionPopUpWindow;
import com.tyut.widget.DeleteActivityPUW;
import com.tyut.widget.ReplyPUW;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tyut.utils.EmojiUtil.decodeSampledBitmapFromResource;

public class ActivityDetailActivity extends AppCompatActivity implements View.OnClickListener,
        FaceFragment.OnEmojiClickListener{

    RelativeLayout whole_sv;
    LinearLayout return_ll;
    ImageView more_iv;
    ImageView userPic_iv;
    TextView userName_tv;
    TextView shareTime_tv;
    TextView content_tv;
    LinearLayout commentNotNone_ll;
    TextView commentNone_tv;
    TextView commentCount_tv;
    RecyclerView comment_Rv;
    LinearLayout like_ll;
    LinearLayout comment_ll;
    LinearLayout favorite_ll;
    NinePhotoView ninePhotoView;

    LinearLayout commentAc_ll;
    EditText commentAc_et;
    ImageView face_commentAc_iv;
    ImageView mention_commentAc_iv;
    TextView send_commentAc_tv;
    LinearLayout addFace_hide_ll;


    ImageView like_iv;
    TextView like_tv;
    TextView comment_tv;
    ImageView favorite_iv;
    TextView favorite_tv;


    private ActivityVO activityVO;
    private Integer activityId;
    private CommentVO commentVO;
    private Integer objectId;
    private Integer category;
    private UserVO userVO;
    private Boolean ifFirst;
    private List<UserVO> vos = new ArrayList<>();


    private static final int COMMENTVOLIST = 1;
    private static final int ACTIVITYVO = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ifFirst = true;
        setContentView(R.layout.activity_activitydetail);
        return_ll = findViewById(R.id.return_o);
        more_iv = findViewById(R.id.more_iv);
        userName_tv = findViewById(R.id.username_activity);
        userPic_iv = findViewById(R.id.userpic_activity);
        shareTime_tv = findViewById(R.id.sharetime_activity);
        content_tv = findViewById(R.id.content_activity);
        commentNone_tv = findViewById(R.id.comment_none_tv);
        commentNotNone_ll = findViewById(R.id.comment_notnone_ll);
        commentCount_tv = findViewById(R.id.commentcount_tv);
        comment_Rv = findViewById(R.id.comment_Rv);
        like_ll = findViewById(R.id.like_ll_detail);
        comment_ll = findViewById(R.id.comment_ll_detail);
        favorite_ll = findViewById(R.id.favorite_ll_detail);
        ninePhotoView = findViewById(R.id.nine_photo);

        commentAc_ll = findViewById(R.id.edittext_commentAc_ll);
        face_commentAc_iv = findViewById(R.id.face_commentAc_iv);
        mention_commentAc_iv = findViewById(R.id.mention_commentAc_iv);
        send_commentAc_tv = findViewById(R.id.send_commentAc_tv);
        commentAc_et = findViewById(R.id.commentAc_et);


        like_iv = findViewById(R.id.like_iv);
        like_tv = findViewById(R.id.like_tv);
        favorite_iv = findViewById(R.id.favorite_iv);
        favorite_tv = findViewById(R.id.favorite_tv);
        comment_tv = findViewById(R.id.comment_tv);
        addFace_hide_ll = findViewById(R.id.addFace_hide_ll);

        whole_sv = findViewById(R.id.whole_sv);
        if (whole_sv.getForeground()!=null){
            whole_sv.getForeground().setAlpha(0);
        }

        like_ll.setOnClickListener(this);
        comment_ll.setOnClickListener(this);
        favorite_ll.setOnClickListener(this);

        mention_commentAc_iv.setOnClickListener(this);
        face_commentAc_iv.setOnClickListener(this);
        commentAc_et.setOnClickListener(this);
        send_commentAc_tv.setOnClickListener(this);
        more_iv.setOnClickListener(this);
        userPic_iv.setOnClickListener(this);



        return_ll.setOnClickListener(this);

        FaceFragment faceFragment = FaceFragment.Instance();
        getSupportFragmentManager().beginTransaction().add(R.id.faceContent,faceFragment).commit();


    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    whole_sv.getForeground().setAlpha((int)msg.obj);
                    break;
                case 1:
                    final List<CommentVO> commentVOList = (List<CommentVO>) msg.obj;
                    if(commentVOList.size() == 0){
                        commentNotNone_ll.setVisibility(View.GONE);
                        commentNone_tv.setVisibility(View.VISIBLE);
                    }else{
                        commentNotNone_ll.setVisibility(View.VISIBLE);
                        commentNone_tv.setVisibility(View.GONE);
                        comment_Rv.setLayoutManager(new LinearLayoutManager(ActivityDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                        comment_Rv.addItemDecoration(new DividerItemDecoration(ActivityDetailActivity.this, DividerItemDecoration.VERTICAL));

                        CommentListAdapter adapter = new CommentListAdapter(ActivityDetailActivity.this,
                                commentVOList,
                                new CommentListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onClick(int position) {
                                        //回复
                                        objectId = commentVOList.get(position).getId();
                                        category = 1;
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if(imm.isActive()){
                                            imm.isActive();
                                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                        }

                                        commentAc_ll.setVisibility(View.VISIBLE);
                                        commentAc_et.setFocusable(true);
                                        commentAc_et.setFocusableInTouchMode(true);
                                        commentAc_et.requestFocus();
                                    }
                                }, new CommentListAdapter.OnUpdateListener(){
                            @Override
                            public void onUpdate() {

                                ActivityDetailActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initComment();
                                    }
                                });
                            }
                        });
                        adapter.setReplyListener(new CommentListAdapter.OnReplyListener() {
                            @Override
                            public void onReply(final Reply reply) {
                                ViewUtil.changeAlpha(mHandler, 0);
                                final ReplyPUW replyPUW = new ReplyPUW(ActivityDetailActivity.this, reply);
                                objectId = reply.getId();
                                category = 2;
                                replyPUW
                                        .setDelete(new ReplyPUW.IDeleteListener() {
                                            @Override
                                            public void onDelete(ReplyPUW puw) {
                                                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/comment/delete.do?id="+reply.getId()+"&userid="+userVO.getId()+"&category=1",
                                                        new OkHttpCallback(){
                                                            @Override
                                                            public void onFinish(String status, final String msg) {
                                                                super.onFinish(status, msg);
                                                                //解析数据
                                                                Gson gson=new Gson();
                                                                final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                                                if(serverResponse.getStatus() == 0){
                                                                    (ActivityDetailActivity.this).runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            initComment();
                                                                        }
                                                                    });

                                                                }
                                                                Looper.prepare();
                                                                Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                                Looper.loop();


                                                            }
                                                        }
                                                );
                                            }
                                        }).setReplyListener(new ReplyPUW.IReplyListener() {
                                    @Override
                                    public void onReply(ReplyPUW puw) {

                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if(imm.isActive()){
                                            imm.isActive();
                                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                        }

                                        commentAc_ll.setVisibility(View.VISIBLE);
                                        commentAc_et.setFocusable(true);
                                        commentAc_et.setFocusableInTouchMode(true);
                                        commentAc_et.requestFocus();
                                    }
                                })
                                        .setReportListener(new ReplyPUW.IReportListener() {
                                            @Override
                                            public void onReport(ReplyPUW puw) {
                                                OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/report/select.do?userid="
                                                                +userVO.getId()
                                                                +"&objectid="+reply.getId()
                                                                +"&category=1",
                                                        new OkHttpCallback(){
                                                            @Override
                                                            public void onFinish(String status, String msg) {
                                                                super.onFinish(status, msg);
                                                                //解析数据
                                                                Gson gson=new Gson();
                                                                ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                                                if(serverResponse.getStatus() == 0){
                                                                    Intent intent = new Intent(ActivityDetailActivity.this, ReportActivity.class);
                                                                    intent.putExtra("category", 1);
                                                                    intent.putExtra("objectid", reply.getId());
                                                                    ActivityDetailActivity.this.startActivity(intent);
                                                                }else{
                                                                    Looper.prepare();
                                                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                                    Looper.loop();
                                                                }
                                                            }
                                                        }
                                                );

                                            }
                                        }).showFoodPopWindow();
                                replyPUW.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                                    @Override
                                    public void onDismiss() {
                                        ViewUtil.changeAlpha(mHandler, 1);
                                    }
                                });
                            }
                        });
                        comment_Rv.setAdapter(adapter);
                    }
                    break;
                case 2 :
                    activityVO = ((List<ActivityVO>)msg.obj).get(0);

                    Glide.with(ActivityDetailActivity.this)
                            .load("http://"+getString(R.string.url)+":8080/userpic/" + activityVO.getUserpic())
                            .transform(new GlideRoundTransform(ActivityDetailActivity.this, 25))
                            .into(userPic_iv);

                    if(activityVO.getPic() != null && !"".equals(activityVO.getPic())) {
                        String picStr = activityVO.getPic();
                        List<String> picList = Arrays.asList(picStr.split("/"));
                        MyNineAdapter adapter = new MyNineAdapter(ActivityDetailActivity.this, picList);
                        ninePhotoView.setAdapter(adapter);
                    }
                    userName_tv.setText(activityVO.getUsername());
                    try {
                        shareTime_tv.setText(StringUtil.convertSharetime(activityVO.getCreateTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    {
                        if (activityVO.getIfFavorite()) {
                            favorite_iv.setImageResource(R.mipmap.icon_favorite_selected);
                            favorite_tv.setTextColor(ActivityDetailActivity.this.getResources().getColor(R.color.green_light));
                        } else {
                            favorite_iv.setImageResource(R.mipmap.icon_favorite_unselected);
                            favorite_tv.setTextColor(ActivityDetailActivity.this.getResources().getColor(R.color.nav_text_default));
                        }
                        if (activityVO.getFavoriteCount() > 0) {
                            favorite_tv.setText(activityVO.getFavoriteCount() + "");
                        } else {
                            favorite_tv.setText("收藏");
                        }
                        if (activityVO.getIfLike()) {
                            like_iv.setImageResource(R.mipmap.icon_like_selected);
                            like_tv.setTextColor(ActivityDetailActivity.this.getResources().getColor(R.color.green_light));
                        } else {
                            like_iv.setImageResource(R.mipmap.icon_like_unselected);
                            like_tv.setTextColor(ActivityDetailActivity.this.getResources().getColor(R.color.nav_text_default));
                        }
                        if (activityVO.getLikeCount() > 0) {
                            like_tv.setText(activityVO.getLikeCount() + "");
                        } else {
                            like_tv.setText("赞");
                        }
                        if (activityVO.getCommentCount() > 0) {
                            comment_tv.setText(activityVO.getCommentCount() + "");
                        } else {
                            comment_tv.setText("评论");
                        }
                    }
                    {
                        final String str = activityVO.getContent();
                        SpannableStringBuilder style=new SpannableStringBuilder(str);
                        final Map<Integer, Integer> mentionMap = StringUtil.getMention(str);
                        final Map<Integer, Integer> topicsMap = StringUtil.getTopics(str);
                        final Map<Integer, Integer> emojiMap = StringUtil.getEmoji(str);
                        Set<Integer> mentionKeys = mentionMap.keySet();
                        Set<Integer> topicKeys = topicsMap.keySet();
                        Set<Integer> emojiKeys = emojiMap.keySet();
                        Iterator<Emoji> iterator = EmojiUtil.getEmojiList().iterator();

                        for (final Integer key : emojiKeys) {
                            Emoji emoji = null;
                            String tempText = str.substring(key, emojiMap.get(key));
                            while (iterator.hasNext()) {
                                emoji = iterator.next();
                                if (tempText.equals(emoji.getContent())) {
                                    //转换为Span并设置Span的大小
                                    style.setSpan(new ImageSpan(ActivityDetailActivity.this, decodeSampledBitmapFromResource(ActivityDetailActivity.this.getResources(), emoji.getImageUri()
                                            , EmojiUtil.dip2px(ActivityDetailActivity.this, 18), EmojiUtil.dip2px(ActivityDetailActivity.this, 18))),
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
                                    ds.setUnderlineText(false);//去掉下划线
                                }
                                @Override
                                public void onClick(View widget) {
                                    //跳转用户动态
                                    Intent intent = new Intent(ActivityDetailActivity.this, ActivityActivity.class);
                                    String sub = str.substring(key, mentionMap.get(key));
                                    intent.putExtra("username", sub.substring(1));
                                    ActivityDetailActivity.this.startActivity(intent);

                                }
                            }, key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            style.setSpan(new ForegroundColorSpan(ActivityDetailActivity.this.getResources().getColor(R.color.green_light)),key,mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
                                    Intent intent = new Intent(ActivityDetailActivity.this, TopicActivity.class);
                                    intent.putExtra("topicname", str.substring(key, topicsMap.get(key)));
                                    ActivityDetailActivity.this.startActivity(intent);
                                }
                            }, key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            style.setSpan(new ForegroundColorSpan(ActivityDetailActivity.this.getResources().getColor(R.color.green_light)),key,topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        }
                        content_tv.setText(style);
                        content_tv.setMovementMethod(LinkMovementMethod.getInstance());

                    }
                    if(getIntent().getIntExtra("action", 0) == 1 && ifFirst == true){
                        ifFirst = false;
                        objectId = activityVO.getId();
                        category = 0;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm.isActive()){
                            imm.isActive();
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }

                        commentAc_ll.setVisibility(View.VISIBLE);
                        commentAc_et.setFocusable(true);
                        commentAc_et.setFocusableInTouchMode(true);
                        commentAc_et.requestFocus();
                }
                    break;


            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        onKeyBoardListener();
        //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        userVO = (UserVO) SPSingleton.get(ActivityDetailActivity.this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        activityId = (Integer) getIntent().getIntExtra("activityid", 0);

        initActivity();
        initComment();
        initFriend();
    }

    private void initActivity(){
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/activity/find.do?currentUserId="+userVO.getId()+"&id=" + activityId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0) {
                            Message message = new Message();
                            message.what= ACTIVITYVO;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                }
        );
    }

    private void initComment(){
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/comment/find.do?objectid="+activityId+"&category=0&currentUserId="+userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<CommentVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<CommentVO>>>(){}.getType());
                        Message message = new Message();
                        message.what= COMMENTVOLIST;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );

    }

    private void initFriend(){
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/friend/find.do?id=" + userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<UserVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<UserVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            vos = serverResponse.getData();
                        }else{
                            Looper.prepare();
                            Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_iv:
                ViewUtil.changeAlpha(mHandler, 0);
                final DeleteActivityPUW deleteActivityPUW = new DeleteActivityPUW(ActivityDetailActivity.this, activityVO);
                deleteActivityPUW
                        .setDelete(new DeleteActivityPUW.IDeleteListener() {
                            @Override
                            public void onDelete(DeleteActivityPUW puw) {
                                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/activity/delete.do?userid="+userVO.getId()+"&id="+activityVO.getId(),
                                        new OkHttpCallback(){
                                            @Override
                                            public void onFinish(String status, final String msg) {
                                                super.onFinish(status, msg);
                                                //解析数据
                                                Gson gson=new Gson();
                                                final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                                if(serverResponse.getStatus() == 0){
                                                    ActivityDetailActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                            ActivityDetailActivity.this.finish();
                                                        }
                                                    });

                                                }else {
                                                    Looper.prepare();
                                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                }

                                            }
                                        }
                                );
                            }
                        }).setReport(new DeleteActivityPUW.IReportListener() {
                    @Override
                    public void onReport(DeleteActivityPUW deleteActivityPUW) {
                        OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/report/select.do?userid="
                                        +userVO.getId()
                                        +"&objectid="+activityVO.getId()
                                        +"&category=0",
                                new OkHttpCallback(){
                                    @Override
                                    public void onFinish(String status, String msg) {
                                        super.onFinish(status, msg);
                                        //解析数据
                                        Gson gson=new Gson();
                                        ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                        if(serverResponse.getStatus() == 0){
                                            Intent intent = new Intent(ActivityDetailActivity.this, ReportActivity.class);
                                            intent.putExtra("category", 0);
                                            intent.putExtra("objectid", activityVO.getId());
                                            ActivityDetailActivity.this.startActivity(intent);
                                        }else{
                                            Looper.prepare();
                                            Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
                                OkHttpUtils.get("http://"+ActivityDetailActivity.this.getString(R.string.url)+":8080/portal/user/update.do?id="+userVO.getId()+"&topacid="+activityId,
                                        new OkHttpCallback(){
                                            @Override
                                            public void onFinish(String status, final String msg) {
                                                super.onFinish(status, msg);
                                                //解析数据
                                                Gson gson=new Gson();
                                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                                if(serverResponse.getStatus() == 0){
                                                    SPSingleton util =  SPSingleton.get(ActivityDetailActivity.this,SPSingleton.USERINFO);
                                                    util.delete("user");
                                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                                    (ActivityDetailActivity.this).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(ActivityDetailActivity.this, "置顶成功", Toast.LENGTH_SHORT).show();
                                                            //onResume();
                                                        }
                                                    });
                                                }else{
                                                    Looper.prepare();
                                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
                                OkHttpUtils.get("http://"+ActivityDetailActivity.this.getString(R.string.url)+":8080/portal/user/update.do?id="+userVO.getId()+"&topacid=0",
                                        new OkHttpCallback(){
                                            @Override
                                            public void onFinish(String status, final String msg) {
                                                super.onFinish(status, msg);
                                                //解析数据
                                                Gson gson=new Gson();
                                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                                if(serverResponse.getStatus() == 0){
                                                    SPSingleton util =  SPSingleton.get(ActivityDetailActivity.this,SPSingleton.USERINFO);
                                                    util.delete("user");
                                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                                    (ActivityDetailActivity.this).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(ActivityDetailActivity.this, "取消置顶成功", Toast.LENGTH_SHORT).show();
                                                            //onResume();
                                                        }
                                                    });
                                                }else{
                                                    Looper.prepare();
                                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
                        })
                        */
                        .showFoodPopWindow();
                deleteActivityPUW.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });
                break;
            case R.id.return_o:
                this.finish();
                break;
            case R.id.like_ll_detail:
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/like/addorcancel.do?userid="+userVO.getId()+"&objectid="+activityVO.getId()+"&category=0",
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
                                        ActivityDetailActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                like_iv.setImageResource(R.mipmap.icon_like_selected);
                                                like_tv.setTextColor(ActivityDetailActivity.this.getResources().getColor(R.color.green_light));
                                                like_tv.setText(serverResponse.getMsg());
                                            }
                                        });
                                        Looper.prepare();
                                        Toast.makeText(ActivityDetailActivity.this, "已点赞", Toast.LENGTH_SHORT).show();
                                        Looper.loop();


                                    }else if((Double) serverResponse.getData() == 58){

                                        ActivityDetailActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                like_iv.setImageResource(R.mipmap.icon_like_unselected);
                                                like_tv.setTextColor(ActivityDetailActivity.this.getResources().getColor(R.color.nav_text_default));
                                                like_tv.setText( Double.parseDouble(serverResponse.getMsg()) == 0 ? "赞" : serverResponse.getMsg());
                                            }
                                        });

                                        Looper.prepare();
                                        Toast.makeText(ActivityDetailActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
            case R.id.favorite_ll_detail:
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/favorite/addorcancel.do?userid="+userVO.getId()+"&category=1&objectid="+activityVO.getId(),
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
                                        ActivityDetailActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                favorite_iv.setImageResource(R.mipmap.icon_favorite_selected);
                                                favorite_tv.setTextColor( ActivityDetailActivity.this.getResources().getColor(R.color.green_light));
                                                favorite_tv.setText(serverResponse.getMsg());
                                            }
                                        });
                                        Looper.prepare();
                                        Toast.makeText( ActivityDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                        Looper.loop();


                                    }else if((Double) serverResponse.getData() == 47){

                                        ActivityDetailActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                favorite_iv.setImageResource(R.mipmap.icon_favorite_unselected);
                                                favorite_tv.setTextColor( ActivityDetailActivity.this.getResources().getColor(R.color.nav_text_default));
                                                favorite_tv.setText( Double.parseDouble(serverResponse.getMsg()) == 0 ? "收藏" : serverResponse.getMsg());
                                            }
                                        });

                                        Looper.prepare();
                                        Toast.makeText( ActivityDetailActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText( ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                            }
                        }
                );
                break;
            case R.id.commentAc_et:
                addFace_hide_ll.setVisibility(View.GONE);
                break;
            case R.id.face_commentAc_iv:
                if(addFace_hide_ll.getVisibility() == View.VISIBLE) {
                    addFace_hide_ll.setVisibility(View.GONE);
                }else {
                    addFace_hide_ll.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.mention_commentAc_iv:
                final ChooseMentionPopUpWindow mentionPopUpWindow =
                        new ChooseMentionPopUpWindow(ActivityDetailActivity.this, vos);
                mentionPopUpWindow.setConfirm(new ChooseMentionPopUpWindow.IOnConfirmListener() {
                    @Override
                    public void onConfirm(ChooseMentionPopUpWindow weightPopUpWindow) {
                        List<String> namesMentioned = mentionPopUpWindow.getUserNameSelected();
                        int selectionStart = commentAc_et.getSelectionStart();// 光标位置
                        Editable editable = commentAc_et.getText();// 原先内容

                        for (String s : namesMentioned) {
                            if (selectionStart >= 0) {
                                editable.insert(selectionStart, "@" +  s + " ");// 在光标位置插入内容
                                //editable.insert(content_et.getSelectionStart(), " ");// 话题后面插入空格,至关重要
                                commentAc_et.setSelection(commentAc_et.getSelectionStart());// 移动光标到添加的内容后面
                            }
                        }

                    }
                }).showMentionPopWindow();
                break;
            case R.id.comment_ll_detail:

                objectId = activityVO.getId();
                category = 0;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()){
                    imm.isActive();
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }

                commentAc_ll.setVisibility(View.VISIBLE);
                commentAc_et.setFocusable(true);
                commentAc_et.setFocusableInTouchMode(true);
                commentAc_et.requestFocus();
                break;
            case R.id.send_commentAc_tv:
                String content = null;
                try {
                    content = URLEncoder.encode(commentAc_et.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/comment/add.do?userid="
                                + userVO.getId()
                                +"&objectid="+objectId
                                +"&content="+content
                                +"&category="+category,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse>(){}.getType());

                                if(serverResponse.getStatus() == 0){

                                    ActivityDetailActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                            initComment();
                                        }
                                    });
                                }
                                    Looper.prepare();
                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                            }
                        }
                );
                break;
            case R.id.userpic_activity:
                Intent intent = new Intent(ActivityDetailActivity.this, ActivityActivity.class);
                intent.putExtra("userid", activityVO.getUserid());
                ActivityDetailActivity.this.startActivity(intent);
                break;

        }

    }
    private void onKeyBoardListener() {
        SoftKeyBoardListener.setListener(ActivityDetailActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                //Log.e("软键盘", "键盘隐藏 高度" + height);
                commentAc_ll.setVisibility(View.GONE);
                addFace_hide_ll.setVisibility(View.GONE);
                commentAc_et.clearFocus();
                commentAc_et.setText(null);
            }
        });
    }

    @Override
    public void onEmojiDelete() {
        String text = commentAc_et.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                commentAc_et.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayTextView();
                return;
            }
            commentAc_et.getText().delete(index, text.length());
            displayTextView();
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        commentAc_et.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        displayTextView();
    }

    private void displayTextView() {
        try {
            EmojiUtil.handlerEmojiText(commentAc_et, null, commentAc_et.getText().toString(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        if (emoji != null) {
            int index = commentAc_et.getSelectionStart();
            Editable editable = commentAc_et.getEditableText();
            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        displayTextView();
    }
}
