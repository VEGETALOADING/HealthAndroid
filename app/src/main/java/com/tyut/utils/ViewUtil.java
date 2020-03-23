package com.tyut.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.NavUtils;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewUtil {

    //背景变亮变暗
    public static void changeAlpha(final Handler mHandler, int x){
        if(x == 0){//背景变暗
            final int[] alpha = {0};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha[0] < 127) {
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        alpha[0] += 1; //每次加1，逐渐变暗
                        msg.obj = alpha[0];
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }else if(x == 1){
            final int[] alpha = {127};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha[0] > 0) {
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        alpha[0] -= 1; //每次加1，逐渐变暗
                        msg.obj = alpha[0];
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();

        }
    }

    //键盘自动弹出
    public static void showSoftInputFromWindow(Activity context, EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    //弹出键盘可用！！
    public static void showSoft(final EditText editText){
        Handler handle=new Handler();
        handle.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager=(InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, 0);
            }
        }, 0);
    }




    public static File bitmap2File(Bitmap bm, File file){

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void initCFPPieChart(PieChart pieChart, float carb, float fat, float protein){
        pieChart.setNoDataText("老哥，我还没吃饭呢，快给我数据");

        float carb_percent = carb / (carb + fat + protein) * 100;
        float pro_percent = protein / (carb + fat + protein) * 100;
        float fat_percent = fat / (carb + fat + protein) * 100;
        PieEntry pieEntry1 = new PieEntry(carb_percent,"碳水化合物");
        PieEntry pieEntry2 = new PieEntry(fat_percent,"脂肪");
        PieEntry pieEntry3 = new PieEntry(pro_percent,"蛋白质");



        List<PieEntry> list = new ArrayList<>();
        list.add(pieEntry1);
        list.add(pieEntry2);
        list.add(pieEntry3);
        PieDataSet pieDataSet = new PieDataSet(list, "");

        //一般有多少项数据，就配置多少个颜色的，少的话会复用最后一个颜色，多的话无大碍
        pieDataSet.addColor(Color.parseColor("#feb64d"));
        pieDataSet.addColor(Color.parseColor("#ff7c7c"));
        pieDataSet.addColor(Color.parseColor("#9287e7"));


        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        //显示值格式化，这里使用Api,添加百分号
        pieData.setValueFormatter(new PercentFormatter());

        pieChart.setDrawEntryLabels(false);
        //设置值得颜色

        pieData.setValueTextColor(Color.parseColor("#FFFFFF"));
        //设置值得大小
        pieData.setValueTextSize(10f);

        Description description = new Description();

        description.setText("");
        //把右下边的Description label 去掉，同学也可以设置成饼图说明
        pieChart.setDescription(description);

        //去掉中心圆，此时中心圆半透明
        pieChart.setHoleRadius(0f);
        //去掉半透明
        pieChart.setTransparentCircleAlpha(0);

        //设置动画
        pieChart.animateX(2000, Easing.EasingOption.EaseInOutQuad);
        //图例设置
        Legend legend = pieChart.getLegend();

        legend.setEnabled(false);//是否显示图例

        pieChart.invalidate();
    }
}
