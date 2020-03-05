package com.tyut.view.calendar.behavior;


import android.util.Log;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tyut.view.calendar.Utils;
import com.tyut.view.calendar.component.CalendarViewAdapter;
import com.tyut.view.calendar.view.MonthPager;


public class MonthPagerBehavior extends CoordinatorLayout.Behavior<MonthPager> {
    private int top = 0;
    private int touchSlop = 1;
    private int offsetY = 0;
    private int dependentViewTop = -1;

    public MonthPagerBehavior() {
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, MonthPager child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    public boolean onLayoutChild(CoordinatorLayout parent, MonthPager child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        child.offsetTopAndBottom(this.top);
        return true;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, MonthPager child, View dependency) {
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter)child.getAdapter();
        if (this.dependentViewTop != -1) {
            int dy = dependency.getTop() - this.dependentViewTop;
            int top = child.getTop();
            if (dy > this.touchSlop) {
                calendarViewAdapter.switchToMonth();
            } else if (dy < -this.touchSlop) {
                calendarViewAdapter.switchToWeek(child.getRowIndex());
            }

            if (dy > -top) {
                dy = -top;
            }

            if (dy < -top - child.getTopMovableDistance()) {
                dy = -top - child.getTopMovableDistance();
            }

            child.offsetTopAndBottom(dy);
            Log.e("ldf", "onDependentViewChanged = " + dy);
        }

        this.dependentViewTop = dependency.getTop();
        this.top = child.getTop();
        if (this.offsetY > child.getCellHeight()) {
            calendarViewAdapter.switchToMonth();
        }

        if (this.offsetY < -child.getCellHeight()) {
            calendarViewAdapter.switchToWeek(child.getRowIndex());
        }

        if (this.dependentViewTop > child.getCellHeight() - 24 && this.dependentViewTop < child.getCellHeight() + 24 && this.top > -this.touchSlop - child.getTopMovableDistance() && this.top < this.touchSlop - child.getTopMovableDistance()) {
            Utils.setScrollToBottom(true);
            calendarViewAdapter.switchToWeek(child.getRowIndex());
            this.offsetY = 0;
        }

        if (this.dependentViewTop > child.getViewHeight() - 24 && this.dependentViewTop < child.getViewHeight() + 24 && this.top < this.touchSlop && this.top > -this.touchSlop) {
            Utils.setScrollToBottom(false);
            calendarViewAdapter.switchToMonth();
            this.offsetY = 0;
        }

        return true;
    }
}
