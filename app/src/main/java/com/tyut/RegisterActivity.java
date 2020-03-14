package com.tyut;

import android.content.Intent;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.fragment.ActivityFragment;
import com.tyut.fragment.FriendFragment;
import com.tyut.fragment.HomeFragment;
import com.tyut.fragment.MyFragment;
import com.tyut.fragment.RgBirthdayFragment;
import com.tyut.fragment.RgGenderFragment;
import com.tyut.fragment.RgGoalFragment;
import com.tyut.fragment.RgWeightFragment;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SettingData;
import com.tyut.vo.UserVO;
import com.tyut.widget.WeightPopUpWindow;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        RgBirthdayFragment.FragmentListener,
        RgGenderFragment.FragmentListener,
        RgGoalFragment.FragmentListener,
        RgWeightFragment.FragmentListener {

    LinearLayout return_ll;
    RelativeLayout fragment_content;
    View progress1;
    View progress2;
    View progress3;
    View progress4;

    private UserVO userVO;
    private Integer currentPage = 1;
    private static final String GENDER="GENDER";
    private static final String BIRTHDAY="BIRTHDAY";
    private static final String WEIGHT="WEIGHT";
    private static final String GOAL="GOAL";

    private Integer gender;
    private String height;
    private String weight;
    private String birthday;
    private Integer goal;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    break;

                case 1:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        return_ll = findViewById(R.id.return_ll);
        fragment_content = findViewById(R.id.content);
        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        progress4 = findViewById(R.id.progress4);



        return_ll.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);

        attachFragment(GENDER);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.return_ll:
                switch (currentPage){
                    case 1:
                        //性别
                        this.startActivity(new Intent(RegisterActivity.this, GuideActivity.class));
                        this.finish();
                        break;
                    case 2:
                        attachFragment(GENDER);
                        break;
                    case 3:
                        attachFragment(BIRTHDAY);
                        break;
                    case 4:
                        attachFragment(WEIGHT);
                        break;

                }
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

            if(fragmentTag.equals(GENDER)){
                fragment = new RgGenderFragment();
                currentPage = 1;
                progress1.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress2.setBackgroundColor(this.getResources().getColor(R.color.line_grey));
                progress3.setBackgroundColor(this.getResources().getColor(R.color.line_grey));
                progress4.setBackgroundColor(this.getResources().getColor(R.color.line_grey));

            }else if(fragmentTag.equals(BIRTHDAY)){
                fragment = new RgBirthdayFragment();
                currentPage = 2;
                progress1.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress2.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress3.setBackgroundColor(this.getResources().getColor(R.color.line_grey));
                progress4.setBackgroundColor(this.getResources().getColor(R.color.line_grey));
            }
            else if(fragmentTag.equals(WEIGHT)){
                fragment = new RgWeightFragment();
                currentPage = 3;
                progress1.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress2.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress3.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress4.setBackgroundColor(this.getResources().getColor(R.color.line_grey));
            }
            else if(fragmentTag.equals(GOAL)){
                fragment = new RgGoalFragment();
                currentPage = 4;
                progress1.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress2.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress3.setBackgroundColor(this.getResources().getColor(R.color.green_light));
                progress4.setBackgroundColor(this.getResources().getColor(R.color.green_light));

            }
            fragmentTransaction.add(fragment, fragmentTag);

        }

        //显示

        fragmentTransaction.replace(R.id.content, fragment, fragmentTag);

        fragmentTransaction.commit();

    }

    @Override
    public void getBirthday(String str) {
        birthday = str;
        attachFragment(WEIGHT);
    }

    @Override
    public void getGender(Integer gender, String height) {
        this.gender = gender;
        this.height = height;
        attachFragment(BIRTHDAY);
    }

    @Override
    public void getGoal(Integer str) {
        this.goal =str;
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/user/update.do?id="+userVO.getId()
                        +"&gender="+gender
                        +"&birthday="+birthday
                        +"&goal="+goal
                        +"&weight="+weight
                        +"&height="+height
                        +"&initialweight="+weight,
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            SPSingleton util =  SPSingleton.get(RegisterActivity.this,SPSingleton.USERINFO);
                            util.delete("user");
                            util.putString("user", gson.toJson(serverResponse.getData()));
                            finish();
                            RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, ShowSchemaActivity.class));
                        }else{
                            Looper.prepare();
                            Toast.makeText(RegisterActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }


                    }
                }
        );
        Toast.makeText(this, gender +"-"+birthday + "-" + weight +"-"+height+"-"+goal, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getWeight(String str) {
        weight = str;
        attachFragment(GOAL);
    }
}
