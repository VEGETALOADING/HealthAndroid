package com.tyut.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.view.CurveView;
import com.tyut.vo.GirthListVO;
import com.tyut.vo.GirthVO;
import com.tyut.vo.ServerResponse;
import com.tyut.widget.GirthPopUpWindow;

import java.util.ArrayList;
import java.util.List;

public class GirthCurveFragment extends Fragment implements View.OnClickListener {


    LineChart yaoChart;
    RelativeLayout noYaoData;
    LineChart datuiChart;
    RelativeLayout noDatuiData;
    LineChart xiaotuiChart;
    RelativeLayout noXiaotuiData;
    LineChart tunChart;
    RelativeLayout noTunData;
    LineChart xiongChart;
    RelativeLayout noXiongData;
    LineChart shoubiChart;
    RelativeLayout noShoubiData;

    Button yaoBtn;
    Button datuiBtn;
    Button xiaotuiBtn;
    Button tunBtn;
    Button xiongBtn;
    Button shoubiBtn;


    GirthListVO girthListVO = new GirthListVO();
    private static final int GIRTHLISTVO = 0;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    if(girthListVO.getDatuiList().size() == 0){
                        datuiChart.setVisibility(View.GONE);
                        noDatuiData.setVisibility(View.VISIBLE);
                    }else{
                        datuiChart.setVisibility(View.VISIBLE);
                        noDatuiData.setVisibility(View.GONE);
                        int index = 0;
                        List<Entry> yList = new ArrayList<>();
                        List<String> xList = new ArrayList<>();
                        for (GirthVO girthVO : girthListVO.getDatuiList()) {
                            xList.add(girthVO.getCreateTime());
                            yList.add(new Entry(index++, Float.parseFloat(girthVO.getValue())));
                        }
                        Float maxY = Float.parseFloat(getMaxValye(girthListVO.getDatuiList()));
                        Float minY =Float.parseFloat(getMinValue(girthListVO.getDatuiList()));
                        CurveView curveView = new CurveView(getContext(), yList, xList, maxY, minY);
                        curveView.drawTheChart();
                        curveView.drawTheChartByMPAndroid(datuiChart);

                    }
                    if(girthListVO.getYaoList().size() == 0){
                        yaoChart.setVisibility(View.GONE);
                        noYaoData.setVisibility(View.VISIBLE);
                    }else{
                        yaoChart.setVisibility(View.VISIBLE);
                        noYaoData.setVisibility(View.GONE);
                        int index = 0;
                        List<Entry> yList = new ArrayList<>();
                        List<String> xList = new ArrayList<>();
                        for (GirthVO girthVO : girthListVO.getYaoList()) {
                            xList.add(girthVO.getCreateTime());
                            yList.add(new Entry(index++, Float.parseFloat(girthVO.getValue())));
                        }
                        Float maxY = Float.parseFloat(getMaxValye(girthListVO.getYaoList()));
                        Float minY =Float.parseFloat(getMinValue(girthListVO.getYaoList()));
                        CurveView curveView1 = new CurveView(getContext(), yList, xList, maxY, minY);
                        curveView1.drawTheChart();
                        curveView1.drawTheChartByMPAndroid(yaoChart);
                    }

                    if(girthListVO.getXiaotuiList().size() == 0){
                        xiaotuiChart.setVisibility(View.GONE);
                        noXiaotuiData.setVisibility(View.VISIBLE);
                    }else{
                        xiaotuiChart.setVisibility(View.VISIBLE);
                        noXiaotuiData.setVisibility(View.GONE);
                        int index = 0;
                        List<Entry> yList = new ArrayList<>();
                        List<String> xList = new ArrayList<>();
                        for (GirthVO girthVO : girthListVO.getXiaotuiList()) {
                            xList.add(girthVO.getCreateTime());
                            yList.add(new Entry(index++, Float.parseFloat(girthVO.getValue())));
                        }
                        Float maxY = Float.parseFloat(getMaxValye(girthListVO.getXiaotuiList()));
                        Float minY =Float.parseFloat(getMinValue(girthListVO.getXiaotuiList()));
                        CurveView curveView = new CurveView(getContext(), yList, xList, maxY, minY);
                        curveView.drawTheChart();
                        curveView.drawTheChartByMPAndroid(xiaotuiChart);
                    }
                    if(girthListVO.getTunList().size() == 0){
                        tunChart.setVisibility(View.GONE);
                        noTunData.setVisibility(View.VISIBLE);
                    }else{
                        tunChart.setVisibility(View.VISIBLE);
                        noTunData.setVisibility(View.GONE);
                        int index = 0;
                        List<Entry> yList = new ArrayList<>();
                        List<String> xList = new ArrayList<>();
                        for (GirthVO girthVO : girthListVO.getTunList()) {
                            xList.add(girthVO.getCreateTime());
                            yList.add(new Entry(index++, Float.parseFloat(girthVO.getValue())));
                        }
                        Float maxY = Float.parseFloat(getMaxValye(girthListVO.getTunList()));
                        Float minY =Float.parseFloat(getMinValue(girthListVO.getTunList()));
                        CurveView curveView = new CurveView(getContext(), yList, xList, maxY, minY);
                        curveView.drawTheChart();
                        curveView.drawTheChartByMPAndroid(tunChart);
                    }
                    if(girthListVO.getXiongList().size() == 0){
                        xiongChart.setVisibility(View.GONE);
                        noXiongData.setVisibility(View.VISIBLE);
                    }else{
                        xiongChart.setVisibility(View.VISIBLE);
                        noXiongData.setVisibility(View.GONE);
                        int index = 0;
                        List<Entry> yList = new ArrayList<>();
                        List<String> xList = new ArrayList<>();
                        for (GirthVO girthVO : girthListVO.getXiongList()) {
                            xList.add(girthVO.getCreateTime());
                            yList.add(new Entry(index++, Float.parseFloat(girthVO.getValue())));
                        }
                        Float maxY = Float.parseFloat(getMaxValye(girthListVO.getXiongList()));
                        Float minY =Float.parseFloat(getMinValue(girthListVO.getXiongList()));
                        CurveView curveView = new CurveView(getContext(), yList, xList, maxY, minY);
                        curveView.drawTheChart();
                        curveView.drawTheChartByMPAndroid(xiongChart);
                    }
                    if(girthListVO.getShoubiList().size() == 0){
                        shoubiChart.setVisibility(View.GONE);
                        noShoubiData.setVisibility(View.VISIBLE);
                    }else{
                        shoubiChart.setVisibility(View.VISIBLE);
                        noShoubiData.setVisibility(View.GONE);
                        int index = 0;
                        List<Entry> yList = new ArrayList<>();
                        List<String> xList = new ArrayList<>();
                        for (GirthVO girthVO : girthListVO.getShoubiList()) {
                            xList.add(girthVO.getCreateTime());
                            yList.add(new Entry(index++, Float.parseFloat(girthVO.getValue())));
                        }
                        Float maxY = Float.parseFloat(getMaxValye(girthListVO.getShoubiList()));
                        Float minY =Float.parseFloat(getMinValue(girthListVO.getShoubiList()));
                        CurveView curveView = new CurveView(getContext(), yList, xList, maxY, minY);
                        curveView.drawTheChart();
                        curveView.drawTheChartByMPAndroid(shoubiChart);
                    }


