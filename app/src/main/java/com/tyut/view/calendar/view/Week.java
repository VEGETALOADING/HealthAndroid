package com.tyut.view.calendar.view;

public class Week {
    public int row;
    public Day[] days = new Day[7];

    public Week(int row) {
        this.row = row;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Day[] getDays() {
        return this.days;
    }

    public void setDays(Day[] days) {
        this.days = days;
    }
}
