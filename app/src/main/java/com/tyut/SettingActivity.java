package com.tyut;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout return_ll;
    RelativeLayout setPw_rl;
    RelativeLayout setUsername_rl;
    RelativeLayout phone_rl;
    RelativeLayout wx_rl;
    RelativeLayout wb_rl;
    RelativeLayout deleteUser_rl;
    TextView wxIsBound_tv;
    TextView wbIsBound_tv;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setPw_rl = findViewById(R.id.setPw_rl);
        phone_rl = findViewById(R.id.setPhone_rl);
        wx_rl = findViewById(R.id.setWx_rl);
        wb_rl = findViewById(R.id.setWb_rl);
        deleteUser_rl = findViewById(R.id.deleteUser_rl);
        wxIsBound_tv = findViewById(R.id.wxIsBound_tv);
        wbIsBound_tv = findViewById(R.id.wbIsBound_tv);
        return_ll = findViewById(R.id.return_ll);
        setUsername_rl = findViewById(R.id.setUsername_rl);

        setPw_rl.setOnClickListener(this);
        phone_rl.setOnClickListener(this);
        wx_rl.setOnClickListener(this);
        wb_rl.setOnClickListener(this);
        deleteUser_rl.setOnClickListener(this);
        return_ll.setOnClickListener(this);
        setUsername_rl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = SPSingleton.get(this, SPSingleton.USERINFO).readInt("userid");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_ll:
                this.finish();
                break;
            case R.id.setPw_rl:
                SettingActivity.this.startActivity(new Intent(SettingActivity.this, UpdatePwActivity.class));
                break;
            case R.id.setPhone_rl:
                SettingActivity.this.startActivity(new Intent(SettingActivity.this, UpdatePhoneActivity.class));
                break;
            case R.id.setWx_rl:
                break;
            case R.id.setWb_rl:
                break;
            case R.id.deleteUser_rl:
                final AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage("该操作会永久删除账号，且不可恢复！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            //添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this)
                                        .setMessage("该操作会永久删除账号，且不可恢复！")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            //添加"Yes"按钮
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                OkHttpUtils.get("http://"+SettingActivity.this.getString(R.string.url)+":8080/portal/user/delete.do?userid="
                                                                +userId,
                                            new OkHttpCallback(){
                                                @Override
                                                public void onFinish(String status, final String msg) {
                                                    super.onFinish(status, msg);
                                                    //解析数据
                                                    Gson gson=new Gson();
                                                    final ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                                    if(serverResponse.getStatus() == 0){
                                                        SPSingleton.get(SettingActivity.this, SPSingleton.USERINFO).clear();
                                                        SPSingleton spSingleton = SPSingleton.get(SettingActivity.this, SPSingleton.SETTINGDATA);
                                                        spSingleton.delete(userId+"");
                                                        SettingActivity.this.startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                                        SettingActivity.this.finish();

                                                    }
                                                    Looper.prepare();
                                                    Toast.makeText(SettingActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                                    Looper.loop();


                                                }
                                            }
                                    );
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            //添加取消
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).create();
                                alertDialog.show();
                            }
                        })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            //添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).create();
                alertDialog.show();
                break;
            case R.id.setUsername_rl:
                SettingActivity.this.startActivity(new Intent(SettingActivity.this, UpdateUsernameActivity.class));
                break;


        }

    }
}

