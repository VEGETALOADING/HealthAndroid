package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tyut.vo.ActivityVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.util.List;

public class ActivityActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    ImageView userPic;
    TextView userName;
    LinearLayout following_ll;
    LinearLayout follower_ll;
    TextView activity_count;
    TextView follower_count;
    TextView following_count;
    EditText search_et;
    ImageView calendar_iv;
    ImageView return_iv;


    private static final int FOLLOWERCOUNT = 0;
    private static final int FOLLOWINGCOUNT = 1;
    private static final int ACTIVITYCOUNT = 2;
    private static final int ACTIVITYVOLIST = 3;


    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    follower_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 1:
                    following_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 2:
                    activity_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 3:
                    List<ActivityVO> activityVOList = (List<ActivityVO>) msg.obj;
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ActivityActivity.this, LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(ActivityActivity.this, DividerItemDecoration.VERTICAL));
                    mRecyclerView.setAdapter(new ActivityListAdapter(ActivityActivity.this, activityVOList));
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
        search_et = findViewById(R.id.searactivity_et);
        calendar_iv = findViewById(R.id.calendar_iv);
        return_iv = findViewById(R.id.return_n);

        mRecyclerView = findViewById(R.id.activity_Rv);

        following_ll.setOnClickListener(this);
        follower_ll.setOnClickListener(this);
        return_iv.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取用户信息
        UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

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
        }

    }
}
