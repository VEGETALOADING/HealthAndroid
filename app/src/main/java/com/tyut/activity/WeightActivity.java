package com.tyut.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tyut.R;
import com.tyut.fragment.WeightCalendarFragment;
import com.tyut.fragment.WeightCurveFragment;
import com.tyut.fragment.WeightProgressFragment;

public class WeightActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout progress_ll;
    LinearLayout curve_ll;
    LinearLayout calendar_ll;

    LinearLayout return_ll;

    RelativeLayout fragment_content;
    ImageView img_progress;
    TextView tv_progress;
    ImageView img_curve;
    TextView tv_curve;
    ImageView img_calendar;
    TextView tv_calendar;

    private static final String PROGRESSFRAGMENT_TAG="PROGRESS";
    private static final String CALENDARFRAGMENT_TAG="CALENDAR";
    private static final String CURVEFRAGMENT_TAG="CURVE";
    private static final String HOMEFRAGMENT_TAG="HOME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        //预加载主页
        fragment_content = findViewById(R.id.weight_content);
        progress_ll = findViewById(R.id.weight_progress);
        curve_ll = findViewById(R.id.weight_curve);
        calendar_ll = findViewById(R.id.weight_calendar);

        img_progress = findViewById(R.id.img_progress);
        img_calendar = findViewById(R.id.img_calendar);
        img_curve = findViewById(R.id.img_curve);
        tv_calendar = findViewById(R.id.tv_calender);
        tv_curve = findViewById(R.id.tv_curve);
        tv_progress = findViewById(R.id.tv_progress);

        return_ll = findViewById(R.id.return_h);






        progress_ll.setOnClickListener(this);
        calendar_ll.setOnClickListener(this);
        curve_ll.setOnClickListener(this);
        return_ll.setOnClickListener(this);

        attachFragment(PROGRESSFRAGMENT_TAG);


        /*Intent intent = getIntent();
        if(intent.getIntExtra("src", 0 ) == 0){
            attachFragment(PROGRESSFRAGMENT_TAG);
        }else if(intent.getIntExtra("src", 0 ) == 1){
            attachFragment(MYFRAGMENT_TAG);
        }*/

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.weight_progress:
                attachFragment(PROGRESSFRAGMENT_TAG);
                img_progress.setImageResource(R.mipmap.icon_progress_selected);
                tv_progress.setTextColor(Color.rgb(16,222,57));
                img_calendar.setImageResource(R.mipmap.icon_calendar_unselected);
                tv_calendar.setTextColor(Color.rgb(94,94,94));
                img_curve.setImageResource(R.mipmap.icon_curve_unselected);
                tv_curve.setTextColor(Color.rgb(94,94,94));

                break;
            case R.id.weight_calendar:
                attachFragment(CALENDARFRAGMENT_TAG);
                img_progress.setImageResource(R.mipmap.icon_progress_unselected);
                tv_progress.setTextColor(Color.rgb(94,94,94));
                img_calendar.setImageResource(R.mipmap.icon_calendar_selected);
                tv_calendar.setTextColor(Color.rgb(16,222,57));
                img_curve.setImageResource(R.mipmap.icon_curve_unselected);
                tv_curve.setTextColor(Color.rgb(94,94,94));
                break;
            case R.id.weight_curve:
                attachFragment(CURVEFRAGMENT_TAG);
                img_progress.setImageResource(R.mipmap.icon_progress_unselected);
                tv_progress.setTextColor(Color.rgb(94,94,94));
                img_calendar.setImageResource(R.mipmap.icon_calendar_unselected);
                tv_calendar.setTextColor(Color.rgb(94,94,94));
                img_curve.setImageResource(R.mipmap.icon_curve_selected);
                tv_curve.setTextColor(Color.rgb(16,222,57));
                break;
            case R.id.return_h:
                Intent intent = new Intent(WeightActivity.this, HomeActivity.class);
                intent.putExtra("src", HOMEFRAGMENT_TAG);
                WeightActivity.this.startActivity(intent);
                break;
        }

    }

    private void attachFragment(String fragmentTag){

        //获取Fragment管理器
        FragmentManager manager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction fragmentTransaction = manager.beginTransaction();

        Fragment fragment = manager.findFragmentByTag(fragmentTag);

        if(fragment == null){
            //管理器中无fragmen

            if(fragmentTag.equals(CALENDARFRAGMENT_TAG)){
                fragment = new WeightCalendarFragment();

            }else if(fragmentTag.equals(PROGRESSFRAGMENT_TAG)){
                fragment = new WeightProgressFragment();
            }
            else if(fragmentTag.equals(CURVEFRAGMENT_TAG)){
                fragment = new WeightCurveFragment();
            }

            fragmentTransaction.add(fragment, fragmentTag);

        }

        //显示

        fragmentTransaction.replace(R.id.weight_content, fragment, fragmentTag);

        fragmentTransaction.commit();

    }
}
