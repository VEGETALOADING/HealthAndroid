package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.FollowerListAdapter;
import com.tyut.adapter.FollowingListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.FoodVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.Topic;
import com.tyut.vo.UserVO;

import java.util.List;

public class TopicListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvMain;
    private LinearLayout return_ll;
    private TextView hotTopic_tv;
    private TextView myTopic_tv;
    private TextView noData_tv;

    private static final int TOPICLIST = 0;

    private UserVO userVO;
    private Integer currentType = 1;
    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    final List<Topic> list = (List<Topic>) msg.obj;
                    if(list.size() != 0) {
                        noData_tv.setVisibility(View.GONE);
                        rvMain.setVisibility(View.VISIBLE);
                        rvMain.setLayoutManager(new LinearLayoutManager(TopicListActivity.this));
                        rvMain.addItemDecoration(new RecycleViewDivider(TopicListActivity.this, LinearLayoutManager.VERTICAL));
                        //rvMain.addItemDecoration(new RecycleViewDivider(FollowingListActivity.this,  LinearLayoutManager.VERTICAL, R.drawable.divider));
                        //rvMain.addItemDecoration(new RecycleViewDivider(FollowingListActivity.this,  LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.nav_text_selected)));

                        rvMain.setAdapter(new FollowingListAdapter(TopicListActivity.this, null, list, new FollowingListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {

                                Intent intent = new Intent(TopicListActivity.this, TopicActivity.class);
                                intent.putExtra("topicname", "#" + list.get(position).getName() + "#");
                                TopicListActivity.this.startActivity(intent);

                            }
                        }));
                    }else{
                        rvMain.setVisibility(View.GONE);
                        noData_tv.setVisibility(View.VISIBLE);
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topiclist);

        rvMain = findViewById(R.id.followerRv_main);
        return_ll = findViewById(R.id.return_ll);
        hotTopic_tv = findViewById(R.id.hotTopic_tv);
        myTopic_tv = findViewById(R.id.myTopic_tv);
        noData_tv = findViewById(R.id.noData_tv);

        return_ll.setOnClickListener(this);
        hotTopic_tv.setOnClickListener(this);
        myTopic_tv.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        hotTopic_tv.setTextColor(this.getResources().getColor(R.color.black));
        myTopic_tv.setTextColor(this.getResources().getColor(R.color.nav_text_default));
        //查数据
        userVO = (UserVO) SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

        findHotTopic();


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.return_ll:
                finish();
                break;
            case R.id.hotTopic_tv:
                if(currentType == 0) {
                    currentType = 1;
                    hotTopic_tv.setTextColor(v.getResources().getColor(R.color.black));
                    myTopic_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    findHotTopic();
                }
                break;
            case R.id.myTopic_tv:
                if(currentType == 1) {
                    currentType = 0;
                    hotTopic_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    myTopic_tv.setTextColor(v.getResources().getColor(R.color.black));
                    OkHttpUtils.get("http://192.168.1.9:8080/portal/like/findtopic.do?category=2&userid=" + userVO.getId(),
                            new OkHttpCallback() {
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson = new Gson();
                                    ServerResponse<List<Topic>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Topic>>>() {
                                    }.getType());
                                    if (serverResponse.getStatus() == 0) {
                                        Message message = new Message();
                                        message.what = TOPICLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(TopicListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );
                }
                break;
        }
    }
    private void findHotTopic(){
        OkHttpUtils.get("http://192.168.1.9:8080/portal/topic/find.do?type=1",
                new OkHttpCallback() {
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson = new Gson();
                        ServerResponse<List<Topic>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Topic>>>() {
                        }.getType());
                        if (serverResponse.getStatus() == 0) {
                            Message message = new Message();
                            message.what = TOPICLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        } else {
                            Looper.prepare();
                            Toast.makeText(TopicListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );
    }
}
