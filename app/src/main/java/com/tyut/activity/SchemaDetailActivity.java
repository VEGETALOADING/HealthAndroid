package com.tyut.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.MSchema;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class SchemaDetailActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout return_ll;
    ImageView schemaPic_iv;
    TextView content_tv;
    PieChart mPieChart;
    TextView carb_tv;
    TextView protein_tv;
    TextView fat_tv;
    TextView cal_tv;
    Button switchSchema_btn;

    private MSchema schema;
    private UserVO userVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemadetail);
        schemaPic_iv = findViewById(R.id.schemaMainPic_iv);
        content_tv = findViewById(R.id.schemaContent_tv);
        mPieChart = findViewById(R.id.myPieChart);
        carb_tv = findViewById(R.id.carb_tv);
        protein_tv = findViewById(R.id.protein_tv);
        fat_tv = findViewById(R.id.fat_tv);
        cal_tv = findViewById(R.id.cal_tv);
        switchSchema_btn = findViewById(R.id.switchSchema);
        return_ll = findViewById(R.id.return_ll);

        switchSchema_btn.setOnClickListener(this);
        return_ll.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        userVO = (UserVO) SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        schema = (MSchema) getIntent().getSerializableExtra("schema");

        //Q:只能取出来比较或者equals？
        if(userVO.getSchemaid().equals(schema.getId())){
            switchSchema_btn.setVisibility(View.GONE);
        }else {
            switchSchema_btn.setVisibility(View.VISIBLE);
        }

        Glide.with(this)
                .load("http://"+this.getString(R.string.url)+":8080/schemapic/" + schema.getMainpic())
                .placeholder(R.mipmap.ic_launcher)
                .into(schemaPic_iv);
        content_tv.setText(schema.getContent());
        carb_tv.setText(schema.getCarbs()+"");
        protein_tv.setText(schema.getProtein()+"");
        fat_tv.setText(schema.getFat()+"");
        cal_tv.setText(schema.getCalories()+"");

        ViewUtil.initCFPPieChart(mPieChart, schema.getCarbs(), schema.getFat(), schema.getProtein());


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.return_ll:
                finish();
                break;
            case R.id.switchSchema:

                OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/user/update.do?schemaid="+schema.getId()+"&id="+userVO.getId(),
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    SPSingleton util =  SPSingleton.get(SchemaDetailActivity.this,SPSingleton.USERINFO);
                                    util.delete("user");
                                    util.putString("user", gson.toJson(serverResponse.getData()));
                                    Looper.prepare();
                                    Toast.makeText(SchemaDetailActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    finish();
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(SchemaDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
        }

    }

}

