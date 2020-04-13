package com.tyut.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.activity.ActivityDetailActivity;
import com.tyut.R;
import com.tyut.activity.TopicActivity;
import com.tyut.activity.TopicListActivity;
import com.tyut.adapter.ActivityListAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.ViewUtil;
import com.tyut.view.GlideRoundTransform;
import com.tyut.vo.ActivityVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.Topic;
import com.tyut.vo.UserVO;
import com.tyut.widget.FindFriendPUW;
import com.tyut.widget.SearchThreePUW;

import java.util.List;

public class FriendFragment extends Fragment implements View.OnClickListener {

    LinearLayout whole_ll;
    TextView search_tv;
    ImageView message_iv;
    ScrollView main_sv;
    HorizontalScrollView topic_hsv;
    LinearLayout moreFriend_ll;
    RecyclerView activity_Rv;
    LinearLayout topicList_ll;


    private UserVO userVO;
    private LayoutInflater mInflater;

    private static final int TOPICS = 1;
    private static final int ACTIVITYVO = 2;
    private FriendFragmentListener listener;


    public interface FriendFragmentListener{
        void senMessage(int i);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    //whole_ll.getForeground().setAlpha((int)msg.obj);
                    break;
                case 1:
                    final List<Topic> topics = (List<Topic>) msg.obj;
                    topicList_ll.removeAllViews();
                    int i = 0;
                    for (final Topic topic : topics) {
                        if(i < 7 && i < topics.size()) {
                            final View view = mInflater.inflate(R.layout.item_hottopic,
                                    topicList_ll, false);
                            final TextView topicName_tv = view.findViewById(R.id.topicname_tv);
                            final ImageView topicPic_iv = view.findViewById(R.id.topicPic_iv);
                            topicName_tv.setText(topic.getName());
                            Glide.with(getActivity())
                                    .load("http://"+getString(R.string.url)+":8080/topicpic/"
                                            + ( (topic.getPic() == null || topic.getPic().equals("")) ? "default.jpg" : topic.getPic()))
                                    .transform(new GlideRoundTransform(getActivity(), 25))
                                    .into(topicPic_iv);
                            topicName_tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), TopicActivity.class);
                                    intent.putExtra("topicname", "#" + topic.getName() + "#");
                                    getActivity().startActivity(intent);
                                }
                            });
                            topicList_ll.addView(view);
                            i++;
                        }
                    }
                    final View view = mInflater.inflate(R.layout.item_hottopic,
                            topicList_ll, false);
                    final TextView topicName_tv = view.findViewById(R.id.topicname_tv);
                    final ImageView topicPic_iv = view.findViewById(R.id.topicPic_iv);
                    topicPic_iv.setBackground(getResources().getDrawable(R.drawable.btn_green));
                    topicName_tv.setText("更多");

                    topicName_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getActivity(), "跳转热门话题Activity", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), TopicListActivity.class));
                        }
                    });
                    topicList_ll.addView(view);

                    break;
                case 2:
                    final List<ActivityVO> activityVOList = (List<ActivityVO>) msg.obj;
                    activity_Rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    activity_Rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                    activity_Rv.setAdapter(new ActivityListAdapter(getActivity(), activityVOList, new ActivityListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            Intent intent = new Intent(getActivity(), ActivityDetailActivity.class);
                            intent.putExtra("activityid", activityVOList.get(position).getId());
                            getActivity().startActivity(intent);
                        }
                    }, new ActivityListAdapter.OnUpdateListener() {
                        @Override
                        public void onUpdate(int position) {
                            initActivityList();
                        }
                    }).setFlushListener(new ActivityListAdapter.OnFlushListener() {
                        @Override
                        public void onFlush(int i) {
                            ViewUtil.changeAlpha(mHandler, i);
                            listener.senMessage(i);
                        }
                    }));
                    break;


            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        search_tv = view.findViewById(R.id.search_tv);
        message_iv = view.findViewById(R.id.message_iv);
        main_sv = view.findViewById(R.id.main_Sv);
        topic_hsv = view.findViewById(R.id.topic_hsv);
        moreFriend_ll = view.findViewById(R.id.moreFriend_ll);
        activity_Rv = view.findViewById(R.id.activity_Rv);
        topicList_ll = view.findViewById(R.id.topic_list_ll);

        whole_ll = view.findViewById(R.id.whole_ll);
        if (whole_ll.getForeground()!=null){
            whole_ll.getForeground().setAlpha(0);
        }
        search_tv.setOnClickListener(this);
        message_iv.setOnClickListener(this);
        moreFriend_ll.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        userVO = (UserVO)  SPSingleton.get(getActivity(), SPSingleton.USERINFO).readObject("user", UserVO.class);
        mInflater = LayoutInflater.from(getActivity());
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/topic/find.do?type=1",
                new OkHttpCallback() {
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson = new Gson();
                        ServerResponse<List<Topic>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Topic>>>() {}.getType());

                        if(serverResponse.getStatus() == 0){
                            Message message = new Message();
                          /*  Bundle bundle = new Bundle();
                            bundle.putString("ket", "value");
                            message.setData(bundle);*/
                            message.what = TOPICS;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{

                            Looper.prepare();
                            Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                }
        );
        initActivityList();



    }

    private void initActivityList(){
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/activity/friend.do?currentUserId="+userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<ActivityVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<ActivityVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0) {
                            Message message = new Message();
                            message.what= ACTIVITYVO;
                            message.obj = serverResponse.getData();
                            mHandler.sendMessage(message);
                        }else{
                            Looper.prepare();
                            Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.moreFriend_ll:
                final FindFriendPUW popUpWindow = new FindFriendPUW(getActivity());
                popUpWindow.showFoodPopWindow();
                break;
            case R.id.search_tv:
                final SearchThreePUW puw =
                        new SearchThreePUW(getActivity());
                puw.showFoodPopWindow();

                puw.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FriendFragmentListener) {
            listener = (FriendFragmentListener)context;
        } else{
            throw new IllegalArgumentException("activity must implements FragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
