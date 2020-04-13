package com.tyut.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.activity.ActivityActivity;
import com.tyut.activity.ActivityDetailActivity;
import com.tyut.R;
import com.tyut.activity.TopicActivity;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.adapter.FollowerListAdapter;
import com.tyut.adapter.TopicAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.Topic;
import com.tyut.vo.UserVO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class SearchThreePUW implements View.OnClickListener, TextView.OnEditorActionListener {


    private EditText search_et;
    private TextView activity_tv;
    private TextView topic_tv;
    private TextView user_tv;
    private TextView cancel_tv;
    private TextView noData_tv;
    private RecyclerView three_Rv;

    private Integer current_category = 1;
    private UserVO userVO;

    PopupWindow popupWindow;

    View contentView;
    Context context;

    private static final int ACTIVITYLIST = 1;
    private static final int TOPICLIST = 2;
    private static final int USERLIST = 3;



    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){

                case 1:
                    final List<ActivityVO> activityVOList = (List<ActivityVO>) msg.obj;
                    if(activityVOList.size() == 0){
                        three_Rv.setVisibility(View.GONE);
                        noData_tv.setVisibility(View.VISIBLE);
                    }else{
                        three_Rv.setVisibility(View.VISIBLE);
                        noData_tv.setVisibility(View.GONE);
                        three_Rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                        three_Rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                        three_Rv.setAdapter(new ActivityListAdapter(context, activityVOList, new ActivityListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(context, ActivityDetailActivity.class);
                                intent.putExtra("activityid", activityVOList.get(position).getId());
                                context.startActivity(intent);
                            }
                        }, new ActivityListAdapter.OnUpdateListener() {
                            @Override
                            public void onUpdate(int position) {
                                //onResume();
                            }
                        }).setFlushListener(new ActivityListAdapter.OnFlushListener() {
                            @Override
                            public void onFlush(int i) {
                                //ViewUtil.changeAlpha(mHandler, i);
                            }
                        }));
                    }

                    break;
                case 2:
                    final List<Topic> topicList = (List<Topic>) msg.obj;
                    if(topicList.size() == 0){
                        three_Rv.setVisibility(View.GONE);
                        noData_tv.setVisibility(View.VISIBLE);
                    }else {
                        final List<String> topics = new ArrayList<>();
                        for (Topic topic : topicList) {
                            topics.add(topic.getName());
                        }
                        three_Rv.setVisibility(View.VISIBLE);
                        noData_tv.setVisibility(View.GONE);
                        three_Rv.setLayoutManager(new LinearLayoutManager(context));
                        three_Rv.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.VERTICAL));
                        three_Rv.setAdapter(new TopicAdapter(context, topics, new TopicAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(context, TopicActivity.class);
                                intent.putExtra("topicname", "#"+topics.get(position)+"#");
                            }
                        }));
                    }
                    break;
                case 3:
                    final List<UserVO> userVOList = (List<UserVO>) msg.obj;
                    if(userVOList.size() == 0){
                        three_Rv.setVisibility(View.GONE);
                        noData_tv.setVisibility(View.VISIBLE);
                    }else {

                        List<FollowerVO> followerVOList = new ArrayList<>();
                        for (UserVO vo : userVOList) {
                            FollowerVO followerVO = new FollowerVO();
                            followerVO.setId(vo.getId());
                            followerVO.setUsername(vo.getUsername());
                            followerVO.setUserpic(vo.getUserpic());
                            followerVO.setRel(vo.getIfFollow()? 0 : 1);
                            followerVOList.add(followerVO);
                        }
                        three_Rv.setVisibility(View.VISIBLE);
                        noData_tv.setVisibility(View.GONE);
                        three_Rv.setLayoutManager(new LinearLayoutManager(context));
                        three_Rv.addItemDecoration(new RecycleViewDivider(context,  LinearLayoutManager.VERTICAL));
                        three_Rv.setAdapter(new FollowerListAdapter(context, followerVOList, new FollowerListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {

                                Intent intent = new Intent(context, ActivityActivity.class);
                                intent.putExtra("userid", userVOList.get(position).getId());
                                context.startActivity(intent);

                            }
                        }));
                    }
                    break;

            }
        }
    };


    public PopupWindow getPopupWindow() {
        return this.popupWindow;
    }

    public SearchThreePUW setPopUpWindow(PopupWindow popUpWindow) {
        this.popupWindow= popUpWindow;
        return this;
    }


    public SearchThreePUW(Context context) {

        userVO = (UserVO) SPSingleton.get(context, SPSingleton.USERINFO).readObject("user", UserVO.class);
        this.context = context;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.puw_searchthree, null);
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

        ViewUtil.showSoft(search_et);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_tv:
                popupWindow.dismiss();
                break;
            case R.id.activity_tv:
                current_category = 0;
                activity_tv.setTextColor(v.getResources().getColor(R.color.black));
                topic_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                user_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                if(search_et.getText().toString()!=null && !"".equals(search_et.getText().toString())) {
                    search(current_category);
                }

                break;
            case R.id.topic_tv:
                current_category = 2;
                activity_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                topic_tv.setTextColor(v.getResources().getColor(R.color.black));
                user_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                if(search_et.getText().toString()!=null && !"".equals(search_et.getText().toString())) {
                    search(current_category);
                }
                break;
            case R.id.user_tv:
                current_category = 3;
                activity_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                topic_tv.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                user_tv.setTextColor(v.getResources().getColor(R.color.black));
                if(search_et.getText().toString()!=null && !"".equals(search_et.getText().toString())) {
                    search(current_category);
                }
                break;

        }
    }



    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_tv);
        activity_tv = contentView.findViewById(R.id.activity_tv);
        topic_tv = contentView.findViewById(R.id.topic_tv);
        user_tv = contentView.findViewById(R.id.user_tv);
        search_et = contentView.findViewById(R.id.search_et);
        three_Rv = contentView.findViewById(R.id.three_Rv);
        noData_tv = contentView.findViewById(R.id.noData_tv);

        cancel_tv.setOnClickListener(this);
        activity_tv.setOnClickListener(this);
        topic_tv.setOnClickListener(this);
        user_tv.setOnClickListener(this);

        search_et.setOnEditorActionListener(this);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(actionId){
            case EditorInfo.IME_ACTION_SEARCH:

                InputMethodManager inputMethodManager =(InputMethodManager)context.getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(search_et.getWindowToken(), 0); //隐藏
                search(current_category);
                break;
        }
        return true;

    }

    private void search(Integer type){
        String name = null;
        try {
            name = URLEncoder.encode(String.valueOf(search_et.getText()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        switch (type){
            case 1:
                OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/activity/find.do?content="+name
                                +"&currentUserId="+userVO.getId()+"&status=0",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){

                                    Message message = new Message();
                                    message.what= ACTIVITYLIST;
                                    message.obj=serverResponse.getData();
;                                    mHandler.sendMessage(message);

                                }else{
                                    Looper.prepare();
                                    Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }



                            }
                        }
                );
                break;
            case 2:
                OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/topic/fuzzy.do?name="+name,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<Topic>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Topic>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){

                                    Message message = new Message();
                                    message.what= TOPICLIST;
                                    message.obj=serverResponse.getData();
                                    mHandler.sendMessage(message);

                                }else{
                                    Looper.prepare();
                                    Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }


                            }
                        }
                );
                break;
            case 3:
                OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/user/fuzzy.do?userName="+name+"&currentUserId="+userVO.getId(),
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<UserVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<UserVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){

                                    Message message = new Message();
                                    message.what= USERLIST;
                                    message.obj=serverResponse.getData();
                                    mHandler.sendMessage(message);

                                }else{
                                    Looper.prepare();
                                    Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }


                            }
                        }
                );
                break;
        }

    }
}
