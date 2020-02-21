package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.R;
import com.tyut.utils.JudgeUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;
import com.tyut.view.ScrollPickView;
import com.tyut.vo.FoodVO;
import com.tyut.vo.MyfoodVO;
import com.tyut.vo.ServerResponse;

import java.util.ArrayList;
import java.util.List;


public class FoodPopUpWindow implements View.OnClickListener {

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
    LinearLayout deleteRecord;


    RulerView rulerView;
    MyHorizontalScrollView horizontalScrollView;

    Boolean showChooseDate = false;
    Boolean tag = false;

    //不给默认值需要滚动
    String time = "早餐";
    String date = "今天";
    String createTime;
    Integer dateIndex=5;//默认今天
    //定义滚动选择器的数据项（年月日的）
    List<String> dateList = new ArrayList<>();
    List<String> originalList = new ArrayList<>();
    List<String> dateWithYearList = new ArrayList<>();

    PopupWindow foodPopupWindow;
    View contentView;
    Context context;

    Integer userId = null;

    private String foodUnit;
    private String foodQuantity;
    private String foodName;
    private String foodCal;
    private String foodPic;

    private MyfoodVO myfoodVO;
    private FoodVO foodVO;


    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public String getQuantity() {
        return quantity_infact2.getText()+"";
    }
    public String getCal() {
        return cal_infact.getText()+"";
    }


    public FoodPopUpWindow setTime(String time) {
        this.time = time;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public FoodPopUpWindow setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }



    public PopupWindow getFoodPopupWindow() {
        return foodPopupWindow;
    }

    public void setFoodPopupWindow(PopupWindow foodPopupWindow) {
        this.foodPopupWindow = foodPopupWindow;
    }

    public FoodPopUpWindow setFoodName(String foodName) {
        this.foodName = foodName;
        return this;
    }
    public Integer getDateIndex(){
        return dateIndex;
    }
    public String getTime(){
        return time;
    }

    public FoodPopUpWindow setFoodUnit(String foodUnit) {
        this.foodUnit = foodUnit;
        return this;
    }

    public FoodPopUpWindow setFoodCal(String foodCal) {
        this.foodCal = foodCal;
        return this;
    }

    public FoodPopUpWindow setFoodPic(String foodPic) {
        this.foodPic = foodPic;
        return this;
    }


