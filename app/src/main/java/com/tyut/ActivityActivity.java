package com.tyut;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.BirthdayPopUpWindow;
import com.tyut.widget.DatePUW;
import com.tyut.widget.SearchActivityPUW;
import com.tyut.widget.TopicPopUpWindow;

import java.util.List;

public class ActivityActivity extends AppCompatActivity implements View.OnClickListener {

    ScrollView whole_sv;
    RecyclerView mRecyclerView;
    ImageView userPic;
    TextView userName;
    LinearLayout following_ll;
    LinearLayout follower_ll;
    TextView activity_count;
    TextView follower_count;
    TextView following_count;
    TextView search_tv;
    ImageView calendar_iv;
    ImageView return_iv;
    TextView activityTime_tv;

    private UserVO userVO;

    private static final int FOLLOWERCOUNT = 1;
    private static final int FOLLOWINGCOUNT = 2;
    private static final int ACTIVITYCOUNT = 3;
    private static final int ACTIVITYVOLIST = 4;
    private static final int UPDATETIME = 5;


    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    whole_sv.getForeground().setAlpha((int)msg.obj);
                    break;
                case 1:
                    follower_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 2:
                    following_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 3:
                    activity_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 4:
                    List<ActivityVO> activityVOList = (List<ActivityVO>) msg.obj;
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ActivityActivity.this, LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(ActivityActivity.this, DividerItemDecoration.VERTICAL));
                    mRecyclerView.setAdapter(new ActivityListAdapter(ActivityActivity.this, activityVOList));
                    break;
                case 5:
                    activityTime_tv.setText((String)msg.obj);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        userPic = findViewById(R.id.userpic_activity);
        userName = findViewById(R.id.username_activity);
        following_ll = findViewById(R.id.following_ll);
        follower_ll = findViewById(R.id.follower_ll);
        activity_count = findViewById(R.id.activity_count);
        follower_count = findViewById(R.id.follower_count);
        following_count = findViewById(R.id.following_count);
        search_tv = findViewById(R.id.searactivity_tv);
        calendar_iv = findViewById(R.id.calendar_iv);
        return_iv = findViewById(R.id.return_n);
        activityTime_tv = findViewById(R.id.activityTime_tv);

        mRecyclerView = findViewById(R.id.activity_Rv);
        whole_sv = findViewById(R.id.whole_sv);
        if (whole_sv.getForeground()!=null){
            whole_sv.getForeground().setAlpha(0);
        }

        following_ll.setOnClickListener(this);
        follower_ll.setOnClickListener(this);
        return_iv.setOnClickListener(this);
        search_tv.setOnClickListener(this);
        calendar_iv.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取用户信息
        userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

        userName.setText(userVO.getUsername());
        Glide.with(this).load("http://192.168.1.9:8080/userpic/" + userVO.getUserpic()).into(userPic);

        OkHttpUtils.get("http://192.168.1.9:8080/portal/activity/find.do?currentUserId="+userVO.getId()+"&userid=" + userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());
                        Message message = new Message();
                        message.what= ACTIVITYVOLIST;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );

        OkHttpUtils.get("http://192.168.1.9:8080/portal/follow/findfollowercount.do?id=" + userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                        Message message = new Message();
                        message.what= FOLLOWERCOUNT;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );
        OkHttpUtils.get("http://192.168.1.9:8080/portal/follow/findfollowingcount.do?followerid=" + userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                        Message message = new Message();
                        message.what= FOLLOWINGCOUNT;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );
        OkHttpUtils.get("http://192.168.1.9:8080/portal/activity/findactivitycount.do?userid=" + userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                        Message message = new Message();
                        message.what= ACTIVITYCOUNT;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.calendar_iv:
                ViewUtil.changeAlpha(mHandler, 0);
                final DatePUW datePopUpWindow = new DatePUW(ActivityActivity.this, StringUtil.getCurrentDate("yyyy-MM-dd"));
                datePopUpWindow.showDatePopUpWindow();
                datePopUpWindow.setConfirm(new DatePUW.IOnConfirmListener() {
                    @Override
                    public void onConfirm(final DatePUW datePUW) {
                        OkHttpUtils.get("http://192.168.1.9:8080/portal/activity/find.do?currentUserId="+userVO.getId()+"&userid=" + userVO.getId()+"&createTime=" + datePUW.getDate(),
                                new OkHttpCallback(){
                                    @Override
                                    public void onFinish(String status, String msg) {
                                        super.onFinish(status, msg);
                                        //解析数据
                                        Gson gson=new Gson();
                                        ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());

                                        Message message1 = new Message();
                                        message1.what= UPDATETIME;
                                        message1.obj = datePUW.getDate()+"的动态";
                                        mHandler.sendMessage(message1);

                                        Message message = new Message();
                                        message.what= ACTIVITYVOLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);

                                    }
                                }
                        );
                    }
                });
                datePopUpWindow.setOnRest(new DatePUW.IOnResetListener() {
                    @Override
                    public void onReset(DatePUW datePUW) {
                        //重置查询所有
                        OkHttpUtils.get("http://192.168.1.9:8080/portal/activity/find.do?currentUserId="+userVO.getId()+"&userid=" + userVO.getId(),
                                new OkHttpCallback(){
                                    @Override
                                    public void onFinish(String status, String msg) {
                                        super.onFinish(status, msg);
                                        //解析数据
                                        Gson gson=new Gson();
                                        ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());

                                        Message message1 = new Message();
                                        message1.what= UPDATETIME;
                                        message1.obj = "我的全部动态";
                                        mHandler.sendMessage(message1);

                                        Message message = new Message();
                                        message.what= ACTIVITYVOLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }
                                }
                        );
                    }
                });
                datePopUpWindow.getDatePopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });
                break;

            case R.id.return_n:
                if("HOMEACTIVITY".equals(getIntent().getStringExtra("src"))){
                    intent = new Intent(ActivityActivity.this, HomeActivity.class);
                    intent.putExtra("homeFragment", getIntent().getIntExtra("homeFragment", 0));
                    ActivityActivity.this.startActivity(intent);
                }
                break;
            case R.id.follower_ll:
                intent = new Intent(ActivityActivity.this, FollowerListActivity.class);
                intent.putExtra("src", "ActivityActivity");
                ActivityActivity.this.startActivity(intent);
                break;
            case R.id.following_ll:
                intent = new Intent(ActivityActivity.this, FollowingListActivity.class);
                intent.putExtra("src", "ActivityActivity");
                ActivityActivity.this.startActivity(intent);
                break;
            case R.id.searactivity_tv:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                final SearchActivityPUW searchActivityPUW =
                        new SearchActivityPUW(ActivityActivity.this);
                searchActivityPUW.showTopicPopWindow();
                if(imm.isActive()){
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                searchActivityPUW.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        View view = ActivityActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) ActivityActivity.this.getSystemService(ActivityActivity.this.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                });
                break;
        }

    }
}
