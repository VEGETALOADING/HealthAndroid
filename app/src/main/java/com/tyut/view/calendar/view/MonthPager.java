package com.tyut.view.calendar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.tyut.view.calendar.behavior.MonthPagerBehavior;
import com.tyut.view.calendar.component.CalendarViewAdapter;


@CoordinatorLayout.DefaultBehavior(MonthPagerBehavior.class)
public class MonthPager extends ViewPager {
    public static int CURRENT_DAY_INDEX = 1000;
    private int currentPosition;
    private int cellHeight;
    private int viewHeight;
    private int rowIndex;
    private OnPageChangeListener monthPageChangeListener;
    private boolean pageChangeByGesture;
    private boolean hasPageChangeListener;
    private boolean scrollable;
    private int pageScrollState;

    public MonthPager(Context context) {
        this(context, (AttributeSet)null);
    }

    public MonthPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.currentPosition = CURRENT_DAY_INDEX;
        this.rowIndex = 6;
        this.pageChangeByGesture = false;
        this.hasPageChangeListener = false;
        this.scrollable = true;
        this.pageScrollState = 0;
        this.init();
    }

    private void init() {
        ViewPager.OnPageChangeListener viewPageChangeListener = new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (MonthPager.this.monthPageChangeListener != null) {
                    MonthPager.this.monthPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

            }

            public void onPageSelected(int position) {
                MonthPager.this.currentPosition = position;
                if (MonthPager.this.pageChangeByGesture) {
                    if (MonthPager.this.monthPageChangeListener != null) {
                        MonthPager.this.monthPageChangeListener.onPageSelected(position);
                    }

                    MonthPager.this.pageChangeByGesture = false;
                }

            }

            public void onPageScrollStateChanged(int state) {
                MonthPager.this.pageScrollState = state;
                if (MonthPager.this.monthPageChangeListener != null) {
                    MonthPager.this.monthPageChangeListener.onPageScrollStateChanged(state);
                }

                MonthPager.this.pageChangeByGesture = true;
            }
        };
        this.addOnPageChangeListener(viewPageChangeListener);
        this.hasPageChangeListener = true;
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (this.hasPageChangeListener) {
            Log.e("ldf", "MonthPager Just Can Use Own OnPageChangeListener");
        } else {
            super.addOnPageChangeListener(listener);
        }

    }

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        this.monthPageChangeListener = listener;
        Log.e("ldf", "MonthPager Just Can Use Own OnPageChangeListener");
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean onTouchEvent(MotionEvent me) {
        return !this.scrollable ? false : super.onTouchEvent(me);
    }

    public boolean onInterceptTouchEvent(MotionEvent me) {
        return !this.scrollable ? false : super.onInterceptTouchEvent(me);
    }

    public void selectOtherMonth(int offset) {
        this.setCurrentItem(this.currentPosition + offset);
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter)this.getAdapter();
        calendarViewAdapter.notifyDataChanged(CalendarViewAdapter.loadSelectedDate());
    }

    public int getPageScrollState() {
        return this.pageScrollState;
    }

    public int getTopMovableDistance() {
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter)this.getAdapter();
        if (calendarViewAdapter == null) {
            return this.cellHeight;
        } else {
            this.rowIndex = ((Calendar)calendarViewAdapter.getPagers().get(this.currentPosition % 3)).getSelectedRowIndex();
            return this.cellHeight * this.rowIndex;
        }
    }

    public int getCellHeight() {
        return this.cellHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.cellHeight = viewHeight / 6;
        this.viewHeight = viewHeight;
    }

    public int getViewHeight() {
        return this.viewHeight;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getRowIndex() {
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter)this.getAdapter();
        this.rowIndex = ((Calendar)calendarViewAdapter.getPagers().get(this.currentPosition % 3)).getSelectedRowIndex();
        Log.e("ldf", "getRowIndex = " + this.rowIndex);
        return this.rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int var1, float var2, int var3);

        void onPageSelected(int var1);

        void onPageScrollStateChanged(int var1);
    }
}
