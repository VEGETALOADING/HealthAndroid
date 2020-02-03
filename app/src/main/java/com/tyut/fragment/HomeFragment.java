package com.tyut.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class HomeFragment extends Fragment {

    ImageView userpic;
    TextView username;
    TextView status;
    TextView weight;
    TextView bmi;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.home_fragment, container, false);

        userpic = view.findViewById(R.id.userpic_home);
        username = view.findViewById(R.id.username_home);
        status = view.findViewById(R.id.now_status);
        weight = view.findViewById(R.id.weight_home);
        bmi = view.findViewById(R.id.bmi_home);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //判断用户是否登录    fragment中获取view：getActivity
        boolean isLogin = SharedPreferencesUtil.getInstance(getActivity()).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(getActivity()).readObject("user", UserVO.class);

            username.setText(userVO.getUsername());
            Glide.with(this).load("http://192.168.1.10:8080/userpic/" + userVO.getUserpic()).into(userpic);
            weight.setText(Double.parseDouble(userVO.getWeight())+"");

            float BMI = StringUtil.getBMI(userVO.getWeight(), userVO.getHeight());
            bmi.setText(BMI+"");

            switch (userVO.getGoal()){
                case 0:
                    status.setText("减脂");
                    break;
                case 1:
                    status.setText("保持");
                    break;
                case 2:
                    status.setText("增肌");
                    break;
            }

        }
    }
}
