package com.tyut;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.utils.CommonPopWindow;
import com.tyut.utils.GetConfigReq;
import com.tyut.utils.JudgeUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.StringUtil;
import com.tyut.view.PickerScrollView;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.ChooseBirthdayDialog;
import com.tyut.widget.ChooseHeightDialog;
import com.tyut.widget.ChooseOneDialog;
import com.tyut.widget.RecordWeightDialog;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserDataActivity extends AppCompatActivity implements View.OnClickListener, CommonPopWindow.ViewClickListener, TextWatcher {

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
    private List<GetConfigReq.DatasBean> datasBeanList = new ArrayList<>();
    private String categoryName;
    private String id;
    private String response;
    private GetConfigReq getConfigReq;
    private String defaultIndex;

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



    /**
     * 将选择器放在底部弹出框
     * @param v
     */
    private void setAddressSelectorPopup(View v, int name, String defaultIndex) {
        int screenHeigh = getResources().getDisplayMetrics().heightPixels;

        CommonPopWindow.newBuilder()
                .setName(name)
                .setDefaultIndex(defaultIndex)
                .setView(R.layout.pickerscoll)
                .setAnimationStyle(R.style.AnimUp)
                .setBackgroundDrawable(new BitmapDrawable())
                .setSize(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(screenHeigh * 0.3f))
                .setViewOnClickListener(this)
                .setBackgroundDarkEnable(true)
                .setBackgroundAlpha(0.7f)
                .setBackgroundDrawable(new ColorDrawable(999999))
                .build(this, name, defaultIndex)
                .showAsBottom(v);
    }
    @Override
    public void getChildView(final PopupWindow mPopupWindow, View view, int mLayoutResId, int name, String defaultIndex) {
        switch (mLayoutResId) {
            case R.layout.pickerscoll:
                TextView imageBtn = view.findViewById(R.id.img_guanbi);
                PickerScrollView addressSelector = view.findViewById(R.id.address);

                // 设置数据，默认选择第一条
                addressSelector.setData(datasBeanList, Integer.parseInt(defaultIndex));
                addressSelector.setSelected(defaultIndex);

                //滚动监听
                addressSelector.setOnSelectListener(new PickerScrollView.onSelectListener() {
                    @Override
                    public void onSelect(GetConfigReq.DatasBean pickers) {
                        categoryName = pickers.getCategoryName();
                        id = pickers.getID();
                    }
                });

                switch (name){
                    case R.id.goal_ll:
                    //完成按钮
                    imageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopupWindow.dismiss();
                            goal_tv.setText(categoryName);
                        }
                    });
                    break;
                    case R.id.gender_ll:
                        //完成按钮
                        imageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPopupWindow.dismiss();
                                gender_tv.setText(categoryName);
                            }
                        });
                        break;
                    case R.id.height_ll:
                        //完成按钮
                        imageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPopupWindow.dismiss();
                                height_tv.setText(Integer.parseInt(id) + 130 + "");
                            }
                        });
                        break;

                }

                break;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        boolean isLogin = SharedPreferencesUtil.getInstance(this).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

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
                final ChooseOneDialog dialog1 = new ChooseOneDialog(UpdateUserDataActivity.this, genders);
                dialog1.setCancel(new ChooseOneDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(ChooseOneDialog dialog) {

                    }
                })
                        .setConfirm(new ChooseOneDialog.IOnConfirmListener() {
                            @Override
                            public void onConfirm(ChooseOneDialog dialog) {
                                gender_tv.setText(dialog1.getDataChosen());
                            }
                        }).show();
                break;
            case R.id.goal_ll:
                List<String> goals = new ArrayList<>();
                goals.add("减脂");
                goals.add("保持");
                goals.add("增肌");
                final ChooseOneDialog dialog2 = new ChooseOneDialog(UpdateUserDataActivity.this, goals);
                dialog2.setCancel(new ChooseOneDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(ChooseOneDialog dialog) {

                    }
                })
                        .setConfirm(new ChooseOneDialog.IOnConfirmListener() {
                            @Override
                            public void onConfirm(ChooseOneDialog dialog) {
                                goal_tv.setText(dialog2.getDataChosen());
                            }
                        }).show();

                break;
            case R.id.height_ll:
                final ChooseHeightDialog dialog4 = new ChooseHeightDialog(UpdateUserDataActivity.this);

                dialog4.setDefaultHeight(height_tv.getText().toString().substring(0, 3))
                        .setCancel(new ChooseHeightDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(ChooseHeightDialog dialog) {

                            }
                        })
                        .setConfirm(new ChooseHeightDialog.IOnConfirmListener() {
                            @Override
                            public void onConfirm(ChooseHeightDialog dialog) {
                                height_tv.setText(dialog4.getHeight());

                            }
                        }).show();
                break;
            case R.id.birthday_ll:

                final ChooseBirthdayDialog dialog3 = new ChooseBirthdayDialog(UpdateUserDataActivity.this);

                dialog3.setDefaultYear(birthday_tv.getText().toString().substring(0, 4))
                        .setDefaultMonth(birthday_tv.getText().toString().substring(5, 7))
                        .setDefaultDay(birthday_tv.getText().toString().substring(8, 10))
                        .setCancel(new ChooseBirthdayDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(ChooseBirthdayDialog dialog) {

                    }
                }).setConfirm(new ChooseBirthdayDialog.IOnConfirmListener() {
                    @Override
                    public void onConfirm(ChooseBirthdayDialog dialog) {
                        birthday_tv.setText(dialog3.getYear().substring(0, 4)+"-"+dialog3.getMonth().substring(0, 2)+"-"+dialog3.getDay().substring(0, 2));
                    }
                }).show();
                break;
            case R.id.weight_ll:
                final RecordWeightDialog dialog5 = new RecordWeightDialog(UpdateUserDataActivity.this);
                dialog5.setDate("今天")
                        .setDefaultWeight(Float.parseFloat(weight_tv.getText().toString().substring(0, 4)))
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
                        .show();
                break;
            case R.id.save_btn:

                int gender = JudgeUtil.getGenderData(gender_tv.getText().toString());
                String height = height_tv.getText().toString();
                String weight = weight_tv.getText().toString();
                int goal = JudgeUtil.getGoalData(goal_tv.getText().toString());

                UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

                OkHttpUtils.get("http://192.168.1.4:8080/portal/user/update.do?gender="+gender+"&height="+height+"&weight="+weight+"&id="+userVO.getId()+"&goal="+goal,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);

                                Gson gson = new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){


                                    SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(UpdateUserDataActivity.this);
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
                                    Toast.makeText(UpdateUserDataActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
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