                    break;

            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_girthcurve, container, false);
        yaoChart = view.findViewById(R.id.curve_yaowei);
        noYaoData = view.findViewById(R.id.noyaoweidata_rl);
        datuiChart = view.findViewById(R.id.curve_datuiwei);
        noDatuiData = view.findViewById(R.id.nodatuiweidate_rl);
        xiaotuiChart = view.findViewById(R.id.curve_xiaotuiwei);
        noXiaotuiData = view.findViewById(R.id.noxiaotuiweidate_rl);
        tunChart = view.findViewById(R.id.curve_tunwei);
        noTunData = view.findViewById(R.id.notunweidate_rl);
        xiongChart = view.findViewById(R.id.curve_xiongwei);
        noXiongData = view.findViewById(R.id.noxiongweidate_rl);
        shoubiChart = view.findViewById(R.id.curve_shoubiwei);
        noShoubiData = view.findViewById(R.id.noshoubiweidate_rl);

        yaoBtn = view.findViewById(R.id.recordyaowei_btn);
        datuiBtn = view.findViewById(R.id.recorddatuiwei_btn);
        xiaotuiBtn = view.findViewById(R.id.recordxiaotuiwei_btn);
        tunBtn = view.findViewById(R.id.recordtunwei_btn);
        xiongBtn = view.findViewById(R.id.recordxiongwei_btn);
        shoubiBtn = view.findViewById(R.id.recordbiwei_btn);
        yaoBtn.setOnClickListener(this);
        datuiBtn.setOnClickListener(this);
        xiaotuiBtn.setOnClickListener(this);
        tunBtn.setOnClickListener(this);
        xiongBtn.setOnClickListener(this);
        shoubiBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Integer userid =  SPSingleton.get(getActivity(), SPSingleton.USERINFO).readInt("userid");

        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/girth/list.do?userid="+userid,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<GirthListVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<GirthListVO>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            girthListVO = serverResponse.getData();
                            Message message = new Message();
                            message.what= GIRTHLISTVO;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recordyaowei_btn:
                showPopUpWindow(0);
                break;
            case R.id.recorddatuiwei_btn:
                showPopUpWindow(1);
                break;
            case R.id.recordtunwei_btn:
                showPopUpWindow(3);
                break;
            case R.id.recordbiwei_btn:
                showPopUpWindow(5);
                break;
            case R.id.recordxiaotuiwei_btn:
                showPopUpWindow(2);
                break;
            case R.id.recordxiongwei_btn:
                showPopUpWindow(4);
                break;
        }


    }

    private void showPopUpWindow(Integer type){
        final GirthPopUpWindow girthPopUpWindow = new GirthPopUpWindow(getActivity(), type, null, null);
        girthPopUpWindow.setCancel(new GirthPopUpWindow.IOnCancelListener() {
            @Override
            public void onCancel(GirthPopUpWindow dialog) {
                girthPopUpWindow.getGirthPopUpWindow().dismiss();
            }
        }).setConfirm(new GirthPopUpWindow.IOnConfirmListener() {
            @Override
            public void onConfirm(GirthPopUpWindow dialog) {
                //Toast.makeText(getActivity(), "onconfirm", Toast.LENGTH_SHORT).show();
                onResume();
            }
        }).showFoodPopWindow();
    }
}
