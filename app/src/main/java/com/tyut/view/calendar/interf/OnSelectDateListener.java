package com.tyut.view.calendar.interf;


import com.tyut.view.calendar.model.CalendarDate;

public interface OnSelectDateListener {
    void onSelectDate(CalendarDate var1);

    void onSelectOtherMonth(int var1);
}
