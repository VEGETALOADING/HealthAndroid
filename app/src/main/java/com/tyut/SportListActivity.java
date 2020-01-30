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
import com.tyut.adapter.FollowingListAdapter;
import com.tyut.adapter.SportListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SportVO;
import com.tyut.vo.UserVO;

import java.util.List;

public class SportListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvMain;
    private LinearLayout return_ll;
    private TextView my_sport;
    private TextView common_sport;
    private static final int SPORTLIST = 1;
    private static final int SPORTLISTACTIVITY = 0;


    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 1:

                    final List<SportVO> list = (List<SportVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(SportListActivity.this));
                    rvMain.addItemDecoration(new RecycleViewDivider(SportListActivity.this,  LinearLayoutManager.VERTICAL));

                    rvMain.setAdapter(new SportListAdapter(SportListActivity.this, list, new SportListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {

                            /*Intent intent = new Intent(SportListActivity.this, FollowingDetailActivity.class);
                            intent.putExtra("followingid", list.get(position).getId());
                            SportListActivity.this.startActivity(intent);*/
                            Toast.makeText(SportListActivity.this, list.get(position).getId()+"", Toast.LENGTH_LONG).show();
                        }
                    }));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportlist);

        rvMain = findViewById(R.id.sportRv_main);
        return_ll = findViewById(R.id.return_f);
        my_sport = findViewById(R.id.my_sport);
        common_sport = findViewById(R.id.common_sport);

        return_ll.setOnClickListener(this);
        my_sport.setOnClickListener(this);
        common_sport.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //查数据
        OkHttpUtils.get("http://192.168.1.10:8080//portal/sport/list.do?userid=0",
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= SPORTLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_f:
                Intent intent = new Intent(SportListActivity.this, HomeActivity.class);
                intent.putExtra("src", SPORTLISTACTIVITY);
                SportListActivity.this.startActivity(intent);
            case R.id.my_sport:
                my_sport.setTextColor(v.getResources().getColor(R.color.black));
                common_sport.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                Integer userid = SharedPreferencesUtil.getInstance(this).readInt("userid");
                OkHttpUtils.get("http://192.168.1.10:8080//portal/sport/list.do?userid="+userid,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= SPORTLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
            case R.id.common_sport:
                my_sport.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                common_sport.setTextColor(v.getResources().getColor(R.color.black));
                OkHttpUtils.get("http://192.168.1.10:8080//portal/sport/list.do?userid=0",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= SPORTLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
        }
    }
}
