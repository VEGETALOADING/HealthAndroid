package com.tyut.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.activity.ActivityDetailActivity;
import com.tyut.R;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.util.List;


public class SearchActivityPUW implements View.OnClickListener, TextView.OnEditorActionListener {

    private EditText search_et;
    private RecyclerView activity_Rv;
    private TextView cancel_tv;



    PopupWindow popupWindow;
    View contentView;
    Context context;
    private Boolean flag;

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    final List<ActivityVO> activityVOList = (List<ActivityVO>) msg.obj;
                    activity_Rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    activity_Rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                    activity_Rv.setAdapter(new ActivityListAdapter(context, activityVOList, new ActivityListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            Intent intent = new Intent(context, ActivityDetailActivity.class);
                            intent.putExtra("activity", activityVOList.get(position));
                            context.startActivity(intent);
                        }
                    }, null));
                    break;
            }
        }
    };


    public SearchActivityPUW(Context context, Boolean flag) {

        this.context = context;
        this.flag = flag;
        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_searchactivity, null);
        initView();

    }

    public void showTopicPopWindow(){

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
            case R.id.cancel_searchactivity:
                popupWindow.dismiss();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(actionId){
            case EditorInfo.IME_ACTION_SEARCH:

                InputMethodManager inputMethodManager =(InputMethodManager)context.getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(search_et.getWindowToken(), 0); //隐藏
                UserVO userVO = (UserVO) SPSingleton.get(context, SPSingleton.USERINFO).readObject("user", UserVO.class);

                String content = String.valueOf(search_et.getText());
                String link = new String();
                if(flag){
                    link = "http://"+context.getString(R.string.url)+":8080/portal/activity/find.do?currentUserId="
                            +userVO.getId()
                            +"&userid="+userVO.getId()
                            +"&content=" + content;
                }else{
                    link = "http://"+context.getString(R.string.url)+":8080/portal/activity/find.do?currentUserId="
                            +userVO.getId()
                            +"&userid="+userVO.getId()
                            +"&content=" + content
                            +"&status=0";
                }
                OkHttpUtils.get(link,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());
                                Message message = new Message();
                                message.what= 0;
                                message.obj = serverResponse.getData();
                                mHandler.sendMessage(message);

                            }
                        }
                );
                break;

        }
        return true;    }

    public interface IOnCancelListener{
        void onCancel(SearchActivityPUW weightPopUpWindow);
    }


    private void initView(){


        cancel_tv = contentView.findViewById(R.id.cancel_searchactivity);
        search_et = contentView.findViewById(R.id.searactivity_et_pop);
        activity_Rv = contentView.findViewById(R.id.activity_Rv);


        search_et.setOnEditorActionListener(this);
        cancel_tv.setOnClickListener(this);

    }

}
