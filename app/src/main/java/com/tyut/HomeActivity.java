package com.tyut;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.tyut.adapter.RecordFoodListAdapter;
import com.tyut.adapter.RecordSportListAdapter;
import com.tyut.fragment.ActivityFragment;
import com.tyut.fragment.FriendFragment;
import com.tyut.fragment.HomeFragment;
import com.tyut.fragment.MyFragment;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.HotVO;
import com.tyut.vo.MyfoodVO;
import com.tyut.vo.MysportVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.FoodPopUpWindow;
import com.tyut.widget.GirthPopUpWindow;
import com.tyut.widget.SportPopUpWindow;
import com.tyut.widget.WeightPopUpWindow;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout whole_rl;
    LinearLayout home_LinearLayout;
    LinearLayout friend_LinearLayout;
    LinearLayout activity_LinearLayout;
    LinearLayout my_LinearLayout;
    LinearLayout add_LinearLayout;
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

    Integer currentFragment;

    private UserVO userVO;
    //private static final int CHANGEALPHA = 0;
    private static final String HOMEFRAGMENT_TAG="HOME";
    private static final String FRIENDFRAGMENT_TAG="FRIEND";
    private static final String ACTIVITYFRAGMENT_TAG="ACTIVITY";
    private static final String MYFRAGMENT_TAG="MY";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    whole_rl.getForeground().setAlpha((int)msg.obj);
                    break;
            }
        }
    };

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
        whole_rl = findViewById(R.id.whole_rl);

        if (whole_rl.getForeground()!=null){
            whole_rl.getForeground().setAlpha(0);
        }

        home_LinearLayout.setOnClickListener(this);
        friend_LinearLayout.setOnClickListener(this);
        activity_LinearLayout.setOnClickListener(this);
        my_LinearLayout.setOnClickListener(this);
        add_LinearLayout.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent.getIntExtra("homeFragment", 0 ) == 0){
            attachFragment(HOMEFRAGMENT_TAG);
        }else if(intent.getIntExtra("homeFragment", 0 ) == 1){
            attachFragment(MYFRAGMENT_TAG);
        }else if(intent.getIntExtra("homeFragment", 0 ) == 2){
            attachFragment(FRIENDFRAGMENT_TAG);
        }else if(intent.getIntExtra("homeFragment", 0 ) == 3){
            attachFragment(ACTIVITYFRAGMENT_TAG);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

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
                View view = getLayoutInflater().inflate(R.layout.popwindow_homeadd, null);

                LinearLayout close_ll = view.findViewById(R.id.close_ll);
                LinearLayout diet_ll = view.findViewById(R.id.diet_ll);
                LinearLayout sport_ll = view.findViewById(R.id.sport_ll);
                LinearLayout weight_ll = view.findViewById(R.id.weight_ll);
                LinearLayout activity_ll = view.findViewById(R.id.activity_ll);
                LinearLayout girth_ll = view.findViewById(R.id.girth_ll);
                LinearLayout punchin_ll = view.findViewById(R.id.punchin_ll);
                close_ll.setOnClickListener(this);
                diet_ll.setOnClickListener(this);
                sport_ll.setOnClickListener(this);
                weight_ll.setOnClickListener(this);
                activity_ll.setOnClickListener(this);
                girth_ll.setOnClickListener(this);
                punchin_ll.setOnClickListener(this);


                mPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPop.setOutsideTouchable(true);
                mPop.setFocusable(true);

                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupHeight = view.getMeasuredHeight();
                int popupWidth = view.getMeasuredWidth();

                int[] location = new int[2];
                add_LinearLayout.getLocationOnScreen(location);
                mPop.setAnimationStyle(R.style.mypopwindow_anim_style);
                mPop.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
                break;
            case R.id.close_ll:
                mPop.dismiss();
                break;
            case R.id.diet_ll:
                mPop.dismiss();
                Intent intent1 = new Intent(HomeActivity.this, DietAndSportActivity.class);
                intent1.putExtra("homeFragment", currentFragment);
                intent1.putExtra("src", "HOMEACTIVITY");
                HomeActivity.this.startActivity(intent1);
                break;
            case R.id.sport_ll:
                mPop.dismiss();
                Intent intent = new Intent(HomeActivity.this, SportListActivity.class);
                intent.putExtra("src", "HOMEACTIVITY");
                HomeActivity.this.startActivity(intent);
                break;
            case R.id.weight_ll:
                mPop.dismiss();
                ViewUtil.changeAlpha(mHandler, 0);
                final WeightPopUpWindow weightPopUpWindow = new WeightPopUpWindow(HomeActivity.this, Float.parseFloat(userVO.getWeight()), null, true);
                weightPopUpWindow.setCancel(new WeightPopUpWindow.IOnCancelListener() {
                    @Override
                    public void onCancel(WeightPopUpWindow dialog) {
                        weightPopUpWindow.getWeightPopUpWindow().dismiss();
                    }
                }).showFoodPopWindow();
                weightPopUpWindow.getWeightPopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });

                break;
            case R.id.activity_ll:
                Intent intent3 = new Intent(HomeActivity.this, ShareActivity.class);
                intent3.putExtra("homeFragment", currentFragment);
                intent3.putExtra("src", "HOMEACTIVITY");
                HomeActivity.this.startActivity(intent3);
                break;
            case R.id.punchin_ll:
                mPop.dismiss();
                OkHttpUtils.get("http://192.168.1.9:8080/portal/punchin/add.do?userid="+userVO.getId()+"&createtime="+ StringUtil.getCurrentDate("yyyy-MM-dd"),
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                if(serverResponse.getStatus() == 65) {
                                    Intent intent4 = new Intent(HomeActivity.this, PunchinActivity.class);
                                    intent4.putExtra("homeFragment", currentFragment);
                                    intent4.putExtra("src", "HOMEACTIVITY");
                                    HomeActivity.this.startActivity(intent4);
                                }else {
                                    Looper.prepare();
                                    Toast.makeText(HomeActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }


                            }
                        }
                );


                break;
            case R.id.girth_ll:
                mPop.dismiss();
                Intent intent2 = new Intent(HomeActivity.this, GirthActivity.class);
                intent2.putExtra("homeFragment", currentFragment);
                intent2.putExtra("src", "HOMEACTIVITY");
                HomeActivity.this.startActivity(intent2);
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
                currentFragment = 0;

            }else if(fragmentTag.equals(FRIENDFRAGMENT_TAG)){
                fragment = new FriendFragment();
                currentFragment = 2;
            }
            else if(fragmentTag.equals(ACTIVITYFRAGMENT_TAG)){
                fragment = new ActivityFragment();
                currentFragment = 3;
            }
            else if(fragmentTag.equals(MYFRAGMENT_TAG)){
                fragment = new MyFragment();
                currentFragment = 1;

            }
            fragmentTransaction.add(fragment, fragmentTag);

        }

        //显示

        fragmentTransaction.replace(R.id.content, fragment, fragmentTag);

        fragmentTransaction.commit();

    }

}
