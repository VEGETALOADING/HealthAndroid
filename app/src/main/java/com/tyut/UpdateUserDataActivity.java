package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.utils.GetConfigReq;
import com.tyut.utils.JudgeUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.ViewUtil;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.BirthdayPopUpWindow;
import com.tyut.widget.ChooseOnePopUpWindow;
import com.tyut.widget.HeightPopUpWindow;
import com.tyut.widget.WeightPopUpWindow;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserDataActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    LinearLayout whole_ll;
    LinearLayout gender_ll;
    LinearLayout birthday_ll;
    LinearLayout height_ll;
    LinearLayout weight_ll;
    LinearLayout goal_ll;
    LinearLayout return_ll;
    TextView gender_tv;
    TextView birthday_tv;
    TextView height_tv;
    TextView weight_tv;
    TextView goal_tv;
    Button save_btn;

    UserVO userVO;
    private List<GetConfigReq.DatasBean> datasBeanList = new ArrayList<>();
    private String categoryName;
    private String id;
    private String response;
    private GetConfigReq getConfigReq;
    private String defaultIndex;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    whole_ll.getForeground().setAlpha((int)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuserdata);
        gender_ll = findViewById(R.id.gender_ll);
        birthday_ll = findViewById(R.id.birthday_ll);
        height_ll = findViewById(R.id.height_ll);
        weight_ll = findViewById(R.id.weight_ll);
        goal_ll = findViewById(R.id.goal_ll);
        gender_tv = findViewById(R.id.gender_tv);
        birthday_tv = findViewById(R.id.birthday_tv);
        weight_tv = findViewById(R.id.weight_tv);
        height_tv = findViewById(R.id.height_tv);
        goal_tv = findViewById(R.id.goal_tv);
        save_btn = findViewById(R.id.save_btn);
        return_ll = findViewById(R.id.return_i);

        whole_ll = findViewById(R.id.whole_ll);
        if (whole_ll.getForeground()!=null){
            whole_ll.getForeground().setAlpha(0);
        }


        gender_ll.setOnClickListener(this);
        goal_ll.setOnClickListener(this);
        height_ll.setOnClickListener(this);
        weight_ll.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        birthday_ll.setOnClickListener(this);
        return_ll.setOnClickListener(this);

        goal_tv.addTextChangedListener(this);
        gender_tv.addTextChangedListener(this);
        height_tv.addTextChangedListener(this);
        weight_tv.addTextChangedListener(this);
        birthday_tv.addTextChangedListener(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isLogin = SPSingleton.get(this, SPSingleton.USERINFO).readBoolean("isLogin");
        userVO = (UserVO) SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO) SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

            gender_tv.setText(userVO.getGender() == 0 ? "女" : "男");
            birthday_tv.setText(userVO.getBirthday().substring(0, 10));
            height_tv.setText(userVO.getHeight());
            weight_tv.setText(userVO.getWeight());
            Integer goal = userVO.getGoal();
            switch (goal){
                case 0:
                    goal_tv.setText("减脂");
                    break;
                case 1:
                    goal_tv.setText("保持");
                    break;
                case 2:
                    goal_tv.setText("增肌");
                    break;
            }
        }
        save_btn.setEnabled(false);
        save_btn.setTextColor(this.getResources().getColor(R.color.btn_unselected));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gender_ll:
                List<String> genders = new ArrayList<>();
                genders.add("男");
                genders.add("女");

                ViewUtil.changeAlpha(mHandler, 0);
                final ChooseOnePopUpWindow chooseOnePopUpWindow = new ChooseOnePopUpWindow(UpdateUserDataActivity.this, genders, gender_tv.getText().toString());
                chooseOnePopUpWindow
                        .setConfirm(new ChooseOnePopUpWindow.IOnConfirmListener() {
                    @Override
                    public void onConfirm(ChooseOnePopUpWindow onePopUpWindow) {
                        gender_tv.setText(onePopUpWindow.getDataChosen());

                    }
                })
                        .showFoodPopWindow();
                chooseOnePopUpWindow.getOnePopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });
                break;
            case R.id.goal_ll:
                List<String> goals = new ArrayList<>();
                goals.add("减脂");
                goals.add("保持");
                goals.add("增肌");

                ViewUtil.changeAlpha(mHandler, 0);
                final ChooseOnePopUpWindow chooseOnePopUpWindow1 = new ChooseOnePopUpWindow(UpdateUserDataActivity.this, goals, goal_tv.getText().toString());
                chooseOnePopUpWindow1
                        .setConfirm(new ChooseOnePopUpWindow.IOnConfirmListener() {
                            @Override
                            public void onConfirm(ChooseOnePopUpWindow onePopUpWindow) {
                                goal_tv.setText(onePopUpWindow.getDataChosen());

                            }
                        })
                        .showFoodPopWindow();
                chooseOnePopUpWindow1.getOnePopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });

                break;
            case R.id.height_ll:
                ViewUtil.changeAlpha(mHandler, 0);
                final HeightPopUpWindow heightPopUpWindow = new HeightPopUpWindow(UpdateUserDataActivity.this, Float.parseFloat(userVO.getHeight()));
                heightPopUpWindow.setConfirm(new HeightPopUpWindow.IOnConfirmListener() {
                    @Override
                    public void onConfirm(HeightPopUpWindow heightPopUpWindow1) {
                        height_tv.setText(heightPopUpWindow.getHeight());

                    }
                }).showFoodPopWindow();
                heightPopUpWindow.getHeightPopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });
                break;
            case R.id.birthday_ll:

                ViewUtil.changeAlpha(mHandler, 0);
                final BirthdayPopUpWindow birthdayPopUpWindow = new BirthdayPopUpWindow(UpdateUserDataActivity.this, birthday_tv.getText().toString());
                birthdayPopUpWindow.setConfirm(new BirthdayPopUpWindow.IOnConfirmListener() {
                @Override
                public void onConfirm(BirthdayPopUpWindow birthdayPopUpWindow1) {
                    birthday_tv.setText(birthdayPopUpWindow.getDate());

                }
            }).showFoodPopWindow();
                birthdayPopUpWindow.getBirthdayPopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });
                break;

            case R.id.weight_ll:
              /*  final RecordWeightDialog dialog5 = new RecordWeightDialog(UpdateUserDataActivity.this);
                dialog5.setDate("今天")
                        .setDefaultWeight(Float.parseFloat(weight_tv.getText().toString()))
                        .setCancel(new RecordWeightDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(RecordWeightDialog dialog) {

                            }
                        })
                        .setConfirm(new RecordWeightDialog.IOnConfirmListener() {
                            @Override
                            public void onConfirm(RecordWeightDialog dialog) {
                                weight_tv.setText(dialog5.getWeight());
                            }
                        })
                        .show();*/
                ViewUtil.changeAlpha(mHandler, 0);
                final WeightPopUpWindow weightPopUpWindow = new WeightPopUpWindow(UpdateUserDataActivity.this, Float.parseFloat(userVO.getWeight()), null, false);
                weightPopUpWindow.setConfirm(new WeightPopUpWindow.IOnConfirmListener() {
                    @Override
                    public void onConfirm(WeightPopUpWindow weightPopUpWindow1) {
                        weight_tv.setText(weightPopUpWindow1.getWeight());

                    }
                }).showFoodPopWindow();
                weightPopUpWindow.getWeightPopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });
                break;
            case R.id.save_btn:

                int gender = JudgeUtil.getGenderData(gender_tv.getText().toString());
                String height = height_tv.getText().toString();
                String weight = weight_tv.getText().toString();
                int goal = JudgeUtil.getGoalData(goal_tv.getText().toString());

                OkHttpUtils.get("http://192.168.1.9:8080/portal/user/update.do?gender="+gender+"&height="+height+"&weight="+weight+"&id="+userVO.getId()+"&goal="+goal,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);

                                Gson gson = new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){


                                    SPSingleton util = SPSingleton.get(UpdateUserDataActivity.this, SPSingleton.USERINFO);
                                    /*util.delete("user");
                                    util.delete("isLogin");
                                    util.delete("userid");*/
                                    util.clear();
                                    util.putBoolean("isLogin", true);
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    util.putInt("userid", serverResponse.getData().getId());

                                    Intent intent = new Intent(UpdateUserDataActivity.this, ShowSchemaActivity.class);
                                    UpdateUserDataActivity.this.startActivity(intent);




                                }else{

                                    Looper.prepare();
                                    Toast.makeText(UpdateUserDataActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        });

                break;
            case R.id.return_i:
                finish();
                break;

        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        save_btn.setTextColor(this.getResources().getColor(R.color.nav_text_selected));
        save_btn.setEnabled(true);
    }
}