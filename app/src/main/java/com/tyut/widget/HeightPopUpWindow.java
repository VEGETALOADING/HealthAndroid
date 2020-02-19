package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.HeightRulerView;
import com.tyut.view.MyHorizontalScrollView;


public class HeightPopUpWindow implements View.OnClickListener {

    private ImageView close_iv;
    private TextView confirm_tv;
    private TextView height_tv;

    private HeightRulerView rulerView;
    private MyHorizontalScrollView horizontalScrollView;

    private String height;
    private float defaultHeight;

    PopupWindow heightPopUpWindow;
    View contentView;
    Context context;

    public float getDefaultWeight() {
        return defaultHeight;
    }

    public HeightPopUpWindow setDefaultHeight(float defaultHeight) {
        this.defaultHeight = defaultHeight;
        return this;
    }

    public String getHeight() {
        return height_tv.getText()+"";
    }
    public PopupWindow getHeightPopUpWindow() {
        return heightPopUpWindow;
    }

    private HeightPopUpWindow.IOnCancelListener cancelListener;
    private HeightPopUpWindow.IOnConfirmListener confirmListener;

    public HeightPopUpWindow setCancel(HeightPopUpWindow.IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public HeightPopUpWindow setConfirm(HeightPopUpWindow.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public HeightPopUpWindow(Context context, Float defaultHeight) {

        this.context = context;
        this.defaultHeight = defaultHeight;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_height, null);
        initView();
        initRuler(rulerView, horizontalScrollView);

    }

    public void showFoodPopWindow(){

        heightPopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        heightPopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        heightPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        heightPopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        heightPopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        heightPopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        heightPopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseheight_dl_close:
                heightPopUpWindow.dismiss();
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.chooseheight_confirm_tv:

                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                heightPopUpWindow.dismiss();
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(HeightPopUpWindow weightPopUpWindow);
    }
    public interface IOnConfirmListener{
        void onConfirm(HeightPopUpWindow weightPopUpWindow);
    }

    private void initRuler(final HeightRulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);
        rulerView.setDefaultScaleValue(defaultHeight-100);


        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new HeightRulerView.onChangedListener(){
            @Override
            public void onSlide(float number) {

                height = StringUtil.keepDecimal(number, 1)+100+"";
                height_tv.setText(height);
            }
        });

    }

    private void initView(){

        close_iv = contentView.findViewById(R.id.chooseheight_dl_close);
        confirm_tv = contentView.findViewById(R.id.chooseheight_confirm_tv);
        height_tv = contentView.findViewById(R.id.ch_height_tv);
        rulerView = contentView.findViewById(R.id.height_rv);
        horizontalScrollView = contentView.findViewById(R.id.height_sv);

        height_tv.setText(defaultHeight+"");

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);
    }

}
