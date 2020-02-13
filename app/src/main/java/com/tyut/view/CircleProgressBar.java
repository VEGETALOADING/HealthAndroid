package com.tyut.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.tyut.R;

import java.text.DecimalFormat;

public class CircleProgressBar extends View {

    private RectF mWheelRect = new RectF();
    private Paint mDefaultWheelPaint;
    private Paint mFinishWheelPaint;
    private Paint mCenterWheelPaint;
    private Paint mTitlePaint, mStepPaint, mTargetPaint;
    private float mCircleStrokeWidth;
    private float mSweepAnglePer;
    private float mPercent;
    private int mStepNum, mCurrStepNum;
    private float pressExtraStrokeWidth;
    private BarAnimation mAnim;
    private int mMaxStepNum;// 默认最大步数
    private float mTitleY, mStepY, mTargetY;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.0");// 格式为保留小数点后一位
    public static String GOAL_STEP;
    public static String PERCENT;

    private int barColor;
    private Float circleWidth;

    public CircleProgressBar(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        mFinishWheelPaint = new Paint();
        mFinishWheelPaint.setColor(Color.rgb(100, 113, 205));
        mFinishWheelPaint.setStyle(Paint.Style.STROKE);// 空心
        mFinishWheelPaint.setStrokeCap(Paint.Cap.ROUND);// 圆角画笔
        mFinishWheelPaint.setAntiAlias(true);// 去锯齿

        mCenterWheelPaint = new Paint();
        mCenterWheelPaint.setColor(Color.rgb(243, 243, 243));
        mCenterWheelPaint.setStyle(Paint.Style.STROKE);
        mCenterWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mCenterWheelPaint.setAntiAlias(true);

        mDefaultWheelPaint = new Paint();
        mDefaultWheelPaint.setColor(Color.rgb(127, 127, 127));
        mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
        mDefaultWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mDefaultWheelPaint.setAntiAlias(true);

        mTitlePaint = new Paint();
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setColor(Color.WHITE);

        mStepPaint = new Paint();
        mStepPaint.setAntiAlias(true);
        mStepPaint.setColor(Color.WHITE);

        mTargetPaint = new Paint();
        mTargetPaint.setAntiAlias(true);
        mTargetPaint.setColor(Color.WHITE);
        mAnim = new BarAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mWheelRect, 0, 359, false, mDefaultWheelPaint);
        canvas.drawArc(mWheelRect, 0, 359, false, mCenterWheelPaint);
        canvas.drawArc(mWheelRect, 90, mSweepAnglePer, false, mFinishWheelPaint);

        SweepGradient sweepGradient = new SweepGradient(mWheelRect.centerX(), mWheelRect.centerY(),
                new int[]{getResources().getColor(barColor),
                       getResources().getColor(barColor)}, null);
        mFinishWheelPaint.setShader(sweepGradient);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);// 获取View最短边的长度
        setMeasuredDimension(min, min);// 强制改View为以最短边为长度的正方形
        mCircleStrokeWidth = getTextScale(circleWidth, min);// 圆弧的宽度
        pressExtraStrokeWidth = getTextScale(10, min);// 圆弧离矩形的距离
        mWheelRect.set(mCircleStrokeWidth + pressExtraStrokeWidth, mCircleStrokeWidth + pressExtraStrokeWidth,
                min - mCircleStrokeWidth - pressExtraStrokeWidth, min - mCircleStrokeWidth - pressExtraStrokeWidth);// 设置矩形
        mTitlePaint.setTextSize(getTextScale(60, min));
        mStepPaint.setTextSize(getTextScale(120, min));
        mTargetPaint.setTextSize(getTextScale(40, min));
        mTitleY = getTextScale(170, min);
        mStepY = getTextScale(300, min);
        mTargetY = getTextScale(380, min);
        mFinishWheelPaint.setStrokeWidth(mCircleStrokeWidth);
        mCenterWheelPaint.setStrokeWidth(mCircleStrokeWidth);
        mDefaultWheelPaint.setStrokeWidth(mCircleStrokeWidth - getTextScale(2, min));
        mDefaultWheelPaint.setShadowLayer(getTextScale(10, min), 0, 0, Color.rgb(127, 127, 127));// 设置阴影
    }

    /**
     * 进度条动画
     *
     * @author Administrator
     */
    public class BarAnimation extends Animation {

        /**
         * 每次系统调用这个方法时， 改变mSweepAnglePer，mPercent，stepnumbernow的值，
         * 然后调用postInvalidate()不停的绘制view。
         */
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
//			if (interpolatedTime < 1.0f) {
//				mPercent = Float.parseFloat(mDecimalFormat.format(interpolatedTime * mStepNum * 100f / mMaxStepNum));// 将浮点值四舍五入保留一位小数
//				mSweepAnglePer = interpolatedTime * mStepNum * 360 / mMaxStepNum;
//				mCurrStepNum = (int) (interpolatedTime * mStepNum);
//			} else {
            mPercent = Float.parseFloat(mDecimalFormat.format(mStepNum * 100f / mMaxStepNum));// 将浮点值四舍五入保留一位小数
            if (mPercent > 100.0f) {
                mPercent = 100.0f;
            }
            PERCENT = String.valueOf(mPercent);
            mSweepAnglePer = mStepNum * 360 / mMaxStepNum;
            mCurrStepNum = mStepNum;
//			}

            requestLayout();
        }
    }

    public float getPercent() {
        return mPercent;
    }

    /**
     * 根据控件的大小改变绝对位置的比例
     *
     * @param n
     * @param m
     * @return
     */
    public float getTextScale(float n, float m) {
        return n / 500 * m;
    }

    /**
     * 更新步数和设置一圈动画时间
     *
     * @param stepCount
     * @param time
     */
    public void update(int stepCount, int time) {
        this.mStepNum = stepCount;
        mAnim.setDuration(time);
        // setAnimationTime(time);
        this.startAnimation(mAnim);
    }

    public void setBarColor(int barColor){
        this.barColor = barColor;
    }
    public void setCircleWidth(Float circleWidth){
        this.circleWidth = circleWidth;
    }

    /**
     * @param stepNum
     */
    public void setMaxStepNum(int stepNum) {
        mMaxStepNum = stepNum;
        GOAL_STEP = String.valueOf(mMaxStepNum);
    }

    public void setColor(int color) {
        mFinishWheelPaint.setColor(color);
        mStepPaint.setColor(color);
    }

    /**
     * 设置动画时间
     *
     * @param time
     */
    public void setAnimationTime(int time) {
        mAnim.setDuration(time * mStepNum / mMaxStepNum);// 按照比例设置动画执行时间
    }

}
