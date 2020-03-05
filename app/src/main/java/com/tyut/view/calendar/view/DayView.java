package com.tyut.view.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tyut.view.calendar.interf.IDayRenderer;

public abstract class DayView extends RelativeLayout implements IDayRenderer {
    protected Day day;
    protected Context context;
    protected int layoutResource;

    public DayView(Context context, int layoutResource) {
        super(context);
        this.setupLayoutResource(layoutResource);
        this.context = context;
        this.layoutResource = layoutResource;
    }

    private void setupLayoutResource(int layoutResource) {
        View inflated = LayoutInflater.from(this.getContext()).inflate(layoutResource, this);
        inflated.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        inflated.layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
    }
    @Override
    public void refreshContent() {
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void drawDay(Canvas canvas, Day day) {
        this.day = day;
        this.refreshContent();
        int saveId = canvas.save();
        canvas.translate((float)this.getTranslateX(canvas, day), (float)(day.getPosRow() * this.getMeasuredHeight()));
        this.draw(canvas);
        canvas.restoreToCount(saveId);
    }

    private int getTranslateX(Canvas canvas, Day day) {
        int canvasWidth = canvas.getWidth() / 7;
        int viewWidth = this.getMeasuredWidth();
        int moveX = (canvasWidth - viewWidth) / 2;
        int dx = day.getPosCol() * canvasWidth + moveX;
        return dx;
    }
}
