package com.tyut.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;
import com.tyut.vo.PunchinVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SettingData;
import com.tyut.vo.UserVO;

public class SplashActivity extends AppCompatActivity {

    private static final Integer INITPUNCHIN = 0;
    private UserVO userVO;
    private TextView appName_tv;

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
                                        SPSingleton spSingleton = SPSingleton.get(SplashActivity.this, SPSingleton.SETTINGDATA);
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
                                        Toast.makeText(SplashActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        //getSupportActionBar().hide();//隐藏标题栏
        setContentView(R.layout.activity_splash);

        appName_tv = findViewById(R.id.appName_tv);
        Animation showAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.textviewshow);
        appName_tv.startAnimation(showAnim);
        userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        Thread myThread=new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    sleep(3000);//使程序休眠五秒
                    if(userVO != null){
                        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/user/autologin.do?userid="+userVO.getId(),
                                new OkHttpCallback(){
                                    @Override
                                    public void onFinish(String status, String msg) {
                                        super.onFinish(status, msg);
                                        Gson gson = new Gson();
                                        ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                        if(serverResponse.getStatus() == 0){
                                            SPSingleton util = SPSingleton.get(SplashActivity.this, SPSingleton.USERINFO);
                                            util.delete("user");
                                            util.delete("isLogin");
                                            util.delete("userid");
                                            util.putBoolean("isLogin", true);
                                            util.putString("user", gson.toJson(serverResponse.getData()));
                                            util.putInt("userid", serverResponse.getData().getId());

                                            Message message = new Message();
                                            message.what= INITPUNCHIN;
                                            mHandler.sendMessage(message);
                                            SplashActivity.this.startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                            SplashActivity.this.finish();
                                            Looper.prepare();
                                            Toast.makeText(SplashActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                            Looper.loop();

                                        }else{
                                            Looper.prepare();
                                            Toast.makeText(SplashActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    }
                                });
                    }else{
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        SplashActivity.this.finish();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}

