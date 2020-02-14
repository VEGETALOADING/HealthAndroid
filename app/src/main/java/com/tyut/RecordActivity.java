package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.FollowingListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyProgressView;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.GirthVO;
import com.tyut.vo.HotVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.util.List;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView userPic;
    TextView userName;
    Button schema_btn;
    TextView remanentHot;
    TextView latestWeight;
    TextView latestGirth;
    Button openStep_btn;
    TextView habitProgress_tv;
    TextView trainTime_tv;

    LinearLayout dietAndSport_ll;
    LinearLayout weight_ll;
    LinearLayout girth_ll;
    LinearLayout step_ll;
    LinearLayout habit_ll;
    LinearLayout train_ll;
    LinearLayout return_ll;

    Integer needHot = null;

    private static final int GIRTHLIST_NULL = 1;
    private static final int GIRTHLIST_NOTNULL = 0;
    private static final int HOTVO = 2;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    final List<GirthVO> list = (List<GirthVO>) msg.obj;
                    latestGirth.setText(list.get(0).getValue());
                    break;
                case 1:
                    latestGirth.setText("0");
                case 2:
                    HotVO hotVO = (HotVO) msg.obj;
                    Integer hotIntake = hotVO.getBreakfastHot()+hotVO.getLunchHot()+hotVO.getDinnerHot();
                    Integer hotConsume = hotVO.getSportHot();
                    Integer remanent = needHot - hotIntake + hotConsume;
                    remanentHot.setText(remanent > 0 ? (remanent+"") : "0");

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        userPic = findViewById(R.id.record_userpic);
        userName = findViewById(R.id.record_username);
        schema_btn = findViewById(R.id.record_schema);
        remanentHot = findViewById(R.id.remanet_hot);
        latestWeight = findViewById(R.id.record_latestweight_tv);
        latestGirth = findViewById(R.id.record_latestgirth_tv);
        openStep_btn = findViewById(R.id.step_btn);
        habitProgress_tv = findViewById(R.id.habitprogress_tv);
        trainTime_tv = findViewById(R.id.traintime_tv);

        dietAndSport_ll = findViewById(R.id.record_dietandsport_ll);
        girth_ll = findViewById(R.id.record_girth_ll);
        weight_ll = findViewById(R.id.record_weight_ll);
        step_ll = findViewById(R.id.record_step_ll);
        habit_ll = findViewById(R.id.record_habit_ll);
        train_ll = findViewById(R.id.record_train_ll);
        return_ll = findViewById(R.id.return_j);

        dietAndSport_ll.setOnClickListener(this);
        girth_ll.setOnClickListener(this);
        weight_ll.setOnClickListener(this);
        habit_ll.setOnClickListener(this);
        train_ll.setOnClickListener(this);
        step_ll.setOnClickListener(this);
        return_ll.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化页面
        UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);
        Glide.with(this).load("http://192.168.1.4:8080/userpic/" + userVO.getUserpic()).into(userPic);
        userName.setText(userVO.getUsername());
        latestWeight .setText(userVO.getWeight());
        needHot = StringUtil.getNutritionData(userVO).getHot();


        OkHttpUtils.get("http://192.168.1.4:8080/portal/girth/select.do?userid="+userVO.getId()+"&type=0",
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<GirthVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<GirthVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            if(serverResponse.getData().size() > 0){
                                Message message = new Message();
                                message.what= GIRTHLIST_NOTNULL;
                                message.obj = serverResponse.getData();
                                mHandler.sendMessage(message);
                            }else {
                                Message message = new Message();
                                message.what= GIRTHLIST_NULL;
                                mHandler.sendMessage(message);
                            }
                        }else{
                            Looper.prepare();
                            Toast.makeText(RecordActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );
        OkHttpUtils.get("http://192.168.1.4:8080/portal/hot/select.do?userId="+userVO.getId()+"&date="+StringUtil.getCurrentDate("yyyy-MM-dd"),
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
                            Toast.makeText(RecordActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_dietandsport_ll:
                Intent intent = new Intent(RecordActivity.this, DietAndSportActivity.class);
                RecordActivity.this.startActivity(intent);
                break;
            case R.id.record_weight_ll:

                break;
            case R.id.record_step_ll:

                break;
            case R.id.record_train_ll:

                break;
            case R.id.record_girth_ll:

                break;
            case R.id.record_habit_ll:

                break;
            case R.id.return_j:
                finish();
                break;
        }
    }
}
