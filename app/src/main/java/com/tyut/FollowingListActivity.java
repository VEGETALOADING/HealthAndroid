package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.FollowingListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.util.List;

public class FollowingListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvMain;
    private LinearLayout return_ll;
    private static final int FOLLOWINGLIST = 0;
    private static final int FOLLOWINGLISTACTIVITY = 1;


    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:

                    final List<FollowerVO> list = (List<FollowerVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(FollowingListActivity.this));
                    rvMain.addItemDecoration(new RecycleViewDivider(FollowingListActivity.this,  LinearLayoutManager.VERTICAL));
                    //rvMain.addItemDecoration(new RecycleViewDivider(FollowingListActivity.this,  LinearLayoutManager.VERTICAL, R.drawable.divider));
                    //rvMain.addItemDecoration(new RecycleViewDivider(FollowingListActivity.this,  LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.nav_text_selected)));

                    rvMain.setAdapter(new FollowingListAdapter(FollowingListActivity.this, list, null, new FollowingListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {

                            Intent intent = new Intent(FollowingListActivity.this, ActivityActivity.class);
                            intent.putExtra("userid", list.get(position).getId());
                            FollowingListActivity.this.startActivity(intent);
                            //Toast.makeText(FollowingListActivity.this, list.get(position).getId()+"", Toast.LENGTH_SHORT).show();
                        }
                    }));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followinglist);

        rvMain = findViewById(R.id.rv_main);
        return_ll = findViewById(R.id.return_d);

        return_ll.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //查数据
        UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

        OkHttpUtils.get("http://192.168.1.9:8080/portal/follow/findfollowing.do?id=" + userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<FollowerVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FollowerVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= FOLLOWINGLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(FollowingListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );


    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.return_d:
                if(getIntent().getStringExtra("src").equals("ActivityActivity")){
                    this.finish();
                }else if(getIntent().getStringExtra("src").equals("HomeActivity")){
                    intent = new Intent(FollowingListActivity.this, HomeActivity.class);
                    intent.putExtra("src", "FollowerListActivity");
                    intent.putExtra("homeFragment", getIntent().getIntExtra("homeFragment", 0));
                    FollowingListActivity.this.startActivity(intent);
                }else{
                    this.finish();
                }
                break;
        }
    }
}
