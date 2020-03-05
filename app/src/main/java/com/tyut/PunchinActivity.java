package com.tyut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@SuppressLint("SetTextI18n")
public class PunchinActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvYear;
    TextView tvMonth;
    TextView backToday;
    CoordinatorLayout content;
    MonthPager monthPager;
    TextView nextMonthBtn;
    TextView lastMonthBtn;
    Button punchIn_btn;

    LinearLayout return_ll;
    TextView continueCount_tv;
    TextView totalCount_tv;
    private UserVO userVO;
    private static final Integer PUNCHINDATA = 1;
    private String dateChosen;
    private List<String> datePunched = new ArrayList<>();

    private ArrayList<Calendar> currentCalendars = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private Context context;
    private CalendarDate currentDate;
    private boolean initiated = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 1:
                    PunchinVO vo = (PunchinVO) msg.obj;
                    continueCount_tv.setText(vo.getContinueCount()+"");
                    totalCount_tv.setText(vo.getPunchinList().size()+"");
                    initMarkData(vo.getPunchinList());
                    for (Punchin punchin : vo.getPunchinList()) {
                        datePunched.add(punchin.getCreatetime().substring(0, 5)
                                + StringUtil.getPrettyNumber(punchin.getCreatetime().substring(5, 7)) + "-"
                                + StringUtil.getPrettyNumber(punchin.getCreatetime().substring(8))
                        );
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punchin);
        context = this;
        content =  findViewById(R.id.content);
        monthPager =  findViewById(R.id.calendar_view);
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        monthPager.setViewHeight(Utils.dpi2px(context, 270));
        tvYear =  findViewById(R.id.show_year_view);
        tvMonth = findViewById(R.id.show_month_view);
        backToday = findViewById(R.id.back_today_button);
        nextMonthBtn = findViewById(R.id.next_month);
        lastMonthBtn = findViewById(R.id.last_month);
        totalCount_tv = findViewById(R.id.totalCount_tv);
        continueCount_tv = findViewById(R.id.continueCount_tv);
        punchIn_btn = findViewById(R.id.punchin_btn);


        initCurrentDate();
        initCalendarView();
        initToolbarClickListener();
        punchIn_btn.setOnClickListener(this);
    }

    /**
     * onWindowFocusChanged回调时，将当前月的种子日期修改为今天
     *
     * @return void
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !initiated) {
            refreshMonthPager();
            initiated = true;
        }
    }

    /*
     * 如果你想以周模式启动你的日历，请在onResume是调用
     * Utils.scrollTo(content, rvToDoList, monthPager.getCellHeight(), 200);
     * calendarAdapter.switchToWeek(monthPager.getRowIndex());
     * */
    @Override
    protected void onResume() {
        super.onResume();

        userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);
        //OkHttpUtils.get("http://192.168.1.9:8080/portal/punchin/select.do?&userid="+userVO.getId(),

        OkHttpUtils.get("http://192.168.1.9:8080/portal/punchin/select.do?&userid="+userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<PunchinVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<PunchinVO>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= PUNCHINDATA;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(PunchinActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
        CustomDayView customDayView = new CustomDayView(context, R.layout.item_calendarday);
        calendarAdapter = new CalendarViewAdapter(
                context,
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
    private void initMarkData(List<Punchin> list) {

        HashMap<String, String> markData = new HashMap<>();
        for (Punchin punchin : list) {
            String date = punchin.getCreatetime().substring(0, 5)
                    + StringUtil.getPrettyNumber(punchin.getCreatetime().substring(5, 7))
                    + "-"
                    + StringUtil.getPrettyNumber(punchin.getCreatetime().substring(8));
            markData.put(date, "0");
        }
        calendarAdapter.setMarkData(markData);
    }

    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);

                dateChosen = date.toString();

                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Calendar calendar = java.util.Calendar.getInstance();

                Date today = StringUtil.string2Date(StringUtil.getCurrentDate("yyyy-MM-dd"), "yyyy-MM-dd");
                calendar.setTime(today);
                calendar.set(java.util.Calendar.DATE, calendar.get(java.util.Calendar.DATE) - 8);
                Date from = calendar.getTime();
                Date temp = StringUtil.string2Date(dateChosen, "yyyy-MM-dd");

                if(!datePunched.contains(dateChosen) && temp.before(today) && temp.after(from)) {
                    punchIn_btn.setBackground(content.getResources().getDrawable(R.drawable.btn_green));
                    punchIn_btn.setClickable(true);
                }else{
                    punchIn_btn.setBackground(content.getResources().getDrawable(R.drawable.btn_grey));
                    punchIn_btn.setClickable(false);

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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.punchin_btn:
                //待实现
                Toast.makeText(context, "可以补打卡", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}



