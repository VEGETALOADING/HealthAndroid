package com.tyut.view.calendar.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.tyut.view.calendar.component.State;
import com.tyut.view.calendar.model.CalendarDate;


public class Day implements Parcelable {
    private State state;
    private CalendarDate date;
    private int posRow;
    private int posCol;
    public static final Creator<Day> CREATOR = new Creator<Day>() {
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    public Day(State state, CalendarDate date, int posRow, int posCol) {
        this.state = state;
        this.date = date;
        this.posRow = posRow;
        this.posCol = posCol;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public CalendarDate getDate() {
        return this.date;
    }

    public void setDate(CalendarDate date) {
        this.date = date;
    }

    public int getPosRow() {
        return this.posRow;
    }

    public void setPosRow(int posRow) {
        this.posRow = posRow;
    }

    public int getPosCol() {
        return this.posCol;
    }

    public void setPosCol(int posCol) {
        this.posCol = posCol;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeSerializable(this.date);
        dest.writeInt(this.posRow);
        dest.writeInt(this.posCol);
    }

    protected Day(Parcel in) {
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : State.values()[tmpState];
        this.date = (CalendarDate)in.readSerializable();
        this.posRow = in.readInt();
        this.posCol = in.readInt();
    }
}
