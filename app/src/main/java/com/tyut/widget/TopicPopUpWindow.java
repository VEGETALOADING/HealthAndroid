package com.tyut.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tyut.R;
import com.tyut.adapter.TopicAdapter;
import com.tyut.utils.RecycleViewDivider;

import java.util.List;


public class TopicPopUpWindow implements View.OnClickListener {

    private TextView clos_tv;
    private RecyclerView recent_Rv;
    private RecyclerView hot_Rv;
    private TopicAdapter mAdapter;

    private LinearLayout recent_ll;
    private LinearLayout hot_ll;

    private List<String> recentTopics;
    private List<String> hotTopics;

    private String selectedTopic;

    PopupWindow topicPopUpWindow;
    View contentView;
    Context context;

    //最近使用话题待实现


    public String getSelectedTopic() {
        return selectedTopic;
    }

    public PopupWindow getTopicPopUpWindow() {
        return this.topicPopUpWindow;
    }

    public TopicPopUpWindow(Context context, List<String> recentTopics, List<String> hotTopics) {

        this.context = context;
        this.recentTopics = recentTopics;
        this.hotTopics = hotTopics;

        contentView = LayoutInflater.from(context).inflate(
                R.layout.popupwindow_topic, null);
        initView();

    }

    public void showTopicPopWindow(){

        topicPopUpWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        topicPopUpWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        topicPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        topicPopUpWindow.setOutsideTouchable(true);
        //设置可以点击
        topicPopUpWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        topicPopUpWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        topicPopUpWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_topic:
                topicPopUpWindow.dismiss();
                break;
        }
    }

    public interface IOnCancelListener{
        void onCancel(TopicPopUpWindow weightPopUpWindow);
    }


    private void initView(){


        recent_ll = contentView.findViewById(R.id.recenttopic_ll);
        hot_ll = contentView.findViewById(R.id.hottopic_ll);
        clos_tv = contentView.findViewById(R.id.close_topic);
        hot_Rv = contentView.findViewById(R.id.hotTopic_Rv);
        recent_Rv = contentView.findViewById(R.id.recentTopic_Rv);
        if(recentTopics!=null) {
            recent_Rv.setLayoutManager(new LinearLayoutManager(context));
            recent_Rv.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.VERTICAL));
            recent_Rv.setAdapter(new TopicAdapter(context, recentTopics, new TopicAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {

                    selectedTopic = recentTopics.get(position);
                    topicPopUpWindow.dismiss();
                }
            }));
        }else{
            recent_ll.setVisibility(View.GONE);
        }
        if(hotTopics!=null) {
            hot_Rv.setLayoutManager(new LinearLayoutManager(context));
            hot_Rv.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.VERTICAL));
            hot_Rv.setAdapter(new TopicAdapter(context, hotTopics, new TopicAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {

                    selectedTopic = hotTopics.get(position);
                    topicPopUpWindow.dismiss();
                }
            }));
        }else{
            hot_ll.setVisibility(View.GONE);
        }

        clos_tv.setOnClickListener(this);
    }

}
