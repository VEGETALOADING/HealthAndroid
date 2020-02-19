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

import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.WeightRulerView;


public class RecordWeightDialog extends Dialog implements View.OnClickListener {

    private ImageView close_iv;
    private TextView confirm_tv;
    private TextView weight_tv;
    private TextView date_tv;
    private WeightRulerView rulerView;
    private MyHorizontalScrollView horizontalScrollView;



    private float defaultWeight;

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public RecordWeightDialog setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
        return this;
    }

    private String weight;
    private String date;

    public String getDate() {
        return date;
    }

    public RecordWeightDialog setDate(String date) {
        this.date = date;
        return this;
    }

    public String getWeight() {
        return weight_tv.getText()+"";
    }

    public RecordWeightDialog setWeight(String weight) {
        this.weight = weight;
        return this;
    }



    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;




    public RecordWeightDialog setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public RecordWeightDialog setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public RecordWeightDialog(@NonNull Context context) {
        super(context);
    }

    public RecordWeightDialog(@NonNull Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_weight);

        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x * 1.0);
        getWindow().setAttributes(p);

        close_iv = findViewById(R.id.weight_dl_close);
        confirm_tv = findViewById(R.id.confirm_recordweight_tv);
        date_tv = findViewById(R.id.recordweight_date_tv);
        horizontalScrollView = findViewById(R.id.recordweight_sv_dl);
        rulerView = findViewById(R.id.recordweight_rv_dl);
        weight_tv = findViewById(R.id.weight_recordweight_dl);

        initRuler(rulerView, horizontalScrollView);
        weight_tv.setText(defaultWeight+"");
        date_tv.setText(date);


        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weight_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.confirm_recordweight_tv:
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(RecordWeightDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(RecordWeightDialog dialog);
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


        rulerView.onChangedListener(new WeightRulerView.onChangedListener() {
            @Override
            public void onSlide(float number) {

                weight = StringUtil.keepDecimal(number, 1)+"";
                weight_tv.setText(weight);


            }
        });

    }
}
