package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tyut.R;
import com.tyut.utils.SPSingleton;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.UserVO;


public class ChoosePhotoPUW implements View.OnClickListener {



    private TextView takePhoto_tv;
    private TextView fromAlbum_tv;
    private TextView cancel_tv;
    PopupWindow popupWindow;

    View contentView;
    Context context;

    private ChoosePhotoPUW.ITakePhotoListener takePhotoListener;
    private ChoosePhotoPUW.IFromAlbumListener fromAlbumListener;

    public PopupWindow getPopupWindow() {
        return this.popupWindow;
    }

    public ChoosePhotoPUW setPopUpWindow(PopupWindow popUpWindow) {
        this.popupWindow= popUpWindow;
        return this;
    }



    public ChoosePhotoPUW setTakePhoto(ITakePhotoListener takePhotoListener) {
        this.takePhotoListener = takePhotoListener;
        return this;
    }

    public ChoosePhotoPUW setFromAlbum(IFromAlbumListener fromAlbumListener) {
        this.fromAlbumListener = fromAlbumListener;
        return this;
    }

    public ChoosePhotoPUW(Context context) {

        this.context = context;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.puw_choosephoto, null);
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
            case R.id.takePhoto_tv:

                if(takePhotoListener != null){
                    takePhotoListener.onTakePhoto(this);
                }
                popupWindow.dismiss();
                break;
            case R.id.fromAlbum_tv:

                if(fromAlbumListener != null){
                    fromAlbumListener.onFromAlbum(this);
                }
                popupWindow.dismiss();
                break;
        }
    }

    public interface ITakePhotoListener{
        void onTakePhoto(ChoosePhotoPUW puw);
    }
    public interface IFromAlbumListener{
        void onFromAlbum(ChoosePhotoPUW puw);
    }


    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_tv);
        takePhoto_tv = contentView.findViewById(R.id.takePhoto_tv);
        fromAlbum_tv = contentView.findViewById(R.id.fromAlbum_tv);

        cancel_tv.setOnClickListener(this);
        takePhoto_tv.setOnClickListener(this);
        fromAlbum_tv.setOnClickListener(this);
    }

}
