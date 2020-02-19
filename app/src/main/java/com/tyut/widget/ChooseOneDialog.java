package com.tyut.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tyut.R;
import com.tyut.view.ScrollPickView;

import java.util.List;


public class ChooseOneDialog extends Dialog implements View.OnClickListener {


    ImageView close_iv;
    TextView confirm_tv;
    ScrollPickView chooseSpv;
    String dataChosen;
    List<String> dataList;

    public String getDataChosen() {
        return dataChosen;
    }

    public void setDataChosen(String dataChosen) {
        this.dataChosen = dataChosen;
    }

    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public List<String> getDataList() {
        return dataList;
    }

    public ChooseOneDialog setDataList(List<String> dataList) {
        this.dataList = dataList;
        return this;
    }

    public ChooseOneDialog setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public ChooseOneDialog setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public ChooseOneDialog(@NonNull Context context, List<String> dataList) {
        super(context);
        this.dataList = dataList;
    }

    public ChooseOneDialog(@NonNull Context context, int themeId, List<String> dataList) {
        super(context, themeId);
        this.dataList = dataList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_chooseone);


        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x *0.8);
        getWindow().setAttributes(p);


        close_iv = findViewById(R.id.chooseone_dl_close);
        chooseSpv = findViewById(R.id.chooseone_spv);
        confirm_tv = findViewById(R.id.chooseone_confirm_tv);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);

        initScrollPick();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseone_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.chooseone_confirm_tv:
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;

        }
    }

    public interface IOnCancelListener{
        void onCancel(ChooseOneDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(ChooseOneDialog dialog);
    }


    private void initScrollPick(){

        chooseSpv.setData(dataList);
        chooseSpv.setSelected(0);

        //滚动选择事件
        chooseSpv.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                dataChosen = data;

            }
        });

    }

}
