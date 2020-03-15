package com.tyut.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tyut.R;
import com.tyut.utils.PhotoUtil;
import com.tyut.utils.SPSingleton;
import com.tyut.vo.Reply;


public class SavePicPUW implements View.OnClickListener {

    private TextView savePic_tv;
    private TextView cancel_tv;
    private Bitmap bitmap;

    PopupWindow popupWindow;

    View contentView;
    Context context;


    private SavePicPUW.IDeleteListener deleteListener;

    public PopupWindow getPopupWindow() {
        return this.popupWindow;
    }



    public SavePicPUW(Context context, Bitmap bitmap) {

        this.context = context;
        this.bitmap = bitmap;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.puw_savepic, null);
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
            case R.id.savePic_tv:

                 String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
                 //检测是否有写的权限
                 int permission = ContextCompat.checkSelfPermission(context,
            "android.permission.WRITE_EXTERNAL_STORAGE");
                 if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                     ActivityCompat.requestPermissions((Activity) context, PERMISSIONS,1);
                 }
                Toast.makeText(context, PhotoUtil.saveImageToGallery(context, bitmap), Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                break;


        }
    }

    public interface IDeleteListener{
        void onDelete(SavePicPUW puw);
    }


    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_tv);
        savePic_tv = contentView.findViewById(R.id.savePic_tv);

        cancel_tv.setOnClickListener(this);
        savePic_tv.setOnClickListener(this);

    }

}
