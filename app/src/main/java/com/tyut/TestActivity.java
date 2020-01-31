package com.tyut;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.SportTimeRulerView;

public class TestActivity extends AppCompatActivity {

    SportTimeRulerView sportTimeRulerView;
    MyHorizontalScrollView horizontalScrollView;
    TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        sportTimeRulerView = (SportTimeRulerView) findViewById(R.id.rule_view);
        tv = (TextView) findViewById(R.id.tv);
        horizontalScrollView = findViewById(R.id.hor_scrollview);
        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        sportTimeRulerView.setHorizontalScrollView(horizontalScrollView);
        sportTimeRulerView.setDefaultScaleValue(200);

        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                sportTimeRulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        sportTimeRulerView.onChangedListener(new SportTimeRulerView.onChangedListener() {
            @Override
            public void onSlide(float number) {

                int num = (int) (number * 10);
                tv.setText(num+"");
            }
        });




    }

}
