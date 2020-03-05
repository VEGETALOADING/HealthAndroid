package com.tyut.view.calendar.interf;

import android.graphics.Canvas;
import com.tyut.view.calendar.view.Day;


public interface IDayRenderer {
    void refreshContent();

    void drawDay(Canvas var1, Day var2);

    IDayRenderer copy();
}