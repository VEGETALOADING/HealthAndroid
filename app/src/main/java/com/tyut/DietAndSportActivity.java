package com.tyut;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.tu.circlelibrary.CirclePercentBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.CircleProgressBar;
import com.tyut.view.MyProgressView;
import com.tyut.vo.GirthVO;
import com.tyut.vo.HotVO;
import com.tyut.vo.NutritionVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import org.w3c.dom.Text;

import java.util.List;

public class DietAndSportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView return_iv;
    ImageView lastDay;
    ImageView nextDay;
    TextView day;
    TextView intake_tv;
    TextView consume_tv;
    TextView remanentHot_tv;
    TextView needHot_tv;

    CircleProgressBar hotBar;

    TextView proteinNeed;
    TextView proteinConsumed;
    TextView fatNeed;
    TextView fatConsumed;
    TextView carbNeed;
    TextView carbConsumed;

    Integer hotNeed = 0;

    private static final int HOTVO = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    final HotVO hotVO = (HotVO) msg.obj;
                    proteinConsumed.setText(hotVO.getProteinConsumed()+"");
                    carbConsumed.setText(hotVO.getCarbConsumed()+"");
                    fatConsumed.setText(hotVO.getFatConsumed()+"");
                    Integer hotIntake = hotVO.getBreakfastHot()+hotVO.getLunchHot()+hotVO.getDinnerHot();
                    intake_tv.setText(hotIntake+"");
                    consume_tv.setText(hotVO.getSportHot()+"");
                    remanentHot_tv.setText((hotNeed - hotIntake)+"");

                   // float percent = StringUtil.keepDecimal((float)hotIntake / (float)hotNeed, 1) * 100;

                    hotBar.setMaxStepNum(hotNeed);
                    hotBar.update(hotIntake,1000);
                    //hotBar.setPercentData(percent, new DecelerateInterpolator());
                    break;


            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietandsport);
        return_iv = findViewById(R.id.return_k);
        lastDay = findViewById(R.id.lastday_iv);
        nextDay = findViewById(R.id.nextday_iv);
        day = findViewById(R.id.day_tv);
        intake_tv = findViewById(R.id.intake_tv);
        consume_tv = findViewById(R.id.consume_tv);
        remanentHot_tv = findViewById(R.id.remanentHot_das);
        needHot_tv = findViewById(R.id.needHot_tv);
        proteinNeed = findViewById(R.id.proteinNeed);
        proteinConsumed = findViewById(R.id.proteinConsumed);
        fatNeed = findViewById(R.id.fatNeed);
        fatConsumed = findViewById(R.id.fatConsumed);
        carbNeed = findViewById(R.id.carbNeed);
        carbConsumed = findViewById(R.id.carbConsumed);

        hotBar = findViewById(R.id.hotCircleBar);
        hotBar.setBarColor(R.color.green_light);
        hotBar.setCircleWidth(40f);

        return_iv.setOnClickListener(this);
        lastDay.setOnClickListener(this);
        nextDay.setOnClickListener(this);
        day.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

        NutritionVO nutritionVO = StringUtil.getNutritionData(userVO);
        carbNeed.setText(nutritionVO.getCarb()+"");
        proteinNeed.setText(nutritionVO.getProtein()+"");
        fatNeed.setText(nutritionVO.getFat()+"");
        hotNeed = nutritionVO.getHot();
        needHot_tv.setText(hotNeed+"");

        OkHttpUtils.get("http://192.168.1.4:8080/portal/hot/select.do?userId="+userVO.getId()+"&date="+ StringUtil.getCurrentDate("yyyy-MM-dd"),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<HotVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<HotVO>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= HOTVO;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(DietAndSportActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.return_k:
                finish();
                break;
            case R.id.lastday_iv:

                break;
            case R.id.nextday_iv:

                break;
            case R.id.day_tv:

                break;
        }

    }
}