    public FoodPopUpWindow setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
        return this;
    }


    public FoodPopUpWindow setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public FoodPopUpWindow setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public FoodPopUpWindow(FoodVO foodVO, MyfoodVO myfoodVO, Context context) {

        this.context = context;
        this.foodVO = foodVO;
        this.myfoodVO = myfoodVO;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_food, null);


    }

    public FoodPopUpWindow initialData(){

        if(myfoodVO == null){
            this.foodName = foodVO.getName();
            this.foodUnit = foodVO.getUnit();
            this.foodCal = foodVO.getCalories().toString();
            this.foodQuantity = foodVO.getQuantity().toString();
            this.foodPic = foodVO.getPic();
            if(createTime == null){
                dateList = StringUtil.getRecengtDateList();
                originalList = StringUtil.getRecengtDateList();
                dateWithYearList = StringUtil.getRecengtDateListWithYear();
            }else{
                dateList = StringUtil.getRecengtDateList(createTime);
                originalList = StringUtil.getRecengtDateList(createTime);
                dateWithYearList = StringUtil.getRecengtDateListWithYear(createTime);
            }
        }else{
            this.foodName = myfoodVO.getName();
            this.foodUnit = myfoodVO.getUnit();
            this.foodCal = myfoodVO.getFoodCal().toString();
            this.foodQuantity = myfoodVO.getFoodQuantity().toString();
            this.foodPic = myfoodVO.getPic();
            this.time = JudgeUtil.getDietName(myfoodVO.getType());
            dateList = StringUtil.getRecengtDateList(myfoodVO.getCreateTime());
            originalList = StringUtil.getRecengtDateList(myfoodVO.getCreateTime());
            dateWithYearList = StringUtil.getRecengtDateListWithYear(myfoodVO.getCreateTime());
        }
        initView();

        if(myfoodVO == null){
            deleteRecord.setVisibility(View.GONE);
            if(createTime == null){
                date_tv.setText(StringUtil.getCurrentDate("MM月-dd日"));
            }else{
                date_tv.setText(createTime.substring(5, 7)+"月"+createTime.substring(8)+"日");
            }
            time_tv.setText(time);
        }else{
            deleteRecord.setVisibility(View.VISIBLE);
            date_tv.setText(myfoodVO.getCreateTime());
            time_tv.setText(JudgeUtil.getDietName(myfoodVO.getType()));
        }
        initRuler(rulerView, horizontalScrollView);
        initScrollPick();
        return this;
    }

    public void showFoodPopWindow(){

        foodPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        foodPopupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        foodPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        foodPopupWindow.setOutsideTouchable(true);
        //设置可以点击
        foodPopupWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        foodPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        foodPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.food_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.confirm_recordfood_tv:
                String quantity = getQuantity();
                String cal = getCal();
                Integer time = JudgeUtil.getDietTime(getTime());
                Integer foodId = null;
                if(foodVO == null){
                    foodId = myfoodVO.getFoodid();
                    OkHttpUtils.get("http://"+context.getString(R.string.localhost)+"/portal/myfood/add.do?userid="+userId+"&type="+time+"&foodid="+foodId+"&quantity="+quantity+"&createTime="+dateWithYearList.get(dateIndex)+"&cal="+cal,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                    Looper.prepare();
                                    Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                    );
                }

                foodPopupWindow.dismiss();
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }


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
            case R.id.deleteRecord_ll:
                tag = false;
                OkHttpUtils.get("http://"+context.getString(R.string.localhost)+"/portal/myfood/delete.do?userid="+myfoodVO.getUserid()+"&id="+myfoodVO.getId(),
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                if(serverResponse.getStatus() == 0){
                                    tag = true;
                                }
                                    Looper.prepare();
                                    Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                            }
                        }
                );
                foodPopupWindow.dismiss();
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }

                break;

        }
    }

    public interface IOnCancelListener{
        void onCancel(FoodPopUpWindow dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(FoodPopUpWindow dialog);
    }

    private void initRuler(final RulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);

        if(myfoodVO == null) {
            if (foodUnit.equals("毫升")) {
                rulerView.setDefaultScaleValue(220);
            } else if (foodUnit.equals("克")) {
                rulerView.setDefaultScaleValue(100);
            }
        }else{
            rulerView.setDefaultScaleValue(myfoodVO.getQuantity());
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
        if(myfoodVO == null) {
            chooseDate_spv.setSelected(dateList.size() - 2);

            chooseTime_spv.setSelected(JudgeUtil.getDietTime(time) - 1);

        }else{
            chooseDate_spv.setSelected(dateList.indexOf(myfoodVO.getCreateTime().substring(5, 7) + "月-" + myfoodVO.getCreateTime().substring(8) + "日"));
            chooseTime_spv.setSelected(timeList.indexOf(JudgeUtil.getDietName(myfoodVO.getType())));
        }


        //滚动选择事件
        chooseDate_spv.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                date = data;
                dateIndex = originalList.indexOf(data);
                createTime = dateWithYearList.get(dateIndex);

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

    private void initView(){
        userId = SharedPreferencesUtil.getInstance(context).readInt("userid");

        chooseDate_ll = contentView.findViewById(R.id.choosedate_ll);
        date_tv = contentView.findViewById(R.id.date_tv);
        time_tv = contentView.findViewById(R.id.time_tv);
        chooseDate_iv = contentView.findViewById(R.id.choosedate_iv);
        close_iv = contentView.findViewById(R.id.food_dl_close);
        food_pic = contentView.findViewById(R.id.food_pic_dl);
        food_name = contentView.findViewById(R.id.food_name_dl);
        food_unit = contentView.findViewById(R.id.food_unit_dl);
        food_quantity = contentView.findViewById(R.id.food_quantity_dl);
        food_cal = contentView.findViewById(R.id.food_cal_dl);
        cal_infact = contentView.findViewById(R.id.calories_infact_tv);
        quantity_infact = contentView.findViewById(R.id.quantity_infact_tv);
        unit_infact = contentView.findViewById(R.id.unit_infact_tv);
        quantity_infact2 = contentView.findViewById(R.id.quantity_infact_tv2);
        unit_infact2 = contentView.findViewById(R.id.unit_infact_tv2);
        confirm_tv = contentView.findViewById(R.id.confirm_recordfood_tv);
        horizontalScrollView = contentView.findViewById(R.id.recordfood_scrollview_dl);
        rulerView = contentView.findViewById(R.id.recordfood_ruler_view_dl);
        deleteRecord = contentView.findViewById(R.id.deleteRecord_ll);
        chooseDate_hide = contentView.findViewById(R.id.choosedate_hide);
        main_ll = contentView.findViewById(R.id.main_ll);
        food_name.setText(foodName+"");
        food_unit.setText(foodUnit + "");
        food_quantity.setText(foodQuantity + "");
        food_cal.setText(foodCal + "");
        unit_infact.setText(foodUnit+"");
        unit_infact2.setText(foodUnit+"");
        Glide.with(context).load("http://"+context.getString(R.string.localhost)+"/foodpic/" + foodPic).into(food_pic);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);
        chooseDate_ll.setOnClickListener(this);
        deleteRecord.setOnClickListener(this);
    }

}
