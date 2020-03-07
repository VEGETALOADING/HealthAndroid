package com.tyut;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.HomeActivity;
import com.tyut.LoginActivity;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyProgressView;
import com.tyut.vo.NutritionVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowSchemaActivity extends AppCompatActivity implements View.OnClickListener {

    TextView bmi_tv;
    TextView result_tv;
    TextView carb_tv;
    TextView hot_tv;
    TextView protein_tv;
    TextView fat_tv;
    TextView standardWeight_tv;
    TextView score_tv;
    PieChart myPieChart;
    MyProgressView bmi_view;
    LinearLayout return_ll;
    Button update_btn;

    float standardWeight;
    int basic;
    int hot;
    int carb;
    int fat;
    int protein;
    String description;
    float score;
    private static final int TESTRESULT_WAHT = 0;
    private static final int SHOWSCHEMAACTIVITY = 1;

    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    result_tv.setText(String.valueOf(msg.obj));
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showschema);
        bmi_tv = findViewById(R.id.bmi_tv);
        result_tv = findViewById(R.id.result_tv);
        carb_tv = findViewById(R.id.carb_tv);
        protein_tv = findViewById(R.id.protein_tv);
        fat_tv = findViewById(R.id.fat_tv);
        myPieChart = findViewById(R.id.myPieChart);
        bmi_view = findViewById(R.id.bmi_view);
        hot_tv = findViewById(R.id.hot_tv);
        score_tv = findViewById(R.id.score_tv);
        return_ll = findViewById(R.id.return_c);
        standardWeight_tv = findViewById(R.id.standardweight);
        update_btn = findViewById(R.id.update_btn);

        return_ll.setOnClickListener(this);
        update_btn.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        UserVO userVO = (UserVO) SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

        float bmi = StringUtil.getBMI(userVO.getWeight(), userVO.getHeight());
        bmi_tv.setText(bmi+"");
        bmi_view.setProgress(bmi);

        NutritionVO nutritionVO = StringUtil.getNutritionData(userVO);
        hot = nutritionVO.getHot();
        protein = nutritionVO.getProtein();
        fat = nutritionVO.getFat();
        carb = nutritionVO.getCarb();
        standardWeight = nutritionVO.getStandardWeight();

        /*int year = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(userVO.getBirthday().substring(0, 4));
        if(userVO.getGender() == 0){
            basic = (int)Math.round((450 + 3.1 * Float.parseFloat(userVO.getHeight()) + 9.2 * Float.parseFloat(weight) - 4.3 * year) * 1.3);
            standardWeight = StringUtil.keepDecimal((float) ((Float.parseFloat(userVO.getHeight()) - 70) * 0.6), 1);

        }else{
            basic = (int)Math.round((90 + 4.8 * Float.parseFloat(userVO.getHeight()) + 13.4 * Float.parseFloat(weight) -5.7 * year) * 1.3);
            standardWeight = StringUtil.keepDecimal((float) ((Float.parseFloat(userVO.getHeight()) - 80) * 0.7), 1);

        }
        if(userVO.getGoal() == 1){
            hot = basic;
            protein = Math.round(Float.parseFloat(weight) * 2 * 1.3f);
            fat = (int) Math.round(hot * 0.2 / 9);
            carb = (int) Math.round((hot * 0.8 - protein * 4) / 4);
        }  else if(userVO.getGoal() == 0){

            hot = basic - 200;
            protein = Math.round(Float.parseFloat(weight) * 2 * 1.4f);
            fat = (int) Math.round(hot * 0.2 / 9);
            carb = (int) Math.round((hot * 0.8 - protein * 4) / 4);

        } else if(userVO.getGoal() == 2){

            hot = basic + 200;
            protein = Math.round(Float.parseFloat(weight) * 2 * 1.5f);
            fat = (int) Math.round(hot * 0.2 / 9);
            carb = (int) Math.round((hot * 0.8 - protein * 4) / 4);
        }*/
        score = 90 -(float) StringUtil.positive(standardWeight - Float.parseFloat(userVO.getWeight()));
        hot_tv.setText(hot+"");
        protein_tv.setText(protein+"");
        fat_tv.setText(fat+"");
        carb_tv.setText(carb+"");
        score_tv.setText(score+"/100");
        standardWeight_tv.setText(StringUtil.keepDecimal((float) (standardWeight * 0.9), 1) +"kg----"+ StringUtil.keepDecimal((float) (standardWeight * 1.1), 1)+"kg");


        initPieChart(myPieChart, carb, fat, protein);
        OkHttpUtils.get("http://192.168.1.9:8080/portal/healthtest/select.do?bmi="+bmi,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);

                        Gson gson = new Gson();
                        ServerResponse<String> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<String>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= TESTRESULT_WAHT;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(ShowSchemaActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });


    }

    private void initPieChart(PieChart pieChart, float carb, float fat, float protein){
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_c:
                Intent intent = new Intent(ShowSchemaActivity.this, HomeActivity.class);
                intent.putExtra("src", SHOWSCHEMAACTIVITY);
                ShowSchemaActivity.this.startActivity(intent);
                break;
            case R.id.update_btn:
                finish();
                break;
        }
    }
}
