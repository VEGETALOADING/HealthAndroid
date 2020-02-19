package com.tyut;

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

import com.tyut.fragment.GirthCurveFragment;
import com.tyut.fragment.GirthDateFragment;
import com.tyut.fragment.WeightCalendarFragment;
import com.tyut.fragment.WeightCurveFragment;
import com.tyut.fragment.WeightProgressFragment;

public class GirthActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout curve_ll;
    LinearLayout date_ll;

    LinearLayout return_ll;

    RelativeLayout fragment_content;
    ImageView img_curve;
    TextView tv_curve;
    ImageView img_date;
    TextView tv_date;

    private static final String DATEFRAGMENT_TAG="DATE";
    private static final String CURVEFRAGMENT_TAG="CURVE";
    private static final String HOMEFRAGMENT_TAG="HOME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girth);

        //预加载主页
        fragment_content = findViewById(R.id.girth_content);
        curve_ll = findViewById(R.id.girth_curve);
        date_ll = findViewById(R.id.girth_date);

        img_date = findViewById(R.id.img_date);
        img_curve = findViewById(R.id.img_curve);
        tv_date = findViewById(R.id.tv_date);
        tv_curve = findViewById(R.id.tv_curve);

        return_ll = findViewById(R.id.return_m);






        date_ll.setOnClickListener(this);
        curve_ll.setOnClickListener(this);
        return_ll.setOnClickListener(this);


        Intent intent = getIntent();
        if(intent.getIntExtra("src", 0 ) == 0){
            attachFragment(CURVEFRAGMENT_TAG);
        }else if(intent.getIntExtra("src", 0 ) == 1){
            attachFragment(DATEFRAGMENT_TAG);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.girth_curve:
                attachFragment(CURVEFRAGMENT_TAG);
                img_date.setImageResource(R.mipmap.icon_calendar_unselected);
                tv_date.setTextColor(Color.rgb(94,94,94));
                img_curve.setImageResource(R.mipmap.icon_curve_selected);
                tv_curve.setTextColor(Color.rgb(16,222,57));
                break;
            case R.id.girth_date:
                attachFragment(DATEFRAGMENT_TAG);
                img_date.setImageResource(R.mipmap.icon_calendar_selected);
                tv_date.setTextColor(Color.rgb(16,222,57));
                img_curve.setImageResource(R.mipmap.icon_curve_unselected);
                tv_curve.setTextColor(Color.rgb(94,94,94));
                break;
            case R.id.return_m:
                Intent intent = null;
                if(getIntent().getStringExtra("src").equals("HOMEACTIVITY")){
                    intent = new Intent(GirthActivity.this, HomeActivity.class);
                    intent.putExtra("homeFragment", getIntent().getIntExtra("homeFragment", 0));

                }else if(getIntent().getStringExtra("src").equals("RECORDACTIVITY")){
                    intent = new Intent(GirthActivity.this, RecordActivity.class);
                }
                GirthActivity.this.startActivity(intent);
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

            if(fragmentTag.equals(CURVEFRAGMENT_TAG)){
                fragment = new GirthCurveFragment();

            }else if(fragmentTag.equals(DATEFRAGMENT_TAG)){
                fragment = new GirthDateFragment();
            }

            fragmentTransaction.add(fragment, fragmentTag);

        }

        //显示

        fragmentTransaction.replace(R.id.girth_content, fragment, fragmentTag);

        fragmentTransaction.commit();

    }
}
