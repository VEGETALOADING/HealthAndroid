package com.tyut.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.HeightRulerView;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;


public class ChooseHeightDialog extends Dialog implements View.OnClickListener {

    private ImageView close_iv;
    private TextView confirm_tv;
    private TextView height_tv;

    private HeightRulerView rulerView;
    private MyHorizontalScrollView horizontalScrollView;

    private String height;
    private String defaultHeight;


    public ChooseHeightDialog setDefaultHeight(String defaultHeight) {
        this.defaultHeight = defaultHeight;
        return this;
    }

    public String getHeight() {
        return height_tv.getText()+"";
    }


    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public ChooseHeightDialog setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public ChooseHeightDialog setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public ChooseHeightDialog(@NonNull Context context) {
        super(context);
    }

    public ChooseHeightDialog(@NonNull Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chooseheight);

        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x * 0.8);
        getWindow().setAttributes(p);

        close_iv = findViewById(R.id.chooseheight_dl_close);
        confirm_tv = findViewById(R.id.chooseheight_confirm_tv);
        height_tv = findViewById(R.id.ch_height_tv);
        rulerView = findViewById(R.id.height_rv);
        horizontalScrollView = findViewById(R.id.height_sv);

        height_tv.setText(defaultHeight);


        initRuler(rulerView, horizontalScrollView);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseheight_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.chooseheight_confirm_tv:
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(ChooseHeightDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(ChooseHeightDialog dialog);
    }

    private void initRuler(final HeightRulerView rulerView, MyHorizontalScrollView horizontalScrollView){

        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);// 去掉超出滑动后出现的阴影效果

        // 设置水平滑动
        rulerView.setHorizontalScrollView(horizontalScrollView);
        rulerView.setDefaultScaleValue(Float.parseFloat(defaultHeight)-100);

        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                rulerView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });


        rulerView.onChangedListener(new HeightRulerView.onChangedListener() {
            @Override
            public void onSlide(float number) {

                height = StringUtil.keepDecimal(number, 1)+100+"";
                height_tv.setText(height);

            }
        });

    }
}
