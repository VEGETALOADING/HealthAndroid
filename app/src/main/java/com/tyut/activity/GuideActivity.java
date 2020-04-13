package com.tyut.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.tyut.R;

public class GuideActivity extends AppCompatActivity {


    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideActivity.this.startActivity( new Intent(GuideActivity.this, RegisterActivity.class));
            }
        });

    }
}

