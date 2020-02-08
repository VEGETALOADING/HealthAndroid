package com.tyut;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;
import com.tyut.view.ScrollPickView;
import com.tyut.view.WeightRulerView;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    MyHorizontalScrollView scrollView;
    WeightRulerView rulerView;
    TextView show;
    String date;
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        btn = findViewById(R.id.btn_test);
        show = findViewById(R.id.show_tv);
        scrollView = findViewById(R.id.sv);
        rulerView = findViewById(R.id.rv);

        //scrollPickView = findViewById(R.id.sv);;
        initRuler(rulerView, scrollView);




    }

    private void initRuler(final WeightRulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);
        rulerView.setDefaultScaleValue(30);

        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new WeightRulerView.onChangedListener() {
            @Override
            public void onSlide(float number) {

                show.setText(StringUtil.keepDecimal(number, 1)+"");


            }
        });

    }



}
