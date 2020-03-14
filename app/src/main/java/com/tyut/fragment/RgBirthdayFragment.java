package com.tyut.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.ScrollPickView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RgBirthdayFragment extends Fragment implements View.OnClickListener {

    Button nextStep_btn;
    private ScrollPickView chooseYear;
    private ScrollPickView chooseMonth;
    private ScrollPickView chooseDay;
    private TextView year_tv;
    private TextView month_tv;
    private TextView day_tv;

    private String year = "1998";
    private String month = "02";
    private String day = "10";

    private FragmentListener listener;
    public interface FragmentListener{
        void getBirthday(String str);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rgbirthday, container, false);

        nextStep_btn = view.findViewById(R.id.nextStep_btn);
        chooseYear = view.findViewById(R.id.chooseyear_spv);
        chooseMonth = view.findViewById(R.id.choosemonth_spv);
        chooseDay = view.findViewById(R.id.chooseday_spv);
        year_tv = view.findViewById(R.id.year_tv);
        month_tv = view.findViewById(R.id.month_tv);
        day_tv= view.findViewById(R.id.day_tv);
        nextStep_btn.setOnClickListener(this);
        initScrollPick();

        return view;
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
        int maxDAY = StringUtil.getDaysOfMonth("1998年02月");
        for (int i = 1; i <= maxDAY ; i++) {
            String day = new DecimalFormat("00").format(i)+"日";
            days.add(day);
        }
        chooseDay.setData(days);
        chooseYear.setData(years);
        chooseMonth.setData(months);
        chooseYear.setSelected(Integer.parseInt(1998+"") - 1900);
        chooseMonth.setSelected(Integer.parseInt(02+"") - 1);
        chooseDay.setSelected(Integer.parseInt(10+"") - 1);


        //滚动选择事件
        chooseYear.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                year = data.substring(0, 4);
                year_tv.setText(year);
                int maxDAY = StringUtil.getDaysOfMonth(year+"年"+month+"月");
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
                month = data.substring(0, 2);
                month_tv.setText(month);
                day_tv.setText("01");
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
                day = data.substring(0, 2);
                day_tv.setText(day);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextStep_btn:
                listener.getBirthday(year+"-"+month+"-"+day);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FragmentListener) {
            listener = (FragmentListener)context;
        } else{
            throw new IllegalArgumentException("activity must implements FragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
