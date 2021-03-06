package com.tyut.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.adapter.SportListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;

import com.tyut.utils.StringUtil;
import com.tyut.vo.Mysport;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SportVO;
import com.tyut.widget.FoodPopUpWindow;
import com.tyut.widget.SportPopUpWindow;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class SportListActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private RecyclerView rvMain;
    private LinearLayout return_ll;
    private TextView my_sport;
    private TextView common_sport;
    private EditText search_et;
    private Button cancel_btn;
    private Button commit_btn;
    private ScrollView scrollView;
    private View line;
    private LinearLayout commonandmy_ll;
    private TextView not_found_tv;
    private View not_found_line;
    private LinearLayout not_found_ll;
    private static final int SPORTLISTACTIVITY = 0;
    private static final int SPORTLIST = 1;
    private static final int SEARCHSPORT_NULL = 2;
    private static final int SEARCHSPORT_NOT_NULL = 3;

    private static int current_category = 1;

    private Badge badge;
    private List<Mysport> mysportList = new ArrayList<>();
    private String currentShowDate = StringUtil.getCurrentDate("yyyy-MM-dd");



    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 1:

                    final List<SportVO> list = (List<SportVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(SportListActivity.this));
                    rvMain.addItemDecoration(new RecycleViewDivider(SportListActivity.this,  LinearLayoutManager.VERTICAL));
                    rvMain.setAdapter(new SportListAdapter(SportListActivity.this, list, new SportListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(final int position) {
                            SportVO sportVO  = list.get(position);

                            final SportPopUpWindow sportPopUpWindow = new SportPopUpWindow(sportVO, null, SportListActivity.this);
                            sportPopUpWindow.setCreateTime(currentShowDate)
                                    .setCancel(new SportPopUpWindow.IOnCancelListener() {
                                @Override
                                public void onCancel(SportPopUpWindow dialog) {
                                    sportPopUpWindow.getSportPopupWindow().dismiss();
                                }
                            }).setConfirm(new SportPopUpWindow.IOnConfirmListener() {
                                @Override
                                public void onConfirm(SportPopUpWindow dialog) {
                                    if(!dialog.getTime().equals("0")) {
                                        Mysport mysport = new Mysport();
                                        mysport.setTime(Integer.parseInt(dialog.getTime()));
                                        mysport.setCal(Integer.parseInt(dialog.getCal()));
                                        mysport.setSportid(list.get(position).getId());
                                        mysport.setUserid(SPSingleton.get(SportListActivity.this, SPSingleton.USERINFO).readInt("userid"));
                                        mysport.setCreateTime(dialog.getCreateTime());
                                        mysportList.add(mysport);
                                        badge.setBadgeNumber(badge.getBadgeNumber() + 1);
                                    }else{
                                        Toast.makeText(SportListActivity.this, "时间为0，未添加", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).showFoodPopWindow();

                        }
                    }));

                    commonandmy_ll.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    scrollView.setVisibility(View.GONE);
                    commonandmy_ll.setVisibility(View.GONE);
                    not_found_line.setVisibility(View.VISIBLE);
                    not_found_ll.setVisibility(View.VISIBLE);
                    not_found_tv.setVisibility(View.VISIBLE);

                    break;
                case 3:
                    final List<SportVO> list2 = (List<SportVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(SportListActivity.this));
                    rvMain.addItemDecoration(new RecycleViewDivider(SportListActivity.this,  LinearLayoutManager.VERTICAL));

                    rvMain.setAdapter(new SportListAdapter(SportListActivity.this, list2, new SportListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(final int position) {

                            SportVO sportVO  = list2.get(position);

                            final SportPopUpWindow sportPopUpWindow = new SportPopUpWindow(sportVO, null, SportListActivity.this);
                            sportPopUpWindow.setCreateTime(currentShowDate)
                                    .setCancel(new SportPopUpWindow.IOnCancelListener() {
                                        @Override
                                        public void onCancel(SportPopUpWindow dialog) {
                                            sportPopUpWindow.getSportPopupWindow().dismiss();
                                        }
                                    }).setConfirm(new SportPopUpWindow.IOnConfirmListener() {
                                @Override
                                public void onConfirm(SportPopUpWindow dialog) {
                                    Toast.makeText(SportListActivity.this, "hhh", Toast.LENGTH_SHORT).show();
                                    if(!dialog.getTime().equals("0")) {
                                        Mysport mysport = new Mysport();
                                        mysport.setTime(Integer.parseInt(dialog.getTime()));
                                        mysport.setCal(Integer.parseInt(dialog.getCal()));
                                        mysport.setSportid(list2.get(position).getId());
                                        mysport.setUserid(SPSingleton.get(SportListActivity.this, SPSingleton.USERINFO).readInt("userid"));
                                        mysport.setCreateTime(dialog.getCreateTime());
                                        mysportList.add(mysport);
                                        badge.setBadgeNumber(badge.getBadgeNumber() + 1);
                                    }else{
                                        Toast.makeText(SportListActivity.this, "时间为0，未添加", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).showFoodPopWindow();

                        }
                    }));
                    commonandmy_ll.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportlist);

        rvMain = findViewById(R.id.sportRv_main);
        return_ll = findViewById(R.id.return_f);
        my_sport = findViewById(R.id.my_sport);
        common_sport = findViewById(R.id.common_sport);
        search_et = findViewById(R.id.searchsport_et);
        commonandmy_ll = findViewById(R.id.commonandmy_ll);
        cancel_btn = findViewById(R.id.cancelsearch_btn);
        commit_btn = findViewById(R.id.commit_btn);
        scrollView = findViewById(R.id.scrollview);
        line = findViewById(R.id.line_view);
        not_found_line = findViewById(R.id.not_fount_line);
        not_found_ll = findViewById(R.id.not_found_ll);
        not_found_tv = findViewById(R.id.not_found_tv);

        return_ll.setOnClickListener(this);
        my_sport.setOnClickListener(this);
        common_sport.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        commit_btn.setOnClickListener(this);
        search_et.setOnEditorActionListener(this);
        search_et.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                commonandmy_ll.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);

                return false;
            }

        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("createtime") != null){
            currentShowDate = getIntent().getStringExtra("createtime");
        }

        //初始化气泡
        initBadge();


        //查数据
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/sport/list.do?userid=0",
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= SPORTLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_f:
               this.finish();
               break;
            case R.id.my_sport:
                current_category = 2;
                my_sport.setTextColor(v.getResources().getColor(R.color.black));
                common_sport.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                Integer userid = SPSingleton.get(this, SPSingleton.USERINFO).readInt("userid");
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/sport/list.do?userid="+userid,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= SPORTLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
            case R.id.common_sport:
                current_category = 1;
                my_sport.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                common_sport.setTextColor(v.getResources().getColor(R.color.black));
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/sport/list.do?userid=0",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= SPORTLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
            case R.id.cancelsearch_btn:

                search_et.clearFocus();
                InputMethodManager inputMethodManager =(InputMethodManager)this
                        .getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(search_et.getWindowToken(), 0); //隐藏

                cancel_btn.setVisibility(View.GONE);
                not_found_line.setVisibility(View.GONE);
                not_found_ll.setVisibility(View.GONE);
                not_found_tv.setVisibility(View.GONE);
                search_et.setText(null);

                if(current_category == 1){
                    my_sport.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    common_sport.setTextColor(v.getResources().getColor(R.color.black));
                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/sport/list.do?userid=0",
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                                    if(serverResponse.getStatus() == 0){
                                        Message message = new Message();
                                        message.what= SPORTLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );
                }else{
                    my_sport.setTextColor(v.getResources().getColor(R.color.black));
                    common_sport.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    Integer userid1 = SPSingleton.get(this, SPSingleton.USERINFO).readInt("userid");
                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/sport/list.do?userid="+userid1,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                                    if(serverResponse.getStatus() == 0){
                                        Message message = new Message();
                                        message.what= SPORTLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );
                }

                break;
            case R.id.commit_btn:
                if(mysportList.size()>0){
                    final Gson gson=new Gson();
                    String list_json = gson.toJson(mysportList);
                    OkHttpUtils.post("http://"+getString(R.string.url)+":8080//portal/mysport/addbatch.do/",list_json,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据

                                    ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() == 0){
                                        SportListActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SportListActivity.this.finish();
                                                if(getIntent().getStringExtra("src").equals("HOMEACTIVITY")){
                                                    Intent intent1 = new Intent(SportListActivity.this, HomeActivity.class);
                                                    SportListActivity.this.startActivity(intent1);
                                                }
                                            }
                                        });
                                    }
                                    Looper.prepare();
                                    Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                                }
                            }
                    );
                }
                break;


        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(actionId){
            case EditorInfo.IME_ACTION_SEARCH:

                InputMethodManager inputMethodManager =(InputMethodManager)this.getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(search_et.getWindowToken(), 0); //隐藏

                String name = String.valueOf(search_et.getText());
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/sport/list.do?userid=0&name="+name,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<SportVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<SportVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    if(serverResponse.getData().size() == 0){
                                        Message message = new Message();
                                        message.what= SEARCHSPORT_NULL;
                                        mHandler.sendMessage(message);
                                    }else{
                                        Message message = new Message();
                                        message.what= SEARCHSPORT_NOT_NULL;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(SportListActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }



                            }
                        }
                );
                break;

        }
        return true;
    }

    private void initBadge() {
        badge = new QBadgeView(this)
                .bindTarget(commit_btn)
                .setBadgeNumber(0)
                .setBadgeTextColor(R.color.green_light)
                .setBadgeTextSize(13,true)
                .setBadgeGravity(Gravity.START | Gravity.TOP)
                .setBadgeBackground(getResources().getDrawable(R.drawable.shape_round_rect));
        //.setBadgeText("PNG")
        //.setBadgeBackgroundColor(R.color.red)
        //.stroke(0xff000000, 1, true);

    }
}
