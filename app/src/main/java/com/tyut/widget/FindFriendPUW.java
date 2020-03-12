package com.tyut.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tyut.ActivityActivity;
import com.tyut.ActivityDetailActivity;
import com.tyut.FollowerListActivity;
import com.tyut.FoodListActivity;
import com.tyut.R;
import com.tyut.TopicActivity;
import com.tyut.TopicListActivity;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.adapter.FollowerListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.Follow;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.Topic;
import com.tyut.vo.UserVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FindFriendPUW implements View.OnClickListener {

    private Button followALL_btn;
    private TextView cancel_tv;
    private RecyclerView user_Rv;
    private List<FollowerVO> followerVOList;
    private UserVO userVO;
    PopupWindow popupWindow;

    private static final Integer FOLLOWERVOLIST = 0;

    View contentView;
    Context context;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    followerVOList = (List<FollowerVO>)msg.obj;
                    user_Rv.setLayoutManager(new LinearLayoutManager(context));
                    user_Rv.addItemDecoration(new RecycleViewDivider(context,  LinearLayoutManager.VERTICAL));
                    user_Rv.setAdapter(new FollowerListAdapter(context, followerVOList, new FollowerListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {

                            Intent intent = new Intent(context, ActivityActivity.class);
                            intent.putExtra("userid", followerVOList.get(position).getId());
                            context.startActivity(intent);

                        }
                    }));
                    break;


            }
        }
    };



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
        initRecycleView();

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
                final Gson gson=new Gson();

                List<Follow> list = new ArrayList<>();
                for (FollowerVO vo : followerVOList) {
                    Follow follow = new Follow();
                    follow.setId(vo.getId());
                    follow.setFollowerid(userVO.getId());
                    list.add(follow);
                }
                String list_json = gson.toJson(list);

                OkHttpUtils.post("http://"+context.getString(R.string.url)+":8080/portal/follow/addbatch.do/",list_json,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据

                                ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);

                                Looper.prepare();
                                Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Looper.loop();


                            }
                        }
                );
                popupWindow.dismiss();
                break;
        }
    }



    private void initView(){

        cancel_tv = contentView.findViewById(R.id.cancel_tv);
        followALL_btn = contentView.findViewById(R.id.followAll_btn);
        user_Rv = contentView.findViewById(R.id.user_Rv);


        cancel_tv.setOnClickListener(this);
        followALL_btn.setOnClickListener(this);
    }

    private void initRecycleView(){
        userVO = (UserVO) SPSingleton.get(context, SPSingleton.USERINFO).readObject("user", UserVO.class);

        OkHttpUtils.get("http://"+context.getString(R.string.url)+":8080/portal/follow/findnewfriend.do?currentUserId=" + userVO.getId(),
                new OkHttpCallback() {
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson = new Gson();
                        ServerResponse<List<FollowerVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FollowerVO>>>() {
                        }.getType());
                        if (serverResponse.getStatus() == 0) {
                            Message message = new Message();
                            message.what = FOLLOWERVOLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        } else {
                            Looper.prepare();
                            Toast.makeText(context, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );

    }

}
