package com.tyut;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tyut.utils.StringUtil;
import com.tyut.view.HeightRulerView;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;
import com.tyut.view.ScrollPickView;
import com.tyut.view.WeightRulerView;

import java.util.Calendar;
import java.util.List;

public class TestActivity extends AppCompatActivity {

   CalendarView calendarView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(TestActivity.this, year+"-"+(month+1)+"-"+dayOfMonth, Toast.LENGTH_LONG).show();
            }
        });





    }




}
