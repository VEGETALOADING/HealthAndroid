package com.tyut;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username_et;
    EditText password_et;
    Button login_btn;
    Button loginByPhone_btn;
    Button forgetPassword_btn;
    public static final int LOGINACTIVITY = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //获取控件
        username_et = findViewById(R.id.username);
        password_et = findViewById(R.id.password);
        login_btn = findViewById(R.id.login);
        loginByPhone_btn = findViewById(R.id.loginbyphone);
        forgetPassword_btn = findViewById(R.id.forgetpassword);

        //注册点击事件
        login_btn.setOnClickListener(this);
        loginByPhone_btn.setOnClickListener(this);
        forgetPassword_btn.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.login:
                //Toast.makeText(this, "loginButton", Toast.LENGTH_LONG).show();

                //获取数据
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();

                //请求接口 -> okHttp在子线程中执行
                OkHttpUtils.get("http://192.168.1.10:8080/portal/user/login.do?username="+username+"&password="+password,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);

                                //解析接口返回的数据
                               /* Looper.prepare();
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                                Looper.loop();*/
                               Gson gson = new Gson();

                               //解析简单对象
                               //ServerResponse<UserVO> serverResponse = gson.fromJson(msg, ServerResponse.class);

                                //解析复杂对象(如List<User>)
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){


                                    SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(LoginActivity.this);
                                    util.delete("user");
                                    util.delete("isLogin");
                                    util.delete("userid");
                                    util.putBoolean("isLogin", true);
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    util.putInt("userid", serverResponse.getData().getId());
                                    Boolean isLogin = util.readBoolean("isLogin");


                                    //Activity跳转(要在Toast之前？？？？)
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("src", LOGINACTIVITY);
                                    LoginActivity.this.startActivity(intent);

                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                                    Looper.loop();



                                }else{

                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        });
                break;



            case R.id.loginbyphone:
                Toast.makeText(this, "loginbyphoneButton", Toast.LENGTH_LONG).show();

                break;
            case R.id.forgetpassword:
                Toast.makeText(this, "forgetpassword", Toast.LENGTH_LONG).show();

                break;

        }


    }
}
