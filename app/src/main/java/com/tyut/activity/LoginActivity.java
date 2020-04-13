package com.tyut.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.MD5Utils;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ValcodeUtil;
import com.tyut.vo.PunchinVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SettingData;
import com.tyut.vo.UserVO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout loginByPw_ll;
    EditText userName_et;
    EditText pw_et;
    TextView jumpValcode_tv;
    TextView forgetPw_tv;
    Button loginByPw_btn;

    LinearLayout loginByValcode_ll;
    EditText phone_et;
    EditText valcode_et;
    TextView jumpPw_tv;
    Button sendValcode_btn;
    Button loginByValcode_btn;

    private TimeCount timeCount;
    private UserVO userVO;

    private String generateValcode;

    private static final Integer INITPUNCHIN = 0;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/punchin/select.do?userid="+userVO.getId()+"&createtime="+ StringUtil.getCurrentDate("yyyy-MM-dd"),
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse<PunchinVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<PunchinVO>>(){}.getType());
                                    if(serverResponse.getStatus() == 0) {
                                        SPSingleton spSingleton = SPSingleton.get(LoginActivity.this, SPSingleton.SETTINGDATA);
                                        SettingData settingData = (SettingData)spSingleton.readObject(userVO.getId()+"", SettingData.class);

                                        if(serverResponse.getData().getPunchinList().size() == 0){

                                            if(settingData == null){
                                                settingData = new SettingData(false, false, false);
                                            }else{
                                                settingData.setToday(false);
                                            }

                                        }else{
                                            if(settingData == null){
                                                settingData = new SettingData(false, false, true);
                                            }else{
                                                settingData.setToday(true);
                                            }
                                        }
                                        spSingleton.putString(userVO.getId()+"", gson.toJson(settingData));

                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(LoginActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }


                                }
                            }
                    );
                    break;

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        /*if(userVO != null){
            OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/user/autologin.do?userid="+userVO.getId(),
                    new OkHttpCallback(){
                        @Override
                        public void onFinish(String status, String msg) {
                            super.onFinish(status, msg);
                            Gson gson = new Gson();
                            ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                            if(serverResponse.getStatus() == 0){
                                SPSingleton util = SPSingleton.get(LoginActivity.this, SPSingleton.USERINFO);
                                util.delete("user");
                                util.delete("isLogin");
                                util.delete("userid");
                                util.putBoolean("isLogin", true);
                                util.putString("user", gson.toJson(serverResponse.getData()));
                                util.putInt("userid", serverResponse.getData().getId());

                                Message message = new Message();
                                message.what= INITPUNCHIN;
                                mHandler.sendMessage(message);

                                LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();


                            }else{
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
        }*/
        loginByPw_ll = findViewById(R.id.loginByPw_ll);
        userName_et = findViewById(R.id.username_et);
        pw_et = findViewById(R.id.password_et);
        jumpValcode_tv = findViewById(R.id.jumpValcode_tv);
        forgetPw_tv = findViewById(R.id.forgetPw_tv);
        loginByPw_btn = findViewById(R.id.loginByPw_btn);

        loginByValcode_ll = findViewById(R.id.loginByValcode_ll);
        phone_et = findViewById(R.id.phone_et);
        valcode_et = findViewById(R.id.valcode_et);
        jumpPw_tv = findViewById(R.id.jumpPw_tv);
        sendValcode_btn = findViewById(R.id.getvalcode_btn);
        loginByValcode_btn = findViewById(R.id.loginByValcode_btn);

        timeCount = new TimeCount(60000, 1000);


        //注册点击事件
        jumpValcode_tv.setOnClickListener(this);
        forgetPw_tv.setOnClickListener(this);
        loginByPw_btn.setOnClickListener(this);
        userName_et.addTextChangedListener(new Watch());
        pw_et.addTextChangedListener(new Watch());
        valcode_et.addTextChangedListener(new valCodeWatch());
        pw_et.setTransformationMethod(PasswordTransformationMethod.getInstance());

        jumpPw_tv.setOnClickListener(this);
        sendValcode_btn.setOnClickListener(this);
        loginByValcode_btn.setOnClickListener(this);




    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.forgetPw_tv:
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, ForgetPwActivity.class));
                break;

            case R.id.jumpPw_tv:
                loginByValcode_ll.setVisibility(View.GONE);
                loginByPw_ll.setVisibility(View.VISIBLE);
                phone_et.setText(null);
                valcode_et.setText(null);
                break;
            case R.id.jumpValcode_tv:
                loginByPw_ll.setVisibility(View.GONE);
                loginByValcode_ll.setVisibility(View.VISIBLE);
                userName_et.setText(null);
                pw_et.setText(null);
                break;
            case R.id.getvalcode_btn:
                if(!StringUtil.checkPhoneNum(phone_et.getText().toString())){
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }else{
                    generateValcode = ValcodeUtil.generateValcode();

                    String jsonParam = StringUtil.param2Json("phone"
                            +phone_et.getText().toString()
                            +"&valcode"+generateValcode);
                    OkHttpUtils.post("http://"+getString(R.string.url)+":8080/portal/user/sendvalcode.do", jsonParam,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() == 0){
                                        timeCount.start();
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(LoginActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );
                }
                break;

            case R.id.loginByPw_btn:

                //获取数据
                String username = userName_et.getText().toString();
                try {
                    username =  URLEncoder.encode(username, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String password = MD5Utils.getMD5Code(pw_et.getText().toString());

                //请求接口 -> okHttp在子线程中执行
                String jsonParam = StringUtil.param2Json(
                        "username="+username
                        +"&password="+password);
                OkHttpUtils.post("http://"+getString(R.string.url)+":8080/portal/user/login.do",jsonParam,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);

                                //解析接口返回的数据
                               /* Looper.prepare();
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                Looper.loop();*/
                               Gson gson = new Gson();

                               //解析简单对象
                               //ServerResponse<UserVO> serverResponse = gson.fromJson(msg, ServerResponse.class);

                                //解析复杂对象(如List<User>)
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){

                                    SPSingleton util = SPSingleton.get(LoginActivity.this, SPSingleton.USERINFO);
                                    util.delete("user");
                                    util.delete("isLogin");
                                    util.delete("userid");
                                    util.putBoolean("isLogin", true);
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    util.putInt("userid", serverResponse.getData().getId());

                                    userVO = serverResponse.getData();

                                    Message message = new Message();
                                    message.what= INITPUNCHIN;
                                    mHandler.sendMessage(message);

                                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    LoginActivity.this.finish();

                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                                }else{

                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        });
                break;
            case R.id.loginByValcode_btn:

                jsonParam = StringUtil.param2Json(
                        "phone="+phone_et.getText().toString()
                        +"&valcode="+generateValcode);
                OkHttpUtils.post("http://"+getString(R.string.url)+":8080/portal/user/login.do",
                                jsonParam,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);

                                Gson gson = new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){

                                    SPSingleton util = SPSingleton.get(LoginActivity.this, SPSingleton.USERINFO);
                                    util.delete("user");
                                    util.delete("isLogin");
                                    util.delete("userid");
                                    util.putBoolean("isLogin", true);
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    util.putInt("userid", serverResponse.getData().getId());

                                    userVO = serverResponse.getData();

                                    Message message = new Message();
                                    message.what= INITPUNCHIN;
                                    mHandler.sendMessage(message);

                                    //Activity跳转(要在Toast之前？？？？)
                                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    LoginActivity.this.finish();
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                                }else if(serverResponse.getStatus() == 15){
                                    //新用户注册成功
                                    SPSingleton util = SPSingleton.get(LoginActivity.this, SPSingleton.USERINFO);
                                    util.delete("user");
                                    util.delete("isLogin");
                                    util.delete("userid");
                                    util.putBoolean("isLogin", true);
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    util.putInt("userid", serverResponse.getData().getId());
                                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, GuideActivity.class));


                                }else{

                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        });
                break;


        }


    }

    class Watch implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(userName_et.getText().toString().length() > 0 && pw_et.getText().toString().length() > 5){
                loginByPw_btn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                loginByPw_btn.setClickable(true);
            }else{
                loginByPw_btn.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                loginByPw_btn.setClickable(false);
            }
        }
    }
    class valCodeWatch implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(valcode_et.getText().toString().length() != 0){
                loginByValcode_btn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                loginByValcode_btn.setClickable(true);
            }else{
                loginByValcode_btn.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                loginByValcode_btn.setClickable(false);
            }
        }
    }

    class TimeCount extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendValcode_btn.setBackground(getResources().getDrawable(R.drawable.btn_greyframe));
            sendValcode_btn.setTextColor(getResources().getColor(R.color.nav_text_default));
            sendValcode_btn.setClickable(false);
            sendValcode_btn.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            sendValcode_btn.setBackground(getResources().getDrawable(R.drawable.btn_greenframe));
            sendValcode_btn.setTextColor(getResources().getColor(R.color.green_light));
            sendValcode_btn.setClickable(true);
            sendValcode_btn.setText("获取验证码");
        }
    }
}
