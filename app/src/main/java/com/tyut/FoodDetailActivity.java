package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.android.tu.circlelibrary.CirclePercentBar;
import com.bumptech.glide.Glide;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.FoodListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.FoodVO;
import com.tyut.vo.Mysport;
import com.tyut.vo.ServerResponse;
import com.tyut.widget.RecordFoodDialog;
import com.tyut.widget.SportTimeDialog;


import java.util.Date;
import java.util.List;

public class FoodDetailActivity extends AppCompatActivity implements View.OnClickListener {

    CirclePercentBar protein_bar;
    CirclePercentBar fat_bar;
    CirclePercentBar carb_bar;

    TextView name_tv;
    TextView calories_tv;
    TextView fat_tv;
    TextView protein_tv;
    TextView carbs_tv;
    TextView quantity1_tv;
    TextView quantity2_tv;
    TextView unit1_tv;
    TextView unit2_tv;
    TextView hotunit_tv;
    TextView favorite_tv;
    ImageView favorite_iv;
    ImageView pic_iv;
    Button qianka_btn;
    Button qianjiao_btn;
    LinearLayout return_ll;
    LinearLayout favorite_ll;
    LinearLayout record_ll;

    Intent intent;
    Integer foodId;
    String name;
    Integer quantity;
    String unit;
    Integer calories;
    String pic;
    String carbs;
    String protein;
    String fat;

