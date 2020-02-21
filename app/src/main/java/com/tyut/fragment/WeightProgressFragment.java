package com.tyut.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.UpdateUserDataActivity;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.vo.Weight;
import com.tyut.widget.RecordWeightDialog;

import java.util.List;

public class WeightProgressFragment extends Fragment implements View.OnClickListener {


    LinearLayout initial_ll;
    LinearLayout latest_ll;
    TextView initialweight_tv;
    TextView initialunit_tv;
    TextView initialtime_tv;
    TextView latestweight_tv;
    TextView latestunit_tv;
    TextView latesttime_tv;
    TextView moreorless_tv;
    TextView morequantity_tv;
    TextView unit1;
    TextView unit2;
    Button jin_btn;
    Button kg_btn;
    Button record_btn;
    Button new_goal;

    private float latestWeight = 60.0f;
    private static final int INITIALANDLATEST = 0;

    Integer userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weightprogress, container, false);

        initialweight_tv = view.findViewById(R.id.initialweight_tv);
        initialunit_tv = view.findViewById(R.id.initialweightunit_tv);
        initialtime_tv = view.findViewById(R.id.initialweight_time_tv);
        latestweight_tv = view.findViewById(R.id.latestweight_tv);
        latestunit_tv = view.findViewById(R.id.latestweightunit_tv);
        latesttime_tv = view.findViewById(R.id.latestweight_time_tv);
        initial_ll = view.findViewById(R.id.initialweight_ll);
        latest_ll = view.findViewById(R.id.latestweight_ll);
        moreorless_tv = view.findViewById(R.id.moreorless_tv);
        morequantity_tv = view.findViewById(R.id.morequantity_tv);
        unit1 = view.findViewById(R.id.initialweightunit_tv);
        unit2 = view.findViewById(R.id.latestweightunit_tv);
        record_btn = view.findViewById(R.id.record_weight);
        new_goal = view.findViewById(R.id.new_goal);

        jin_btn = view.findViewById(R.id.jin_btn);
        kg_btn = view.findViewById(R.id.kg_btn);

        jin_btn.setOnClickListener(this);
        kg_btn.setOnClickListener(this);
        record_btn.setOnClickListener(this);
        new_goal.setOnClickListener(this);

        return view;
    }

    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:

                    final List<Weight> list = (List<Weight>) msg.obj;
                    if(list.size() == 0){
                        initial_ll.setVisibility(View.GONE);
                        latest_ll.setVisibility(View.GONE);
                        morequantity_tv.setText("0");
                    }else if(list.size() == 1){
                        initial_ll.setVisibility(View.VISIBLE);
                        latest_ll.setVisibility(View.GONE);

                        initialweight_tv.setText(list.get(0).getWeight());
                        initialtime_tv.setText(list.get(0).getCreateTime()+"");
                        morequantity_tv.setText(StringUtil.keepDecimal(Float.parseFloat(list.get(0).getWeight()), 1)+"");
                    }else{
                        initial_ll.setVisibility(View.VISIBLE);
                        latest_ll.setVisibility(View.VISIBLE);

                        Float gap = StringUtil.keepDecimal(Float.parseFloat(list.get(list.size()-1).getWeight()) - Float.parseFloat(list.get(0).getWeight()), 1);

                        if(gap <= 0){
                            moreorless_tv.setText("重");
                            morequantity_tv.setText(-gap+"");
                        }else{
                            moreorless_tv.setText("轻");
                            morequantity_tv.setText(gap+"");
                        }
                        initialweight_tv.setText(list.get(list.size()-1).getWeight());
                        initialtime_tv.setText(list.get(list.size()-1).getCreateTime()+"");
                        latestWeight = Float.parseFloat(list.get(0).getWeight());
                        latestweight_tv.setText(list.get(0).getWeight());
                        latesttime_tv.setText(list.get(0).getCreateTime()+"");

                    }
                    break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("tag", "onResume");
        //查数据
        userId = SharedPreferencesUtil.getInstance(getActivity()).readInt("userid");

        OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"/portal/weight/list.do?userid="+userId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<Weight>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Weight>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= INITIALANDLATEST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jin_btn:
                jin_btn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                kg_btn.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                unit1.setText("斤");
                unit2.setText("斤");
                morequantity_tv.setText(StringUtil.keepDecimal(Float.parseFloat(morequantity_tv.getText().toString()) * 2, 1) +"");
                initialweight_tv.setText(StringUtil.keepDecimal(Float.parseFloat(initialweight_tv.getText().toString()) * 2, 1) +"");
                latestweight_tv.setText(StringUtil.keepDecimal(Float.parseFloat(latestweight_tv.getText().toString()) * 2, 1) +"");
                break;
            case R.id.kg_btn:
                jin_btn.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                kg_btn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                unit1.setText("公斤");
                unit2.setText("公斤");
                morequantity_tv.setText(StringUtil.keepDecimal(Float.parseFloat(morequantity_tv.getText().toString()) / 2, 1) +"");
                initialweight_tv.setText(StringUtil.keepDecimal(Float.parseFloat(initialweight_tv.getText().toString()) / 2, 1) +"");
                latestweight_tv.setText(StringUtil.keepDecimal(Float.parseFloat(latestweight_tv.getText().toString()) / 2, 1) +"");
                break;
            case R.id.record_weight:

                final RecordWeightDialog dialog1 = new RecordWeightDialog(getActivity());
                dialog1.setDate("今天")
                        .setDefaultWeight(latestWeight)
                        .setCancel(new RecordWeightDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(RecordWeightDialog dialog) {

                            }
                        })
                        .setConfirm(new RecordWeightDialog.IOnConfirmListener() {
                            @Override
                            public void onConfirm(RecordWeightDialog dialog) {

                                String weight = dialog.getWeight();
                                OkHttpUtils.get("http://"+getActivity().getString(R.string.localhost)+"/portal/weight/add.do?userid="+userId+"&weight="+weight+"&createTime="+ StringUtil.getCurrentDate("yyyy-MM-dd"),
                                        new OkHttpCallback(){
                                            @Override
                                            public void onFinish(String status, String msg) {
                                                super.onFinish(status, msg);
                                                //解析数据
                                                Gson gson=new Gson();
                                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                                SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(getActivity());
                                                util.clear();
                                                util.putBoolean("isLogin", true);
                                                util.putString("user", gson.toJson(serverResponse.getData()));
                                                util.putInt("userid", serverResponse.getData().getId());
                                                onResume();
                                                Looper.prepare();
                                                Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                                Looper.loop();

                                            }
                                        }
                                );

                            }
                        }).show();
                break;
            case R.id.new_goal:
                Intent intent = new Intent(getActivity(), UpdateUserDataActivity.class);
                getActivity().startActivity(intent);
                break;


        }
    }
}
