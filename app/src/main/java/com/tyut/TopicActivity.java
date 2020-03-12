package com.tyut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.Topic;
import com.tyut.vo.UserVO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class TopicActivity extends AppCompatActivity implements View.OnClickListener {


    private ScrollView activity_Sv;
    private TextView notExist_tv;
    private RelativeLayout whole_rl;
    private RecyclerView activity_Rv;
    private TextView topicName_tv;
    private LinearLayout return_ll;
    private TextView mention_tv;
    private LinearLayout shareActivity_ll;

    private String topicName;
    private Topic topic;
    private String temp;
    private UserVO currentUserVO;

    private static final int ACTIVITYVOLIST = 1;
    private static final int TOPICINFO = 2;
    private static final int NOTEXIST = 3;
    private static final int IFMENTION = 4;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case 0:
                    whole_rl.getForeground().setAlpha((int) msg.obj);
                    break;
                case 1:
                    final List<ActivityVO> activityVOList = (List<ActivityVO>) msg.obj;
                    activity_Rv.setLayoutManager(new LinearLayoutManager(TopicActivity.this, LinearLayoutManager.VERTICAL, false));
                    activity_Rv.addItemDecoration(new DividerItemDecoration(TopicActivity.this, DividerItemDecoration.VERTICAL));
                    activity_Rv.setAdapter(new ActivityListAdapter(TopicActivity.this, activityVOList, new ActivityListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            Intent intent = new Intent(TopicActivity.this, ActivityDetailActivity.class);
                            intent.putExtra("activityid", activityVOList.get(position).getId());
                            TopicActivity.this.startActivity(intent);
                        }
                    }, new ActivityListAdapter.OnUpdateListener() {
                        @Override
                        public void onUpdate(int position) {
                            onResume();
                        }
                    }).setFlushListener(new ActivityListAdapter.OnFlushListener() {
                        @Override
                        public void onFlush(int i) {
                            ViewUtil.changeAlpha(mHandler, i);
                        }
                    }));
                    break;
                case 2:
                    activity_Sv.setVisibility(View.VISIBLE);
                    notExist_tv.setVisibility(View.GONE);
                    topic = ((List<Topic>) msg.obj).get(0);
                    mention_tv.setOnClickListener(TopicActivity.this);

                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/like/find.do?objectid="
                                    +topic.getId()+
                                    "&userid=" + currentUserVO.getId() +
                                    "&category=2",
                            new OkHttpCallback() {
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson = new Gson();
                                    ServerResponse<Boolean> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<Boolean>>() {
                                    }.getType());

                                    Message message = new Message();
                                    message.what = IFMENTION;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);

                                }
                            }
                    );

                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/activity/find.do?currentUserId="+currentUserVO.getId()+"&content=" + temp,
                            new OkHttpCallback() {
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson = new Gson();
                                    ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>() {
                                    }.getType());


                                    Message message = new Message();
                                    message.what = ACTIVITYVOLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);

                                }
                            }
                    );

                    break;
                case 3:
                    activity_Sv.setVisibility(View.GONE);
                    notExist_tv.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    if((Boolean)msg.obj){
                        mention_tv.setText("已关注");
                    }else{
                        mention_tv.setText("关注");
                    }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        whole_rl = findViewById(R.id.whole_rl);
        activity_Rv = findViewById(R.id.activity_Rv);
        topicName_tv = findViewById(R.id.topicName_tv);
        mention_tv = findViewById(R.id.mentionTopic_tv);
        shareActivity_ll = findViewById(R.id.shareActivity_ll);
        return_ll = findViewById(R.id.return_ll);
        activity_Sv = findViewById(R.id.activity_sv);
        notExist_tv = findViewById(R.id.notExist_tv);


        whole_rl = findViewById(R.id.whole_rl);
        if (whole_rl.getForeground() != null) {
            whole_rl.getForeground().setAlpha(0);
        }
        topicName_tv.setOnClickListener(this);
        return_ll.setOnClickListener(this);
        shareActivity_ll.setOnClickListener(this);



    }

    @Override
    protected void onResume() {
        super.onResume();

        topicName = getIntent().getStringExtra("topicname");
        topicName_tv.setText(topicName);
        currentUserVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        try {
            temp = URLEncoder.encode(topicName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/topic/find.do?name="+temp,
                new OkHttpCallback() {
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson = new Gson();
                        ServerResponse<List<Topic>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Topic>>>() {}.getType());

                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what = TOPICINFO;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Message message = new Message();
                            message.what = NOTEXIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
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
            case R.id.shareActivity_ll:
                Intent intent = new Intent(TopicActivity.this, ShareActivity.class);
                intent.putExtra("src", "TOPICACTIVITY");
                intent.putExtra("topicname", topicName);
                TopicActivity.this.startActivity(intent);
                break;
            case R.id.mentionTopic_tv:

                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/like/addorcancel.do?userid="+currentUserVO.getId()+"&objectid="+topic.getId()+"&category=2",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, final String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                if(serverResponse.getStatus() == 0){
                                    if((Double) serverResponse.getData() == 56){

                                        //关注成功
                                        (TopicActivity.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                               mention_tv.setText("已关注");
                                                Toast.makeText(TopicActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }else if((Double) serverResponse.getData() == 58){

                                        //取关成功
                                        (TopicActivity.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mention_tv.setText("关注");
                                                Toast.makeText(TopicActivity.this, "取关成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(TopicActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                            }
                        }
                );
                break;
        }

    }
}
