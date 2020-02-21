package com.tyut.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;

import java.util.List;

public class FollowerListAdapter extends RecyclerView.Adapter<FollowerListAdapter.LinearViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private static final int CANCELFOLLOW_INLIST = 0;
    private static final int ADDFOLLOW_INLIST = 1;


    private List<FollowerVO> mList;
    public FollowerListAdapter(Context context, List<FollowerVO> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList = list;
    }

    //子线程主线程通讯
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case 0:
                    //取消关注
                    LinearViewHolder holder = (LinearViewHolder) msg.obj;
                    holder.follow_btn.setText("关注");
                    holder.follow_btn.setBackground(mContext.getResources().getDrawable(R.drawable.btn_green));
                    break;
                case 1:
                    //关注
                    LinearViewHolder holder1 = (LinearViewHolder) msg.obj;
                    holder1.follow_btn.setText("已关注");
                    holder1.follow_btn.setBackground(mContext.getResources().getDrawable(R.drawable.btn_grey));
                    break;

            }
        }
    };
    @NonNull
    @Override
    public FollowerListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //传入Item布局
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_follower, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final FollowerListAdapter.LinearViewHolder holder, final int position) {

        if(mList.size() > position){
            holder.username_tv.setText(mList.get(position).getUsername());
            Glide.with(mContext).load("http://"+mContext.getString(R.string.localhost)+"/userpic/" + mList.get(position).getUserpic()).into(holder.userpic_iv);
            if(mList.get(position).getRel() == 0){
                holder.follow_btn.setText("关注");
                holder.follow_btn.setBackground(mContext.getResources().getDrawable(R.drawable.btn_green));
            }else{
                holder.follow_btn.setText("已关注");
                holder.follow_btn.setBackground(mContext.getResources().getDrawable(R.drawable.btn_grey));
            }
            holder.follow_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserVO userVO = (UserVO) SharedPreferencesUtil.getInstance(mContext).readObject("user", UserVO.class);

                    OkHttpUtils.get("http://"+mContext.getString(R.string.localhost)+"/portal/follow/followornot.do?id=" + mList.get(position).getId() + "&followerid=" + userVO.getId(),
                            new OkHttpCallback(){
                                @Override
                                public void onFinish(String status, String msg) {
                                    super.onFinish(status, msg);
                                    //解析数据
                                    Gson gson=new Gson();
                                    ServerResponse serverResponse=gson.fromJson(msg, ServerResponse.class);

                                    if((double)serverResponse.getData() == 19){
                                        //取关成功
                                        Log.d("xxx", "取关成功");
                                        Message message = new Message();
                                        message.what= CANCELFOLLOW_INLIST;
                                        message.obj = holder;
                                        mHandler.sendMessage(message);
                                    /*Looper.prepare();
                                    Toast.makeText(FollowingDetailActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();*/

                                    }else  if((double)serverResponse.getData() == 20){
                                        //取关失败
                                        Looper.prepare();
                                        Toast.makeText(mContext, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }else  if((double)serverResponse.getData() == 17){
                                        //关注成功
                                        Log.d("xxx", "关注成功");
                                        Message message = new Message();
                                        message.what= ADDFOLLOW_INLIST;
                                        message.obj = holder;
                                        mHandler.sendMessage(message);


                                    }else  if((double)serverResponse.getData() == 18){
                                        //关注失败
                                        Looper.prepare();
                                        Toast.makeText(mContext, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }


                                }
                            }
                    );
                }
            });
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toast
            }
        });*/
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });

        }



    }

    @Override
    public int getItemCount() {
       return mList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView username_tv;
        private ImageView userpic_iv;
        private Button follow_btn;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            username_tv = itemView.findViewById(R.id.username_tv);
            userpic_iv = itemView.findViewById(R.id.userpic_iv);
            follow_btn = itemView.findViewById(R.id.follow_btn);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
