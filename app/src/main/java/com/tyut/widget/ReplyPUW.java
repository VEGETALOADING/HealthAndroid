package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tyut.R;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.CommentVO;
import com.tyut.vo.Reply;


public class ReplyPUW implements View.OnClickListener {

    private TextView reply_tv;
    private TextView delete_tv;
    private TextView cancel_tv;
    private TextView report_tv;
    private Reply reply;


    PopupWindow popupWindow;

    View contentView;
    Context context;

    Integer userId = null;


    private ReplyPUW.IDeleteListener deleteListener;
    private ReplyPUW.IReplyListener replyListener;


    public PopupWindow getPopupWindow() {
        return this.popupWindow;
    }



    public ReplyPUW setReplyListener(IReplyListener replyListener) {
        this.replyListener = replyListener;
        return this;
    }

    public ReplyPUW setDelete(ReplyPUW.IDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }


    public ReplyPUW(Context context, Reply reply) {

        this.context = context;
        this.reply = reply;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.puw_reply, null);
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
            case R.id.deleteReply_tv:

                if(deleteListener != null){
                    deleteListener.onDelete(this);
                }
                popupWindow.dismiss();
                break;
            case R.id.reply_tv:

                if(replyListener != null){
                    replyListener.onReply(this);
                }
                popupWindow.dismiss();
                break;
            case R.id.reportReply_tv:
                popupWindow.dismiss();
                Toast.makeText(context, "举报", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    public interface IDeleteListener{
        void onDelete(ReplyPUW puw);
    }
    public interface IReplyListener{
        void onReply(ReplyPUW puw);
    }



    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_tv);
        reply_tv = contentView.findViewById(R.id.reply_tv);
        delete_tv = contentView.findViewById(R.id.deleteReply_tv);
        report_tv = contentView.findViewById(R.id.reportReply_tv);
        Integer userid = SPSingleton.get(context, SPSingleton.USERINFO).readInt("userid");

        if(reply.getUserid() == userid){
            delete_tv.setVisibility(View.VISIBLE);
            report_tv.setVisibility(View.GONE);
        }else{
            delete_tv.setVisibility(View.GONE);
            report_tv.setVisibility(View.VISIBLE);
        }
        cancel_tv.setOnClickListener(this);
        reply_tv.setOnClickListener(this);
        delete_tv.setOnClickListener(this);
        report_tv.setOnClickListener(this);

    }

}
