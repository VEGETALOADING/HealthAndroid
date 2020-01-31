package com.tyut.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.tyut.R;
import com.tyut.utils.RulerUtill;

import java.text.DecimalFormat;

public class SportTimeRulerView extends View {

    private Paint paint;

    private Context context;

    private int maxValue = 900;
    /**
     * 起点x的坐标
     */
    private float startX ;

    private float startY ;
    /**
     * 刻度线的长度
     */
    private float yLenght ;
    /**
     * 刻度的间隙
     */
//    private float gap = 8f;
    private float gap = 10;
    /**
     * 文本的间隙
     */
    private float textGap = 10f;
    /**
     * 短竖线的高度
     */
    private float smallHeight = 10f;
    /**
     * 长竖线的高度
     */
    private float largeHeight = 22f;

    /**
     * 文本显示格式化
     */
    private DecimalFormat format;

    private DisplayMetrics metrics = null;
    /**
     * 文本的字体大小
     */
    private float mFontSize;

    private Handler mScrollHandler = null;

    private MyHorizontalScrollView horizontalScrollView;

    private int mCurrentX = -999999999;
    /**
     * 刻度进制
     */
//    private float unit = 10f;
    private int unit = 10;//隔unit个刻度写一个数字
    //每个大刻度代表值iValue
    private int iValue = 10;

    boolean isDraw = true;

    public SportTimeRulerView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setHorizontalScrollView(
            MyHorizontalScrollView horizontalScrollView) {
        this.horizontalScrollView = horizontalScrollView;

        this.horizontalScrollView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:

                        mScrollHandler.removeCallbacks(mScrollRunnable);
                        break;
                    case MotionEvent.ACTION_UP:

                        mScrollHandler.post(mScrollRunnable);
                        break;
                }
                return false;
            }
        });
    }

    public SportTimeRulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    public void init() {

//        format = new DecimalFormat("0.0");
        format = new DecimalFormat("0");//不使用浮点数格式

        metrics = new DisplayMetrics();
        WindowManager wmg = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wmg.getDefaultDisplay().getMetrics(metrics);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.text_h2));
//        paint.setStrokeWidth(2);
        paint.setColor(Color.parseColor("#999999"));

        mFontSize = RulerUtill.dip2px(context, 16);
//        startY = ScreenUtil.dip2px(context, 20f);
        startY = RulerUtill.dip2px(context, 0);//Y轴由0开始，即最顶端，不用设置适配布局文件RuleView的android:layout_marginTop="-20dp"
        yLenght = RulerUtill.dip2px(context, 10);
//        gap = ScreenUtil.dip2px(context, 8f);
        gap = RulerUtill.dip2px(context, 10);