    Integer userid;
    private static final int ADDFAVORITE = 0;
    private static final int CANCELFAVORITE = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){

                case 0:
                    favorite_iv.setImageResource(R.mipmap.icon_fav_selected);
                    favorite_tv.setText("已收藏");
                    favorite_tv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 1:
                    favorite_iv.setImageResource(R.mipmap.icon_fav_unselected);
                    favorite_tv.setText("收藏");
                    favorite_tv.setTextColor(getResources().getColor(R.color.nav_text_default));
                    break;


            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooddetail);
        protein_bar = findViewById(R.id.protein_circleBar);
        fat_bar = findViewById(R.id.fat_circleBar);
        carb_bar = findViewById(R.id.carb_circleBar);
        name_tv = findViewById(R.id.foodname_detail);
        calories_tv = findViewById(R.id.calories_detail);
        fat_tv = findViewById(R.id.fat_detail);
        protein_tv = findViewById(R.id.protein_detail);
        carbs_tv = findViewById(R.id.carbs_detail);
        quantity1_tv = findViewById(R.id.quantity1_detail);
        quantity2_tv = findViewById(R.id.quantity2_detail);
        unit1_tv = findViewById(R.id.unit1_detail);
        unit2_tv = findViewById(R.id.unit2_detail);
        pic_iv = findViewById(R.id.foodpic_detail);
        qianka_btn = findViewById(R.id.qianka_btn);
        qianjiao_btn = findViewById(R.id.qianjiao_btn);
        return_ll = findViewById(R.id.return_g);
        hotunit_tv = findViewById(R.id.hotunit_detail);
        favorite_iv = findViewById(R.id.favoritefood_detail_img);
        favorite_tv = findViewById(R.id.favoritefood_detail_tv);
        favorite_ll = findViewById(R.id.favoritefood_detail_ll);
        record_ll = findViewById(R.id.record_fooddetail_ll);

        qianka_btn.setOnClickListener(this);
        qianjiao_btn.setOnClickListener(this);
        return_ll.setOnClickListener(this);
        favorite_ll.setOnClickListener(this);
        record_ll.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userid = SharedPreferencesUtil.getInstance(FoodDetailActivity.this).readInt("userid");
        intent = getIntent();
        foodId = intent.getIntExtra("id", 0);
        name = intent.getStringExtra("name");
        quantity = intent.getIntExtra("quantity", 0);
        unit = intent.getStringExtra("unit");
        calories = intent.getIntExtra("calories", 0);
        pic = intent.getStringExtra("pic");
        carbs = intent.getStringExtra("carbs");
        protein = intent.getStringExtra("protein");
        fat = intent.getStringExtra("fat");

        name_tv.setText(name);
        quantity1_tv.setText(quantity+"");
        quantity2_tv.setText(quantity+"");
        unit1_tv.setText(unit);
        unit2_tv.setText(unit);
        calories_tv.setText(calories+"");
        Glide.with(this).load("http://192.168.1.4:8080/foodpic/" + pic).into(pic_iv);
        carbs_tv.setText(carbs);
        protein_tv.setText(protein);
        fat_tv.setText(fat);


        float protein_Percent = StringUtil.keepDecimal((Float.parseFloat(protein) / (Float.parseFloat(protein)+Float.parseFloat(carbs)+Float.parseFloat(fat))*100),1);
        float fat_Percent = StringUtil.keepDecimal((Float.parseFloat(fat) / (Float.parseFloat(protein)+Float.parseFloat(carbs)+Float.parseFloat(fat))*100),1);
        float carbs_Percent = StringUtil.keepDecimal((Float.parseFloat(carbs) / (Float.parseFloat(protein)+Float.parseFloat(carbs)+Float.parseFloat(fat))*100),1);

        protein_bar.setPercentData(protein_Percent, new DecelerateInterpolator());
        fat_bar.setPercentData(fat_Percent, new DecelerateInterpolator());
        carb_bar.setPercentData(carbs_Percent, new DecelerateInterpolator());

        OkHttpUtils.get("http://192.168.1.4:8080/portal/favorite/find.do?userid="+ userid +"&category="+0+"&objectid="+foodId,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            if(serverResponse.getData().size() == 0){

                                Message message = new Message();
                                message.what= CANCELFAVORITE;
                                mHandler.sendMessage(message);
                            }else {
                                Message message = new Message();
                                message.what= ADDFAVORITE;
                                mHandler.sendMessage(message);
                            }


                        }else{
                            Looper.prepare();
                            Toast.makeText(FoodDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }

                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id. return_g:
                this.finish();
                break;
            case R.id.qianka_btn:
                qianka_btn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                qianjiao_btn.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                calories_tv.setText(calories+"");
                hotunit_tv.setText("千卡");
                break;
            case R.id.qianjiao_btn:
                qianka_btn.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                qianjiao_btn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                calories_tv.setText( Math.round(calories * 4.19)+"");
                hotunit_tv.setText("千焦");
                break;
            case R.id.favoritefood_detail_ll:
                OkHttpUtils.get("http://192.168.1.4:8080/portal/favorite/addorcancel.do?userid="+ userid +"&category="+0+"&objectid="+foodId,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                if(serverResponse.getStatus() == 0){
                                    if((Double) serverResponse.getData() == 45){

                                        Message message = new Message();
                                        message.what= ADDFAVORITE;
                                        mHandler.sendMessage(message);

                                    }else if((Double) serverResponse.getData() == 47){

                                        Message message = new Message();
                                        message.what= CANCELFAVORITE;
                                        mHandler.sendMessage(message);

                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(FoodDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }

                                }else{
                                    Looper.prepare();
                                    Toast.makeText(FoodDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }

                            }
                        }
                );
            case R.id.record_fooddetail_ll:
                RecordFoodDialog dialog = new RecordFoodDialog(FoodDetailActivity.this);
                dialog.setFoodName(name)
                        .setFoodUnit(unit)
                        .setFoodQuantity(quantity+"")
                        .setFoodPic(pic)
                        .setFoodCal(calories+"")
                        .setCancel(new RecordFoodDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(RecordFoodDialog dialog) {

                            }
                        })
                        .setConfirm(new RecordFoodDialog.IOnConfirmListener() {
                            @Override
                            public void onConfirm(RecordFoodDialog dialog) {


                            }
                        }).show();
                break;


        }
    }
}
