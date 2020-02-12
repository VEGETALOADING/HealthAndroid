package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class UpdateUserInfoActivity extends AppCompatActivity implements View.OnClickListener
{

    LinearLayout return_ll;
    LinearLayout userpic_ll;
    LinearLayout username_ll;
    LinearLayout phone_ll;
    ImageView user_pic;
    TextView user_name;
    TextView phone_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuserinfo);
        return_ll = findViewById(R.id.return_ll);
        username_ll = findViewById(R.id.username_ll);
        userpic_ll = findViewById(R.id.userpic_ll);
        phone_ll = findViewById(R.id.phone_ll);

        user_pic = findViewById(R.id.user_photo);
        user_name = findViewById(R.id.user_name);
        phone_status = findViewById(R.id.phone_status);

        return_ll.setOnClickListener(this);
        username_ll.setOnClickListener(this);
        userpic_ll.setOnClickListener(this);
        phone_ll.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_ll:
                finish();
                /*Intent intent = new Intent(UpdateUserInfoActivity.this, HomeActivity.class);
                this.startActivity(intent);*/
                break;
            case R.id.userpic_ll:
                break;
            case R.id.username_ll:
                Intent intent2 = new Intent(UpdateUserInfoActivity.this, UpdateUsernameActivity.class);
                this.startActivity(intent2);
                break;
            case R.id.phone_ll:
                Intent intent3 = new Intent(UpdateUserInfoActivity.this, UpdatePhoneActivity.class);
                this.startActivity(intent3);
                break;
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onResume();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        //判断用户是否登录    fragment中获取view：getActivity
        boolean isLogin = SharedPreferencesUtil.getInstance(this).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);


            user_name.setText(userVO.getUsername());
            if(userVO.getPhone() == null || "".equals(userVO.getPhone())){
                phone_status.setText("未验证");
            }else{
                phone_status.setText("已验证");
            }
            Glide.with(this).load("http://192.168.1.4:8080/userpic/" + userVO.getUserpic()).into(user_pic);

        }
    }
}
