package com.tyut.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
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

        boolean isLogin =  SPSingleton.get(this, SPSingleton.USERINFO).readBoolean("isLogin");
        if(isLogin == true){
            //获取用户信息
            UserVO userVO = (UserVO)  SPSingleton.get(this,SPSingleton.USERINFO).readObject("user", UserVO.class);
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
                UserVO userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

                String username = username_et.getText().toString();
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/user/update.do?id="+userVO.getId()+"&username="+username,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    SPSingleton util =  SPSingleton.get(UpdateUsernameActivity.this,SPSingleton.USERINFO);
                                    util.delete("user");
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    Looper.prepare();
                                    finish();
                                    /*Intent intent = new Intent(UpdateUsernameActivity.this, UpdateUserInfoActivity.class);
                                    UpdateUsernameActivity.this.startActivity(intent);*/
                                    Toast.makeText(UpdateUsernameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                                }else{
                                    Looper.prepare();
                                    Toast.makeText(UpdateUsernameActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }


                            }
                        }
                );
                break;
        }

    }
}
