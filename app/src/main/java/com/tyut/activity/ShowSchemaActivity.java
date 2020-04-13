package com.tyut.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.view.MyProgressView;
import com.tyut.vo.NutritionVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

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


        ViewUtil.initCFPPieChart(myPieChart, carb, fat, protein);
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/healthtest/select.do?bmi="+bmi,
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



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_c:
                /*Intent intent = new Intent(ShowSchemaActivity.this, HomeActivity.class);
                if(getIntent().getIntExtra("homeFragment", 0) == 1) {
                    intent.putExtra("homeFragment", 1);
                    ShowSchemaActivity.this.startActivity(intent);
                }else if(getIntent().getIntExtra("homeFragment", 0) == 0){
                    intent.putExtra("homeFragment", 0);
                    ShowSchemaActivity.this.startActivity(intent);
                }else{
                    this.finish();
                }*/
                this.finish();
                break;
            case R.id.update_btn:
                finish();
                break;
        }
    }
}
