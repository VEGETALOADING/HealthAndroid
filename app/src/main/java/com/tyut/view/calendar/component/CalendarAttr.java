package com.tyut.view.calendar.component;


public class CalendarAttr {
    private WeekArrayType weekArrayType;
    private CalendarType calendarType;
    private int cellHeight;
    private int cellWidth;

    public CalendarAttr() {
    }

    public WeekArrayType getWeekArrayType() {
        return this.weekArrayType;
    }

    public void setWeekArrayType(WeekArrayType weekArrayType) {
        this.weekArrayType = weekArrayType;
    }

    public CalendarType getCalendarType() {
        return this.calendarType;
    }

    public void setCalendarType(CalendarType calendarType) {
        this.calendarType = calendarType;
    }

    public int getCellHeight() {
        return this.cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getCellWidth() {
        return this.cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public static enum CalendarType {
        WEEK,
        MONTH;

        private CalendarType() {
        }
    }

    public static enum WeekArrayType {
        Sunday,
        Monday;

        private WeekArrayType() {
        }
    }
}
