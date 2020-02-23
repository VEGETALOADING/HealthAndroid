package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tyut.R;
import com.tyut.adapter.ChooseMentionAdapter;
import com.tyut.utils.StringUtil;
import com.tyut.view.HeightRulerView;
import com.tyut.view.MyHorizontalScrollView;
import com.tyut.vo.UserVO;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ChooseMentionPopUpWindow implements View.OnClickListener {

    private TextView cancel_tv;
    private TextView confirm_tv;
    private EditText search_et;
    private RecyclerView rvMain;
    private List<String> userNameSelected = new ArrayList<>();
    private List<Integer> userIdSelected = new ArrayList<>();
    private ChooseMentionAdapter mAdapter;

    private List<UserVO> userVOList;

    PopupWindow mentionPopUpWindow;
    View contentView;
    Context context;


    private ChooseMentionPopUpWindow.IOnCancelListener cancelListener;
    private ChooseMentionPopUpWindow.IOnConfirmListener confirmListener;

    public List<String> getUserNameSelected() {
        return userNameSelected;
    }

    public List<Integer> getUserIdSelected() {
        return userIdSelected;
    }

    public PopupWindow getMentionPopUpWindow() {
        return this.mentionPopUpWindow;
    }

    public ChooseMentionPopUpWindow setCancel(ChooseMentionPopUpWindow.IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }


    public ChooseMentionPopUpWindow setConfirm(ChooseMentionPopUpWindow.IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public ChooseMentionPopUpWindow(Context context, List<UserVO> userVOList) {

        this.context = context;
        this.userVOList = userVOList;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_mention, null);
        initView();

    }

    public void showMentionPopWindow(){

        mentionPopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mentionPopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        mentionPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        mentionPopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        mentionPopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        mentionPopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        mentionPopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_mention:
                mentionPopUpWindow.dismiss();
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
            case R.id.confirm_mention:

                if(confirmListener != null){
                    confirmListener.onConfirm(this);
                }
                mentionPopUpWindow.dismiss();
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(ChooseMentionPopUpWindow weightPopUpWindow);
    }
    public interface IOnConfirmListener{
        void onConfirm(ChooseMentionPopUpWindow weightPopUpWindow);
    }


    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_mention);
        confirm_tv = contentView.findViewById(R.id.confirm_mention);
        search_et = contentView.findViewById(R.id.searchfriend_et);
        rvMain = contentView.findViewById(R.id.friendRv_main);

        rvMain.setHasFixedSize(true);
        rvMain.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new ChooseMentionAdapter(context, userVOList);
        rvMain.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ChooseMentionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!mAdapter.isSelected.get(position)){
                    mAdapter.isSelected.put(position, true); // 修改map的值保存状态
                    mAdapter.notifyItemChanged(position);
                    userNameSelected.add(userVOList.get(position).getUsername());
                    userIdSelected.add(userVOList.get(position).getId());

                }else {
                    mAdapter.isSelected.put(position, false); // 修改map的值保存状态
                    mAdapter.notifyItemChanged(position);
                    userNameSelected.remove(userVOList.get(position).getUsername());
                    userIdSelected.remove(userVOList.get(position).getId());
                }

            }

        });


        confirm_tv.setOnClickListener(this);
        cancel_tv.setOnClickListener(this);
    }

}
