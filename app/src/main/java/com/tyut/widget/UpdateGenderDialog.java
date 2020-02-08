package com.tyut.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.tyut.R;
import com.tyut.utils.StringUtil;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.view.RulerView;
import com.tyut.view.ScrollPickView;

import java.util.ArrayList;
import java.util.List;


public class UpdateGenderDialog extends Dialog implements View.OnClickListener {


    ImageView close_iv;
    TextView confirm_tv;
    ScrollPickView chooseGender;
    String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public UpdateGenderDialog setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public UpdateGenderDialog setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public UpdateGenderDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateGenderDialog(@NonNull Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gender);


        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x *0.8);
        getWindow().setAttributes(p);


        close_iv = findViewById(R.id.gender_dl_close);
        chooseGender = findViewById(R.id.choosegender_spv);
        confirm_tv = findViewById(R.id.confirmgender_tv);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);

        initScrollPick();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gender_dl_close:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.confirmgender_tv:
                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;

        }
    }

    public interface IOnCancelListener{
        void onCancel(UpdateGenderDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(UpdateGenderDialog dialog);
    }


    private void initScrollPick(){

        List<String> genders = new ArrayList<>();
        genders.add("男");
        genders.add("女");

        chooseGender.setData(genders);
        chooseGender.setSelected(0);

        //滚动选择事件
        chooseGender.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                gender = data;

            }
        });

    }

}
