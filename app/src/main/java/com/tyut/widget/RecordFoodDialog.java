package com.tyut.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;
import com.tyut.view.ScrollPickView;

import java.util.ArrayList;
import java.util.List;


public class RecordFoodDialog extends Dialog implements View.OnClickListener {

    LinearLayout chooseDate_ll;
    TextView date_tv;
    TextView time_tv;
    ImageView chooseDate_iv;
    ImageView close_iv;
    ImageView food_pic;
    TextView food_name;
    TextView food_cal;
    TextView food_quantity;
    TextView food_unit;
    TextView cal_infact;
    TextView quantity_infact;
    TextView unit_infact;
    TextView quantity_infact2;
    TextView unit_infact2;
    TextView confirm_tv;

    LinearLayout chooseDate_hide;
    LinearLayout main_ll;
    ScrollPickView chooseDate_spv;
    ScrollPickView chooseTime_spv;
    TextView confirm_dateandtime_tv;


    RulerView rulerView;
    MyHorizontalScrollView horizontalScrollView;

    Boolean showChooseDate = false;

    //不给默认值需要滚动
    String time = "早餐";
    String date = "今天";
    Integer dateIndex=5;//默认今天
    //定义滚动选择器的数据项（年月日的）
    List<String> dateList = StringUtil.getRecengtDateList();




    public String getQuantity() {
        return quantity_infact2.getText()+"";
    }

    private String foodUnit;
    private String foodQuantity;
    private String foodName;
    private String foodCal;
    private String foodPic;


    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;



    public RecordFoodDialog setFoodName(String foodName) {
        this.foodName = foodName;
        return this;
    }
    public Integer getDateIndex(){
        return dateIndex;
    }
    public String getTime(){
        return time;
    }




    public RecordFoodDialog setFoodUnit(String foodUnit) {
        this.foodUnit = foodUnit;
        return this;
    }

    public RecordFoodDialog setFoodCal(String foodCal) {
        this.foodCal = foodCal;
        return this;
    }

    public RecordFoodDialog setFoodPic(String foodPic) {
        this.foodPic = foodPic;
        return this;
    }


    public RecordFoodDialog setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
        return this;
    }


    public RecordFoodDialog setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public RecordFoodDialog setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public RecordFoodDialog(@NonNull Context context) {
        super(context);
    }

    public RecordFoodDialog(@NonNull Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_recordfood);


        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x *1);
        getWindow().setAttributes(p);

        chooseDate_ll = findViewById(R.id.choosedate_ll);
        date_tv = findViewById(R.id.date_tv);
        time_tv = findViewById(R.id.time_tv);
        chooseDate_iv = findViewById(R.id.choosedate_iv);
        close_iv = findViewById(R.id.food_dl_close);
        food_pic = findViewById(R.id.food_pic_dl);
        food_name = findViewById(R.id.food_name_dl);
        food_unit = findViewById(R.id.food_unit_dl);
        food_quantity = findViewById(R.id.food_quantity_dl);
        food_cal = findViewById(R.id.food_cal_dl);
        cal_infact = findViewById(R.id.calories_infact_tv);
        quantity_infact = findViewById(R.id.quantity_infact_tv);
        unit_infact = findViewById(R.id.unit_infact_tv);
        quantity_infact2 = findViewById(R.id.quantity_infact_tv2);
        unit_infact2 = findViewById(R.id.unit_infact_tv2);
        confirm_tv = findViewById(R.id.confirm_recordfood_tv);
        horizontalScrollView = findViewById(R.id.recordfood_scrollview_dl);
        rulerView = findViewById(R.id.recordfood_ruler_view_dl);

        chooseDate_hide = findViewById(R.id.choosedate_hide);
        main_ll = findViewById(R.id.main_ll);


        initRuler(rulerView, horizontalScrollView);
        initScrollPick();

        food_name.setText(foodName+"");
        food_unit.setText(foodUnit + "");
        food_quantity.setText(foodQuantity + "");
        food_cal.setText(foodCal + "");
        unit_infact.setText(foodUnit+"");
        unit_infact2.setText(foodUnit+"");
        Glide.with(getContext()).load("http://192.168.1.4:8080/foodpic/" + foodPic).into(food_pic);


        date_tv.setText(StringUtil.getCurrentDate("MM月-dd日"));

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);
        chooseDate_ll.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.food_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.confirm_recordfood_tv:
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;
            case R.id.choosedate_ll:
                if(!showChooseDate){
                    chooseDate_iv.setImageResource(R.mipmap.icon_triangle_up);
                    main_ll.setVisibility(View.GONE);
                    chooseDate_hide.setVisibility(View.VISIBLE);
                    showChooseDate = true;
                }else{
                    main_ll.setVisibility(View.VISIBLE);
                    chooseDate_hide.setVisibility(View.GONE);
                    chooseDate_iv.setImageResource(R.mipmap.icon_triangle_down);
                    showChooseDate = false;
                }
                break;

        }
    }

    public interface IOnCancelListener{
        void onCancel(RecordFoodDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(RecordFoodDialog dialog);
    }

    private void initRuler(final RulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);
        if(foodUnit.equals("毫升")){
            rulerView.setDefaultScaleValue(220);
        }else if(foodUnit.equals("克")){
            rulerView.setDefaultScaleValue(100);
        }

        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new RulerView.onChangedListener() {
            @Override
            public void onSlide(float number) {

                int num = (int) (number * 10);
                quantity_infact.setText(num+"");
                quantity_infact2.setText(num+"");

                int calories = (int) ((Double.parseDouble(num+"") / Double.parseDouble(food_quantity.getText().toString())) * Integer.parseInt(food_cal.getText().toString()));
                cal_infact.setText(calories+"");

            }
        });

    }


    private void initScrollPick(){

        chooseDate_spv = chooseDate_hide.findViewById(R.id.choosedate_spv);
        chooseTime_spv = chooseDate_hide.findViewById(R.id.choosetime_spv);
        confirm_dateandtime_tv = chooseDate_hide.findViewById(R.id.confirm_dateandtime_tv);


        List<String> timeList = new ArrayList<>();
        timeList.add("早餐");
        timeList.add("午餐");
        timeList.add("晚餐");
        chooseDate_spv.setData(dateList);
        chooseTime_spv.setData(timeList);
        chooseDate_spv.setSelected(dateList.size() - 2);
        chooseTime_spv.setSelected(0);

        //滚动选择事件
        chooseDate_spv.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                date = data;
                List<String> originalList = StringUtil.getRecengtDateList();
                dateIndex = originalList.indexOf(data);
                int x  = dateIndex;
            }
        });
        chooseTime_spv.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                time = data;
            }
        });
        confirm_dateandtime_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date_tv.setText(date);
                time_tv.setText(time);
                main_ll.setVisibility(View.VISIBLE);
                chooseDate_hide.setVisibility(View.GONE);
                chooseDate_iv.setImageResource(R.mipmap.icon_triangle_down);

            }
        });





    }

}
