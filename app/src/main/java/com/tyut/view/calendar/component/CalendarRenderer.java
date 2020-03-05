package com.tyut.view.calendar.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import com.tyut.view.calendar.Utils;
import com.tyut.view.calendar.interf.IDayRenderer;
import com.tyut.view.calendar.interf.OnSelectDateListener;
import com.tyut.view.calendar.model.CalendarDate;
import com.tyut.view.calendar.view.Calendar;
import com.tyut.view.calendar.view.Day;
import com.tyut.view.calendar.view.Week;

public class CalendarRenderer {
    private Week[] weeks = new Week[6];
    private Calendar calendar;
    private CalendarAttr attr;
    private IDayRenderer dayRenderer;
    private Context context;
    private OnSelectDateListener onSelectDateListener;
    private CalendarDate seedDate;
    private CalendarDate selectedDate;
    private int selectedRowIndex = 0;

    public CalendarRenderer(Calendar calendar, CalendarAttr attr, Context context) {
        this.calendar = calendar;
        this.attr = attr;
        this.context = context;
    }

    public void draw(Canvas canvas) {
        for(int row = 0; row < 6; ++row) {
            if (this.weeks[row] != null) {
                for(int col = 0; col < 7; ++col) {
                    if (this.weeks[row].days[col] != null) {
                        this.dayRenderer.drawDay(canvas, this.weeks[row].days[col]);
                    }
                }
            }
        }

    }

    public void onClickDate(int col, int row) {
        if (col < 7 && row < 6) {
            if (this.weeks[row] != null) {
                if (this.attr.getCalendarType() == CalendarAttr.CalendarType.MONTH) {
                    if (this.weeks[row].days[col].getState() == State.CURRENT_MONTH) {
                        this.weeks[row].days[col].setState(State.SELECT);
                        this.selectedDate = this.weeks[row].days[col].getDate();
                        CalendarViewAdapter.saveSelectedDate(this.selectedDate);
                        this.onSelectDateListener.onSelectDate(this.selectedDate);
                        this.seedDate = this.selectedDate;
                    } else if (this.weeks[row].days[col].getState() == State.PAST_MONTH) {
                        this.selectedDate = this.weeks[row].days[col].getDate();
                        CalendarViewAdapter.saveSelectedDate(this.selectedDate);
                        this.onSelectDateListener.onSelectOtherMonth(-1);
                        this.onSelectDateListener.onSelectDate(this.selectedDate);
                    } else if (this.weeks[row].days[col].getState() == State.NEXT_MONTH) {
                        this.selectedDate = this.weeks[row].days[col].getDate();
                        CalendarViewAdapter.saveSelectedDate(this.selectedDate);
                        this.onSelectDateListener.onSelectOtherMonth(1);
                        this.onSelectDateListener.onSelectDate(this.selectedDate);
                    }
                } else {
                    this.weeks[row].days[col].setState(State.SELECT);
                    this.selectedDate = this.weeks[row].days[col].getDate();
                    CalendarViewAdapter.saveSelectedDate(this.selectedDate);
                    this.onSelectDateListener.onSelectDate(this.selectedDate);
                    this.seedDate = this.selectedDate;
                }
            }

        }
    }

    public void updateWeek(int rowIndex) {
        CalendarDate currentWeekLastDay;
        if (this.attr.getWeekArrayType() == CalendarAttr.WeekArrayType.Sunday) {
            currentWeekLastDay = Utils.getSaturday(this.seedDate);
        } else {
            currentWeekLastDay = Utils.getSunday(this.seedDate);
        }

        int day = currentWeekLastDay.day;

        for(int i = 6; i >= 0; --i) {
            CalendarDate date = currentWeekLastDay.modifyDay(day);
            if (this.weeks[rowIndex] == null) {
                this.weeks[rowIndex] = new Week(rowIndex);
            }

            if (this.weeks[rowIndex].days[i] != null) {
                if (date.equals(CalendarViewAdapter.loadSelectedDate())) {
                    this.weeks[rowIndex].days[i].setState(State.SELECT);
                    this.weeks[rowIndex].days[i].setDate(date);
                } else {
                    this.weeks[rowIndex].days[i].setState(State.CURRENT_MONTH);
                    this.weeks[rowIndex].days[i].setDate(date);
                }
            } else if (date.equals(CalendarViewAdapter.loadSelectedDate())) {
                this.weeks[rowIndex].days[i] = new Day(State.SELECT, date, rowIndex, i);
            } else {
                this.weeks[rowIndex].days[i] = new Day(State.CURRENT_MONTH, date, rowIndex, i);
            }

            --day;
        }

    }

