package com.tyut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.adapter.CommentListAdapter;
import com.tyut.fragment.FaceFragment;
import com.tyut.utils.EmojiUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.SoftKeyBoardListener;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.CommentVO;
import com.tyut.vo.Emoji;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.ChooseMentionPopUpWindow;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActivityDetailActivity extends AppCompatActivity implements View.OnClickListener,
        FaceFragment.OnEmojiClickListener{

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
    private CommentVO commentVO;
    private Integer objectId;
    private Integer category;
    private UserVO userVO;
    private List<UserVO> vos = new ArrayList<>();


    private static final int COMMENTVOLIST = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        like_ll.setOnClickListener(this);
        comment_ll.setOnClickListener(this);
        favorite_ll.setOnClickListener(this);

        mention_commentAc_iv.setOnClickListener(this);
        face_commentAc_iv.setOnClickListener(this);
        commentAc_et.setOnClickListener(this);
        send_commentAc_tv.setOnClickListener(this);



        return_ll.setOnClickListener(this);

        FaceFragment faceFragment = FaceFragment.Instance();
        getSupportFragmentManager().beginTransaction().add(R.id.faceContent,faceFragment).commit();


    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    final List<CommentVO> commentVOList = (List<CommentVO>) msg.obj;
                    if(commentVOList.size() == 0){
                        commentNotNone_ll.setVisibility(View.GONE);
                        commentNone_tv.setVisibility(View.VISIBLE);
                    }else{
                        commentNotNone_ll.setVisibility(View.VISIBLE);
                        commentNone_tv.setVisibility(View.GONE);
                        comment_Rv.setLayoutManager(new LinearLayoutManager(ActivityDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                        comment_Rv.addItemDecoration(new DividerItemDecoration(ActivityDetailActivity.this, DividerItemDecoration.VERTICAL));
                        comment_Rv.setAdapter(new CommentListAdapter(ActivityDetailActivity.this, commentVOList, new CommentListAdapter.OnItemClickListener() {
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
                        }));
                    }
                    break;


            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        onKeyBoardListener();
        userVO = (UserVO) SharedPreferencesUtil.getInstance(ActivityDetailActivity.this).readObject("user", UserVO.class);
        activityVO = (ActivityVO) getIntent().getSerializableExtra("activity");
        Glide.with(this).load("http://192.168.1.9:8080/userpic/" + activityVO.getUserpic()).into(userPic_iv);
        userName_tv.setText(activityVO.getUsername());
        try {
            shareTime_tv.setText(StringUtil.convertSharetime(activityVO.getCreateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        {
            if (activityVO.getIfFavorite()) {
                favorite_iv.setImageResource(R.mipmap.icon_favorite_selected);
                favorite_tv.setTextColor(this.getResources().getColor(R.color.green_light));
            } else {
                favorite_iv.setImageResource(R.mipmap.icon_favorite_unselected);
                favorite_tv.setTextColor(this.getResources().getColor(R.color.nav_text_default));
            }
            if (activityVO.getFavoriteCount() > 0) {
                favorite_tv.setText(activityVO.getFavoriteCount() + "");
            } else {
                favorite_tv.setText("收藏");
            }
            if (activityVO.getIfLike()) {
                like_iv.setImageResource(R.mipmap.icon_like_selected);
                like_tv.setTextColor(this.getResources().getColor(R.color.green_light));
            } else {
                like_iv.setImageResource(R.mipmap.icon_favorite_unselected);
                like_tv.setTextColor(this.getResources().getColor(R.color.nav_text_default));
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
            String str = activityVO.getContent();
            SpannableStringBuilder style = new SpannableStringBuilder(str);
            Map<Integer, Integer> mentionMap = StringUtil.getMention(str);
            Map<Integer, Integer> topicsMap = StringUtil.getTopics(str);
            Set<Integer> mentionKeys = mentionMap.keySet();
            Set<Integer> topicKeys = topicsMap.keySet();
            //设置部分文字点击事件
            content_tv.setMovementMethod(LinkMovementMethod.getInstance());
            ClickableSpan mentionClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Toast.makeText(ActivityDetailActivity.this, "mentionClickableSpan!", Toast.LENGTH_SHORT).show();
                }
            };
            ClickableSpan topicsClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Toast.makeText(ActivityDetailActivity.this, "topicsClickableSpan!", Toast.LENGTH_SHORT).show();
                }
            };
            for (Integer key : mentionKeys) {

                style.setSpan(new ForegroundColorSpan(ActivityDetailActivity.this.getResources().getColor(R.color.green_light)), key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(mentionClickableSpan, key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            for (Integer key : topicKeys) {

                style.setSpan(new ForegroundColorSpan(ActivityDetailActivity.this.getResources().getColor(R.color.green_light)), key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(topicsClickableSpan, key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
            content_tv.setText(style);
        }
        OkHttpUtils.get("http://192.168.1.9:8080/portal/comment/find.do?objectid="+activityVO.getId()+"&category=0&currentUserId="+userVO.getId(),
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

        OkHttpUtils.get("http://192.168.1.9:8080/portal/friend/find.do?id=" + userVO.getId(),
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
                            Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_o:
                this.finish();
                break;
            case R.id.like_ll_detail:
                OkHttpUtils.get("http://192.168.1.9:8080/portal/like/addorcancel.do?userid="+userVO.getId()+"&objectid="+activityVO.getId()+"&category=0",
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
                                        Toast.makeText(ActivityDetailActivity.this, "已点赞", Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(ActivityDetailActivity.this, "已取消", Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
            case R.id.favorite_ll_detail:
                OkHttpUtils.get("http://192.168.1.9:8080/portal/favorite/addorcancel.do?userid="+userVO.getId()+"&category=1&objectid="+activityVO.getId(),
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
                                        Toast.makeText( ActivityDetailActivity.this, "收藏成功", Toast.LENGTH_LONG).show();
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
                                        Toast.makeText( ActivityDetailActivity.this, "取消收藏", Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText( ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
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
                OkHttpUtils.get("http://192.168.1.9:8080/portal/comment/add.do?userid="
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
                                            onResume();
                                        }
                                    });
                                }
                                    Looper.prepare();
                                    Toast.makeText(ActivityDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();

                            }
                        }
                );

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
            EmojiUtil.handlerEmojiText(commentAc_et, commentAc_et.getText().toString(), this);
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
