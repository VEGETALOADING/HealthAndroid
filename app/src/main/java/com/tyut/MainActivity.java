package com.tyut;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tyut.view.MyProgressView;

public class MainActivity extends AppCompatActivity {

    MyProgressView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.progess);
        view.setProgress(32);
    }
}
