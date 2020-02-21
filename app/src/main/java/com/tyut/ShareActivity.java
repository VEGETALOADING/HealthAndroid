package com.tyut;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.adapter.ChooseMentionAdapter;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SharedPreferencesUtil;
import com.tyut.utils.SoftKeyBoardListener;
import com.tyut.utils.ViewUtil;
import com.tyut.view.MyCheckBox;
import com.tyut.vo.FollowerVO;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.BirthdayPopUpWindow;
import com.tyut.widget.ChooseMentionPopUpWindow;
import com.tyut.widget.ChooseOnePopUpWindow;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout whole_rl;
    private MyCheckBox seeable_checkBox;
    private EditText content_et;
    private ImageView camera_iv;
    private LinearLayout photoList_ll;
    private List<Integer> photoList;
    private List<Integer> userIdMentioned;
    private List<UserVO> vos;

    private RelativeLayout mention_rl;

    private Badge badge;
    private View badgeBound;
    private Integer index = 0;

    private UserVO userVO;

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
        setContentView(R.layout.activity_share);

        photoList_ll = findViewById(R.id.photo_list_ll);
        content_et = findViewById(R.id.content_et);
        seeable_checkBox = findViewById(R.id.mycheckbox);
        camera_iv = findViewById(R.id.camera_iv);
        badgeBound = findViewById(R.id.badgeBound);
        mention_rl = findViewById(R.id.mention_rl);
        whole_rl = findViewById(R.id.whole_rl);

        if (whole_rl.getForeground()!=null){
            whole_rl.getForeground().setAlpha(0);
        }


        onKeyBoardListener();
        ViewUtil.showSoftInputFromWindow(ShareActivity.this, content_et);

        initBadge();

        seeable_checkBox.setOnCheckChangeListener(new MyCheckBox.OnCheckChangeListener() {
            @Override
            public void onCheckChange(boolean isCheck) {
                String msg;
                if (isCheck) {
                    msg = "开";
                } else {
                    msg = "关";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        photoList = new ArrayList<>();
        photoList.add(R.mipmap.diet);
        photoList.add(R.mipmap.icon_calendar_selected);
        photoList.add(R.mipmap.icon_foodgrey);
        photoList.add(R.mipmap.add_sport);
        photoList.add(R.mipmap.icon_girth);

        badge.setBadgeNumber(photoList.size());

        for (int i = 0; i < photoList.size(); i++)
        {
            LayoutInflater mInflater = LayoutInflater.from(this);
            final View view = mInflater.inflate(R.layout.item_sharephoto,
                    photoList_ll, false);
            final ImageView photoItem = view.findViewById(R.id.photo_item);
            ImageView deleteItem = view.findViewById(R.id.deletephoto_iv);
            photoItem.setImageResource(photoList.get(i));
            final int finalI = i;
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoList.remove(photoList.get(finalI - index++));
                    photoList_ll.removeView(view);
                    badge.setBadgeNumber(badge.getBadgeNumber() - 1);
                }
            });
            photoList_ll.addView(view);
        }

        mention_rl.setOnClickListener(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        userVO = (UserVO) SharedPreferencesUtil.getInstance(this).readObject("user", UserVO.class);

        OkHttpUtils.get("http://"+this.getString(R.string.localhost)+"/portal/friend/find.do?id=" + userVO.getId(),
                new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        //解析数据
                        Gson gson=new Gson();
                        ServerResponse<List<UserVO>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<UserVO>>>(){}.getType());
                        if(serverResponse.getStatus() == 0){
                            vos = serverResponse.getData();
                        }else{
                            Looper.prepare();
                            Toast.makeText(ShareActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.mention_rl:

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive(content_et)){
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                final ChooseMentionPopUpWindow mentionPopUpWindow =
                        new ChooseMentionPopUpWindow(ShareActivity.this, vos);
                mentionPopUpWindow.setConfirm(new ChooseMentionPopUpWindow.IOnConfirmListener() {
                    @Override
                    public void onConfirm(ChooseMentionPopUpWindow weightPopUpWindow) {
                        List<String> namesMentioned = mentionPopUpWindow.getUserNameSelected();
                        int selectionStart = content_et.getSelectionStart();// 光标位置
                        Editable editable = content_et.getText();// 原先内容

                        for (String s : namesMentioned) {
                            if (selectionStart >= 0) {
                                editable.insert(selectionStart, "@" +  s + " ");// 在光标位置插入内容
                                //editable.insert(content_et.getSelectionStart(), " ");// 话题后面插入空格,至关重要
                                content_et.setSelection(content_et.getSelectionStart());// 移动光标到添加的内容后面
                            }
                        }

                    }
                }).showFoodPopWindow();
               /* mentionPopUpWindow.getMentionPopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });*/

                break;
        }

    }
    private void initBadge() {
        badge = new QBadgeView(this)
                .bindTarget(badgeBound)
                .setBadgeNumber(0)
                .setBadgeTextSize(10,true)
                .setBadgeGravity(Gravity.CENTER | Gravity.TOP)
                .setBadgeBackground(getResources().getDrawable(R.drawable.shape_round_rect));
        //.setBadgeText("PNG")
        //.setBadgeBackgroundColor(R.color.red)
        //.stroke(0xff000000, 1, true);

    }

    //监听软件盘是否弹起
    private void onKeyBoardListener() {
        SoftKeyBoardListener.setListener(ShareActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //Log.e("软键盘", "键盘显示 高度" + height);
                ViewGroup.LayoutParams  lp = content_et.getLayoutParams();
                lp.height = 800;
                content_et.setLayoutParams(lp);

            }

            @Override
            public void keyBoardHide(int height) {
                //Log.e("软键盘", "键盘隐藏 高度" + height);
                ViewGroup.LayoutParams  lp = content_et.getLayoutParams();
                lp.height = 1100;
                content_et.setLayoutParams(lp);
            }
        });
    }




}
