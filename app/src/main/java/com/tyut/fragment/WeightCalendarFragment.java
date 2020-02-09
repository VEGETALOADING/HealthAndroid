package com.tyut.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.RecordWeightDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeightCalendarFragment extends Fragment implements OnClickListener {

    CalendarView calendarView;
    Button button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weightcalendar_fragment, container, false);

        calendarView = view.findViewById(R.id.weight_cv);
        button = view.findViewById(R.id.calendar_record_btn);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String currentYear = StringUtil.getCurrentDate("yyyy");
                String currentMonth = StringUtil.getCurrentDate("MM");
                String currentDay = StringUtil.getCurrentDate("dd");

                if(year <= Integer.parseInt(currentYear)
                        && month <= Integer.parseInt(currentMonth)
                        && dayOfMonth <= Integer.parseInt(currentDay)){

                    String date = year+"-"+(month+1)+"-"+dayOfMonth;
                    showDialog(date);

                }else {
                    Toast.makeText(getActivity(), "不能记录未来的日期", Toast.LENGTH_LONG).show();
                }

            }
        });

        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.calendar_record_btn:
               showDialog("今天");
               break;
        }
    }

    private void showDialog(final String date){
        final UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(getActivity()).readObject("user", UserVO.class);
        final RecordWeightDialog dialog = new RecordWeightDialog(getActivity());
        dialog.setDate(date);
        dialog.setDefaultWeight(Float.parseFloat(userVO.getWeight()));
        dialog.setCancel(new RecordWeightDialog.IOnCancelListener() {
            @Override
            public void onCancel(RecordWeightDialog dialog) {

            }
        });
        dialog.setConfirm(new RecordWeightDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(RecordWeightDialog dialog) {
                String createTime = null;
                if ("今天".equals(date)) {
                    createTime = StringUtil.getCurrentDate("yyyy-MM-dd");
                }else{
                    createTime = date;
                }
                String weight = dialog.getWeight();
                OkHttpUtils.get("http://192.168.1.4:8080/portal/weight/add.do?userid=" + userVO.getId() + "&weight=" + weight + "&createTime=" + createTime,
                        new OkHttpCallback() {
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson = new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>() {
                                }.getType());
                                SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(getActivity());
                                util.clear();
                                util.putBoolean("isLogin", true);
                                util.putString("user", gson.toJson(serverResponse.getData()));
                                util.putInt("userid", serverResponse.getData().getId());
                                onResume();
                                Looper.prepare();
                                Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                Looper.loop();

                            }
                        }
                );

            }
        });
        dialog.show();
    }
}
