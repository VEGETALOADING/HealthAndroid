package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class UpdateUsernameActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username_et;
    LinearLayout return_a;
    TextView submit_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatename);

        username_et = findViewById(R.id.username_et);
        return_a = findViewById(R.id.return_a);
        submit_tv = findViewById(R.id.finish_tv);

        submit_tv.setOnClickListener(this);
        return_a.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isLogin = SharedPreferencesUtil.getInstance(this).readBoolean("isLogin");
        if(isLogin == true){
            //获取用户信息
            UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);
            username_et.setText(userVO.getUsername());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_a:
                finish();
                break;
            case R.id.finish_tv:
                UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

                String username = username_et.getText().toString();
                OkHttpUtils.get("http://192.168.1.10:8080/portal/user/update.do?id="+userVO.getId()+"&username="+username,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                if(serverResponse.getStatus() == 0){
                                    SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(UpdateUsernameActivity.this);
                                    util.delete("user");
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    Looper.prepare();
                                    finish();
                                    /*Intent intent = new Intent(UpdateUsernameActivity.this, UpdateUserInfoActivity.class);
                                    UpdateUsernameActivity.this.startActivity(intent);*/
                                    Toast.makeText(UpdateUsernameActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                                    Looper.loop();

                                }else{
                                    Looper.prepare();
                                    Toast.makeText(UpdateUsernameActivity.this, "修改失败", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }


                            }
                        }
                );
                break;
        }

    }
}
