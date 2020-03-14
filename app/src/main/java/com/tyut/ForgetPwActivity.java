package com.tyut;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.utils.MD5Utils;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ValcodeUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ForgetPwActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout return_ll;

    EditText phone_et;
    EditText valcode_et;
    EditText newPw_et;
    TextView resetPw_tv;
    Button sendValcode_btn;

    private TimeCount timeCount;

    private String generateValcode;
    private static final Integer SENDVALCODE = 0;

    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
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
                                        Toast.makeText(ForgetPwActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            });
                    break;


            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpw);

        return_ll = findViewById(R.id.return_ll);
        phone_et = findViewById(R.id.phone_et);
        valcode_et = findViewById(R.id.valcode_et);
        newPw_et = findViewById(R.id.newPw_et);
        resetPw_tv = findViewById(R.id.resetPw_tv);
        sendValcode_btn = findViewById(R.id.getvalcode_btn);

        timeCount = new TimeCount(60000, 1000);


        //注册点击事件
        return_ll.setOnClickListener(this);
        resetPw_tv.setOnClickListener(this);
        newPw_et.setTransformationMethod(PasswordTransformationMethod.getInstance());

        sendValcode_btn.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.return_ll:
                finish();
                break;
            case R.id.getvalcode_btn:
                if(!StringUtil.checkPhoneNum(phone_et.getText().toString())){
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }else{
                    generateValcode = ValcodeUtil.generateValcode();

                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/user/isbound.do?phone="+phone_et.getText().toString(),
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() != 0){
                                        Message message = new Message();
                                        message.what = SENDVALCODE;
                                        mHandler.sendMessage(message);
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(ForgetPwActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );

                }
                break;

            case R.id.resetPw_tv:

                if(!StringUtil.checkPw(newPw_et.getText().toString())){
                    Toast.makeText(this, "密码至少包含 数字和英文，长度6-20", Toast.LENGTH_SHORT).show();
                }else if(valcode_et.getText().toString().length() == 0){
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                }else{

                    String jsonParam = StringUtil.param2Json(
                            "passwprd="+MD5Utils.getMD5Code(newPw_et.getText().toString())
                            +"&phone="+phone_et.getText().toString()
                            +"&valcode"+generateValcode);
                    OkHttpUtils.post("http://"+getString(R.string.url)+":8080/portal/user/resetpw.do",jsonParam,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);

                                    Gson gson = new Gson();
                                    ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() == 0) {
                                        ForgetPwActivity.this.startActivity(new Intent(ForgetPwActivity.this, LoginActivity.class));
                                    }
                                        Looper.prepare();
                                        Toast.makeText(ForgetPwActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();

                                }
                            });
                }
                break;



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
