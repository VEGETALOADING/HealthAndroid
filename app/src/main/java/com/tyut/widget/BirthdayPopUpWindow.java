package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.ScrollPickView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class BirthdayPopUpWindow implements View.OnClickListener {

    private ImageView close_iv;
    private TextView confirm_tv;
    private ScrollPickView chooseYear;
    private ScrollPickView chooseMonth;
    private ScrollPickView chooseDay;
    private String year;
    private String month;
    private String day;
    private String defaultYear;
    private String defaultMonth;
    private String defaultDay;

    private PopupWindow birthdayPopUpWindow;
    private View contentView;
    private Context context;

    public String getDate(){
        return getYear().substring(0, 4) + "-" + getMonth().substring(0, 2) + "-" + getDay().substring(0, 2);
    }

    public String getDefaultYear() {
        return defaultYear;
    }

    public BirthdayPopUpWindow setDefaultYear(String defaultYear) {
        this.defaultYear = defaultYear;
        return this;
    }

    public String getDefaultMonth() {
        return defaultMonth;
    }

    public BirthdayPopUpWindow setDefaultMonth(String defaultMonth) {
        this.defaultMonth = defaultMonth;
        return this;
    }

    public String getDefaultDay() {
        return defaultDay;
    }

    public BirthdayPopUpWindow setDefaultDay(String defaultDay) {
        this.defaultDay = defaultDay;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    private BirthdayPopUpWindow.IOnCancelListener cancelListener;
    private BirthdayPopUpWindow.IOnConfirmListener confirmListener;

    public PopupWindow getBirthdayPopUpWindow() {
        return birthdayPopUpWindow;
    }


    public BirthdayPopUpWindow setCancel(BirthdayPopUpWindow.IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public BirthdayPopUpWindow setConfirm(BirthdayPopUpWindow.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public BirthdayPopUpWindow(Context context, String defaultBirthday) {

        this.context = context;
        this.defaultYear = defaultBirthday.substring(0, 4);
        this.defaultMonth =  defaultBirthday.substring(5, 7);
        this.defaultDay =  defaultBirthday.substring(8, 10);
        this.year = defaultBirthday.substring(0, 4)+"年";
        this.month =  defaultBirthday.substring(5, 7)+"月";
        this.day =  defaultBirthday.substring(8, 10)+"日";

        contentView = LayoutInflater.from(context).inflate(
                R.layout.PopUpWindow_birthday, null);
        initView();
        initScrollPick();


    }

    public void showFoodPopWindow(){

        birthdayPopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        birthdayPopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        birthdayPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        birthdayPopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        birthdayPopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        birthdayPopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        birthdayPopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choosebirthday_dl_close:
                birthdayPopUpWindow.dismiss();
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.choosebirthday_confirm_tv:

                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                birthdayPopUpWindow.dismiss();
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(BirthdayPopUpWindow weightPopUpWindow);
    }
    public interface IOnConfirmListener{
        void onConfirm(BirthdayPopUpWindow weightPopUpWindow);
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
        chooseDay.setData(days);
        chooseYear.setData(years);
        chooseMonth.setData(months);
        chooseYear.setSelected(Integer.parseInt(defaultYear) - 1900);
        chooseMonth.setSelected(Integer.parseInt(defaultMonth) - 1);
        chooseDay.setSelected(Integer.parseInt(defaultDay) - 1);


        //滚动选择事件
        chooseYear.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                year = data;
                int maxDAY = StringUtil.getDaysOfMonth(year+month);
                List<String> days = new ArrayList<>();
                for (int i = 1; i <= maxDAY ; i++) {
                    String day = new DecimalFormat("00").format(i)+"日";
                    days.add(day);
                }
                chooseDay.setData(days);
                chooseDay.setSelected(0);
            }
        });
        chooseMonth.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                month = data;
                int maxDAY = StringUtil.getDaysOfMonth(year+month);
                List<String> days = new ArrayList<>();
                for (int i = 1; i <= maxDAY ; i++) {
                    String day = new DecimalFormat("00").format(i)+"日";
                    days.add(day);
                }
                chooseDay.setData(days);
                chooseDay.setSelected(0);
            }
        });
        //滚动选择事件
        chooseDay.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                day = data;
            }
        });

    }

    private void initView(){

        close_iv = contentView.findViewById(R.id.choosebirthday_dl_close);
        chooseYear = contentView.findViewById(R.id.chooseyear_spv);
        chooseMonth = contentView.findViewById(R.id.choosemonth_spv);
        chooseDay = contentView.findViewById(R.id.chooseday_spv);
        confirm_tv = contentView.findViewById(R.id.choosebirthday_confirm_tv);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);

    }

}
