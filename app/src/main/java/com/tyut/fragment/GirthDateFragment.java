package com.tyut.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.adapter.GirthOutAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.GirthVO;
import com.tyut.vo.ServerResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GirthDateFragment extends Fragment {


   RecyclerView recyclerView;


    Map<String, List<GirthVO>> map = new HashMap<>();
    private static final int GIRTBYDATE = 0;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    map = (Map<String, List<GirthVO>> ) msg.obj;
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    //recyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),  LinearLayoutManager.VERTICAL));
                    recyclerView.setAdapter(new GirthOutAdapter(getActivity(), map, new GirthOutAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(final int position) {
                        }
                    }));
                    break;

            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_girthdate, container, false);
        recyclerView = view.findViewById(R.id.girthOutRv_main);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Integer userid = SharedPreferencesUtil.getInstance(getActivity()).readInt("userid");

        OkHttpUtils.get("http://192.168.1.9:8080/portal/girth/date.do?userid="+userid,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<Map<String, List<GirthVO>>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<Map<String, List<GirthVO>>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= GIRTBYDATE;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);


                        }else {
                            Looper.prepare();
                            Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );

    }


    private String getMinValue(List<GirthVO> girthVOS){
        String minValue = null;
        for (GirthVO girthVO : girthVOS) {
            if(minValue == null){
                minValue = girthVO.getValue();
            }else{
                if(Float.parseFloat(girthVO.getValue()) < Float.parseFloat(minValue)){
                    minValue = girthVO.getValue();
                }
            }
        }
        minValue = String.valueOf((Integer.parseInt(minValue.substring(0, minValue.length() - 2)) / 10) * 10);
        return minValue;
    }
    private String getMaxValye(List<GirthVO> girthVOS){

        String maxValue = null;
        for (GirthVO girthVO : girthVOS) {
            if(maxValue == null){
                maxValue = girthVO.getValue();
            }else{
                if(Float.parseFloat(girthVO.getValue()) > Float.parseFloat(maxValue)){
                    maxValue = girthVO.getValue();
                }
            }
        }
        maxValue = String.valueOf((Integer.parseInt(maxValue.substring(0, maxValue.length() - 2)) / 10 + 1) * 10);
        return maxValue;


    }


}
