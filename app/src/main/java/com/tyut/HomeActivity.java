package com.tyut;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tyut.fragment.ActivityFragment;
import com.tyut.fragment.FriendFragment;
import com.tyut.fragment.HomeFragment;
import com.tyut.fragment.MyFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout home_LinearLayout;
    LinearLayout friend_LinearLayout;
    LinearLayout activity_LinearLayout;
    LinearLayout my_LinearLayout;
    LinearLayout add_LinearLayout;
    LinearLayout nav_ll;
    RelativeLayout fragment_content;
    ImageView img_home;
    TextView tv_home;
    ImageView img_friend;
    TextView tv_friend;
    ImageView img_activity;
    TextView tv_activity;
    ImageView img_my;
    TextView tv_my;
    private PopupWindow mPop;

    private static final String HOMEFRAGMENT_TAG="HOME";
    private static final String FRIENDFRAGMENT_TAG="FRIEND";
    private static final String ACTIVITYFRAGMENT_TAG="ACTIVITY";
    private static final String MYFRAGMENT_TAG="MY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //预加载主页
        fragment_content = (RelativeLayout)findViewById(R.id.content);
        home_LinearLayout = (LinearLayout)findViewById(R.id.home);
        friend_LinearLayout = (LinearLayout)findViewById(R.id.friend);
        activity_LinearLayout = (LinearLayout)findViewById(R.id.activity);
        add_LinearLayout = (LinearLayout)findViewById(R.id.add);
        my_LinearLayout = (LinearLayout)findViewById(R.id.my);
        img_home = (ImageView)findViewById(R.id.img_home);
        img_friend = (ImageView)findViewById(R.id.img_friend);
        img_activity = (ImageView)findViewById(R.id.img_activity);
        img_my = (ImageView)findViewById(R.id.img_my);
        tv_home = (TextView)findViewById(R.id.tv_home);
        tv_friend = (TextView)findViewById(R.id.tv_friend);
        tv_activity = (TextView)findViewById(R.id.tv_activity);
        tv_my = (TextView)findViewById(R.id.tv_my);
        nav_ll = findViewById(R.id.nav);


        home_LinearLayout.setOnClickListener(this);
        friend_LinearLayout.setOnClickListener(this);
        activity_LinearLayout.setOnClickListener(this);
        my_LinearLayout.setOnClickListener(this);
        add_LinearLayout.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent.getIntExtra("src", 0 ) == 0){
            attachFragment(HOMEFRAGMENT_TAG);
        }else if(intent.getIntExtra("src", 0 ) == 1){
            attachFragment(MYFRAGMENT_TAG);
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Log.d("fresh", "ok");
            attachFragment(MYFRAGMENT_TAG);
            img_home.setImageResource(R.mipmap.home_unselected);
            tv_home.setTextColor(Color.rgb(94,94,94));
            img_friend.setImageResource(R.mipmap.friend_unselected);
            tv_friend.setTextColor(Color.rgb(94,94,94));
            img_activity.setImageResource(R.mipmap.activity_unselected);
            tv_activity.setTextColor(Color.rgb(94, 94, 94));
            img_my.setImageResource(R.mipmap.my_selected);
            tv_my.setTextColor(Color.rgb(16,222,57));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.home:
                attachFragment(HOMEFRAGMENT_TAG);
                img_home.setImageResource(R.mipmap.home_selected);
                tv_home.setTextColor(Color.rgb(16,222,57));
                img_friend.setImageResource(R.mipmap.friend_unselected);
                tv_friend.setTextColor(Color.rgb(94,94,94));
                img_activity.setImageResource(R.mipmap.activity_unselected);
                tv_activity.setTextColor(Color.rgb(94,94,94));
                img_my.setImageResource(R.mipmap.my_unselected);
                tv_my.setTextColor(Color.rgb(94,94,94));
                break;
            case R.id.friend:
                attachFragment(FRIENDFRAGMENT_TAG);
                img_home.setImageResource(R.mipmap.home_unselected);
                tv_home.setTextColor(Color.rgb(94,94,94));
                img_friend.setImageResource(R.mipmap.friend_selected);
                tv_friend.setTextColor(Color.rgb(16,222,57));
                img_activity.setImageResource(R.mipmap.activity_unselected);
                tv_activity.setTextColor(Color.rgb(94,94,94));
                img_my.setImageResource(R.mipmap.my_unselected);
                tv_my.setTextColor(Color.rgb(94,94,94));
                break;
            case R.id.activity:
                attachFragment(ACTIVITYFRAGMENT_TAG);
                img_home.setImageResource(R.mipmap.home_unselected);
                tv_home.setTextColor(Color.rgb(94,94,94));
                img_friend.setImageResource(R.mipmap.friend_unselected);
                tv_friend.setTextColor(Color.rgb(94,94,94));
                img_activity.setImageResource(R.mipmap.activity_selected);
                tv_activity.setTextColor(Color.rgb(16,222,57));
                img_my.setImageResource(R.mipmap.my_unselected);
                tv_my.setTextColor(Color.rgb(94,94,94));
                break;
            case R.id.my:
                attachFragment(MYFRAGMENT_TAG);
                img_home.setImageResource(R.mipmap.home_unselected);
                tv_home.setTextColor(Color.rgb(94,94,94));
                img_friend.setImageResource(R.mipmap.friend_unselected);
                tv_friend.setTextColor(Color.rgb(94,94,94));
                img_activity.setImageResource(R.mipmap.activity_unselected);
                tv_activity.setTextColor(Color.rgb(94, 94, 94));
                img_my.setImageResource(R.mipmap.my_selected);
                tv_my.setTextColor(Color.rgb(16,222,57));
                break;
            case R.id.add:
                Toast.makeText(HomeActivity.this,"xx",Toast.LENGTH_LONG).show();
                View view = getLayoutInflater().inflate(R.layout.popwindow_homeadd, null);
                mPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPop.setOutsideTouchable(true);
                mPop.setFocusable(true);

                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupHeight = view.getMeasuredHeight();
                int popupWidth = view.getMeasuredWidth();

                int[] location = new int[2];
                add_LinearLayout.getLocationOnScreen(location);
                mPop.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight-70);
                break;
        }

    }

    private void attachFragment(String fragmentTag){

        //获取Fragment管理器
        FragmentManager manager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction fragmentTransaction = manager.beginTransaction();

        Fragment fragment = manager.findFragmentByTag(fragmentTag);

        if(fragment == null){
            //管理器中无fragmen

            if(fragmentTag.equals(HOMEFRAGMENT_TAG)){
                fragment = new HomeFragment();

            }else if(fragmentTag.equals(FRIENDFRAGMENT_TAG)){
                fragment = new FriendFragment();
            }
            else if(fragmentTag.equals(ACTIVITYFRAGMENT_TAG)){
                fragment = new ActivityFragment();
            }
            else if(fragmentTag.equals(MYFRAGMENT_TAG)){
                fragment = new MyFragment();
            }
            fragmentTransaction.add(fragment, fragmentTag);

        }

        //显示

        fragmentTransaction.replace(R.id.content, fragment, fragmentTag);

        fragmentTransaction.commit();

    }
}
