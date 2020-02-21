package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.WeightRulerView;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;


public class WeightPopUpWindow implements View.OnClickListener {

    private ImageView close_iv;
    private TextView confirm_tv;
    private TextView weight_tv;
    private TextView date_tv;
    private WeightRulerView rulerView;
    private MyHorizontalScrollView horizontalScrollView;

    private float defaultWeight;
    private Boolean ifUpdate;

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public WeightPopUpWindow setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
        return this;
    }

    private String weight;
    private String date;

    public String getDate() {
        return date;
    }

    public WeightPopUpWindow setDate(String date) {
        this.date = date;
        return this;
    }

    public String getWeight() {
        return weight_tv.getText()+"";
    }

    public WeightPopUpWindow setWeight(String weight) {
        this.weight = weight;
        return this;
    }


    PopupWindow weightPopUpWindow;

    View contentView;
    Context context;

    Integer userId = null;


    private WeightPopUpWindow.IOnCancelListener cancelListener;
    private WeightPopUpWindow.IOnConfirmListener confirmListener;


    public PopupWindow getWeightPopUpWindow() {
        return this.weightPopUpWindow;
    }

    public WeightPopUpWindow setWeightPopUpWindow(PopupWindow weightPopUpWindow) {
        this.weightPopUpWindow= weightPopUpWindow;
        return this;
    }



    public WeightPopUpWindow setCancel(WeightPopUpWindow.IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public WeightPopUpWindow setConfirm(WeightPopUpWindow.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public WeightPopUpWindow(Context context,Float defaultWeight, String date, Boolean ifUpdate) {

        this.context = context;
        this.defaultWeight = defaultWeight;
        this.date = date;
        this.ifUpdate = ifUpdate;


        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_weight, null);
        initView();
        if(date == null){
            this.date = StringUtil.getCurrentDate("yyyy-MM-dd");
            if(ifUpdate) {
                date_tv.setText(StringUtil.getCurrentDate("今天"));
            }else{
                date_tv.setText(StringUtil.getCurrentDate("体重"));
            }
        }else{

            date_tv.setText(date.substring(5, 7)+"月"+date.substring(8)+"日");
        }
        initRuler(rulerView, horizontalScrollView);

    }

    public void showFoodPopWindow(){

        weightPopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        weightPopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        weightPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        weightPopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        weightPopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        weightPopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        weightPopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weight_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.confirm_recordweight_tv:
                if(ifUpdate) {
                    OkHttpUtils.get("http://"+context.getString(R.string.localhost)+"/portal/weight/add.do?userid=" + userId + "&weight=" + weight + "&createTime=" + date,
                            new OkHttpCallback() {
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson = new Gson();
                                    ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>() {
                                    }.getType());
                                    SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(context);
                                    if (date.equals(StringUtil.getCurrentDate("yyyy-MM-dd"))) {
                                        util.clear();
                                        util.putBoolean("isLogin", true);
                                        util.putString("user", gson.toJson(serverResponse.getData()));
                                        util.putInt("userid", serverResponse.getData().getId());
                                    }
                                    Looper.prepare();
                                    Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();

                                }
                            }
                    );
                }
                weightPopUpWindow.dismiss();
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(WeightPopUpWindow weightPopUpWindow);
    }
    public interface IOnConfirmListener{
        void onConfirm(WeightPopUpWindow weightPopUpWindow);
    }

    private void initRuler(final WeightRulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);


        rulerView.setDefaultScaleValue(defaultWeight);
        rulerView.setMaxScaleValue(200.0f);


        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new WeightRulerView.onChangedListener(){
            @Override
            public void onSlide(float number) {

                weight = StringUtil.keepDecimal(number, 1)+"";
                weight_tv.setText(weight);
            }
        });

    }

    private void initView(){

        userId = SharedPreferencesUtil.getInstance(context).readInt("userid");
        close_iv = contentView.findViewById(R.id.weight_dl_close);
        confirm_tv = contentView.findViewById(R.id.confirm_recordweight_tv);
        date_tv = contentView.findViewById(R.id.recordweight_date_tv);
        horizontalScrollView = contentView.findViewById(R.id.recordweight_sv_dl);
        rulerView = contentView.findViewById(R.id.recordweight_rv_dl);
        weight_tv = contentView.findViewById(R.id.weight_recordweight_dl);

        weight_tv.setText(defaultWeight+"");

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);
    }

}
