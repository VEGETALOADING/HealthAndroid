package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tyut.ActivityDetailActivity;
import com.tyut.R;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.view.ScrollPickView;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.UserVO;

import java.util.List;


public class DeleteActivityPUW implements View.OnClickListener {


    private LinearLayout shareOrDelete_ll;
    private LinearLayout report_ll;
    private TextView top_tv;
    private TextView share_tv;
    private TextView delete_tv;
    private TextView cancel_tv;
    private TextView cancelTop_tv;
    private TextView report_tv;
    private ActivityVO activityVO;


    PopupWindow popupWindow;

    View contentView;
    Context context;

    Integer userId = null;


    private DeleteActivityPUW.ITopListener topListener;
    private DeleteActivityPUW.IDeleteListener deleteListener;
    private DeleteActivityPUW.IShareListener shareListener;
    private DeleteActivityPUW.IReportListener reportListener;
    private DeleteActivityPUW.ICancelTopListener cancelTopListener;


    public PopupWindow getPopupWindow() {
        return this.popupWindow;
    }

    public DeleteActivityPUW setPopUpWindow(PopupWindow popUpWindow) {
        this.popupWindow= popUpWindow;
        return this;
    }



    public DeleteActivityPUW setShare(DeleteActivityPUW.IShareListener shareListener) {
        this.shareListener = shareListener;
        return this;
    }

    public DeleteActivityPUW setCancelTop(ICancelTopListener cancelTopListener) {
        this.cancelTopListener = cancelTopListener;
        return this;
    }

    public DeleteActivityPUW setTOP(DeleteActivityPUW.ITopListener topListener) {
        this.topListener = topListener;
        return this;
    }

    public DeleteActivityPUW setDelete(DeleteActivityPUW.IDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }
    public DeleteActivityPUW setReport(DeleteActivityPUW.IReportListener reportListener) {
        this.reportListener = reportListener;
        return this;
    }


    public DeleteActivityPUW(Context context, ActivityVO activityVO) {

        this.context = context;
        this.activityVO = activityVO;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.puw_deleteactivity, null);
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
            case R.id.deleteactivity_tv:

                if(deleteListener != null){
                    deleteListener.onDelete(this);
                }
                popupWindow.dismiss();
                break;
            case R.id.sharetoweibo_tv:

                if(shareListener != null){
                    shareListener.onShare(this);
                }
                popupWindow.dismiss();
                break;
            case R.id.top_tv:

                if(topListener != null){
                    topListener.onTop(this);
                }
                popupWindow.dismiss();
                break;
            case R.id.cancelTop_tv:

                if(cancelTopListener != null){
                    cancelTopListener.onCancelTop(this);
                }
                popupWindow.dismiss();
                break;
            case R.id.reportactivity_tv:
                if(reportListener != null){
                    reportListener.onReport(this);
                }
                popupWindow.dismiss();
                break;
        }
    }

    public interface IDeleteListener{
        void onDelete(DeleteActivityPUW deleteActivityPUW);
    }
    public interface IShareListener{
        void onShare(DeleteActivityPUW deleteActivityPUW);
    }
    public interface ITopListener{
        void onTop(DeleteActivityPUW deleteActivityPUW);
    }
    public interface ICancelTopListener{
        void onCancelTop(DeleteActivityPUW deleteActivityPUW);
    }
    public interface IReportListener{
        void onReport(DeleteActivityPUW deleteActivityPUW);
    }


    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_tv);
        cancelTop_tv = contentView.findViewById(R.id.cancelTop_tv);
        top_tv = contentView.findViewById(R.id.top_tv);
        share_tv = contentView.findViewById(R.id.sharetoweibo_tv);
        delete_tv = contentView.findViewById(R.id.deleteactivity_tv);

        shareOrDelete_ll = contentView.findViewById(R.id.shareordelete_ll);
        report_ll = contentView.findViewById(R.id.report_ll);
        report_tv = contentView.findViewById(R.id.reportactivity_tv);
        UserVO userVO = (UserVO) SPSingleton.get(context, SPSingleton.USERINFO).readObject("user", UserVO.class);

        if(activityVO.getUserid() == userVO.getId()){
            shareOrDelete_ll.setVisibility(View.VISIBLE);
            if(userVO.getTopacid() == activityVO.getId()){
                cancelTop_tv.setVisibility(View.VISIBLE);
                top_tv.setVisibility(View.GONE);
            }else {
                cancelTop_tv.setVisibility(View.GONE);
                top_tv.setVisibility(View.VISIBLE);
            }
            report_ll.setVisibility(View.GONE);
        }else{
            shareOrDelete_ll.setVisibility(View.GONE);
            report_ll.setVisibility(View.VISIBLE);
        }

        cancel_tv.setOnClickListener(this);
        top_tv.setOnClickListener(this);
        cancelTop_tv.setOnClickListener(this);
        share_tv.setOnClickListener(this);
        delete_tv.setOnClickListener(this);
        report_tv.setOnClickListener(this);
    }

}
