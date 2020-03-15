package com.tyut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.RecordFoodListAdapter;
import com.tyut.adapter.RecordSportListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.CircleProgressBar;
import com.tyut.vo.FoodVO;
import com.tyut.vo.HotVO;
import com.tyut.vo.MyfoodVO;
import com.tyut.vo.MysportVO;
import com.tyut.vo.NutritionVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.FoodPopUpWindow;
import com.tyut.widget.SportPopUpWindow;

import java.text.DecimalFormat;
import java.util.List;

public class DietAndSportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView return_iv;
    ImageView lastDay;
    ImageView nextDay;
    TextView day;
    TextView intake_tv;
    TextView consume_tv;
    TextView remanentHot_tv;
    TextView needHot_tv;

    CircleProgressBar hotBar;

    TextView proteinNeed;
    TextView proteinConsumed;
    TextView fatNeed;
    TextView fatConsumed;
    TextView carbNeed;
    TextView carbConsumed;

    LinearLayout noRecord_ll;
    LinearLayout record_ll;
    LinearLayout breakfast_ll;
    LinearLayout lunch_ll;
    LinearLayout dinner_ll;
    LinearLayout sport_ll;
    LinearLayout navAddBreakfast;
    LinearLayout navAddLunch;
    LinearLayout navAddDinner;
    LinearLayout navAddSpoort;
    TextView breakfastHot_tv;
    TextView lunchHot_tv;
    TextView dinnerHot_tv;
    TextView sportHot_tv;
    ScrollView scrollView;
    LinearLayout nav;
    RelativeLayout whole_rl;

    RecyclerView breakfastRv;
    RecyclerView lunchRv;
    RecyclerView dinnerRv;
    RecyclerView sportRv;

    PopupWindow calendar_pop;

    PopupWindow foodPopupWindow;
    View contentView;

    Integer hotNeed = 0;
    String currentShowDate = StringUtil.getCurrentDate("yyyy-MM-dd");
    UserVO userVO = null;
    FoodVO foodVO = null;


    private static final int HOTVO_NULL = 0;
    private static final int HOTVO_NOTNULL = 1;
    private static final int CHANGEALPHA = 2;
    private static int alpha;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){

                case 0:
                    noRecord_ll.setVisibility(View.VISIBLE);
                    record_ll.setVisibility(View.GONE);
                    proteinConsumed.setText("0");
                    carbConsumed.setText("0");
                    fatConsumed.setText("0");
                    intake_tv.setText("0");
                    consume_tv.setText("0");
                    remanentHot_tv.setText(hotNeed+"");
                    hotBar.setMaxStepNum(hotNeed);
                    hotBar.update(0,1000);
                    break;
                case 1:
                    noRecord_ll.setVisibility(View.GONE);
                    record_ll.setVisibility(View.VISIBLE);
                    final HotVO hotVO = (HotVO) msg.obj;
                    proteinConsumed.setText(hotVO.getProteinConsumed()+"");
                    carbConsumed.setText(hotVO.getCarbConsumed()+"");
                    fatConsumed.setText(hotVO.getFatConsumed()+"");
                    Integer hotIntake = hotVO.getBreakfastHot()+hotVO.getLunchHot()+hotVO.getDinnerHot();
                    Integer hotConsume = hotVO.getSportHot();
                    intake_tv.setText(hotIntake+"");
                    consume_tv.setText(hotVO.getSportHot()+"");
                    remanentHot_tv.setText((hotNeed - hotIntake + hotConsume)+"");
                    hotBar.setMaxStepNum(hotNeed+hotConsume);
                    hotBar.update(hotIntake,1000);

                    final List<MyfoodVO> breakfastList = hotVO.getBreakfastList();
                    final List<MyfoodVO> lunchList = hotVO.getLunchList();
                    final List<MyfoodVO> dinnerList = hotVO.getDinnerList();
                    final List<MysportVO> sportList = hotVO.getMysportVOList();
                    if(breakfastList.size() > 0){
                        breakfast_ll.setVisibility(View.VISIBLE);
                        breakfastHot_tv.setText(hotVO.getBreakfastHot()+"");
                        breakfastRv.setLayoutManager(new LinearLayoutManager(DietAndSportActivity.this));
                        breakfastRv.addItemDecoration(new RecycleViewDivider(DietAndSportActivity.this,  LinearLayoutManager.VERTICAL));
                        breakfastRv.setAdapter(new RecordFoodListAdapter(DietAndSportActivity.this, breakfastList, new RecordFoodListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                MyfoodVO myfoodVO  = breakfastList.get(position);
                                final FoodPopUpWindow foodPopUpWindow = new FoodPopUpWindow(null, myfoodVO, DietAndSportActivity.this);
                                foodPopUpWindow.initialData().setCancel(new FoodPopUpWindow.IOnCancelListener() {
                                    @Override
                                    public void onCancel(FoodPopUpWindow dialog) {
                                        foodPopUpWindow.getFoodPopupWindow().dismiss();
                                    }
                                }).setConfirm(new FoodPopUpWindow.IOnConfirmListener() {
                                    @Override
                                    public void onConfirm(FoodPopUpWindow dialog) {
                                        onResume();

                                    }
                                }).showFoodPopWindow();

                            }
                        }));
                    }else{
                        breakfast_ll.setVisibility(View.GONE);
                    }

                    if(lunchList.size() > 0){
                        lunch_ll.setVisibility(View.VISIBLE);
                        lunchHot_tv.setText(hotVO.getLunchHot()+"");
                        lunchRv.setLayoutManager(new LinearLayoutManager(DietAndSportActivity.this));
                        lunchRv.addItemDecoration(new RecycleViewDivider(DietAndSportActivity.this,  LinearLayoutManager.VERTICAL));
                        lunchRv.setAdapter(new RecordFoodListAdapter(DietAndSportActivity.this, lunchList, new RecordFoodListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                MyfoodVO myfoodVO  = lunchList.get(position);

                                final FoodPopUpWindow foodPopUpWindow = new FoodPopUpWindow(null, myfoodVO, DietAndSportActivity.this);
                                foodPopUpWindow.initialData().setCancel(new FoodPopUpWindow.IOnCancelListener() {
                                    @Override
                                    public void onCancel(FoodPopUpWindow dialog) {
                                        foodPopUpWindow.getFoodPopupWindow().dismiss();
                                    }
                                }).setConfirm(new FoodPopUpWindow.IOnConfirmListener() {
                                    @Override
                                    public void onConfirm(FoodPopUpWindow dialog) {

                                        onResume();

                                    }
                                }).showFoodPopWindow();
                            }
                        }));
                    }else{
                        lunch_ll.setVisibility(View.GONE);
                    }

                    if(dinnerList.size() > 0){
                        dinner_ll.setVisibility(View.VISIBLE);
                        dinnerHot_tv.setText(hotVO.getDinnerHot()+"");
                        dinnerRv.setLayoutManager(new LinearLayoutManager(DietAndSportActivity.this));
                        dinnerRv.addItemDecoration(new RecycleViewDivider(DietAndSportActivity.this,  LinearLayoutManager.VERTICAL));
                        dinnerRv.setAdapter(new RecordFoodListAdapter(DietAndSportActivity.this, dinnerList, new RecordFoodListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                MyfoodVO myfoodVO  = dinnerList.get(position);

                                final FoodPopUpWindow foodPopUpWindow = new FoodPopUpWindow(null, myfoodVO, DietAndSportActivity.this);
                                foodPopUpWindow.initialData().setCancel(new FoodPopUpWindow.IOnCancelListener() {
                                    @Override
                                    public void onCancel(FoodPopUpWindow dialog) {
                                        foodPopUpWindow.getFoodPopupWindow().dismiss();
                                    }
                                }).setConfirm(new FoodPopUpWindow.IOnConfirmListener() {
                                    @Override
                                    public void onConfirm(FoodPopUpWindow dialog) {
                                        onResume();
                                    }
                                }).showFoodPopWindow();
                            }
                        }));
                    }else{
                        dinner_ll.setVisibility(View.GONE);
                    }

                    if(sportList.size() > 0){
                        sport_ll.setVisibility(View.VISIBLE);
                        sportHot_tv.setText(hotVO.getSportHot()+"");
                        sportRv.setLayoutManager(new LinearLayoutManager(DietAndSportActivity.this));
                        sportRv.addItemDecoration(new RecycleViewDivider(DietAndSportActivity.this,  LinearLayoutManager.VERTICAL));
                        sportRv.setAdapter(new RecordSportListAdapter(DietAndSportActivity.this, sportList, new RecordSportListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                MysportVO mysportVO  = sportList.get(position);

                                final SportPopUpWindow sportPopUpWindow = new SportPopUpWindow(null, mysportVO, DietAndSportActivity.this);
                                sportPopUpWindow.setCancel(new SportPopUpWindow.IOnCancelListener() {
                                    @Override
                                    public void onCancel(SportPopUpWindow dialog) {
                                        sportPopUpWindow.getSportPopupWindow().dismiss();
                                    }
                                }).setConfirm(new SportPopUpWindow.IOnConfirmListener() {
                                    @Override
                                    public void onConfirm(SportPopUpWindow dialog) {
                                        onResume();
                                    }
                                }).showFoodPopWindow();

                            }
                        }));
                    }else{
                        sport_ll.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    scrollView.getForeground().setAlpha((int)msg.obj);
                    nav.getForeground().setAlpha((int)msg.obj);
                    break;


            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietandsport);
        return_iv = findViewById(R.id.return_k);
        lastDay = findViewById(R.id.lastday_iv);
        nextDay = findViewById(R.id.nextday_iv);
        day = findViewById(R.id.day_tv);
        intake_tv = findViewById(R.id.intake_tv);
        consume_tv = findViewById(R.id.consume_tv);
        remanentHot_tv = findViewById(R.id.remanentHot_das);
        needHot_tv = findViewById(R.id.needHot_tv);
        proteinNeed = findViewById(R.id.proteinNeed);
        proteinConsumed = findViewById(R.id.proteinConsumed);
        fatNeed = findViewById(R.id.fatNeed);
        fatConsumed = findViewById(R.id.fatConsumed);
        carbNeed = findViewById(R.id.carbNeed);
        carbConsumed = findViewById(R.id.carbConsumed);

        noRecord_ll = findViewById(R.id.noRecord_ll);
        record_ll = findViewById(R.id.record_ll);

        breakfastRv = findViewById(R.id.breakfastRv_main);
        lunchRv = findViewById(R.id.lunchRv_main);
        dinnerRv = findViewById(R.id.dinnerRv_main);
        sportRv = findViewById(R.id.sportRv_main);
        breakfast_ll = findViewById(R.id.breakfast_ll);
        lunch_ll = findViewById(R.id.lunch_ll);
        dinner_ll = findViewById(R.id.dinner_ll);
        sport_ll = findViewById(R.id.sport_ll);
        breakfastHot_tv = findViewById(R.id.breakfastHot);
        lunchHot_tv = findViewById(R.id.lunchHot);
        dinnerHot_tv = findViewById(R.id.dinnerHot);
        sportHot_tv = findViewById(R.id.sportHot);

        navAddBreakfast = findViewById(R.id.breakfast_das);
        navAddLunch = findViewById(R.id.lunch_das);
        navAddDinner = findViewById(R.id.dinner_das);
        navAddSpoort = findViewById(R.id.sport_das);

        scrollView = findViewById(R.id.scrollview_das);
        nav = findViewById(R.id.nav_das);
        whole_rl = findViewById(R.id.whole_rl);
        if (whole_rl.getForeground()!=null){
            whole_rl.getForeground().setAlpha(0);
        }
        if (scrollView.getForeground()!=null){
            scrollView.getForeground().setAlpha(0);
        }
        if (nav.getForeground()!=null){
            nav.getForeground().setAlpha(0);
        }


        hotBar = findViewById(R.id.hotCircleBar);
        hotBar.setBarColor(R.color.green_light);
        hotBar.setCircleWidth(40f);

        return_iv.setOnClickListener(this);
        lastDay.setOnClickListener(this);
        nextDay.setOnClickListener(this);
        day.setOnClickListener(this);
        navAddBreakfast.setOnClickListener(this);
        navAddLunch.setOnClickListener(this);
        navAddDinner.setOnClickListener(this);
        navAddSpoort.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        Log.d("tag", "onResume");
        super.onResume();
        userVO = (UserVO) SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        if(getIntent().getStringExtra("date")!=null){
            currentShowDate = getIntent().getStringExtra("date");
            day.setText(currentShowDate.substring(5, 7)+"月"+currentShowDate.substring(8)+"日");
        }
        getHotData(currentShowDate);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.return_k:

                Intent intent5 = null;
                if(("HOMEACTIVITY").equals(getIntent().getStringExtra("src")) || getIntent().getStringExtra("src") == null){
                    intent5 = new Intent(DietAndSportActivity.this, HomeActivity.class);
                    intent5.putExtra("homeFragment", getIntent().getIntExtra("homeFragment", 0));

                }else if(("RECORDACTIVITY").equals(getIntent().getStringExtra("src"))){
                    intent5 = new Intent(DietAndSportActivity.this, RecordActivity.class);
                }

                DietAndSportActivity.this.startActivity(intent5);
                break;
            case R.id.lastday_iv:
                currentShowDate = StringUtil.getLastDay(currentShowDate);
                if(currentShowDate.equals(StringUtil.getCurrentDate("yyyy-MM-dd"))){
                    day.setText("今天");
                }else {
                    day.setText(currentShowDate.substring(5, 7) + "月" + currentShowDate.substring(8, 10) + "日");
                }
                getHotData(currentShowDate);


                break;
            case R.id.nextday_iv:

                currentShowDate= StringUtil.getNextDay(currentShowDate);
                if(currentShowDate.equals(StringUtil.getCurrentDate("yyyy-MM-dd"))){
                    day.setText("今天");
                }else {
                    day.setText(currentShowDate.substring(5, 7) + "月" + currentShowDate.substring(8, 10) + "日");
                }
                getHotData(currentShowDate);

                break;
            case R.id.day_tv:
                View view = getLayoutInflater().inflate(R.layout.popwindow_calendar, null);
                calendar_pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                calendar_pop.setOutsideTouchable(true);
                calendar_pop.setFocusable(true);
                calendar_pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                       /* WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1f;
                        getWindow().setAttributes(lp);*/
                       changeAlpha(1);
                    }
                });

                calendar_pop.showAsDropDown(day);
                //背景变暗
                changeAlpha(0);
               /* WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.4f;
                getWindow().setAttributes(lp);*/

                CalendarView calendarView = view.findViewById(R.id.pop_calendar);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        calendar_pop.dismiss();

                        currentShowDate = year+"-"+new DecimalFormat("00").format(month+1)+"-"+new DecimalFormat("00").format(dayOfMonth);
                        if(currentShowDate.equals(StringUtil.getCurrentDate("yyyy-MM-dd"))){
                            day.setText("今天");
                        }else {
                            day.setText(currentShowDate.substring(5, 7) + "月" + currentShowDate.substring(8, 10) + "日");
                        }
                        getHotData(currentShowDate);
                    }
                });
                break;
            case R.id.breakfast_das:
                Intent intent = new Intent(DietAndSportActivity.this, FoodListActivity.class);
                intent.putExtra("createtime", currentShowDate);
                intent.putExtra("src", "DIETANDSPORTACTIVITY");
                intent.putExtra("time", "早餐");
                DietAndSportActivity.this.startActivity(intent);
                break;
            case R.id.lunch_das:
                Intent intent1 = new Intent(DietAndSportActivity.this, FoodListActivity.class);
                intent1.putExtra("createtime", currentShowDate);
                intent1.putExtra("src", "DIETANDSPORTACTIVITY");
                intent1.putExtra("time", "午餐");
                DietAndSportActivity.this.startActivity(intent1);
                break;
            case R.id.dinner_das:
                Intent intent2 = new Intent(DietAndSportActivity.this, FoodListActivity.class);
                intent2.putExtra("createtime", currentShowDate);
                intent2.putExtra("src", "DIETANDSPORTACTIVITY");
                intent2.putExtra("time", "晚餐");
                DietAndSportActivity.this.startActivity(intent2);
                break;
            case R.id.sport_das:
                Intent intent4 = new Intent(DietAndSportActivity.this, SportListActivity.class);
                intent4.putExtra("createtime", currentShowDate);
                intent4.putExtra("src", "DIETANDSPORTACTIVITY");
                DietAndSportActivity.this.startActivity(intent4);
                break;
        }

    }

    private void getHotData(String date){
        NutritionVO nutritionVO = StringUtil.getNutritionData(userVO);
        carbNeed.setText(nutritionVO.getCarb()+"");
        proteinNeed.setText(nutritionVO.getProtein()+"");
        fatNeed.setText(nutritionVO.getFat()+"");
        hotNeed = nutritionVO.getHot();
        needHot_tv.setText(hotNeed+"");
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/hot/select.do?userId="+userVO.getId()+"&date="+ currentShowDate,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<HotVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<HotVO>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            if(serverResponse.getData().getSportHot() == 0
                                    && serverResponse.getData().getBreakfastHot() == 0
                                    && serverResponse.getData().getLunchHot() == 0
                                    && serverResponse.getData().getDinnerHot() == 0){
                                Message message = new Message();
                                message.what= HOTVO_NULL;
                                mHandler.sendMessage(message);

                            }else{
                                Message message = new Message();
                                message.what= HOTVO_NOTNULL;
                                message.obj = serverResponse.getData();
                                mHandler.sendMessage(message);
                            }

                        }else{
                            Looper.prepare();
                            Toast.makeText(DietAndSportActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );
    }

    private void changeAlpha(int x){
        if(x == 0){//背景变暗
            alpha = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha < 127) {
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = CHANGEALPHA;
                        alpha += 1; //每次加1，逐渐变暗
                        msg.obj = alpha;
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }else if(x == 1){
            alpha = 127;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha > 0) {
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = CHANGEALPHA;
                        alpha -= 1; //每次加1，逐渐变暗
                        msg.obj = alpha;
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();

        }
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            if(foodPopupWindow!=null&&foodPopupWindow.isShowing()){
                foodPopupWindow.dismiss();
                return true;
            }
        }
        return false;
    }*/

}
