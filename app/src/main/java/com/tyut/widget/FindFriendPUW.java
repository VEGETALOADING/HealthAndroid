package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tyut.R;


public class FindFriendPUW implements View.OnClickListener {

    private Button followALL_btn;
    private TextView cancel_tv;

    PopupWindow popupWindow;

    View contentView;
    Context context;




    public PopupWindow getPopupWindow() {
        return this.popupWindow;
    }

    public FindFriendPUW setPopUpWindow(PopupWindow popUpWindow) {
        this.popupWindow= popUpWindow;
        return this;
    }


    public FindFriendPUW(Context context) {

        this.context = context;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.puw_findfriend, null);
        initView();

    }

    public void showFoodPopWindow(){

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_tv:
                popupWindow.dismiss();
                break;
            case R.id.followAll_btn:
                //待实现
                popupWindow.dismiss();
                break;
        }
    }



    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_tv);
        followALL_btn = contentView.findViewById(R.id.followAll_btn);


        cancel_tv.setOnClickListener(this);
        followALL_btn.setOnClickListener(this);
    }

}
