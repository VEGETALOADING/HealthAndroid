package com.tyut.view.calendar.component;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;


import com.tyut.view.calendar.Utils;
import com.tyut.view.calendar.interf.IDayRenderer;
import com.tyut.view.calendar.interf.OnAdapterSelectListener;
import com.tyut.view.calendar.interf.OnSelectDateListener;
import com.tyut.view.calendar.model.CalendarDate;
import com.tyut.view.calendar.view.Calendar;
import com.tyut.view.calendar.view.MonthPager;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarViewAdapter extends PagerAdapter {
    private static CalendarDate date = new CalendarDate();
    private ArrayList<Calendar> calendars = new ArrayList();
    private int currentPosition;
    private CalendarAttr.CalendarType calendarType;
    private int rowCount;
    private CalendarDate seedDate;
    private OnCalendarTypeChanged onCalendarTypeChangedListener;
    private CalendarAttr.WeekArrayType weekArrayType;

    public CalendarViewAdapter(Context context, OnSelectDateListener onSelectDateListener, CalendarAttr.CalendarType calendarType, CalendarAttr.WeekArrayType weekArrayType, IDayRenderer dayView) {
        this.calendarType = CalendarAttr.CalendarType.MONTH;
        this.rowCount = 0;
        this.weekArrayType = CalendarAttr.WeekArrayType.Monday;
        this.calendarType = calendarType;
        this.weekArrayType = weekArrayType;
        this.init(context, onSelectDateListener);
        this.setCustomDayRenderer(dayView);
    }

    private void init(Context context, OnSelectDateListener onSelectDateListener) {
        saveSelectedDate(new CalendarDate());
        this.seedDate = new CalendarDate();

        for(int i = 0; i < 3; ++i) {
            CalendarAttr calendarAttr = new CalendarAttr();
            calendarAttr.setCalendarType(CalendarAttr.CalendarType.MONTH);
            calendarAttr.setWeekArrayType(this.weekArrayType);
            Calendar calendar = new Calendar(context, onSelectDateListener, calendarAttr);
            calendar.setOnAdapterSelectListener(new OnAdapterSelectListener() {
                public void cancelSelectState() {
                    CalendarViewAdapter.this.cancelOtherSelectState();
                }

                public void updateSelectState() {
                    CalendarViewAdapter.this.invalidateCurrentCalendar();
                }
            });
            this.calendars.add(calendar);
        }

    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Log.e("ldf", "setPrimaryItem");
        super.setPrimaryItem(container, position, object);
        this.currentPosition = position;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("ldf", "instantiateItem");
        if (position < 2) {
            return null;
        } else {
            Calendar calendar = (Calendar)this.calendars.get(position % this.calendars.size());
            CalendarDate current;
            if (this.calendarType == CalendarAttr.CalendarType.MONTH) {
                current = this.seedDate.modifyMonth(position - MonthPager.CURRENT_DAY_INDEX);
                current.setDay(1);
                calendar.showDate(current);
            } else {
                current = this.seedDate.modifyWeek(position - MonthPager.CURRENT_DAY_INDEX);
                if (this.weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                    calendar.showDate(Utils.getSaturday(current));
                } else {
                    calendar.showDate(Utils.getSunday(current));
                }

                calendar.updateWeek(this.rowCount);
            }

            if (container.getChildCount() == this.calendars.size()) {
                container.removeView((View)this.calendars.get(position % 3));
            }

            if (container.getChildCount() < this.calendars.size()) {
                container.addView(calendar, 0);
            } else {
                container.addView(calendar, position % 3);
            }

            return calendar;
        }
    }

    public int getCount() {
        return 2147483647;
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == (View)object;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(container);
    }

    public ArrayList<Calendar> getPagers() {
        return this.calendars;
    }

    public void cancelOtherSelectState() {
        for(int i = 0; i < this.calendars.size(); ++i) {
            Calendar calendar = (Calendar)this.calendars.get(i);
            calendar.cancelSelectState();
        }

    }

    public void invalidateCurrentCalendar() {
        for(int i = 0; i < this.calendars.size(); ++i) {
            Calendar calendar = (Calendar)this.calendars.get(i);
            calendar.update();
            if (calendar.getCalendarType() == CalendarAttr.CalendarType.WEEK) {
                calendar.updateWeek(this.rowCount);
            }
        }

    }

    public void setMarkData(HashMap<String, String> markData) {
        Utils.setMarkData(markData);
    }

    public void switchToMonth() {
        if (this.calendars != null && this.calendars.size() > 0 && this.calendarType != CalendarAttr.CalendarType.MONTH) {
            this.onCalendarTypeChangedListener.onCalendarTypeChanged(CalendarAttr.CalendarType.MONTH);
            this.calendarType = CalendarAttr.CalendarType.MONTH;
            MonthPager.CURRENT_DAY_INDEX = this.currentPosition;
            Calendar v = (Calendar)this.calendars.get(this.currentPosition % 3);
            this.seedDate = v.getSeedDate();
            Calendar v1 = (Calendar)this.calendars.get(this.currentPosition % 3);
            v1.switchCalendarType(CalendarAttr.CalendarType.MONTH);
            v1.showDate(this.seedDate);
            Calendar v2 = (Calendar)this.calendars.get((this.currentPosition - 1) % 3);
            v2.switchCalendarType(CalendarAttr.CalendarType.MONTH);
            CalendarDate last = this.seedDate.modifyMonth(-1);
            last.setDay(1);
            v2.showDate(last);
            Calendar v3 = (Calendar)this.calendars.get((this.currentPosition + 1) % 3);
            v3.switchCalendarType(CalendarAttr.CalendarType.MONTH);
            CalendarDate next = this.seedDate.modifyMonth(1);
            next.setDay(1);
            v3.showDate(next);
        }

    }

    public void switchToWeek(int rowIndex) {
        this.rowCount = rowIndex;
        if (this.calendars != null && this.calendars.size() > 0 && this.calendarType != CalendarAttr.CalendarType.WEEK) {
            this.onCalendarTypeChangedListener.onCalendarTypeChanged(CalendarAttr.CalendarType.WEEK);
            this.calendarType = CalendarAttr.CalendarType.WEEK;
            MonthPager.CURRENT_DAY_INDEX = this.currentPosition;
            Calendar v = (Calendar)this.calendars.get(this.currentPosition % 3);
            this.seedDate = v.getSeedDate();
            this.rowCount = v.getSelectedRowIndex();
            Calendar v1 = (Calendar)this.calendars.get(this.currentPosition % 3);
            v1.switchCalendarType(CalendarAttr.CalendarType.WEEK);
            v1.showDate(this.seedDate);
            v1.updateWeek(rowIndex);
            Calendar v2 = (Calendar)this.calendars.get((this.currentPosition - 1) % 3);
            v2.switchCalendarType(CalendarAttr.CalendarType.WEEK);
            CalendarDate last = this.seedDate.modifyWeek(-1);
            if (this.weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v2.showDate(Utils.getSaturday(last));
            } else {
                v2.showDate(Utils.getSunday(last));
            }

            v2.updateWeek(rowIndex);
            Calendar v3 = (Calendar)this.calendars.get((this.currentPosition + 1) % 3);
            v3.switchCalendarType(CalendarAttr.CalendarType.WEEK);
            CalendarDate next = this.seedDate.modifyWeek(1);
            if (this.weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v3.showDate(Utils.getSaturday(next));
            } else {
                v3.showDate(Utils.getSunday(next));
            }

            v3.updateWeek(rowIndex);
        }

    }

    public void notifyMonthDataChanged(CalendarDate date) {
        this.seedDate = date;
        this.refreshCalendar();
    }

    public void notifyDataChanged(CalendarDate date) {
        this.seedDate = date;
        saveSelectedDate(date);
        this.refreshCalendar();
    }

    public void notifyDataChanged() {
        this.refreshCalendar();
    }

    private void refreshCalendar() {
        Calendar v1;
        Calendar v2;
        CalendarDate last;
        Calendar v3;
        CalendarDate next;
        if (this.calendarType == CalendarAttr.CalendarType.WEEK) {
            MonthPager.CURRENT_DAY_INDEX = this.currentPosition;
            v1 = (Calendar)this.calendars.get(this.currentPosition % 3);
            v1.showDate(this.seedDate);
            v1.updateWeek(this.rowCount);
            v2 = (Calendar)this.calendars.get((this.currentPosition - 1) % 3);
            last = this.seedDate.modifyWeek(-1);
            if (this.weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v2.showDate(Utils.getSaturday(last));
            } else {
                v2.showDate(Utils.getSunday(last));
            }

            v2.updateWeek(this.rowCount);
            v3 = (Calendar)this.calendars.get((this.currentPosition + 1) % 3);
            next = this.seedDate.modifyWeek(1);
            if (this.weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v3.showDate(Utils.getSaturday(next));
            } else {
                v3.showDate(Utils.getSunday(next));
            }

            v3.updateWeek(this.rowCount);
        } else {
            MonthPager.CURRENT_DAY_INDEX = this.currentPosition;
            v1 = (Calendar)this.calendars.get(this.currentPosition % 3);
            v1.showDate(this.seedDate);
            v2 = (Calendar)this.calendars.get((this.currentPosition - 1) % 3);
            last = this.seedDate.modifyMonth(-1);
            last.setDay(1);
            v2.showDate(last);
            v3 = (Calendar)this.calendars.get((this.currentPosition + 1) % 3);
            next = this.seedDate.modifyMonth(1);
            next.setDay(1);
            v3.showDate(next);
        }

    }

    public static void saveSelectedDate(CalendarDate calendarDate) {
        date = calendarDate;
    }

    public static CalendarDate loadSelectedDate() {
        return date;
    }

    public CalendarAttr.CalendarType getCalendarType() {
        return this.calendarType;
    }

    public void setCustomDayRenderer(IDayRenderer dayRenderer) {
        Calendar c0 = (Calendar)this.calendars.get(0);
        c0.setDayRenderer(dayRenderer);
        Calendar c1 = (Calendar)this.calendars.get(1);
        c1.setDayRenderer(dayRenderer.copy());
        Calendar c2 = (Calendar)this.calendars.get(2);
        c2.setDayRenderer(dayRenderer.copy());
    }

    public void setOnCalendarTypeChangedListener(OnCalendarTypeChanged onCalendarTypeChangedListener) {
        this.onCalendarTypeChangedListener = onCalendarTypeChangedListener;
    }

    public CalendarAttr.WeekArrayType getWeekArrayType() {
        return this.weekArrayType;
    }

    public interface OnCalendarTypeChanged {
        void onCalendarTypeChanged(CalendarAttr.CalendarType var1);
    }
}
