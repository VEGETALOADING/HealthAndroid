package com.tyut;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.ScrollPickView;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    ScrollPickView scrollPickView;
    MyHorizontalScrollView scrollView;
    TextView show;
    String date;
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        btn = findViewById(R.id.btn_test);
        show = findViewById(R.id.show_tv);

        //scrollPickView = findViewById(R.id.sv);;


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalendar();
            }
        });


    }

    public void myCalendar(){
        //初始化对话框             R.style.CalendarDialog 是自定义的弹框主题，在styles设置
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CalendarDialog);
        //初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        //绑定布局
        View customLayout = layoutInflater.inflate(R.layout.testpicked, (ViewGroup) findViewById(R.id.customDialog));
        //为对话框设置视图
        builder.setView(customLayout);

        scrollPickView = customLayout.findViewById(R.id.testView);


        //定义滚动选择器的数据项（年月日的）
        List<String> dateList = StringUtil.getRecengtDateList();



        //为滚动选择器设置数据
        scrollPickView.setData(dateList);

        //滚动选择事件
        scrollPickView.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {

                date = data;

            }
        });


        //对话框的确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                show.setText(date);

            }
        });
        //对话框的取消按钮
        builder.setNegativeButton("取消", null);
        //显示对话框
        builder.show();


    }

}
