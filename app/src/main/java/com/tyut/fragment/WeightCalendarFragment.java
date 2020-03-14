package com.tyut.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.PunchinActivity;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.calendar.CustomDayView;
import com.tyut.view.calendar.Utils;
import com.tyut.view.calendar.component.CalendarAttr;
import com.tyut.view.calendar.component.CalendarViewAdapter;
import com.tyut.view.calendar.interf.OnSelectDateListener;
import com.tyut.view.calendar.model.CalendarDate;
import com.tyut.view.calendar.view.Calendar;
import com.tyut.view.calendar.view.MonthPager;
import com.tyut.vo.Punchin;
import com.tyut.vo.PunchinVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.vo.Weight;
import com.tyut.widget.RecordWeightDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WeightCalendarFragment extends Fragment implements OnClickListener {

    TextView tvYear;
    TextView tvMonth;
    TextView backToday;
    CoordinatorLayout content;
    MonthPager monthPager;
    TextView nextMonthBtn;
    TextView lastMonthBtn;
    Button button;

    private ArrayList<Calendar> currentCalendars = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private CalendarDate currentDate;
    private boolean initiated = false;
    private UserVO userVO;
    private List<Weight> weightList;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 1:
                    initMarkData(weightList);
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weightcalendar, container, false);

        button = view.findViewById(R.id.calendar_record_btn);

        content = view.findViewById(R.id.content);
        monthPager =  view.findViewById(R.id.calendar_view);
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        monthPager.setViewHeight(Utils.dpi2px(getActivity(), 270));
        tvYear = view.findViewById(R.id.show_year_view);
        tvMonth = view.findViewById(R.id.show_month_view);
        backToday = view.findViewById(R.id.back_today_button);
        nextMonthBtn = view.findViewById(R.id.next_month);
        lastMonthBtn = view.findViewById(R.id.last_month);

        button.setOnClickListener(this);

        initCurrentDate();
        initCalendarView();
        initToolbarClickListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        userVO = (UserVO)  SPSingleton.get(getActivity(), SPSingleton.USERINFO).readObject("user", UserVO.class);
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/weight/list.do?&userid="+userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<Weight>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Weight>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            weightList = serverResponse.getData();
                            Message message = new Message();
                            message.what= 1;
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );
    }

    /**
     * 初始化对应功能的listener
     *
     * @return void
     */
    private void initToolbarClickListener() {
        backToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBackToDayBtn();
            }
        });
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthPager.setCurrentItem(monthPager.getCurrentPosition() + 1);
            }
        });
        lastMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthPager.setCurrentItem(monthPager.getCurrentPosition() - 1);
            }
        });
    }

    /**
     * 初始化currentDate
     *
     * @return void
     */
    private void initCurrentDate() {
        currentDate = new CalendarDate();
        tvYear.setText(currentDate.getYear() + "年");
        tvMonth.setText(currentDate.getMonth() + "");
    }

    /**
     * 初始化CustomDayView，并作为CalendarViewAdapter的参数传入
     */
    private void initCalendarView() {
        initListener();
        CustomDayView customDayView = new CustomDayView(getActivity(), R.layout.item_calendarday);
        calendarAdapter = new CalendarViewAdapter(
                getActivity(),
                onSelectDateListener,
                CalendarAttr.CalendarType.MONTH,
                CalendarAttr.WeekArrayType.Monday,
                customDayView);

        initMonthPager();
    }

    /**
     * 初始化标记数据，HashMap的形式，可自定义
     * 如果存在异步的话，在使用setMarkData之后调用 calendarAdapter.notifyDataChanged();
     */
    private void initMarkData(List<Weight> list) {

        HashMap<String, String> markData = new HashMap<>();
        for (Weight weight: list) {
            String date = weight.getCreateTime().substring(0, 5)
                    + StringUtil.getPrettyNumber(weight.getCreateTime().substring(5, 7))
                    + "-"
                    + StringUtil.getPrettyNumber(weight.getCreateTime().substring(8));
            markData.put(date, "0");
        }
        calendarAdapter.setMarkData(markData);
    }

    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);

                String dateChosen = date.toString();

                Date today = StringUtil.string2Date(StringUtil.getCurrentDate("yyyy-MM-dd"), "yyyy-MM-dd");
                Date temp = StringUtil.string2Date(dateChosen, "yyyy-MM-dd");

                if(temp.after(today)) {
                    Toast.makeText(getActivity(), "不能记录未来的日期", Toast.LENGTH_SHORT).show();
                }else{
                    showDialog(dateChosen, userVO.getWeight());
                }
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
                monthPager.selectOtherMonth(offset);
            }
        };
    }


    private void refreshClickDate(CalendarDate date) {
        currentDate = date;
        tvYear.setText(date.getYear() + "年");
        tvMonth.setText(date.getMonth() + "");
    }

    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     *
     * @return void
     */
    private void initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getPagers();
                if (currentCalendars.get(position % currentCalendars.size()) != null) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    currentDate = date;
                    tvYear.setText(date.getYear() + "年");
                    tvMonth.setText(date.getMonth() + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onClickBackToDayBtn() {
        refreshMonthPager();
    }

    private void refreshMonthPager() {
        CalendarDate today = new CalendarDate();
        calendarAdapter.notifyDataChanged(today);
        tvYear.setText(today.getYear() + "年");
        tvMonth.setText(today.getMonth() + "");
    }

    private void showDialog(final String date, String weight){

        final RecordWeightDialog dialog = new RecordWeightDialog(getActivity());
        dialog.setDate(date);
        dialog.setDefaultWeight(Float.parseFloat(weight));
        dialog.setCancel(new RecordWeightDialog.IOnCancelListener() {
            @Override
            public void onCancel(RecordWeightDialog dialog) {

            }
        });
        dialog.setConfirm(new RecordWeightDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(RecordWeightDialog dialog) {
                //String createTime = null;
                /*if ("今天".equals(date)) {
                    createTime = StringUtil.getCurrentDate("yyyy-MM-dd");
                }else{
                    createTime = date;
                }*/
                String weight = dialog.getWeight();
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/weight/add.do?userid=" + userVO.getId() + "&weight=" + weight + "&createTime=" + date,
                        new OkHttpCallback() {
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson = new Gson();
                                ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>() {
                                }.getType());
                                SPSingleton util = SPSingleton.get(getActivity(), SPSingleton.USERINFO);
                                util.clear();
                                util.putBoolean("isLogin", true);
                                util.putString("user", gson.toJson(serverResponse.getData()));
                                util.putInt("userid", serverResponse.getData().getId());
                                onResume();
                                Looper.prepare();
                                Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Looper.loop();

                            }
                        }
                );

            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.calendar_record_btn:
                showDialog(StringUtil.getCurrentDate("yyyy-MM-dd"), userVO.getWeight());
                break;
        }
    }
}
