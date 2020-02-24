package com.tyut.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.ActivityActivity;
import com.tyut.FollowerListActivity;
import com.tyut.FollowingListActivity;
import com.tyut.LoginActivity;
import com.tyut.R;
import com.tyut.UpdateUserDataActivity;
import com.tyut.UpdateUserInfoActivity;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

public class MyFragment extends Fragment implements View.OnClickListener {

    TextView user_name;
    ImageView user_photo;
    Button logout_btn;
    TextView follower_count;
    TextView following_count;
    TextView activity_count;
    LinearLayout activity_ll;
    LinearLayout follower_ll;
    LinearLayout following_ll;
    RelativeLayout userinfo_rl;
    RelativeLayout menu1_1;
    RelativeLayout menu1_2;

    private static final int FOLLOWERCOUNT = 0;
    private static final int FOLLOWINGCOUNT = 1;
    private static final int ACTIVITYCOUNT = 2;

    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    follower_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 1:
                    following_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
                case 2:
                    activity_count.setText(String.valueOf(Math.round((Double) msg.obj)));
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_my, container, false);

        user_photo = view.findViewById(R.id.user_photo);
        user_name = view.findViewById(R.id.user_username);
        follower_count = view.findViewById(R.id.follower_count);
        following_count = view.findViewById(R.id.following_count);
        activity_count = view.findViewById(R.id.activity_count);
        logout_btn = view.findViewById(R.id.logout_btn);
        activity_ll = view.findViewById(R.id.activity_ll);
        following_ll = view.findViewById(R.id.following_ll);
        follower_ll = view.findViewById(R.id.follower_ll);
        userinfo_rl = view.findViewById(R.id.userinfo_rl);
        menu1_1 = view.findViewById(R.id.menu1_1);
        menu1_2 = view.findViewById(R.id.menu1_2);
        logout_btn.setOnClickListener(this);
        follower_ll.setOnClickListener(this);
        following_ll.setOnClickListener(this);
        activity_ll.setOnClickListener(this);
        userinfo_rl.setOnClickListener(this);
        menu1_1.setOnClickListener(this);
        menu1_2.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //判断用户是否登录    fragment中获取view：getActivity
        boolean isLogin = SharedPreferencesUtil.getInstance(getActivity()).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(getActivity()).readObject("user", UserVO.class);



            user_name.setText(userVO.getUsername());
            Glide.with(this).load("http://192.168.1.9:8080/userpic/" + userVO.getUserpic()).into(user_photo);


            OkHttpUtils.get("http://192.168.1.9:8080/portal/follow/findfollowercount.do?id=" + userVO.getId(),
                    new OkHttpCallback(){
                        @Override
                        public void onFinish(String status, String msg) {
                            super.onFinish(status, msg);
                            //解析数据
                            Gson gson=new Gson();
                            ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                            Message message = new Message();
                            message.what= FOLLOWERCOUNT;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);

                        }
                    }
            );
            OkHttpUtils.get("http://192.168.1.9:8080/portal/follow/findfollowingcount.do?followerid=" + userVO.getId(),
                    new OkHttpCallback(){
                        @Override
                        public void onFinish(String status, String msg) {
                            super.onFinish(status, msg);
                            //解析数据
                            Gson gson=new Gson();
                            ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                            Message message = new Message();
                            message.what= FOLLOWINGCOUNT;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);

                        }
                    }
            );
            OkHttpUtils.get("http://192.168.1.9:8080/portal/activity/findactivitycount.do?userid=" + userVO.getId(),
                    new OkHttpCallback(){
                        @Override
                        public void onFinish(String status, String msg) {
                            super.onFinish(status, msg);
                            //解析数据
                            Gson gson=new Gson();
                            ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                            Message message = new Message();
                            message.what= ACTIVITYCOUNT;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);

                        }
                    }
            );
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout_btn:
                //退出登录

                System.out.println("exit");
                OkHttpUtils.get("http://192.168.1.9:8080/portal/user/logout.do",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                                if(serverResponse.getStatus() == 0){
                                    SharedPreferencesUtil.getInstance(getActivity()).clear();
                                    //Activity跳转(要在Toast之前？？？？)
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    getActivity().startActivity(intent);
                                }
                                Looper.prepare();
                                Toast.makeText(getActivity(),serverResponse.getMsg(),Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        }
                        );
                break;
            case R.id.activity_ll:
                Intent intent4 = new Intent(getActivity(), ActivityActivity.class);
                intent4.putExtra("src", "HOMEACTIVITY");
                intent4.putExtra("homeFragment", 1);
                getActivity().startActivity(intent4);
                break;

            case R.id.follower_ll:
                Intent intent3 = new Intent(getActivity(), FollowerListActivity.class);
                intent3.putExtra("src", "HOMEACTIVITY");
                intent3.putExtra("homeFragment", 1);
                getActivity().startActivity(intent3);
                break;
            case R.id.following_ll:
                Intent intent = new Intent(getActivity(), FollowingListActivity.class);
                intent.putExtra("src", "HOMEACTIVITY");
                intent.putExtra("homeFragment", 1);
                getActivity().startActivity(intent);
                break;
            case R.id.userinfo_rl:
                Intent intent1 = new Intent(getActivity(), UpdateUserInfoActivity.class);
                getActivity().startActivity(intent1);
                break;
            case R.id.menu1_1:
                Intent intent2 = new Intent(getActivity(), UpdateUserDataActivity.class);
                getActivity().startActivity(intent2);
                break;
            case R.id.menu1_2:

                break;


        }

    }
}
