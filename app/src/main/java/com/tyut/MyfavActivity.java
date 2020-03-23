package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.adapter.FoodListAdapter;
import com.tyut.utils.JudgeUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.FoodVO;
import com.tyut.vo.Myfood;
import com.tyut.vo.ServerResponse;
import com.tyut.widget.FoodPopUpWindow;

import java.util.List;

public class MyfavActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    LinearLayout return_ll;
    TextView favActivity_tv;
    TextView favFood_tv;
    LinearLayout whole_ll;

    private final Integer ACTIVITYLIST = 1;
    private final Integer FOODLIST = 2;
    private Integer userId;


    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    whole_ll.getForeground().setAlpha((int)msg.obj);
                    break;
                case 1:
                    final List<ActivityVO> activityVOList = (List<ActivityVO>) msg.obj;
                    if(activityVOList.size() != 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyfavActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.addItemDecoration(new DividerItemDecoration(MyfavActivity.this, DividerItemDecoration.VERTICAL));
                        recyclerView.setAdapter(new ActivityListAdapter(MyfavActivity.this, activityVOList, new ActivityListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(MyfavActivity.this, ActivityDetailActivity.class);
                                intent.putExtra("activityid", activityVOList.get(position).getId());
                                MyfavActivity.this.startActivity(intent);
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
                    }else {
                        recyclerView.setAdapter(null);
                    }

                    break;

                case 2:
                    final List<FoodVO> list = (List<FoodVO>) msg.obj;
                    if(list.size() != 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyfavActivity.this));
                        recyclerView.addItemDecoration(new RecycleViewDivider(MyfavActivity.this, LinearLayoutManager.VERTICAL));
                        recyclerView.setAdapter(new FoodListAdapter(MyfavActivity.this, list, new FoodListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(final int position) {

                                Intent intent = new Intent(MyfavActivity.this, FoodDetailActivity.class);
                                intent.putExtra("foodvo", list.get(position));
                                MyfavActivity.this.startActivity(intent);

                            }
                        }));
                    }else {
                        recyclerView.setAdapter(null);
                    }
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfav);

        recyclerView = findViewById(R.id.fav_Rv);
        return_ll = findViewById(R.id.return_ll);
        favActivity_tv = findViewById(R.id.favActivity_tv);
        favFood_tv = findViewById(R.id.favFood_tv);

        whole_ll = findViewById(R.id.whole_ll);
        if (whole_ll.getForeground()!=null){
            whole_ll.getForeground().setAlpha(0);
        }

        return_ll.setOnClickListener(this);
        favActivity_tv.setOnClickListener(this);
        favFood_tv.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = SPSingleton.get(this, SPSingleton.USERINFO).readInt("userid");
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/favorite/find.do?userid="+ userId +"&category=1",
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= ACTIVITYLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(MyfavActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
            case R.id. favActivity_tv:
                favFood_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                favFood_tv.setBackground(v.getResources().getDrawable(R.color.defaultBackground));
                favActivity_tv.setTextColor(v.getResources().getColor(R.color.black));
                favActivity_tv.setBackground(v.getResources().getDrawable(R.color.green_lighter));
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/favorite/find.do?userid="+ userId +"&category=1",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= ACTIVITYLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(MyfavActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                            }
                        }
                );
                break;

            case R.id.favFood_tv:
                favFood_tv.setBackground(v.getResources().getDrawable(R.color.green_lighter));
                favFood_tv.setTextColor(v.getResources().getColor(R.color.black));
                favActivity_tv.setBackground(v.getResources().getDrawable(R.color.defaultBackground));
                favActivity_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/favorite/find.do?userid="+ userId +"&category=0",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= FOODLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(MyfavActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                            }
                        }
                );
                break;
        }
    }
}

