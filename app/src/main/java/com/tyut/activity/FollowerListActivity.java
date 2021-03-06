package com.tyut.activity;

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
import com.tyut.R;
import com.tyut.adapter.FollowerListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.util.List;

public class FollowerListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvMain;
    private LinearLayout return_ll;
    private TextView textView;
    private static final int FOLLOWERLIST = 0;


    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:

                    final List<FollowerVO> list = (List<FollowerVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(FollowerListActivity.this));
                    rvMain.addItemDecoration(new RecycleViewDivider(FollowerListActivity.this,  LinearLayoutManager.VERTICAL));
                    //rvMain.addItemDecoration(new RecycleViewDivider(FollowingListActivity.this,  LinearLayoutManager.VERTICAL, R.drawable.divider));
                    //rvMain.addItemDecoration(new RecycleViewDivider(FollowingListActivity.this,  LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.nav_text_selected)));

                    rvMain.setAdapter(new FollowerListAdapter(FollowerListActivity.this, list, new FollowerListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {

                            Intent intent = new Intent(FollowerListActivity.this, ActivityActivity.class);
                            intent.putExtra("userid", list.get(position).getId());
                            FollowerListActivity.this.startActivity(intent);

                        }
                    }));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followerlist);

        rvMain = findViewById(R.id.followerRv_main);
        return_ll = findViewById(R.id.return_ll);
        return_ll.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //查数据
        UserVO userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        Integer userId = userVO.getId();
        if(getIntent().getIntExtra("userid", userId) != userId){
            userId = getIntent().getIntExtra("userid", userId);
        }
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/follow/findfollower.do?id=" + userId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<FollowerVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FollowerVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= FOLLOWERLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(FollowerListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
                if(getIntent().getStringExtra("src").equals("HomeActivity")){
                    Intent intent = new Intent(FollowerListActivity.this, HomeActivity.class);
                    intent.putExtra("homeFragment", getIntent().getIntExtra("homeFragment", 0));
                    FollowerListActivity.this.startActivity(intent);
                }else{
                    this.finish();
                }
                break;
        }
    }
}
