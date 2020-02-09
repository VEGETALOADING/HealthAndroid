package com.tyut.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.ScrollPickView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChooseBirthdayDialog extends Dialog implements View.OnClickListener {


    ImageView close_iv;
    TextView confirm_tv;
    ScrollPickView chooseYear;
    ScrollPickView chooseMonth;
    ScrollPickView chooseDay;
    String year;
    String month;
    String day;
    String defaultYear;
    String defaultMonth;
    String defaultDay;

    public String getDefaultYear() {
        return defaultYear;
    }

    public ChooseBirthdayDialog setDefaultYear(String defaultYear) {
        this.defaultYear = defaultYear;
        return this;
    }

    public String getDefaultMonth() {
        return defaultMonth;
    }

    public ChooseBirthdayDialog setDefaultMonth(String defaultMonth) {
        this.defaultMonth = defaultMonth;
        return this;
    }

    public String getDefaultDay() {
        return defaultDay;
    }

    public ChooseBirthdayDialog setDefaultDay(String defaultDay) {
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

    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;


    public ChooseBirthdayDialog setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public ChooseBirthdayDialog setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public ChooseBirthdayDialog(@NonNull Context context) {
        super(context);
    }

    public ChooseBirthdayDialog(@NonNull Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choosebirthday);


        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x *0.8);
        getWindow().setAttributes(p);


        close_iv = findViewById(R.id.choosebirthday_dl_close);
        chooseYear = findViewById(R.id.chooseyear_spv);
        chooseMonth = findViewById(R.id.choosemonth_spv);
        chooseDay = findViewById(R.id.chooseday_spv);
        confirm_tv = findViewById(R.id.choosebirthday_confirm_tv);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);

        initScrollPick();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choosebirthday_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.choosebirthday_confirm_tv:
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;

        }
    }

    public interface IOnCancelListener{
        void onCancel(ChooseBirthdayDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(ChooseBirthdayDialog dialog);
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

}
