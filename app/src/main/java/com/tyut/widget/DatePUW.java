package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.ScrollPickView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DatePUW implements View.OnClickListener {

    private TextView reset_tv;
    private TextView confirm_tv;
    private ScrollPickView chooseYear;
    private ScrollPickView chooseMonth;
    private String year;
    private String month;
    private String defaultYear;
    private String defaultMonth;

    private PopupWindow datePopUpWindow;
    private View contentView;
    private Context context;

    public String getDate(){
        return getYear().substring(0, 4) + "-" + getMonth().substring(0, 2);
    }

    public String getDefaultYear() {
        return defaultYear;
    }

    public DatePUW setDefaultYear(String defaultYear) {
        this.defaultYear = defaultYear;
        return this;
    }

    public String getDefaultMonth() {
        return defaultMonth;
    }

    public DatePUW setDefaultMonth(String defaultMonth) {
        this.defaultMonth = defaultMonth;
        return this;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    private DatePUW.IOnConfirmListener confirmListener;

    private DatePUW.IOnResetListener resetListener;

    public PopupWindow getDatePopUpWindow() {
        return datePopUpWindow;
    }


    public DatePUW setOnRest(DatePUW.IOnResetListener resetListener) {
        this.resetListener = resetListener;
        return this;
    }


    public DatePUW setConfirm(DatePUW.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public DatePUW(Context context, String defaultDate) {

        this.context = context;
        this.defaultYear = defaultDate.substring(0, 4);
        this.defaultMonth =  defaultDate.substring(5, 7);
        this.year = defaultDate.substring(0, 4)+"年";
        this.month =  defaultDate.substring(5, 7)+"月";

        contentView = LayoutInflater.from(context).inflate(
                R.layout.puw_date, null);
        initView();
        initScrollPick();


    }

    public void showDatePopUpWindow(){

        datePopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        datePopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        datePopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        datePopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        datePopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        datePopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        datePopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reset_tv:
                datePopUpWindow.dismiss();
                if(resetListener != null){
                    resetListener.onReset(this);
                }
                break;
            case R.id.choosedate_confirm_tv:

                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                datePopUpWindow.dismiss();
                break;
        }
    }

    public interface IOnResetListener{
        void onReset(DatePUW datePUW);
    }
    public interface IOnConfirmListener{
        void onConfirm(DatePUW weightPopUpWindow);
    }

    private void initScrollPick(){

        final List<String> years = new ArrayList<>();
        for (int i = 1900; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
            String year = i+"年";
            years.add(year);
        }
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12 ; i++) {
            String month = new DecimalFormat("00").format(i)+"月";
            months.add(month);
        }

        List<String> days = new ArrayList<>();
        int maxDAY = StringUtil.getDaysOfMonth(defaultYear+"年"+month+"月");
        for (int i = 1; i <= maxDAY ; i++) {
            String day = new DecimalFormat("00").format(i)+"日";
            days.add(day);
        }
        chooseYear.setData(years);
        chooseMonth.setData(months);
        chooseYear.setSelected(Integer.parseInt(defaultYear) - 1900);
        chooseMonth.setSelected(Integer.parseInt(defaultMonth) - 1);

        //滚动选择事件
        chooseYear.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                year = data;
            }
        });
        chooseMonth.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                month = data;
            }
        });

    }

    private void initView(){

        reset_tv = contentView.findViewById(R.id.reset_tv);
        chooseYear = contentView.findViewById(R.id.chooseyear_spv);
        chooseMonth = contentView.findViewById(R.id.choosemonth_spv);
        confirm_tv = contentView.findViewById(R.id.choosedate_confirm_tv);

        confirm_tv.setOnClickListener(this);
        reset_tv.setOnClickListener(this);

    }

}
