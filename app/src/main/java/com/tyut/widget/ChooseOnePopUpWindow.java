package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tyut.R;
import com.tyut.view.ScrollPickView;

import java.util.List;


public class ChooseOnePopUpWindow implements View.OnClickListener {

    private ImageView close_iv;
    private TextView confirm_tv;
    private ScrollPickView chooseSpv;
    private String dataChosen;
    private List<String> dataList;

    public String getDataChosen() {
        return dataChosen;
    }

    public ChooseOnePopUpWindow setDataChosen(String dataChosen) {
        this.dataChosen = dataChosen;
        return this;
    }

    PopupWindow onePopUpWindow;

    View contentView;
    Context context;

    Integer userId = null;


    private ChooseOnePopUpWindow.IOnCancelListener cancelListener;
    private ChooseOnePopUpWindow.IOnConfirmListener confirmListener;


    public PopupWindow getOnePopUpWindow() {
        return this.onePopUpWindow;
    }

    public ChooseOnePopUpWindow setOnePopUpWindow(PopupWindow onePopUpWindow) {
        this.onePopUpWindow= onePopUpWindow;
        return this;
    }



    public ChooseOnePopUpWindow setCancel(ChooseOnePopUpWindow.IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public ChooseOnePopUpWindow setConfirm(ChooseOnePopUpWindow.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public ChooseOnePopUpWindow(Context context,  List<String> dataList, String dataChosen) {

        this.context = context;
        this.dataList = dataList;
        this.dataChosen = dataChosen;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_chooseone, null);
        initView();
        initScrollPick();

    }

    public void showFoodPopWindow(){

        onePopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        onePopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        onePopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        onePopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        onePopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        onePopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        onePopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }

    private void initScrollPick(){

        chooseSpv.setData(dataList);
        int x = dataList.indexOf(dataChosen);
        if(dataChosen != null && 0 <= dataList.indexOf(dataChosen) && dataList.indexOf(dataChosen)< dataList.size()) {
            chooseSpv.setSelected(dataList.indexOf(dataChosen));
        }else{
            chooseSpv.setSelected(0);
        }

        //滚动选择事件
        chooseSpv.setOnSelectListener(new ScrollPickView.onSelectListener() {
            @Override
            public void onSelect(String data) {
                dataChosen = data;

            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseone_dl_close:
                onePopUpWindow.dismiss();
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.chooseone_confirm_tv:

                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                onePopUpWindow.dismiss();


                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(ChooseOnePopUpWindow weightPopUpWindow);
    }
    public interface IOnConfirmListener{
        void onConfirm(ChooseOnePopUpWindow weightPopUpWindow);
    }


    private void initView(){

        close_iv = contentView.findViewById(R.id.chooseone_dl_close);
        chooseSpv = contentView.findViewById(R.id.chooseone_spv);
        confirm_tv = contentView.findViewById(R.id.chooseone_confirm_tv);

        confirm_tv.setOnClickListener(this);
        close_iv.setOnClickListener(this);
    }

}
