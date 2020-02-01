package com.tyut.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.SportTimeRulerView;


public class SportTimeDialog extends Dialog implements View.OnClickListener {

    private ImageView close_iv;

    private ImageView sport_pic;
    private TextView sport_name;
    private TextView sport_unit;
    private TextView sport_quantity;
    private TextView sport_calories;
    private TextView confirm_tv;
    private TextView time_tv;
    private TextView calories_infact;
    private SportTimeRulerView sportTimeRulerView;
    private MyHorizontalScrollView horizontalScrollView;

    private String sportUnit;

    public String getTime() {
        return time_tv.getText()+"";
    }

    private String sportQuantity;
    private String sportName;
    private String sportCalories;
    private String sportPic;


    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;


    public SportTimeDialog setSportName(String sportName) {
        this.sportName = sportName;
        return this;
    }



    public SportTimeDialog setSportUnit(String sportUnit) {
        this.sportUnit = sportUnit;
        return this;
    }

    public SportTimeDialog setSportCalories(String sportCalories) {
        this.sportCalories = sportCalories;
        return this;
    }

    public SportTimeDialog setSportPic(String sportPic) {
        this.sportPic = sportPic;
        return this;
    }


    public SportTimeDialog setSportQuantity(String sportQuantity) {
        this.sportQuantity = sportQuantity;
        return this;
    }


    public SportTimeDialog setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public SportTimeDialog setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public SportTimeDialog(@NonNull Context context) {
        super(context);
    }

    public SportTimeDialog(@NonNull Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sporttime);

        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x *1.08);
        getWindow().setAttributes(p);

        close_iv = findViewById(R.id.st_dl_close);
        sport_pic = findViewById(R.id.sport_pic_dl);
        sport_name = findViewById(R.id.sport_name_dl);
        sport_unit = findViewById(R.id.sport_unit_dl);
        sport_quantity = findViewById(R.id.sport_quantity_dl);
        sport_calories = findViewById(R.id.sport_cal_dl);
        confirm_tv = findViewById(R.id.confirm_tv);
        horizontalScrollView = findViewById(R.id.hor_scrollview_dl);
        sportTimeRulerView = findViewById(R.id.ruler_view_dl);
        time_tv = findViewById(R.id.time_infact_dl);
        calories_infact = findViewById(R.id.calories_infact_tv);

        initRuler(sportTimeRulerView, horizontalScrollView);

        sport_name.setText(sportName+"");
        sport_unit.setText(sportUnit + "");
        sport_quantity.setText(sportQuantity + "");
        sport_calories.setText(sportCalories + "");
        Glide.with(getContext()).load("http://192.168.1.10:8080/sportpic/" + sportPic).into(sport_pic);


        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.st_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.confirm_tv:
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(SportTimeDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(SportTimeDialog dialog);
    }

    private void initRuler(final SportTimeRulerView sportTimeRulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        sportTimeRulerView.setHorizontalScrollView(horizontalScrollView);
        sportTimeRulerView.setDefaultScaleValue(30);

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
                time_tv.setText(num+"");

                int calories = (int) ((Double.parseDouble(num+"") / Double.parseDouble(sport_quantity.getText().toString())) * Integer.parseInt(sport_calories.getText().toString()));
                calories_infact.setText(calories+"");

            }
        });

    }
}
