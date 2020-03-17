package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.utils.MD5Utils;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class UpdatePwActivity extends AppCompatActivity implements View.OnClickListener {

    EditText oldPw_et;
    EditText newPw_et;
    LinearLayout return_ll;
    TextView confirm_tv;

    UserVO userVO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepw);

        oldPw_et= findViewById(R.id.oldPw_et);
        newPw_et = findViewById(R.id.newPw_et);
        return_ll = findViewById(R.id.return_ll);
        confirm_tv = findViewById(R.id.confirm_tv);

        oldPw_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPw_et.setTransformationMethod(PasswordTransformationMethod.getInstance());


        return_ll.setOnClickListener(this);
        confirm_tv.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userVO = (UserVO)  SPSingleton.get(this,SPSingleton.USERINFO).readObject("user", UserVO.class);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_ll:
                finish();
                break;
            case R.id.confirm_tv:
                if (oldPw_et.getText().toString().length() == 0 || newPw_et.getText().toString().length() < 6 || newPw_et.getText().toString().length() > 20){
                    Toast.makeText(this, "密码至少包含 数字和英文，长度6-20", Toast.LENGTH_SHORT).show();
                }else{
                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/user/updatepw.do?userid="
                                    +userVO.getId()
                                    +"&oldpw="+MD5Utils.getMD5Code(oldPw_et.getText().toString())
                                    +"&newpw="+MD5Utils.getMD5Code(newPw_et.getText().toString()),
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() == 0){
                                       UpdatePwActivity.this.finish();

                                    }
                                    Looper.prepare();
                                    Toast.makeText(UpdatePwActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            });
                }
                break;
        }

    }
}
