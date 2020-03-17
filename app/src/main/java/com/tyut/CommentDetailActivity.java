package com.tyut;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.ReplyListAdapter;
import com.tyut.fragment.FaceFragment;
import com.tyut.utils.EmojiUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SoftKeyBoardListener;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.view.GlideRoundTransform;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tyut.utils.EmojiUtil.decodeSampledBitmapFromResource;

public class CommentDetailActivity extends AppCompatActivity implements View.OnClickListener,
        FaceFragment.OnEmojiClickListener{

    private TextView username_tv;
    private ImageView userpic_iv;
    private TextView commentTime_tv;
    private TextView content_tv;
    private LinearLayout return_ll;

    LinearLayout commentAc_ll;
    EditText commentAc_et;
    ImageView face_commentAc_iv;
    ImageView mention_commentAc_iv;
    TextView send_commentAc_tv;
    LinearLayout addFace_hide_ll;
    RelativeLayout whole_rl;

    private LinearLayout replyCount_ll;
    private TextView replyCount_tv;
    private RecyclerView replyRv;
    private CommentVO commentVO;
    private Integer commentObjectId;
    private Integer commentCategory;

    private List<UserVO> vos = new ArrayList<>();
    private UserVO userVO;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    whole_rl.getForeground().setAlpha((int)msg.obj);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentdetail);
        username_tv = findViewById(R.id.username_commentItem);
        userpic_iv = findViewById(R.id.userpic_commentItem);
        commentTime_tv = findViewById(R.id.commentTime_tv);
        content_tv = findViewById(R.id.content_commentItem);
        replyCount_ll = findViewById(R.id.replycount_ll);
        replyCount_tv = findViewById(R.id.replycount_tv);
        replyRv = findViewById(R.id.reply_Rv);
        return_ll = findViewById(R.id.return_ll);

        commentAc_ll = findViewById(R.id.edittext_commentAc_ll);
        face_commentAc_iv = findViewById(R.id.face_commentAc_iv);
        mention_commentAc_iv = findViewById(R.id.mention_commentAc_iv);
        send_commentAc_tv = findViewById(R.id.send_commentAc_tv);
        commentAc_et = findViewById(R.id.commentAc_et);
        addFace_hide_ll = findViewById(R.id.addFace_hide_ll);
        return_ll.setOnClickListener(this);
        whole_rl = findViewById(R.id.whole_rl);


        if (whole_rl.getForeground()!=null){
            whole_rl.getForeground().setAlpha(0);
        }


        mention_commentAc_iv.setOnClickListener(this);
        face_commentAc_iv.setOnClickListener(this);
        commentAc_et.setOnClickListener(this);
        send_commentAc_tv.setOnClickListener(this);
        userpic_iv.setOnClickListener(this);



        return_ll.setOnClickListener(this);

        FaceFragment faceFragment = FaceFragment.Instance();
        getSupportFragmentManager().beginTransaction().add(R.id.faceContent,faceFragment).commit();


    }

    @Override
    protected void onResume() {
        super.onResume();
        onKeyBoardListener();
        if(commentVO == null) {
            commentVO = (CommentVO) getIntent().getSerializableExtra("commentvo");
        }
        userVO = (UserVO) SPSingleton.get(CommentDetailActivity.this, SPSingleton.USERINFO).readObject("user", UserVO.class);

        username_tv.setText(commentVO.getUserName());
        Glide.with(CommentDetailActivity.this)
                .load("http://"+CommentDetailActivity.this.getString(R.string.url)+":8080/userpic/" + commentVO.getUserpic())
                .transform(new GlideRoundTransform(CommentDetailActivity.this, 25))
                .into(userpic_iv);

        try {
            commentTime_tv.setText(StringUtil.convertSharetime(commentVO.getCreateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        {
            String str = commentVO.getContent();
            SpannableStringBuilder style = new SpannableStringBuilder(str);
            Map<Integer, Integer> mentionMap = StringUtil.getMention(str);
            Map<Integer, Integer> topicsMap = StringUtil.getTopics(str);
            Map<Integer, Integer> emojiMap = StringUtil.getEmoji(str);
            Set<Integer> mentionKeys = mentionMap.keySet();
            Set<Integer> topicKeys = topicsMap.keySet();
            Set<Integer> emojiKeys = emojiMap.keySet();
            //设置部分文字点击事件
            content_tv.setMovementMethod(LinkMovementMethod.getInstance());
            ClickableSpan mentionClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Toast.makeText(CommentDetailActivity.this, "mentionClickableSpan!", Toast.LENGTH_SHORT).show();
                }
            };
            ClickableSpan topicsClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Toast.makeText(CommentDetailActivity.this, "topicsClickableSpan!", Toast.LENGTH_SHORT).show();
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
                        style.setSpan(new ImageSpan(CommentDetailActivity.this, decodeSampledBitmapFromResource(CommentDetailActivity.this.getResources(), emoji.getImageUri()
                                , EmojiUtil.dip2px(CommentDetailActivity.this, 18), EmojiUtil.dip2px(CommentDetailActivity.this, 18))),
                                key, emojiMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                }
            }
            for (Integer key : mentionKeys) {

                style.setSpan(new ForegroundColorSpan(CommentDetailActivity.this.getResources().getColor(R.color.green_light)), key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(mentionClickableSpan, key, mentionMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            for (Integer key : topicKeys) {

                style.setSpan(new ForegroundColorSpan(CommentDetailActivity.this.getResources().getColor(R.color.green_light)), key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(topicsClickableSpan, key, topicsMap.get(key), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
            content_tv.setText(style);
        }

        content_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回复
                commentObjectId = commentVO.getId();
                commentCategory = 1;
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
        });

        replyCount_ll.setVisibility(View.VISIBLE);

        if(commentVO.getReplyList().size() != 0) {
            replyRv.setLayoutManager(new LinearLayoutManager(CommentDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            ReplyListAdapter replyListAdapter = new ReplyListAdapter(CommentDetailActivity.this, commentVO.getReplyList(), new ReplyListAdapter.OnItemClickListener() {
                @Override
                public void onClick(final Reply reply) {

                    ViewUtil.changeAlpha(mHandler, 0);
                    final ReplyPUW replyPUW = new ReplyPUW(CommentDetailActivity.this, reply);
                    commentObjectId = reply.getId();
                    commentCategory = 2;
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
                                                        (CommentDetailActivity.this).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                onResume();
                                                            }
                                                        });

                                                    }
                                                    Looper.prepare();
                                                    Toast.makeText(CommentDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
                                                        Intent intent = new Intent(CommentDetailActivity.this, ReportActivity.class);
                                                        intent.putExtra("category", 1);
                                                        intent.putExtra("objectid", reply.getId());
                                                        CommentDetailActivity.this.startActivity(intent);
                                                    }else{
                                                        Looper.prepare();
                                                        Toast.makeText(CommentDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
            }, true);

            replyRv.setAdapter(replyListAdapter);
        }else{
            replyRv.setVisibility(View.GONE);
        }

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
                            Toast.makeText(CommentDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_ll:
                this.finish();
                break;
            case R.id.commentAc_et:
                addFace_hide_ll.setVisibility(View.INVISIBLE);
                break;
            case R.id.face_commentAc_iv:
                if(addFace_hide_ll.getVisibility() == View.VISIBLE) {
                    addFace_hide_ll.setVisibility(View.INVISIBLE);
                }else {
                    addFace_hide_ll.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.mention_commentAc_iv:
                final ChooseMentionPopUpWindow mentionPopUpWindow =
                        new ChooseMentionPopUpWindow(CommentDetailActivity.this, vos);
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
            case R.id.send_commentAc_tv:
                String content = null;
                try {
                    content = URLEncoder.encode(commentAc_et.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/comment/add.do?userid="
                                + userVO.getId()
                                +"&objectid="+commentObjectId
                                +"&content="+content
                                +"&category="+commentCategory,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse>(){}.getType());

                                if(serverResponse.getStatus() == 0){

                                    CommentDetailActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                            onResume();
                                        }
                                    });
                                }
                                Looper.prepare();
                                Toast.makeText(CommentDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Looper.loop();

                            }
                        }
                );
                break;
            case R.id.userpic_commentItem:
                Intent intent = new Intent(this, ActivityActivity.class);
                intent.putExtra("username", commentVO.getUserName());
                this.startActivity(intent);

        }
    }

    private void onKeyBoardListener() {
        SoftKeyBoardListener.setListener(CommentDetailActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                //Log.e("软键盘", "键盘隐藏 高度" + height);
                commentAc_ll.setVisibility(View.GONE);
                addFace_hide_ll.setVisibility(View.INVISIBLE);
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