    private void instantiateMonth() {
        int lastMonthDays = Utils.getMonthDays(this.seedDate.year, this.seedDate.month - 1);
        int currentMonthDays = Utils.getMonthDays(this.seedDate.year, this.seedDate.month);
        int firstDayPosition = Utils.getFirstDayWeekPosition(this.seedDate.year, this.seedDate.month, this.attr.getWeekArrayType());
        Log.e("ldf", "firstDayPosition = " + firstDayPosition);
        int day = 0;

        for(int row = 0; row < 6; ++row) {
            day = this.fillWeek(lastMonthDays, currentMonthDays, firstDayPosition, day, row);
        }

    }

    private int fillWeek(int lastMonthDays, int currentMonthDays, int firstDayWeek, int day, int row) {
        for(int col = 0; col < 7; ++col) {
            int position = col + row * 7;
            if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {
                ++day;
                this.fillCurrentMonthDate(day, row, col);
            } else if (position < firstDayWeek) {
                this.instantiateLastMonth(lastMonthDays, firstDayWeek, row, col, position);
            } else if (position >= firstDayWeek + currentMonthDays) {
                this.instantiateNextMonth(currentMonthDays, firstDayWeek, row, col, position);
            }
        }

        return day;
    }

    private void fillCurrentMonthDate(int day, int row, int col) {
        CalendarDate date = this.seedDate.modifyDay(day);
        if (this.weeks[row] == null) {
            this.weeks[row] = new Week(row);
        }

        if (this.weeks[row].days[col] != null) {
            if (date.equals(CalendarViewAdapter.loadSelectedDate())) {
                this.weeks[row].days[col].setDate(date);
                this.weeks[row].days[col].setState(State.SELECT);
            } else {
                this.weeks[row].days[col].setDate(date);
                this.weeks[row].days[col].setState(State.CURRENT_MONTH);
            }
        } else if (date.equals(CalendarViewAdapter.loadSelectedDate())) {
            this.weeks[row].days[col] = new Day(State.SELECT, date, row, col);
        } else {
            this.weeks[row].days[col] = new Day(State.CURRENT_MONTH, date, row, col);
        }

        if (date.equals(this.seedDate)) {
            this.selectedRowIndex = row;
        }

    }

    private void instantiateNextMonth(int currentMonthDays, int firstDayWeek, int row, int col, int position) {
        CalendarDate date = new CalendarDate(this.seedDate.year, this.seedDate.month + 1, position - firstDayWeek - currentMonthDays + 1);
        if (this.weeks[row] == null) {
            this.weeks[row] = new Week(row);
        }

        if (this.weeks[row].days[col] != null) {
            this.weeks[row].days[col].setDate(date);
            this.weeks[row].days[col].setState(State.NEXT_MONTH);
        } else {
            this.weeks[row].days[col] = new Day(State.NEXT_MONTH, date, row, col);
        }

    }

    private void instantiateLastMonth(int lastMonthDays, int firstDayWeek, int row, int col, int position) {
        CalendarDate date = new CalendarDate(this.seedDate.year, this.seedDate.month - 1, lastMonthDays - (firstDayWeek - position - 1));
        if (this.weeks[row] == null) {
            this.weeks[row] = new Week(row);
        }

        if (this.weeks[row].days[col] != null) {
            this.weeks[row].days[col].setDate(date);
            this.weeks[row].days[col].setState(State.PAST_MONTH);
        } else {
            this.weeks[row].days[col] = new Day(State.PAST_MONTH, date, row, col);
        }

    }

    public void showDate(CalendarDate seedDate) {
        if (seedDate != null) {
            this.seedDate = seedDate;
        } else {
            this.seedDate = new CalendarDate();
        }

        this.update();
    }

    public void update() {
        this.instantiateMonth();
        this.calendar.invalidate();
    }

    public CalendarDate getSeedDate() {
        return this.seedDate;
    }

    public void cancelSelectState() {
        for(int i = 0; i < 6; ++i) {
            if (this.weeks[i] != null) {
                for(int j = 0; j < 7; ++j) {
                    if (this.weeks[i].days[j].getState() == State.SELECT) {
                        this.weeks[i].days[j].setState(State.CURRENT_MONTH);
                        this.resetSelectedRowIndex();
                        break;
                    }
                }
            }
        }

    }

    public void resetSelectedRowIndex() {
        this.selectedRowIndex = 0;
    }

    public int getSelectedRowIndex() {
        return this.selectedRowIndex;
    }

    public void setSelectedRowIndex(int selectedRowIndex) {
        this.selectedRowIndex = selectedRowIndex;
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public CalendarAttr getAttr() {
        return this.attr;
    }

    public void setAttr(CalendarAttr attr) {
        this.attr = attr;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnSelectDateListener(OnSelectDateListener onSelectDateListener) {
        this.onSelectDateListener = onSelectDateListener;
    }

    public void setDayRenderer(IDayRenderer dayRenderer) {
        this.dayRenderer = dayRenderer;
    }
}
