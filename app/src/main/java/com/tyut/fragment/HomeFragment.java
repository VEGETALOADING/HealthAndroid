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
import com.tyut.FoodDetailActivity;
import com.tyut.HomeActivity;
import com.tyut.R;
import com.tyut.ShowSchemaActivity;
import com.tyut.SportListActivity;
import com.tyut.adapter.FoodListAdapter;
import com.tyut.adapter.SportListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.RecycleViewDivider;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.StringUtil;
import com.tyut.vo.FoodVO;
import com.tyut.vo.Mysport;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.SportVO;
import com.tyut.vo.UserVO;
import com.tyut.widget.SportTimeDialog;

import org.w3c.dom.Text;

import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    ImageView userpic;
    ImageView message;
    EditText search;
    TextView username;
    TextView status;
    TextView weight;
    TextView bmi;
    TextView cancel;

    TextView hot_tv;
    TextView carb_tv ;
    TextView protein_tv;
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


    private static final int SEARCHFOOD_NULL = 0;
    private static final int SEARCHFOOD_NOT_NULL = 1;

    private String condition1 = null;
    private String condition2 = null;

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
                            intent.putExtra("id", list.get(position).getId());
                            intent.putExtra("name", list.get(position).getName());
                            intent.putExtra("quantity", list.get(position).getQuantity());
                            intent.putExtra("unit", list.get(position).getUnit());
                            intent.putExtra("calories", list.get(position).getCalories());
                            intent.putExtra("pic", list.get(position).getPic());
                            intent.putExtra("protein", list.get(position).getProtein().toString());
                            intent.putExtra("fat", list.get(position).getFat().toString());
                            intent.putExtra("carbs", list.get(position).getCarbs().toString());
                            getActivity().startActivity(intent);

                        }
                    }));
                    not_found.setVisibility(View.GONE);
                    searchResult_ll.setVisibility(View.VISIBLE);
                    compreandhot_ll.setVisibility(View.VISIBLE);
                    listScrollView.setVisibility(View.VISIBLE);

                    break;

                case 3:
                    break;

            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.home_fragment, container, false);

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


        blank_ll = view.findViewById(R.id.blank_ll);

        message.setOnClickListener(this);
        cancel.setOnClickListener(this);
        commnon_food.setOnClickListener(this);
        hot_food.setOnClickListener(this);
        schema_ll.setOnClickListener(this);

        search.setOnEditorActionListener(this);
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
        boolean isLogin = SharedPreferencesUtil.getInstance(getActivity()).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(getActivity()).readObject("user", UserVO.class);

            username.setText(userVO.getUsername());
            Glide.with(this).load("http://192.168.1.4:8080/userpic/" + userVO.getUserpic()).into(userpic);
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

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                    OkHttpUtils.get("http://192.168.1.4:8080/portal/food/condition.do?name="+ search.getText().toString() +"&order="+condition2+"&orderby="+condition1,
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
                                        Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }



                                }
                            }
                    );

                }
            case R.id.schema_ll:
                Intent intent = new Intent(getActivity(), ShowSchemaActivity.class);
                getActivity().startActivity(intent);



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
                OkHttpUtils.get("http://192.168.1.4:8080//portal/food/list.do?name="+name,
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
                                    Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_LONG).show();
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
