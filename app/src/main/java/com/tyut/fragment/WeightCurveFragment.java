package com.tyut.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.Weight;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class WeightCurveFragment extends Fragment {


    LineChart lineChart_weight;
    RelativeLayout noWeightData;
    RelativeLayout noBodyFatData;

    List<Entry> yList = new ArrayList<>();

    List<String> xList = new ArrayList<>();
    List<Integer> integerList = new ArrayList<>();
    List<Weight> weightList = new ArrayList<>();

    private static final int WEIGHTLIST_NULL = 0;
    private static final int WEIGHTLIST_NOTNULL = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    lineChart_weight.setVisibility(View.GONE);
                    noWeightData.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    lineChart_weight.setVisibility(View.VISIBLE);
                    noWeightData.setVisibility(View.GONE);
                    int index = 0;
                    weightList = (List<Weight>) msg.obj;
                    for (Weight weight : weightList) {
                        xList.add(weight.getCreateTime());
                        yList.add(new Entry(index++, Float.parseFloat(weight.getWeight())));
                    }
                    drawTheChart();
                    drawTheChartByMPAndroid();
                    break;


            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weightcurve, container, false);
        lineChart_weight = view.findViewById(R.id.curve_weight);
        noWeightData = view.findViewById(R.id.noweightdata_rl);
        noBodyFatData = view.findViewById(R.id.nobodyfatdate_rl);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Integer userid = SharedPreferencesUtil.getInstance(getActivity()).readInt("userid");

        OkHttpUtils.get("http://192.168.1.9:8080/portal/weight/list.do?userid="+userid,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<Weight>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Weight>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            weightList = serverResponse.getData();
                            if(weightList.size() == 0){

                                Message message = new Message();
                                message.what= WEIGHTLIST_NULL;
                                mHandler.sendMessage(message);
                            }else {
                                Message message = new Message();
                                message.what= WEIGHTLIST_NOTNULL;
                                message.obj = serverResponse.getData();
                                mHandler.sendMessage(message);
                            }

                        }else {
                            Looper.prepare();
                            Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );
       /* xList.add("a");
        xList.add("b");
        xList.add("c");
        xList.add("d");
        xList.add("e");
        yList.add(new Entry(0, 50));
        yList.add(new Entry(1, 60));
        yList.add(new Entry(2, 65));
        yList.add(new Entry(3, 55));
        yList.add(new Entry(4, 70));*/

    }

    private void drawTheChartByMPAndroid() {
        LineData lineData = getLineData();
        showChart(lineChart_weight, lineData, Color.rgb(137, 230, 81));
    }

    private void showChart(LineChart lineChart, LineData lineData, int color) {
        lineChart.setDrawBorders(false); //在折线图上添加边框
        lineChart.setDescription(null); //数据描述
        lineChart.setNoDataText("You need to provide data for the chart.");

        lineChart.setDrawGridBackground(false); //表格颜色
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); //表格的颜色，设置一个透明度

        lineChart.setTouchEnabled(true); //可点击

        lineChart.setDragEnabled(true);  //可拖拽
        lineChart.setScaleEnabled(true);  //可缩放

        lineChart.setPinchZoom(false);

        lineChart.setBackgroundColor(color); //设置背景颜色

        lineChart.setData(lineData);  //填充数据

        Legend mLegend = lineChart.getLegend(); //设置标示，就是那个一组y的value的

        mLegend.setForm(Legend.LegendForm.CIRCLE); //样式
        mLegend.setFormSize(6f); //字体
        mLegend.setTextColor(Color.WHITE); //颜色

        lineChart.setVisibleXRange(1, 6);   //x轴可显示的坐标范围
        XAxis xAxis = lineChart.getXAxis();  //x轴的标示
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x轴位置
        xAxis.setTextColor(Color.WHITE);    //字体的颜色
        xAxis.setTextSize(10f); //字体大小
        xAxis.setGridColor(Color.WHITE);//网格线颜色
        xAxis.setDrawGridLines(false); //不显示网格线

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                if(value <= xList.size()){
                    String data = xList.get((int)value);
                    return data;
                }
                return "";
            }
        });

        YAxis axisLeft = lineChart.getAxisLeft(); //y轴左边标示
        YAxis axisRight = lineChart.getAxisRight(); //y轴右边标示
        axisLeft.setTextColor(Color.WHITE); //字体颜色
        axisLeft.setTextSize(10f); //字体大小
        axisLeft.setAxisMaxValue(Float.parseFloat(getMaxWeight(weightList))); //最大值
        axisLeft.setAxisMinimum(Float.parseFloat(getMinWeight(weightList)));
        axisLeft.setLabelCount(5, true); //显示格数
        axisLeft.setGridColor(Color.WHITE); //网格线颜色

        axisRight.setDrawAxisLine(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawLabels(false);

        lineChart.animateX(2500);  //立即执行动画
    }

    private LineData getLineData() {

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yList, "体重统计");


        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setColor(Color.WHITE);// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setValueTextColor(Color.WHITE); //数值显示的颜色
        lineDataSet.setValueTextSize(15f);     //数值显示的大小

        /*ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSet); // 添加数据集合*/

        //创建lineData
        LineData lineData = new LineData(lineDataSet);
        return lineData;
    }

    private void setLists() {
        integerList.clear();
        for (int i = 1; i < 20; i++) {
            int value = ((int) (Math.random() * 100));
            integerList.add(value);
        }
    }
    public void drawTheChart() {
        XYMultipleSeriesRenderer mRenderer = getXYMulSeriesRenderer();

        XYSeriesRenderer renderer = getXYSeriesRenderer();

        mRenderer.addSeriesRenderer(renderer);

        setLists();

        XYMultipleSeriesDataset dataset =  getDataSet();

        GraphicalView chartView = ChartFactory.getLineChartView(getActivity(), dataset, mRenderer);

    }

    public XYSeriesRenderer getXYSeriesRenderer() {
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        //设置折线宽度
        renderer.setLineWidth(2);
        //设置折线颜色
        renderer.setColor(Color.GRAY);
        renderer.setDisplayBoundingPoints(true);
        //点的样式
        renderer.setPointStyle(PointStyle.CIRCLE);
        //设置点的大小
        renderer.setPointStrokeWidth(3);
        //设置数值显示的字体大小
        renderer.setChartValuesTextSize(40);
        //显示数值
        renderer.setDisplayChartValues(true);
        return renderer;
    }

    public XYMultipleSeriesDataset getDataSet() {
        XYMultipleSeriesDataset barDataset = new XYMultipleSeriesDataset();
        CategorySeries barSeries = new CategorySeries("测试");


        for (int i = 0; i < integerList.size(); i++) {
            barSeries.add(integerList.get(i));
        }

        barDataset.addSeries(barSeries.toXYSeries());
        return barDataset;
    }

    public XYMultipleSeriesRenderer getXYMulSeriesRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setMarginsColor(Color.argb(0x00, 0xF3, 0xF3, 0xF3));

        // 设置背景颜色
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);

        //设置Title的内容和大小
        renderer.setChartTitle("访问量统计");
        renderer.setChartTitleTextSize(50);

        //图表与四周的边距
        renderer.setMargins(new int[]{80, 80, 50, 50});

        //设置X,Y轴title的内容和大小
        renderer.setXTitle("日期");
        renderer.setYTitle("访问数");
        renderer.setAxisTitleTextSize(30);
        //renderer.setAxesColor(Color.WHITE);
        renderer.setLabelsColor(Color.BLACK);

        //图例文字的大小
        renderer.setLegendTextSize(20);

        // x、y轴上刻度颜色和大小
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setLabelsTextSize(20);
        renderer.setYLabelsPadding(30);

        // 设置X轴的最小数字和最大数字，由于我们的数据是从1开始，所以设置为0.5就可以在1之前让出一部分
        // 有兴趣的童鞋可以删除下面两行代码看一下效果
        renderer.setPanEnabled(false, false);

        //显示网格
        renderer.setShowGrid(true);

        //X,Y轴上的数字数量
        renderer.setXLabels(10);
        renderer.setYLabels(5);

        // 设置X轴的最小数字和最大数字
        renderer.setXAxisMin(1);
        renderer.setXAxisMax(20);
        // 设置Y轴的最小数字和最大数字
        renderer.setYAxisMin(30);
        renderer.setYAxisMax(80);

        // 设置渲染器显示缩放按钮
        renderer.setZoomButtonsVisible(true);
        // 设置渲染器允许放大缩小
        renderer.setZoomEnabled(true);
        // 消除锯齿
        renderer.setAntialiasing(true);

        // 刻度线与X轴坐标文字左侧对齐
        renderer.setXLabelsAlign(Paint.Align.LEFT);
        // Y轴与Y轴坐标文字左对齐
        renderer.setYLabelsAlign(Paint.Align.LEFT);

        // 允许左右拖动,但不允许上下拖动.
        renderer.setPanEnabled(true, true);

        return renderer;
    }

    private String getMinWeight(List<Weight> weights){
        String minWeight = null;
        for (Weight weight : weights) {
            if(minWeight == null){
                minWeight = weight.getWeight();
            }else{
                if(Float.parseFloat(weight.getWeight()) < Float.parseFloat(minWeight)){
                    minWeight = weight.getWeight();
                }
            }
        }
        minWeight = String.valueOf((Integer.parseInt(minWeight.substring(0, minWeight.length() - 2)) / 10) * 10);
        return minWeight;
    }
    private String getMaxWeight(List<Weight> weights){

        String maxWeight = null;
        for (Weight weight : weights) {
            if(maxWeight == null){
                maxWeight = weight.getWeight();
            }else{
                if(Float.parseFloat(weight.getWeight()) > Float.parseFloat(maxWeight)){
                    maxWeight = weight.getWeight();
                }
            }
        }
        maxWeight = String.valueOf((Integer.parseInt(maxWeight.substring(0, maxWeight.length() - 2)) / 10 + 1) * 10);
        return maxWeight;
    }
}
