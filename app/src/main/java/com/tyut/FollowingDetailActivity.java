package com.tyut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class FollowingDetailActivity extends AppCompatActivity implements OnClickListener
{

    ImageView user_photo;
    TextView user_name;
    TextView follower_count;
    TextView following_count;
    TextView activity_count;
    LinearLayout return_ll;
    Button follow_btn;

    private static final int INITNAMEANDPIC = 0;
    private static final int INITFOLLOWERCOUNT = 1;
    private static final int INITFOLLOWINGCOUNT = 2;
    private static final int INITACTIVITYCOUNT = 3;
    private static final int CANCELFOLLOW = 4;
    private static final int ADDFOLLOW = 5;

    int current_followingId;

    //子线程主线程通讯
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    UserVO vo = (UserVO) msg.obj;
                    Glide.with(FollowingDetailActivity.this).load("http://192.168.1.10:8080/" + vo.getUserpic()).into(user_photo);
                    user_name.setText(vo.getUsername()+"");
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
                case 4://取消关注
                    follow_btn.setText("关注");
                    follow_btn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                    break;
                case 5://关注
                    follow_btn.setText("已关注");
                    follow_btn.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                    break;

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followingdetail);
        user_photo = findViewById(R.id.user_photo);
        user_name = findViewById(R.id.user_name);
        follower_count = findViewById(R.id.follower_count);
        following_count = findViewById(R.id.following_count);
        activity_count = findViewById(R.id.activity_count);
        return_ll = findViewById(R.id.return_e);
        follow_btn = findViewById(R.id.following_isFollow_btn);

        return_ll.setOnClickListener(this);
        follow_btn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xxx", "OnResume被调用");
        Intent intent = getIntent();
        current_followingId = intent.getIntExtra("followingid", 0);
        OkHttpUtils.get("http://192.168.1.10:8080/portal/user/search.do?id="+current_followingId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);

                        Gson gson = new Gson();

                        ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= INITNAMEANDPIC;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(FollowingDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                });

        OkHttpUtils.get("http://192.168.1.10:8080/portal/follow/findfollowercount.do?id=" + current_followingId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                        Message message = new Message();
                        message.what= INITFOLLOWERCOUNT;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );
        OkHttpUtils.get("http://192.168.1.10:8080/portal/follow/findfollowingcount.do?followerid=" + current_followingId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                        Message message = new Message();
                        message.what= INITFOLLOWINGCOUNT;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );
        OkHttpUtils.get("http://192.168.1.10:8080/portal/activity/findactivitycount.do?userid=" + current_followingId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                        Message message = new Message();
                        message.what= INITACTIVITYCOUNT;
                        message.obj = serverResponse.getData();
                        mHandler.sendMessage(message);

                    }
                }
        );


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_e:
                Intent intent = new Intent(FollowingDetailActivity.this, FollowingListActivity.class);
                FollowingDetailActivity.this.startActivity(intent);
                break;
            case R.id.following_isFollow_btn:
                UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(FollowingDetailActivity.this).readObject("user", UserVO.class);
                OkHttpUtils.get("http://192.168.1.10:8080/portal/follow/followornot.do?id=" + current_followingId + "&followerid=" + userVO.getId(),
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                                if((double)serverResponse.getData() == 19){
                                    //取关成功
                                    Log.d("xxx", "取关成功");
                                    Message message = new Message();
                                    message.what= CANCELFOLLOW;
                                    mHandler.sendMessage(message);
                                    /*Looper.prepare();
                                    Toast.makeText(FollowingDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();*/

                                }else  if((double)serverResponse.getData() == 20){
                                    //取关失败
                                    Looper.prepare();
                                    Toast.makeText(FollowingDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }else  if((double)serverResponse.getData() == 17){
                                    //关注成功
                                    Log.d("xxx", "关注成功");
                                    Message message = new Message();
                                    message.what= ADDFOLLOW;
                                    mHandler.sendMessage(message);
                                   /* Looper.prepare();
                                    Toast.makeText(FollowingDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();*/

                                }else  if((double)serverResponse.getData() == 18){
                                    //关注失败
                                    Looper.prepare();
                                    Toast.makeText(FollowingDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }


                            }
                        }
                );
                break;
        }
    }
}
