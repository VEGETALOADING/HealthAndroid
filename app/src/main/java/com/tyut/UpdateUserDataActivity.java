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
import com.tyut.view.PickerScrollView;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserDataActivity extends AppCompatActivity implements View.OnClickListener, CommonPopWindow.ViewClickListener, TextWatcher {

    LinearLayout gender_ll;
    LinearLayout birthday_ll;
    LinearLayout height_ll;
    LinearLayout weight_ll;
    LinearLayout goal_ll;
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

        gender_ll.setOnClickListener(this);
        goal_ll.setOnClickListener(this);
        height_ll.setOnClickListener(this);
        save_btn.setOnClickListener(this);

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
                //模拟请求后台返回数据
                //response = "{\"datas\":[{\"ID\":\"0\",\"categoryName\":\"女\"},{\"ID\":\"1\",\"categoryName\":\"男\"}]}";
                GetConfigReq configReq = new GetConfigReq();

                List<GetConfigReq.DatasBean> list = new ArrayList<>();
                GetConfigReq.DatasBean bean1 = new GetConfigReq.DatasBean("0", "女");
                GetConfigReq.DatasBean bean2 = new GetConfigReq.DatasBean("1", "男");
                list.add(bean1);
                list.add(bean2);
                for (GetConfigReq.DatasBean bean : list) {
                    if(bean.getCategoryName().equals(gender_tv.getText().toString())){
                        defaultIndex = bean.getID();
                    }
                }
                configReq.setDatas(list);
                response = new Gson().toJson(configReq);
                getConfigReq = new Gson().fromJson(response, GetConfigReq.class);
                //滚动选择数据集合
                datasBeanList = getConfigReq.getDatas();
                setAddressSelectorPopup(v, R.id.gender_ll, defaultIndex);
                break;
            case R.id.goal_ll:

                GetConfigReq configReq1 = new GetConfigReq();

                List<GetConfigReq.DatasBean> list1 = new ArrayList<>();
                GetConfigReq.DatasBean bean3 = new GetConfigReq.DatasBean("0", "减脂");
                GetConfigReq.DatasBean bean4 = new GetConfigReq.DatasBean("1", "保持");
                GetConfigReq.DatasBean bean5 = new GetConfigReq.DatasBean("2", "增肌");
                list1.add(bean3);
                list1.add(bean4);
                list1.add(bean5);
                for (GetConfigReq.DatasBean bean : list1) {
                    if(bean.getCategoryName().equals(goal_tv.getText().toString())){
                        defaultIndex = bean.getID();
                    }
                }
                configReq1.setDatas(list1);
                response = new Gson().toJson(configReq1);
                getConfigReq = new Gson().fromJson(response, GetConfigReq.class);
                //滚动选择数据集合
                datasBeanList = getConfigReq.getDatas();
                setAddressSelectorPopup(v, R.id.goal_ll, defaultIndex);
                break;
            case R.id.height_ll:
                GetConfigReq configReq2 = new GetConfigReq();
                List<GetConfigReq.DatasBean> list2 = new ArrayList<>();
                for (int i = 0; i <= 130 ; i++) {
                    GetConfigReq.DatasBean bean = new GetConfigReq.DatasBean(""+ i, ""+(i+130)+"厘米");
                    list2.add(bean);
                }
                for (GetConfigReq.DatasBean bean : list2) {
                    if(bean.getCategoryName().equals(height_tv.getText().toString() + "厘米")){
                        defaultIndex = bean.getID();
                    }
                }
                configReq2.setDatas(list2);
                response = new Gson().toJson(configReq2);
                getConfigReq = new Gson().fromJson(response, GetConfigReq.class);
                //滚动选择数据集合
                datasBeanList = getConfigReq.getDatas();
                setAddressSelectorPopup(v, R.id.height_ll, defaultIndex);
                break;
            case R.id.save_btn:

                int gender = JudgeUtil.getGenderData(gender_tv.getText().toString());
                String height = height_tv.getText().toString();
                String weight = weight_tv.getText().toString();
                int goal = JudgeUtil.getGoalData(goal_tv.getText().toString());

                UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

                OkHttpUtils.get("http://192.168.1.10:8080/portal/user/update.do?gender="+gender+"&height="+height+"&weight="+weight+"&id="+userVO.getId()+"&goal="+goal,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);

                                Gson gson = new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){


                                    SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(UpdateUserDataActivity.this);
                                    util.delete("user");
                                    util.delete("isLogin");
                                    util.delete("userid");
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