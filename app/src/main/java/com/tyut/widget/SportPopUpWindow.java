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
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;
import com.tyut.vo.MysportVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SportVO;


public class SportPopUpWindow implements View.OnClickListener {

    private ImageView close_iv;

    private ImageView sport_pic;
    private TextView sport_name;
    private TextView sport_unit;
    private TextView sport_quantity;
    private TextView sport_calories;
    private TextView confirm_tv;
    private TextView time_tv;
    private TextView addOrUpdate_tv;
    private TextView calories_infact;
    private RulerView rulerView;
    private LinearLayout deleteRecord;
    private MyHorizontalScrollView horizontalScrollView;

    private String sportUnit;

    public String getTime() {
        return time_tv.getText()+"";
    }
    public String getCal() {
        return calories_infact.getText()+"";
    }

    private String sportQuantity;
    private String sportName;
    private String sportCalories;
    private String sportPic;
    private String createTime = StringUtil.getCurrentDate("yyyy-MM-dd");
    private MysportVO mysportVO;
    private SportVO sportVO;

    PopupWindow sportPopupWindow;

    View contentView;
    Context context;

    Integer userId = null;


    private SportPopUpWindow.IOnCancelListener cancelListener;
    private SportPopUpWindow.IOnConfirmListener confirmListener;


    public PopupWindow getSportPopupWindow() {
        return sportPopupWindow;
    }

    public void setSportPopupWindow(PopupWindow sportPopupWindow) {
        this.sportPopupWindow = sportPopupWindow;
    }

    public String getCreateTime() {
        return createTime;
    }

    public SportPopUpWindow setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public SportPopUpWindow setSportName(String sportName) {
        this.sportName = sportName;
        return this;
    }



    public SportPopUpWindow setSportUnit(String sportUnit) {
        this.sportUnit = sportUnit;
        return this;
    }

    public SportPopUpWindow setSportCalories(String sportCalories) {
        this.sportCalories = sportCalories;
        return this;
    }

    public SportPopUpWindow setSportPic(String sportPic) {
        this.sportPic = sportPic;
        return this;
    }


    public SportPopUpWindow setSportQuantity(String sportQuantity) {
        this.sportQuantity = sportQuantity;
        return this;
    }


    public SportPopUpWindow setCancel(SportPopUpWindow.IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public SportPopUpWindow setConfirm(SportPopUpWindow.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public SportPopUpWindow(SportVO sportVO, MysportVO mysportVO, Context context) {

        this.context = context;
        this.mysportVO = mysportVO;
        this.sportVO = sportVO;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_sport, null);

        if(mysportVO == null){
            this.sportName = sportVO.getName();
            this.sportUnit = sportVO.getUnit();
            this.sportCalories = sportVO.getCalories().toString();
            this.sportQuantity = sportVO.getQuantity().toString();
            this.sportPic = sportVO.getPic();

        }else{
            this.sportName = mysportVO.getName();
            this.sportUnit = mysportVO.getUnit();
            this.sportCalories = mysportVO.getSportCal().toString();
            this.sportQuantity = mysportVO.getSportQuantity().toString();
            this.sportPic = mysportVO.getPic();

        }
        initView();

        if(mysportVO == null){
            deleteRecord.setVisibility(View.GONE);
        }else{
            deleteRecord.setVisibility(View.VISIBLE);
            addOrUpdate_tv.setText("修改运动");

        }
        initRuler(rulerView, horizontalScrollView);

    }

    public void showFoodPopWindow(){

        sportPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        sportPopupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        sportPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        sportPopupWindow.setOutsideTouchable(true);
        //设置可以点击
        sportPopupWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        sportPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        sportPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.st_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.confirm_tv:
                String cal = getCal();
                String time = getTime();

                if(sportVO == null){
                    //更新运动
                    OkHttpUtils.get("http://"+context.getString(R.string.localhost)+"/portal/mysport/update.do?userid="+userId+"&id="+mysportVO.getId()+"&time="+time+"&cal="+cal,
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

                }else{

                    //添加运动到列表
                }

                sportPopupWindow.dismiss();
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                break;

            case R.id.deleteRecord_ll:
                OkHttpUtils.get("http://"+context.getString(R.string.localhost)+"/portal/mysport/delete.do?userid="+userId+"&id="+mysportVO.getId(),
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);
                                Looper.prepare();
                                Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        }
                );
                sportPopupWindow.dismiss();
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                break;

        }
    }

    public interface IOnCancelListener{
        void onCancel(SportPopUpWindow sportPopUpWindow);
    }
    public interface IOnConfirmListener{
        void onConfirm(SportPopUpWindow sportPopUpWindow);
    }

    private void initRuler(final RulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);

        if(mysportVO == null) {
            rulerView.setDefaultScaleValue(30);

        }else{
            rulerView.setDefaultScaleValue(mysportVO.getTime());
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
                time_tv.setText(num+"");

                int calories = (int) ((Double.parseDouble(num+"") / Double.parseDouble(sport_quantity.getText().toString())) * Integer.parseInt(sport_calories.getText().toString()));
                calories_infact.setText(calories+"");
            }
        });

    }


    private void initView(){
        userId = SharedPreferencesUtil.getInstance(context).readInt("userid");
        close_iv = contentView.findViewById(R.id.st_dl_close);
        sport_pic = contentView.findViewById(R.id.sport_pic_dl);
        sport_name = contentView.findViewById(R.id.sport_name_dl);
        sport_unit = contentView.findViewById(R.id.sport_unit_dl);
        sport_quantity = contentView.findViewById(R.id.sport_quantity_dl);
        sport_calories = contentView.findViewById(R.id.sport_cal_dl);
        confirm_tv = contentView.findViewById(R.id.confirm_tv);
        time_tv = contentView.findViewById(R.id.time_infact_dl);
        calories_infact = contentView.findViewById(R.id.calories_infact_tv);
        rulerView = contentView.findViewById(R.id.ruler_view_dl);
        deleteRecord = contentView.findViewById(R.id.deleteRecord_ll);
        horizontalScrollView = contentView.findViewById(R.id.hor_scrollview_dl);
        addOrUpdate_tv = contentView.findViewById(R.id.addorupdate_tv);


        sport_name.setText(sportName+"");
        sport_unit.setText(sportUnit + "");
        sport_quantity.setText(sportQuantity + "");
        sport_calories.setText(sportCalories + "");
        time_tv.setText(sportQuantity+"");
        Glide.with(context).load("http://"+context.getString(R.string.localhost)+"/sportpic/" + sportPic).into(sport_pic);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);
        deleteRecord.setOnClickListener(this);
    }

}
