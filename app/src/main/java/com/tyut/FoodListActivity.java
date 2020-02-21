package com.tyut;

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
import com.tyut.adapter.FoodListAdapter;
import com.tyut.utils.JudgeUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.FoodVO;
import com.tyut.vo.Myfood;
import com.tyut.vo.ServerResponse;
import com.tyut.widget.FoodPopUpWindow;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class FoodListActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private TextView addWhat_tv;
    private RecyclerView rvMain;
    private LinearLayout return_ll;
    private TextView common_food;
    private TextView favorite_food;
    private TextView diy_food;
    private EditText search_et;
    private Button cancel_btn;
    private Button commit_btn;
    private ScrollView scrollView;
    private View line;
    private LinearLayout category_ll;
    private TextView not_found_tv;
    private View not_found_line;
    private LinearLayout not_found_ll;
    private static final int FOODLISTACTIVITY = 0;
    private static final int FOODLIST = 1;
    private static final int SEARCHFOOD_NULL = 2;
    private static final int SEARCHFOOD_NOT_NULL = 3;

    private static int current_category = 1;

    private Badge badge;
    private List<Myfood> myfoodList = new ArrayList<>();
    private String currentShowDate = StringUtil.getCurrentDate("yyyy-MM-dd");
    private String currentDietTime = "早餐";
    Integer userid;




    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 1:

                    final List<FoodVO> list = (List<FoodVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(FoodListActivity.this));
                    rvMain.addItemDecoration(new RecycleViewDivider(FoodListActivity.this,  LinearLayoutManager.VERTICAL));
                    rvMain.setAdapter(new FoodListAdapter(FoodListActivity.this, list, new FoodListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(final int position) {
                            FoodVO foodVO= list.get(position);

                            final FoodPopUpWindow foodPopUpWindow = new FoodPopUpWindow(foodVO, null, FoodListActivity.this);
                            foodPopUpWindow.setCreateTime(currentShowDate)
                                    .setTime(currentDietTime)
                                    .initialData()
                                    .setCancel(new FoodPopUpWindow.IOnCancelListener() {
                                @Override
                                public void onCancel(FoodPopUpWindow dialog) {
                                    foodPopUpWindow.getFoodPopupWindow().dismiss();
                                }
                            }).setConfirm(new FoodPopUpWindow.IOnConfirmListener() {
                                @Override
                                public void onConfirm(FoodPopUpWindow dialog) {
                                    if(!dialog.getQuantity().equals("0")) {
                                        Myfood myfood= new Myfood();
                                        myfood.setCal(Integer.parseInt(dialog.getCal()));
                                        myfood.setFoodid(list.get(position).getId());
                                        myfood.setUserid(SharedPreferencesUtil.getInstance(FoodListActivity.this).readInt("userid"));
                                        myfood.setCreateTime(dialog.getCreateTime());
                                        myfood.setQuantity(Integer.parseInt(dialog.getQuantity()));
                                        myfood.setType(JudgeUtil.getDietTime(dialog.getTime()));
                                        myfoodList.add(myfood);
                                        badge.setBadgeNumber(badge.getBadgeNumber() + 1);
                                    }else{
                                        Toast.makeText(FoodListActivity.this, "数量为0，未添加", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).showFoodPopWindow();

                        }
                    }));

                    category_ll.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    scrollView.setVisibility(View.GONE);
                    category_ll.setVisibility(View.GONE);
                    not_found_line.setVisibility(View.VISIBLE);
                    not_found_ll.setVisibility(View.VISIBLE);
                    not_found_tv.setVisibility(View.VISIBLE);

                    break;
                case 3:
                    final List<FoodVO> list2 = (List<FoodVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(FoodListActivity.this));
                    rvMain.addItemDecoration(new RecycleViewDivider(FoodListActivity.this,  LinearLayoutManager.VERTICAL));
                    rvMain.setAdapter(new FoodListAdapter(FoodListActivity.this, list2, new FoodListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(final int position) {
                            FoodVO foodVO= list2.get(position);

                            final FoodPopUpWindow foodPopUpWindow = new FoodPopUpWindow(foodVO, null, FoodListActivity.this);
                            foodPopUpWindow.setCreateTime(currentShowDate)
                                    .setTime(currentDietTime)
                                    .initialData()
                                    .setCancel(new FoodPopUpWindow.IOnCancelListener() {
                                        @Override
                                        public void onCancel(FoodPopUpWindow dialog) {
                                            foodPopUpWindow.getFoodPopupWindow().dismiss();
                                        }
                                    }).setConfirm(new FoodPopUpWindow.IOnConfirmListener() {
                                @Override
                                public void onConfirm(FoodPopUpWindow dialog) {
                                    if(!dialog.getQuantity().equals("0")) {
                                        Myfood myfood= new Myfood();
                                        myfood.setCal(Integer.parseInt(dialog.getCal()));
                                        myfood.setFoodid(list2.get(position).getId());
                                        myfood.setUserid(SharedPreferencesUtil.getInstance(FoodListActivity.this).readInt("userid"));
                                        myfood.setCreateTime(dialog.getCreateTime());
                                        myfood.setQuantity(Integer.parseInt(dialog.getQuantity()));
                                        myfood.setType(JudgeUtil.getDietTime(dialog.getTime()));
                                        myfoodList.add(myfood);
                                        badge.setBadgeNumber(badge.getBadgeNumber() + 1);
                                    }else{
                                        Toast.makeText(FoodListActivity.this, "数量为0，未添加", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).showFoodPopWindow();

                        }
                    }));
                    category_ll.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);

        rvMain = findViewById(R.id.foodRv_main);
        return_ll = findViewById(R.id.return_l);
        common_food = findViewById(R.id.common_food);
        favorite_food = findViewById(R.id.favorite_food);
        diy_food = findViewById(R.id.diy_food);
        search_et = findViewById(R.id.searchfood_et);
        category_ll = findViewById(R.id.category_ll);
        cancel_btn = findViewById(R.id.cancelsearch_btn);
        commit_btn = findViewById(R.id.commitAddFood_btn);
        scrollView = findViewById(R.id.scrollview);
        line = findViewById(R.id.line_view);
        not_found_line = findViewById(R.id.not_fount_line);
        not_found_ll = findViewById(R.id.not_found_ll);
        not_found_tv = findViewById(R.id.not_found_tv);
        addWhat_tv = findViewById(R.id.addwhat_tv);

        return_ll.setOnClickListener(this);
        common_food.setOnClickListener(this);
        favorite_food.setOnClickListener(this);
        diy_food.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        commit_btn.setOnClickListener(this);
        search_et.setOnEditorActionListener(this);
        search_et.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                category_ll.setVisibility(View.GONE);
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
        userid = SharedPreferencesUtil.getInstance(this).readInt("userid");

        if (getIntent().getStringExtra("createtime") != null){
            currentShowDate = getIntent().getStringExtra("createtime");
        }
        if(getIntent().getStringExtra("time") != null){
            currentDietTime = getIntent().getStringExtra("time");
        }
        addWhat_tv.setText("添加"+currentDietTime);

        //初始化气泡
        initBadge();


        //查数据
        OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal/food/list.do?userid=0",
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                            message.what= FOODLIST;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_l:
                /*Intent intent = new Intent(FoodListActivity.this, HomeActivity.class);
                //intent.putExtra("src", SPORTLISTACTIVITY);
                FoodListActivity.this.startActivity(intent);*/
                this.finish();
                break;
            case R.id.favorite_food:
                current_category = 2;
                favorite_food.setTextColor(v.getResources().getColor(R.color.black));
                common_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                diy_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal//favorite/find.do?userid="+userid+"&category=0",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= FOODLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
            case R.id.common_food:
                current_category = 1;
                favorite_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                diy_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                common_food.setTextColor(v.getResources().getColor(R.color.black));
                OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal/food/list.do?userid=0",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= FOODLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                break;
            case R.id.diy_food:
                current_category = 3;
                favorite_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                diy_food.setTextColor(v.getResources().getColor(R.color.black));
                common_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal/food/list.do?userid="+userid,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    Message message = new Message();
                                    message.what= FOODLIST;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
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
                    favorite_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    diy_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    common_food.setTextColor(v.getResources().getColor(R.color.black));
                    OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal/food/list.do?userid=0",
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                    if(serverResponse.getStatus() == 0){
                                        Message message = new Message();
                                        message.what= FOODLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );
                }else if(current_category == 2){
                    favorite_food.setTextColor(v.getResources().getColor(R.color.black));
                    common_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    diy_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal/food/list.do?userid="+userid,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                    if(serverResponse.getStatus() == 0){
                                        Message message = new Message();
                                        message.what= FOODLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );
                } else if(current_category == 3){
                    favorite_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    common_food.setTextColor(v.getResources().getColor(R.color.nav_text_default));
                    diy_food.setTextColor(v.getResources().getColor(R.color.black));
                    OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal/food/list.do?userid="+userid,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                    if(serverResponse.getStatus() == 0){
                                        Message message = new Message();
                                        message.what= FOODLIST;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                }
                            }
                    );
                }

                break;
            case R.id.commitAddFood_btn:
                if(myfoodList.size()>0){
                    final Gson gson=new Gson();
                    String list_json = gson.toJson(myfoodList);
                    /*try {
                        list_json = URLEncoder.encode(list_json, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                    OkHttpUtils.post("http://"+this.getString(R.string.localhost)+"//portal/myfood/addbatch.do/",list_json,
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据

                                    ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                                    if(serverResponse.getStatus() != 0){
                                        Looper.prepare();
                                        Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }

                                }
                            }
                    );
                }
                //this.finish();
                Intent intent1 = null;
                if(getIntent().getStringExtra("src").equals("DIETANDSPORTACTIVITY")){
                    intent1 = new Intent(FoodListActivity.this, DietAndSportActivity.class);
                    intent1.putExtra("date", currentShowDate);
                }else if(getIntent().getStringExtra("src").equals("HOMEACTIVITY")){
                    intent1 = new Intent(FoodListActivity.this, HomeActivity.class);
                }
                FoodListActivity.this.startActivity(intent1);
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
                OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"//portal/food/list.do?userid=0&name="+name,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<FoodVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<FoodVO>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    if(serverResponse.getData().size() == 0){
                                        Message message = new Message();
                                        message.what= SEARCHFOOD_NULL;
                                        mHandler.sendMessage(message);
                                    }else{
                                        Message message = new Message();
                                        message.what= SEARCHFOOD_NOT_NULL;
                                        message.obj = serverResponse.getData();
                                        mHandler.sendMessage(message);
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(FoodListActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
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
