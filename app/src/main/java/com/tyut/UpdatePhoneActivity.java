package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.service.autofill.TextValueSanitizer;
import android.view.View;
import android.widget.Button;
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
import com.tyut.utils.ValcodeUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class UpdatePhoneActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout return_ll;
    TextView isBound_tv;
    LinearLayout bound_ll;
    LinearLayout notBound_ll;
    TextView phone_tv;
    Button change_btn;
    EditText phoneNum_et;
    Button getValcode_btn;
    EditText valcode_et;
    Button bound_btn;
    String generateValcode;
    String phoneNum;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uupdatephone);
        return_ll = findViewById(R.id.return_b);
        isBound_tv = findViewById(R.id.isBound_tv);
        bound_ll = findViewById(R.id.bound_ll);
        notBound_ll = findViewById(R.id.notBound_ll);
        phone_tv = findViewById(R.id.phone_tv);
        change_btn = findViewById(R.id.change_btn);
        phoneNum_et = findViewById(R.id.phoneNum_et);
        getValcode_btn = findViewById(R.id.getvalcode_btn);
        valcode_et = findViewById(R.id.valcode_et);
        bound_btn = findViewById(R.id.bount_btn);

        return_ll.setOnClickListener(this);
        change_btn.setOnClickListener(this);
        getValcode_btn.setOnClickListener(this);
        bound_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_b:
                finish();
                /*Intent intent = new Intent(UpdatePhoneActivity.this, UpdateUserInfoActivity.class);
                this.startActivity(intent);*/
                break;
            case R.id.change_btn:
                bound_ll.setVisibility(View.GONE);
                notBound_ll.setVisibility(View.VISIBLE);
                isBound_tv.setText("为了账号安全，请绑定手机号");
                break;
            case R.id.getvalcode_btn:
                generateValcode = ValcodeUtil.generateValcode();
                phoneNum = phoneNum_et.getText().toString();
                if(phoneNum.length() != 11){
                    Toast.makeText(UpdatePhoneActivity.this, "手机格式不正确", Toast.LENGTH_LONG).show();
                }else{
                    OkHttpUtils.get("http://192.168.1.4:8080/portal/user/isbound.do?phone="+phoneNum,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() != 0){
                                        Looper.prepare();
                                        Toast.makeText(UpdatePhoneActivity.this, "手机号已绑定", Toast.LENGTH_LONG).show();
                                        Looper.loop();

                                    }else{
                                        OkHttpUtils.get("http://192.168.1.4:8080/portal/user/sendvalcode.do?phone="+phoneNum+"&valcode="+generateValcode,
                                                new OkHttpCallback(){
                                                    @Override
                                                    public void onFinish(String status, String msg) {
                                                        super.onFinish(status, msg);
                                                        //解析数据
                                                        Gson gson=new Gson();
                                                        ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                                        if(serverResponse.getStatus() != 0){
                                                            Looper.prepare();
                                                            Toast.makeText(UpdatePhoneActivity.this, "验证码发送失败", Toast.LENGTH_LONG).show();
                                                            Looper.loop();
                                                        }
                                                    }
                                                }
                                        );


                                    }


                                }
                            }
                    );
                }

                break;
            case R.id.bount_btn:
                String inputValcode = valcode_et.getText().toString();
                if(inputValcode.equals(generateValcode)){
                    UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);
                    //验证码正确，绑定手机号
                    OkHttpUtils.get("http://192.168.1.4:8080/portal/user/update.do?id="+userVO.getId()+"&phone="+phoneNum,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() == 0){
                                        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(UpdatePhoneActivity.this);
                                        util.delete("user");
                                        util.putString("user", gson.toJson(serverResponse.getData()));
                                        Looper.prepare();
                                        finish();
                                        Toast.makeText(UpdatePhoneActivity.this, "绑定成功", Toast.LENGTH_LONG).show();
                                        Looper.loop();

                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(UpdatePhoneActivity.this, "绑定失败", Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }

                                }
                            }
                    );
                }else{
                    //验证码错误
                }


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断用户是否登录    fragment中获取view：getActivity
        boolean isLogin = SharedPreferencesUtil.getInstance(this).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

            if(userVO.getPhone() != null && !"".equals(userVO.getPhone())){
                bound_ll.setVisibility(View.VISIBLE);
                notBound_ll.setVisibility(View.GONE);
                isBound_tv.setText("手机号已绑定");
                phone_tv.setText(userVO.getPhone());
            }else{
                bound_ll.setVisibility(View.GONE);
                notBound_ll.setVisibility(View.VISIBLE);
                isBound_tv.setText("手机号未绑定");
            }


        }
    }
}
