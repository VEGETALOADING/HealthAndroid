package com.tyut.activity;

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
import com.tyut.R;
import com.tyut.adapter.SchemaListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.vo.MSchema;
import com.tyut.vo.ServerResponse;

import java.util.List;

public class SchemaListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvMain;
    private LinearLayout return_ll;
    private static final int SCHEMARLIST = 0;


    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:

                    final List<MSchema> list = (List<MSchema>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(SchemaListActivity.this));
                    rvMain.setAdapter(new SchemaListAdapter(SchemaListActivity.this, list));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemalist);

        rvMain = findViewById(R.id.schemaRv_main);
        return_ll = findViewById(R.id.return_ll);

        return_ll.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //查数据
        //UserVO userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/schema/select.do?userid=0",
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<MSchema>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<MSchema>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= SCHEMARLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(SchemaListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
        }
    }
}
