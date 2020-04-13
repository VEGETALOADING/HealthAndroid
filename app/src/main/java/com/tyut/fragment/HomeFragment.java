package com.tyut.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.activity.DietAndSportActivity;
import com.tyut.activity.FoodDetailActivity;
import com.tyut.activity.MyfavActivity;
import com.tyut.R;
import com.tyut.activity.RecordActivity;
import com.tyut.activity.SchemaDetailActivity;
import com.tyut.activity.ShowSchemaActivity;
import com.tyut.activity.WeightActivity;
import com.tyut.adapter.FoodListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.StringUtil;

import com.tyut.view.GlideRoundTransform;
import com.tyut.vo.FoodVO;
import com.tyut.vo.HotVO;
import com.tyut.vo.MSchema;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;


import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    ImageView userpic;
    ImageView message;
    EditText search;
    TextView username;
    TextView status;
    TextView weight;
    TextView bmi;
    TextView cancel;

    TextView schemaName_tv;
    TextView schemaIntro_tv;
    ImageView schemaPic_iv;

    TextView hot_tv;
    TextView carb_tv ;
    TextView protein_tv;
    TextView restCal_tv;
    TextView fat_tv;
    TextView desc_tv;
    TextView asc_tv;
    TextView cancelInpop;
    TextView confirmInpop;


    LinearLayout blank_ll;

    LinearLayout searchResult_ll;
    LinearLayout compreandhot_ll;
    TextView commnon_food;
    TextView hot_food;
    RecyclerView rvMain;
    TextView not_found;
    ScrollView listScrollView;
    ScrollView defaultScrollView;
    LinearLayout schema_ll;
    LinearLayout weight_ll;
    LinearLayout healthInfo_ll;
    RelativeLayout diet_diary_rl;
    RelativeLayout myfav_rl;
    //运动训练视频待实现
    //分享待实现


    private static final int SEARCHFOOD_NULL = 0;
    private static final int SEARCHFOOD_NOT_NULL = 1;
    private static final int INITSCHEMA = 2;
    private static final int HOTVO_NULL = 3;
    private static final int HOTVO_NOTNULL = 4;

    private UserVO userVO;
    private String condition1 = null;
    private String condition2 = null;
    private MSchema currentSchema;

    private PopupWindow mPop;

    //子线程主线程通讯
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){

                case 0:
                    listScrollView.setVisibility(View.GONE);
                    defaultScrollView.setVisibility(View.GONE);
                    searchResult_ll.setVisibility(View.VISIBLE);
                    compreandhot_ll.setVisibility(View.VISIBLE);
                    not_found.setVisibility(View.VISIBLE);

                    break;
                case 1:

                    final List<FoodVO> list = (List<FoodVO>) msg.obj;
                    rvMain.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rvMain.addItemDecoration(new RecycleViewDivider(getActivity(),  LinearLayoutManager.VERTICAL));

                    rvMain.setAdapter(new FoodListAdapter(getActivity(), list, new FoodListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(final int position) {

                            Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
                            intent.putExtra("foodvo", list.get(position));
                            getActivity().startActivity(intent);

                        }
                    }));
                    not_found.setVisibility(View.GONE);
                    searchResult_ll.setVisibility(View.VISIBLE);
                    compreandhot_ll.setVisibility(View.VISIBLE);
                    listScrollView.setVisibility(View.VISIBLE);

                    break;

                case 2:
                    final List<MSchema> schemaList = (List<MSchema>) msg.obj;
                    currentSchema = schemaList.get(new Random().nextInt(schemaList.size()));
                    schemaName_tv.setText("#"+currentSchema.getName()+"#");
                    schemaIntro_tv.setText(currentSchema.getIntro());
                    Glide.with(getActivity())
                            .load("http://"+getActivity().getString(R.string.url)+":8080/schemapic/" + currentSchema.getMainpic())
                            .placeholder(R.mipmap.ic_launcher)
                            .into(schemaPic_iv);

                    break;
                case 3:
                    restCal_tv.setText(StringUtil.getNutritionData(userVO).getHot()+"");
                    break;
                case 4:
                    final HotVO hotVO = (HotVO) msg.obj;
                    Integer hotIntake = hotVO.getBreakfastHot()+hotVO.getLunchHot()+hotVO.getDinnerHot();
                    Integer hotConsume = hotVO.getSportHot();
                    restCal_tv.setText((StringUtil.getNutritionData(userVO).getHot() - hotIntake + hotConsume)+"");
                    break;

            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        userpic = view.findViewById(R.id.userpic_home);
        username = view.findViewById(R.id.username_home);
        status = view.findViewById(R.id.now_status);
        weight = view.findViewById(R.id.weight_home);
        bmi = view.findViewById(R.id.bmi_home);
        message = view.findViewById(R.id.message_iv);
        search = view.findViewById(R.id.searchfood_et);
        cancel = view.findViewById(R.id.cancelsearchfood_tv);
        compreandhot_ll = view.findViewById(R.id.compreandhot_ll);

        searchResult_ll = view.findViewById(R.id.searchhide_ll);
        commnon_food = view.findViewById(R.id.common_food);
        hot_food = view.findViewById(R.id.hot_food);
        rvMain = view.findViewById(R.id.foodRv_main);
        not_found = view.findViewById(R.id.not_foundfood_tv);
        listScrollView = view.findViewById(R.id.scrollview_food);
        defaultScrollView = view.findViewById(R.id.default_scrollview);
        schema_ll = view.findViewById(R.id.schema_ll);
        healthInfo_ll = view.findViewById(R.id.healthinfo);
        diet_diary_rl = view.findViewById(R.id.diet_diary);


        blank_ll = view.findViewById(R.id.blank_ll);
        weight_ll = view.findViewById(R.id.weight_ll);
        myfav_rl = view.findViewById(R.id.myfav_rl);

        restCal_tv = view.findViewById(R.id.rest_calories);

        schemaName_tv = view.findViewById(R.id.schemaName_tv);
        schemaIntro_tv = view.findViewById(R.id.schemaIntro_tv);
        schemaPic_iv = view.findViewById(R.id.schemaMainPic_iv);

        healthInfo_ll.setOnClickListener(this);
        message.setOnClickListener(this);
        cancel.setOnClickListener(this);
        commnon_food.setOnClickListener(this);
        hot_food.setOnClickListener(this);
        schema_ll.setOnClickListener(this);
        weight_ll.setOnClickListener(this);
        diet_diary_rl.setOnClickListener(this);
        myfav_rl.setOnClickListener(this);

        search.setOnEditorActionListener(this);
        schemaPic_iv.setOnClickListener(this);
        search.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                message.setVisibility(View.GONE);
                defaultScrollView.setVisibility(View.GONE);
                blank_ll.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);

                return false;
            }

        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //判断用户是否登录    fragment中获取view：getActivity
        boolean isLogin = SPSingleton.get(getActivity(), SPSingleton.USERINFO).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            userVO = (UserVO) SPSingleton.get(getActivity(), SPSingleton.USERINFO).readObject("user", UserVO.class);

            username.setText(userVO.getUsername());
            Glide.with(this)
                    .load("http://"+getString(R.string.url)+":8080/userpic/" + userVO.getUserpic())
                    .transform(new GlideRoundTransform(getContext(), 25))
                    .into(userpic);
            weight.setText(Double.parseDouble(userVO.getWeight())+"");

            float BMI = StringUtil.getBMI(userVO.getWeight(), userVO.getHeight());
            bmi.setText(BMI+"");

            switch (userVO.getGoal()){
                case 0:
                    status.setText("减脂");
                    break;
                case 1:
                    status.setText("保持");
                    break;
                case 2:
                    status.setText("增肌");
                    break;
            }
            if(currentSchema == null) {
                OkHttpUtils.get("http://" + getString(R.string.url) + ":8080/portal/schema/select.do?userid=0",
                        new OkHttpCallback() {
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson = new Gson();
                                ServerResponse<List<MSchema>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<MSchema>>>() {
                                }.getType());
                                if (serverResponse.getStatus() == 0) {
                                    Message message = new Message();
                                    message.what = INITSCHEMA;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
            }
            OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/hot/select.do?userId="+userVO.getId()+"&date="+ StringUtil.getCurrentDate("yyyy-MM-dd"),
                    new OkHttpCallback(){
                        @Override
                        public void onFinish(String status, String msg) {
                            super.onFinish(status, msg);
                            //解析数据
                            Gson gson=new Gson();
                            ServerResponse<HotVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<HotVO>>(){}.getType());
                            if(serverResponse.getStatus() == 0){
                                if(serverResponse.getData().getSportHot() == 0
                                        && serverResponse.getData().getBreakfastHot() == 0
                                        && serverResponse.getData().getLunchHot() == 0
                                        && serverResponse.getData().getDinnerHot() == 0){
                                    Message message = new Message();
                                    message.what= HOTVO_NULL;
                                    mHandler.sendMessage(message);

                                }else{
                                    Message message = new Message();
                                    message.what= HOTVO_NOTNULL;
                                    message.obj = serverResponse.getData();
                                    mHandler.sendMessage(message);
                                }

                            }else{
                                Looper.prepare();
                                Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    }
            );

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.diet_diary:
                Intent intent4 = new Intent(getActivity(), DietAndSportActivity.class);
                intent4.putExtra("src", 0);
                getActivity().startActivity(intent4);
                getActivity().finish();
                break;
            case R.id.cancelsearchfood_tv:
                search.clearFocus();
                InputMethodManager inputMethodManager =(InputMethodManager)getActivity()
                        .getApplicationContext().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(search.getWindowToken(), 0); //隐藏
                cancel.setVisibility(View.GONE);
                searchResult_ll.setVisibility(View.GONE);
                not_found.setVisibility(View.GONE);
                search.setText(null);
                message.setVisibility(View.VISIBLE);
                defaultScrollView.setVisibility(View.VISIBLE);
                listScrollView.setVisibility(View.GONE);
                blank_ll.setVisibility(View.GONE);
                hot_food.setTextColor(getResources().getColor(R.color.nav_text_default));
                commnon_food.setTextColor(getResources().getColor(R.color.black));
                condition1 = null;
                condition2 = null;
                break;
            case R.id.common_food:
                hot_food.setTextColor(getResources().getColor(R.color.nav_text_default));
                commnon_food.setTextColor(getResources().getColor(R.color.black));

                break;
            case R.id.hot_food:
                hot_food.setTextColor(getResources().getColor(R.color.black));
                commnon_food.setTextColor(getResources().getColor(R.color.nav_text_default));
                View view = getLayoutInflater().inflate(R.layout.popwindow_searchfoodbyhot, null);

                hot_tv = view.findViewById(R.id.byhot_tv);
                carb_tv = view.findViewById(R.id.bycarb_tv);
                protein_tv = view.findViewById(R.id.byprotein_tv);
                fat_tv = view.findViewById(R.id.byfat_tv);
                desc_tv = view.findViewById(R.id.desc_tv);
                asc_tv = view.findViewById(R.id.asc_tv);
                cancelInpop = view.findViewById(R.id.cancel_searchfoodpop);
                confirmInpop = view.findViewById(R.id.confirm_searchfoodpop);

                hot_tv.setOnClickListener(this);
                carb_tv.setOnClickListener(this);
                protein_tv.setOnClickListener(this);
                fat_tv.setOnClickListener(this);
                desc_tv.setOnClickListener(this);
                asc_tv.setOnClickListener(this);
                cancelInpop.setOnClickListener(this);
                confirmInpop.setOnClickListener(this);

                mPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPop.setFocusable(true);
                mPop.showAsDropDown(hot_food);

                break;
            case R.id.byhot_tv:
                hot_tv.setBackground(getResources().getDrawable(R.drawable.btn_green));
                carb_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                protein_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                fat_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                desc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                asc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                condition1 = "calories";
                changeConfirmTv();
                break;
            case R.id.bycarb_tv:
                hot_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                carb_tv.setBackground(getResources().getDrawable(R.drawable.btn_green));
                protein_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                fat_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                desc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                asc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                condition1 = "carbs";
                break;
            case R.id.byprotein_tv:
                hot_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                carb_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                protein_tv.setBackground(getResources().getDrawable(R.drawable.btn_green));
                fat_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                desc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                asc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                condition1 = "protein";
                changeConfirmTv();
                break;
            case R.id.byfat_tv:
                hot_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                carb_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                protein_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                fat_tv.setBackground(getResources().getDrawable(R.drawable.btn_green));
                desc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                asc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                condition1 = "fat";
                changeConfirmTv();
                break;
            case R.id.desc_tv:
                desc_tv.setBackground(getResources().getDrawable(R.drawable.btn_green));
                asc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                condition2 = "desc";
                changeConfirmTv();
                break;
            case R.id.asc_tv:
                desc_tv.setBackground(getResources().getDrawable(R.drawable.btn_grey));
                asc_tv.setBackground(getResources().getDrawable(R.drawable.btn_green));
                condition2 = "asc";
                changeConfirmTv();
                break;
            case R.id.cancel_searchfoodpop:
                hot_food.setTextColor(getResources().getColor(R.color.nav_text_default));
                commnon_food.setTextColor(getResources().getColor(R.color.black));
                mPop.dismiss();
                break;
            case R.id.confirm_searchfoodpop:
                if(changeConfirmTv()){
                    mPop.dismiss();
                    OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/food/condition.do?name="+ search.getText().toString() +"&order="+condition2+"&orderby="+condition1,
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
                                        Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }



                                }
                            }
                    );

                }
                break;
            case R.id.schema_ll:
                Intent intent = new Intent(getActivity(), ShowSchemaActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.weight_ll:

                Intent intent1 = new Intent(getActivity(), WeightActivity.class);
                getActivity().startActivity(intent1);
                break;
            case R.id.healthinfo:
                Intent intent2 = new Intent(getActivity(), RecordActivity.class);
                getActivity().startActivity(intent2);
                break;
            case R.id.myfav_rl:
                getActivity().startActivity(new Intent(getActivity(), MyfavActivity.class));
                break;
            case R.id.schemaMainPic_iv:
                if(currentSchema!=null) {
                    Intent intent3 = new Intent(getActivity(), SchemaDetailActivity.class);
                    intent3.putExtra("schema", currentSchema);
                    getActivity().startActivity(intent3);
                }
                break;



        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(actionId){
            case EditorInfo.IME_ACTION_SEARCH:

                InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(search.getWindowToken(), 0); //隐藏

                String name = String.valueOf(search.getText());
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080//portal/food/list.do?name="+name,
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
                                    Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }



                            }
                        }
                );
                break;

        }
        return true;
    }

    public boolean changeConfirmTv(){
        if(condition1 != null && condition2 != null){
            confirmInpop.setBackgroundColor(getResources().getColor(R.color.green_lighter));
            confirmInpop.setTextColor(getResources().getColor(R.color.green));
            return true;
        }
        return false;
    }
}
