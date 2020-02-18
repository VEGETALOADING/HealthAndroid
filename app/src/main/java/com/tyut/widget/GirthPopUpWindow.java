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
import com.tyut.view.GirthRulerView;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;
import com.tyut.view.ScrollPickView;
import com.tyut.vo.MysportVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SportVO;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class GirthPopUpWindow implements View.OnClickListener {

    private TextView cancel;
    private TextView confirm_girth;
    private TextView confirm_date;
    private ImageView date_iv;
    private TextView length_tv;
    private TextView name_tv;
    private TextView how_tv;
    private TextView date_tv;
    private GirthRulerView rulerView;
    private MyHorizontalScrollView horizontalScrollView;

    private LinearLayout chooseDate_hide;
    private LinearLayout chooseDate_ll;
    private LinearLayout main_ll;
    private ScrollPickView chooseDate_spv;


    /*List<String> dateList = StringUtil.getRecengtDateList();
    List<String> originalList = StringUtil.getRecengtDateList();
    List<String> dateWithYearList = StringUtil.getRecengtDateListWithYear();*/
    List<String> dateList = new ArrayList<>();
    List<String> originalList = new ArrayList<>();
    List<String> dateWithYearList = new ArrayList<>();

    Boolean showChooseDate = false;
    private String date;
    Integer dateIndex=5;//默认今天
    private Float defaultValue = null;
    private Integer type;
    private String length;
    private String createTime;

    public String getLength() {
        return length_tv.getText()+"";
    }


    PopupWindow girthPopUpWindow;

    View contentView;
    Context context;

    Integer userId = null;


    private GirthPopUpWindow.IOnCancelListener cancelListener;
    private GirthPopUpWindow.IOnConfirmListener confirmListener;


    public PopupWindow getGirthPopUpWindow() {
        return girthPopUpWindow;
    }

    public GirthPopUpWindow setGirthPopUpWindow(PopupWindow girthPopUpWindow) {
        this.girthPopUpWindow = girthPopUpWindow;
        return this;
    }



    public GirthPopUpWindow setCancel(GirthPopUpWindow.IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public GirthPopUpWindow setConfirm(GirthPopUpWindow.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public GirthPopUpWindow(Context context, Integer type, Float defaultValue, String createTime) {

        this.context = context;
        this.type = type;
        this.defaultValue = defaultValue;
        this.createTime = createTime;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.dialog_recordgirth, null);
        initView();
        if(createTime == null){
            dateList = StringUtil.getRecengtDateList();
            originalList = StringUtil.getRecengtDateList();
            dateWithYearList = StringUtil.getRecengtDateListWithYear();
            date_tv.setText(StringUtil.getCurrentDate("MM月-dd日"));
        }else{
            dateList = StringUtil.getRecengtDateList(createTime);
            originalList = StringUtil.getRecengtDateList(createTime);
            dateWithYearList = StringUtil.getRecengtDateListWithYear(createTime);
            date_tv.setText(createTime.substring(5, 7)+"月"+createTime.substring(8)+"日");
        }
        initRuler(rulerView, horizontalScrollView);
        initScrollPick();

    }

    public void showFoodPopWindow(){

        girthPopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        girthPopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        girthPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        girthPopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        girthPopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        girthPopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        girthPopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_recordgirth_tv:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.confirm_recordgirth_tv:
                OkHttpUtils.get("http://192.168.1.4:8080/portal/girth/add.do?userid="+userId+"&type="+type+"&value="+getLength()+"&createTime="+dateWithYearList.get(dateIndex),
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
                girthPopUpWindow.dismiss();
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                break;
            case R.id.choosedate_ll:
                if(!showChooseDate){
                    date_iv.setImageResource(R.mipmap.icon_triangle_up);
                    main_ll.setVisibility(View.GONE);
                    chooseDate_hide.setVisibility(View.VISIBLE);
                    showChooseDate = true;
                }else{
                    main_ll.setVisibility(View.VISIBLE);
                    chooseDate_hide.setVisibility(View.GONE);
                    date_iv.setImageResource(R.mipmap.icon_triangle_down);
                    showChooseDate = false;
                }
                break;


        }
    }

    public interface IOnCancelListener{
        void onCancel(GirthPopUpWindow sportPopUpWindow);
    }
    public interface IOnConfirmListener{
        void onConfirm(GirthPopUpWindow sportPopUpWindow);
    }

    private void initRuler(final GirthRulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);


        rulerView.setDefaultScaleValue(defaultValue-20);



        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new GirthRulerView.onChangedListener(){
            @Override
            public void onSlide(float number) {

                 length = StringUtil.keepDecimal(number, 1)+20+"";
                 length_tv.setText(length);
            }
        });

    }

    private void initScrollPick(){

        chooseDate_spv = chooseDate_hide.findViewById(R.id.choosedate_spv);
        confirm_date = chooseDate_hide.findViewById(R.id.confirm_date_tv);



        chooseDate_spv.setData(dateList);

        chooseDate_spv.setSelected(dateIndex);

        //滚动选择事件
        chooseDate_spv.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                date = data;
                dateIndex = originalList.indexOf(data);
                createTime = dateWithYearList.get(dateIndex);

            }
        });

        confirm_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date_tv.setText(date);
                main_ll.setVisibility(View.VISIBLE);
                chooseDate_hide.setVisibility(View.GONE);
                date_iv.setImageResource(R.mipmap.icon_triangle_down);

            }
        });





    }
    private void initView(){

        userId = SharedPreferencesUtil.getInstance(context).readInt("userid");
        cancel = contentView.findViewById(R.id.cancel_recordgirth_tv);
        confirm_girth = contentView.findViewById(R.id.confirm_recordgirth_tv);
        confirm_date = contentView.findViewById(R.id.confirm_date_tv);
        length_tv = contentView.findViewById(R.id.length);
        rulerView = contentView.findViewById(R.id.recordgirth_rv);
        horizontalScrollView = contentView.findViewById(R.id.recordgirth_hsv);
        date_iv = contentView.findViewById(R.id.choosedate_iv);
        date_tv = contentView.findViewById(R.id.date_tv);
        main_ll = contentView.findViewById(R.id.main_ll);
        chooseDate_hide = contentView.findViewById(R.id.choosedate_hide);
        chooseDate_ll = contentView.findViewById(R.id.choosedate_ll);
        name_tv = contentView.findViewById(R.id.name);
        how_tv = contentView.findViewById(R.id.how);


        switch (type){
            case 0:
                name_tv.setText("腰围");
                how_tv.setText("屏住呼吸，水平测量腰部最细处");
                if (defaultValue == null) {
                    defaultValue = 40.0f;
                }
                break;
            case 1:
                name_tv.setText("大腿围");
                how_tv.setText("双腿分开，水平测量大腿根下方3厘米");
                if (defaultValue == null) {
                    defaultValue = 40.0f;
                }
                break;
            case 2:
                name_tv.setText("小腿围");
                how_tv.setText("双腿分开，水平测量小腿最粗处");
                if (defaultValue == null) {
                    defaultValue = 30.0f;
                }
                break;
            case 3:
                name_tv.setText("臀围");
                how_tv.setText("双腿并拢，水平测量臀部最丰满处");
                if (defaultValue == null) {
                    defaultValue = 60.0f;
                }
                break;
            case 4:
                name_tv.setText("胸围");
                how_tv.setText("屏住呼吸，水平测量胸部最丰满处");
                if (defaultValue == null) {
                    defaultValue = 80.0f;
                }
                break;
            case 5:
                name_tv.setText("手臂围");
                how_tv.setText("手臂自然下垂，水平测量上臂最粗处");
                if (defaultValue == null) {
                    defaultValue = 30.0f;
                }
                break;
        }


        chooseDate_ll.setOnClickListener(this);
        confirm_girth.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

}