//        startX = ScreenUtil.getScreenWidth(context)/ 2.0f- getResources().getDimension(R.dimen.ui_10_dip)  ;
        startX = RulerUtill.getScreenWidth(context)/ 2.0f;//X轴不减去10dp，则三角形顶点可以刚好最准0位置

        // + getResources().getDimension(R.dimen.text_h2)/2.0f
        // Util.dip2px(context, 13f) +

        mScrollHandler = new Handler(context.getMainLooper());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        float width =  maxValue * gap + RulerUtill.getScreenWidth(context) - getResources().getDimension(R.dimen.text_h2)*2.0f ;

        // int widthMode = MeasureSpec.getMode(heightMeasureSpec);
        // if(widthMode == MeasureSpec.AT_MOST){
        // Log.d("TAG", "mode::AT_MOST");
        // }else if(widthMode == MeasureSpec.EXACTLY){
        // Log.d("TAG", "mode::EXACTLY");
        // }else if(widthMode == MeasureSpec.UNSPECIFIED){
        // Log.d("TAG", "mode::UNSPECIFIED");
        // }

        setMeasuredDimension((int) width, heightMeasureSpec);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        // 画刻度线
        paint.setColor(getResources().getColor(R.color.btn_unselected));// 刻度颜色
        for (int i = 0; i <= maxValue; i++) {

            if (i % 5 == 0) {
                yLenght = RulerUtill.dip2px(context, largeHeight);
            } else {
                yLenght = RulerUtill.dip2px(context, smallHeight);
            }
            canvas.drawLine(i * gap + startX, startY, i * gap + startX, yLenght
                    + startY, paint);
        }

        paint.setTextSize(mFontSize);

        // 每10个刻度写一个数字
        textGap = gap * unit;

        // 画刻度文字30
        paint.setColor(getResources().getColor(R.color.btn_unselected));// 文字颜色
        for (int i = 0; i <= maxValue / unit; i++) {

//            String text = format.format(i + 1) + "";//从0开始计数时不用加1
            String text = format.format(i * iValue) + "";//乘以每刻度的值iValue
            // 获取文本的宽度
            float width = RulerUtill.px2dip(context, calculateTextWidth(text)) / 2f;

            canvas.drawText(
                    text,
                    startX - width + i * textGap,
                    (startY + RulerUtill.dip2px(context, largeHeight))
                            + RulerUtill.dip2px(context, 24), paint);//字体大小
        }
    }

    /**
     * 获取TextView中文本的宽度
     */
    private float calculateTextWidth(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mFontSize * metrics.scaledDensity);
        final float textWidth = textPaint.measureText(text);

        return textWidth;
    }

    DecimalFormat df = new DecimalFormat("0.0");

    /**
     * 当滑动尺子的时候
     */
    int scrollWidth = 0;

    public void setScrollerChanaged(int l, int t, int oldl, int oldt) {
        // 滑动的距离
        scrollWidth = l;

        float number = scrollWidth / gap;
        float result = number / unit;

        listener.onSlide(result);
    }

    public onChangedListener listener;

    public interface onChangedListener {

        void onSlide(float number);
    }

    public void onChangedListener(onChangedListener listener) {
        this.listener = listener;
    }

    /**
     * 滚动监听线程
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            if (mCurrentX == horizontalScrollView.getScrollX()) {// 滚动停止了

                try {

                    float x = horizontalScrollView.getScrollX();
                    float value = (x / (gap * unit));// 当前的值
                    String s = df.format(value);

                    // 滑动到11.0 ok
                    int scrollX = (int) (Double.parseDouble(s) * gap * unit);

                    horizontalScrollView.smoothScrollTo(scrollX, 0);

                } catch (NumberFormatException numExp) {
                    numExp.printStackTrace();
                }
                mScrollHandler.removeCallbacks(this);
            } else {
                mCurrentX = horizontalScrollView.getScrollX();
                mScrollHandler.postDelayed(this, 50);
            }
        }
    };

    /**
     * 设置默认刻度尺的刻度值,不会滚动到相应的位置
     *
     * @param scaleValue
     */
    public void setDefaultScaleValue(float scaleValue) {

//        final int scrollX = (int) ((scaleValue - 1.0f) * gap * unit);//从0开始计数时不用减去1
        final int scrollX = (int) (scaleValue * gap * unit / 10);//每个值在设置刻度时会乘以10，所以除去

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                horizontalScrollView.smoothScrollTo(scrollX, 0);
            }
        }, 100);
    }

    /**
     * 设置刻度值
     */
    public void setScaleValue(int iValue) {
        this.iValue = iValue;
    }

    /**
     * 设置刻度最小值
     */
    public void setMinScaleValue(Float minScaleValue) {
        // this.minScaleValue = minScaleValue;
    }

    /**
     * 获取刻度最大值
     */
    public Float getMaxScaleValue() {
        // return maxScaleValue;
        return 33.0f;
    }

    /**
     * 设置刻度最大值
     */
    public void setMaxScaleValue(Float maxScaleValue) {
        // this.maxScaleValue = maxScaleValue;
    }

    /**
     * 设置当前刻度尺的刻度值,并滚动到相应的位置
     *
     * @param scaleValue
     */
    public void setScaleScroll(float scaleValue) {

        int scrollX = (int) ((scaleValue - 1.0f) * gap * unit);

        horizontalScrollView.smoothScrollTo(scrollX, 0);
    }
}
