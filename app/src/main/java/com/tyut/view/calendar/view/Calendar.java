package com.tyut.view.calendar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.tyut.view.calendar.Utils;
import com.tyut.view.calendar.component.CalendarAttr;
import com.tyut.view.calendar.component.CalendarRenderer;
import com.tyut.view.calendar.interf.IDayRenderer;
import com.tyut.view.calendar.interf.OnAdapterSelectListener;
import com.tyut.view.calendar.interf.OnSelectDateListener;
import com.tyut.view.calendar.model.CalendarDate;


@SuppressLint({"ViewConstructor"})
public class Calendar extends View {
    private CalendarAttr.CalendarType calendarType;
    private int cellHeight;
    private int cellWidth;
    private OnSelectDateListener onSelectDateListener;
    private Context context;
    private CalendarAttr calendarAttr;
    private CalendarRenderer renderer;
    private OnAdapterSelectListener onAdapterSelectListener;
    private float touchSlop;
    private float posX = 0.0F;
    private float posY = 0.0F;

    public Calendar(Context context, OnSelectDateListener onSelectDateListener, CalendarAttr attr) {
        super(context);
        this.onSelectDateListener = onSelectDateListener;
        this.calendarAttr = attr;
        this.init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.touchSlop = (float) Utils.getTouchSlop(context);
        this.initAttrAndRenderer();
    }

    private void initAttrAndRenderer() {
        this.renderer = new CalendarRenderer(this, this.calendarAttr, this.context);
        this.renderer.setOnSelectDateListener(this.onSelectDateListener);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.renderer.draw(canvas);
    }

    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        this.cellHeight = h / 6;
        this.cellWidth = w / 7;
        this.calendarAttr.setCellHeight(this.cellHeight);
        this.calendarAttr.setCellWidth(this.cellWidth);
        this.renderer.setAttr(this.calendarAttr);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case 0:
                this.posX = event.getX();
                this.posY = event.getY();
                break;
            case 1:
                float disX = event.getX() - this.posX;
                float disY = event.getY() - this.posY;
                if (Math.abs(disX) < this.touchSlop && Math.abs(disY) < this.touchSlop) {
                    int col = (int)(this.posX / (float)this.cellWidth);
                    int row = (int)(this.posY / (float)this.cellHeight);
                    this.onAdapterSelectListener.cancelSelectState();
                    this.renderer.onClickDate(col, row);
                    this.onAdapterSelectListener.updateSelectState();
                    this.invalidate();
                }
        }

        return true;
    }

    public CalendarAttr.CalendarType getCalendarType() {
        return this.calendarAttr.getCalendarType();
    }

    public void switchCalendarType(CalendarAttr.CalendarType calendarType) {
        this.calendarAttr.setCalendarType(calendarType);
        this.renderer.setAttr(this.calendarAttr);
    }

    public int getCellHeight() {
        return this.cellHeight;
    }

    public void resetSelectedRowIndex() {
        this.renderer.resetSelectedRowIndex();
    }

    public int getSelectedRowIndex() {
        return this.renderer.getSelectedRowIndex();
    }

    public void setSelectedRowIndex(int selectedRowIndex) {
        this.renderer.setSelectedRowIndex(selectedRowIndex);
    }

    public void setOnAdapterSelectListener(OnAdapterSelectListener onAdapterSelectListener) {
        this.onAdapterSelectListener = onAdapterSelectListener;
    }

    public void showDate(CalendarDate current) {
        this.renderer.showDate(current);
    }

    public void updateWeek(int rowCount) {
        this.renderer.updateWeek(rowCount);
        this.invalidate();
    }

    public void update() {
        this.renderer.update();
    }

    public void cancelSelectState() {
        this.renderer.cancelSelectState();
    }

    public CalendarDate getSeedDate() {
        return this.renderer.getSeedDate();
    }

    public void setDayRenderer(IDayRenderer dayRenderer) {
        this.renderer.setDayRenderer(dayRenderer);
    }
}