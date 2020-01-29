package com.tyut.view;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tyut.R;

public class MyProgressView extends View {

    private Paint mCircleOutPaint;
    private Paint mCircleInPaint;
    private Paint mLinePaint;

    private Paint mDefaltCircleOutPaint;
    private Paint mDefaltCircleInPaint;
    private Paint mDefaltLinePaint;

    private Paint mTvPaint;
    private int circleOutRadius = 16;
    private int circleInRadius = 8;
    private int margin = 60; //左右margin

    private float mProgress;
    private ValueAnimator animation;

    public MyProgressView(Context context) {
        super(context);
        initView();
    }

    public MyProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        mCircleOutPaint = new Paint();
        mCircleOutPaint.setStyle(Paint.Style.FILL);
        mCircleOutPaint.setColor(Color.parseColor("#40A5FF"));
        mCircleOutPaint.setAntiAlias(true);

        mCircleInPaint = new Paint();
        mCircleInPaint.setStyle(Paint.Style.FILL);
        mCircleInPaint.setColor(Color.WHITE);
        mCircleInPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.parseColor("#40A5FF"));
        mLinePaint.setStrokeWidth(8);

        mDefaltCircleOutPaint = new Paint();
        mDefaltCircleOutPaint.setStyle(Paint.Style.FILL);
        mDefaltCircleOutPaint.setColor(Color.parseColor("#858585"));
        mDefaltCircleOutPaint.setAntiAlias(true);

        mDefaltCircleInPaint = new Paint();
        mDefaltCircleInPaint.setStyle(Paint.Style.FILL);
        mDefaltCircleInPaint.setColor(Color.WHITE);
        mDefaltCircleInPaint.setAntiAlias(true);

        mDefaltLinePaint = new Paint();
        mDefaltLinePaint.setColor(Color.parseColor("#858585"));
        mDefaltLinePaint.setStrokeWidth(8);

        mTvPaint = new Paint();
        mTvPaint.setColor(Color.parseColor("#858585"));
        mTvPaint.setTextSize(sp2px(getContext(), 12));
    }

    public void setProgress(float progress) {
        if (animation == null) {
            animation = ValueAnimator.ofFloat(0f, Float.valueOf(progress));
        }

        if (progress > 0 && progress <= 15) {
            mLinePaint.setColor(getResources().getColor(R.color.colorAccent));
            mCircleOutPaint.setColor(getResources().getColor(R.color.colorAccent));
        } else if (progress > 15 && progress <= 25) {
            mLinePaint.setColor(getResources().getColor(R.color.nav_text_selected));
            mCircleOutPaint.setColor(getResources().getColor(R.color.nav_text_selected));
        } else if (progress > 25 && progress <= 30) {
            mLinePaint.setColor(getResources().getColor(R.color.colorAccent));
            mCircleOutPaint.setColor(getResources().getColor(R.color.colorAccent));
        } else if (progress > 30 && progress <= 60) {
            mLinePaint.setColor(getResources().getColor(R.color.red));
            mCircleOutPaint.setColor(getResources().getColor(R.color.red));
        } else if (progress > 60) {
            mLinePaint.setColor(getResources().getColor(R.color.red));
            mCircleOutPaint.setColor(getResources().getColor(R.color.red));
        }

        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animation.setDuration(3000);
        animation.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getSuggestedMinimumWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int realWidth = (width - 2 * margin - 10 * circleOutRadius) / 3;  //20

        canvas.drawLine(margin + circleOutRadius * 2, height / 2, width - margin - circleOutRadius * 2, height / 2, mDefaltLinePaint);

       /* for (int i = 0; i < 4; i++) {
            canvas.drawCircle(margin + circleOutRadius * (i * 2 + 1) + realWidth * i, height / 2, circleOutRadius, mDefaltCircleOutPaint);
            canvas.drawCircle(margin + circleOutRadius * (i * 2 + 1) + realWidth * i, height / 2, circleInRadius, mDefaltCircleInPaint);

        }*/


        canvas.drawCircle(margin + circleOutRadius , height / 2, circleOutRadius, mDefaltCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius , height / 2, circleInRadius, mDefaltCircleInPaint);

        canvas.drawCircle(margin + circleOutRadius * (1 * 2 + 1) + realWidth * 3 / 4, height / 2, circleOutRadius, mDefaltCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (1 * 2 + 1) + realWidth * 3 / 4, height / 2, circleInRadius, mDefaltCircleInPaint);

        canvas.drawCircle(margin + circleOutRadius * (2 * 2 + 1) + realWidth * 5 / 4, height / 2, circleOutRadius, mDefaltCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (2 * 2 + 1) + realWidth * 5 / 4, height / 2, circleInRadius, mDefaltCircleInPaint);

        canvas.drawCircle(margin + circleOutRadius * (3 * 2 + 1) + realWidth * 3 / 2, height / 2, circleOutRadius, mDefaltCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (3 * 2 + 1) + realWidth * 3 / 2, height / 2, circleInRadius, mDefaltCircleInPaint);

        canvas.drawCircle(margin + circleOutRadius * (4 * 2 + 1) + realWidth * 3, height / 2, circleOutRadius, mDefaltCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (4 * 2 + 1) + realWidth * 3, height / 2, circleInRadius, mDefaltCircleInPaint);


        canvas.drawText("0", margin + circleOutRadius - getTextWidth("0"), height / 2 - 30, mTvPaint);
        canvas.drawText("15", margin + circleOutRadius * (1 * 2 + 1) + realWidth * 3 / 4 - getTextWidth("15"), height / 2 - 30, mTvPaint);
        canvas.drawText("25", margin + circleOutRadius * (2 * 2 + 1) + realWidth * 5 / 4 - getTextWidth("25"), height / 2 - 30, mTvPaint);
        canvas.drawText("30", margin + circleOutRadius * (3 * 2 + 1) + realWidth * 3 / 2 - getTextWidth("30"), height / 2 - 30, mTvPaint);
        canvas.drawText("60+", margin + circleOutRadius * (4 * 2 + 1) + realWidth * 3 - getTextWidth("60"), height / 2 - 30, mTvPaint);

        canvas.drawText("偏瘦", margin + circleOutRadius - getTextWidth("0") + realWidth * 3 / 8 , height / 2 + 50, mTvPaint);
        canvas.drawText("正常", margin + circleOutRadius * (1 * 2 + 1) + realWidth * 3 / 4 - getTextWidth("15") + realWidth * 1 / 4 , height / 2 + 50, mTvPaint);
        canvas.drawText("超重", margin + circleOutRadius * (2 * 2 + 1) + realWidth * 5 / 4 - getTextWidth("25") + realWidth * 1 / 8, height / 2 + 50, mTvPaint);
        canvas.drawText("肥胖", margin + circleOutRadius * (3 * 2 + 1) + realWidth * 3 / 2 - getTextWidth("30") + realWidth * 3 / 4, height / 2 + 50, mTvPaint);
        float ratio;
        float result = 0;

        /*if (mProgress > 0 && mProgress <= 400) {
            ratio = mProgress / 400;
            result = margin + circleOutRadius * 2 + (ratio * realWidth);
        } else if (mProgress > 400 && mProgress <= 700) {
            ratio = (mProgress - 400) / 300;
            result = margin + circleOutRadius * 4 + (ratio * realWidth) + realWidth;
        } else if (mProgress > 700 && mProgress <= 900) {
            ratio = (mProgress - 700) / 200;
            result = margin + circleOutRadius * 6 + realWidth * 2 + (ratio * realWidth);
        } else if (mProgress > 900) {
            result = margin + circleOutRadius * 8 + 3 * realWidth;
        }
        */
        if (mProgress > 0 && mProgress <= 15) {
            ratio = mProgress / 15;
            result = margin + circleOutRadius * 2 + (ratio * realWidth * 3 / 4);
        } else if (mProgress > 15 && mProgress <= 25) {
            ratio = (mProgress - 15) / 10;
            result = margin + circleOutRadius * 4 + (ratio * realWidth * 1 / 2) + realWidth * 3 / 4;
        } else if (mProgress > 25 && mProgress <= 30) {
            ratio = (mProgress - 25) / 5;
            result = margin + circleOutRadius * 6 + realWidth * 5 / 4 + (ratio * realWidth * 1 / 4);
        } else if (mProgress > 30 && mProgress <= 60) {
            ratio = (mProgress - 30) / 30;
            result = margin + circleOutRadius * 8 + realWidth * 3 / 2 + (ratio * realWidth * 3 /2);
        } else if (mProgress > 60) {
            result = margin + circleOutRadius * 10 + 3 * realWidth;
        }
        //canvas.drawText(Float.toString(mProgress), result, height / 2 - 100, mTvPaint);
        canvas.drawLine(margin + circleOutRadius, height / 2, result, height / 2, mLinePaint);

        if (mProgress <= 0) {
            return;
        }
        canvas.drawCircle(margin + circleOutRadius, height / 2, circleOutRadius, mCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius, height / 2, circleInRadius, mCircleInPaint);
        if (mProgress < 15) {
            return;
        }
        canvas.drawCircle(margin + circleOutRadius * (1 * 2 + 1) + realWidth * 3 / 4, height / 2, circleOutRadius, mCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (1 * 2 + 1) + realWidth * 3 / 4, height / 2, circleInRadius, mCircleInPaint);
        if (mProgress < 25) {
            return;
        }
        canvas.drawCircle(margin + circleOutRadius * (2 * 2 + 1) + realWidth * 5 / 4, height / 2, circleOutRadius, mCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (2 * 2 + 1) + realWidth * 5 / 4, height / 2, circleInRadius, mCircleInPaint);
        if (mProgress < 30) {
            return;
        }
        canvas.drawCircle(margin + circleOutRadius * (3 * 2 + 1) + realWidth * 3 / 2, height / 2, circleOutRadius, mCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (3 * 2 + 1) + realWidth * 3 / 2, height / 2, circleInRadius, mCircleInPaint);
        if (mProgress < 60) {
            return;
        }
        canvas.drawCircle(margin + circleOutRadius * (4 * 2 + 1) + realWidth * 3, height / 2, circleOutRadius, mCircleOutPaint);
        canvas.drawCircle(margin + circleOutRadius * (4 * 2 + 1) + realWidth * 3, height / 2, circleInRadius, mCircleInPaint);

    }
    public float getTextWidth(String text) {
        return mTvPaint.measureText(text) / 2;
    }
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
