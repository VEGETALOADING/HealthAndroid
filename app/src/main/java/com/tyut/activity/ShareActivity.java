package com.tyut.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.fragment.FaceFragment;
import com.tyut.utils.EmojiUtil;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.SoftKeyBoardListener;
import com.tyut.utils.StringUtil;
import com.tyut.utils.ViewUtil;
import com.tyut.view.MyCheckBox;
import com.tyut.vo.Emoji;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.Topic;
import com.tyut.vo.UserVO;
import com.tyut.widget.ChooseMentionPopUpWindow;
import com.tyut.widget.TopicPopUpWindow;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener,  FaceFragment.OnEmojiClickListener {

    private RelativeLayout whole_rl;
    private MyCheckBox seeable_checkBox;
    private EditText content_et;
    private ImageView camera_iv;
    private TextView wordCount;
    private LinearLayout fromAlbum_ll;
    private LinearLayout takePhoto_ll;
    private TextView commit_tv;
    private TextView cancelShare_tv;
    private TextView photoCount_tv;

    private LinearLayout photoList_ll;
    private List<Integer> photoList;
    private List<Integer> userIdMentioned;
    private List<UserVO> vos;
    private List<String> photos = new ArrayList<>();
    private List<String> recentTopics = new ArrayList<>();

    private LinearLayout addPhoto_hide_ll;
    private LinearLayout addFace_hide_ll;


    private LayoutInflater mInflater;
    private File tempFile;
    private Bitmap bm;
    private Integer activityStatus = 0;

    private Integer random = 0;
    private Integer activityId = null;

    private Uri imageUri;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 3;
    private static final int PHOTO_REQUEST_CUT = 2;


    private RelativeLayout mention_rl;
    private RelativeLayout topic_rl;
    private RelativeLayout photo_rl;
    private RelativeLayout face_rl;

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
        photoCount_tv = findViewById(R.id.photoCount_tv);
        mention_rl = findViewById(R.id.mention_rl);
        topic_rl = findViewById(R.id.topic_rl);
        face_rl = findViewById(R.id.face_rl);
        photo_rl = findViewById(R.id.photo_rl);

        whole_rl = findViewById(R.id.whole_rl);
        wordCount = findViewById(R.id.wordCount);
        fromAlbum_ll = findViewById(R.id.fromAlbum);
        takePhoto_ll = findViewById(R.id.takePhoto_ll);
        commit_tv = findViewById(R.id.commit_share);
        cancelShare_tv = findViewById(R.id.cancel_share);


        addFace_hide_ll = findViewById(R.id.addFace_hide_ll);
        addPhoto_hide_ll = findViewById(R.id.addPhoto_hide_ll);

        content_et.addTextChangedListener(new textWatcher());
        if (whole_rl.getForeground()!=null){
            whole_rl.getForeground().setAlpha(0);
        }


        onKeyBoardListener();
        ViewUtil.showSoftInputFromWindow(ShareActivity.this, content_et);

        initBadge();

        seeable_checkBox.setOnCheckChangeListener(new MyCheckBox.OnCheckChangeListener() {
            @Override
            public void onCheckChange(boolean isCheck) {
                if (isCheck) {
                    activityStatus = 1;
                } else {
                    activityStatus = 0;
                }
                Toast.makeText(getApplicationContext(), activityStatus == 0 ? "取消仅自己可见":"开启", Toast.LENGTH_SHORT).show();
            }
        });

        FaceFragment faceFragment = FaceFragment.Instance();
        getSupportFragmentManager().beginTransaction().add(R.id.faceContent,faceFragment).commit();


        mention_rl.setOnClickListener(this);
        face_rl.setOnClickListener(this);
        photo_rl.setOnClickListener(this);
        fromAlbum_ll.setOnClickListener(this);
        takePhoto_ll.setOnClickListener(this);
        commit_tv.setOnClickListener(this);
        topic_rl.setOnClickListener(this);
        cancelShare_tv.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mInflater = LayoutInflater.from(this);
        userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);
        if("TOPICACTIVITY".equals(getIntent().getStringExtra("src"))){

            content_et.setText(getIntent().getStringExtra("topicname")+" ");
            content_et.setSelection((getIntent().getStringExtra("topicname")+" ").length());
        }
        OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/friend/find.do?id=" + userVO.getId(),
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
                            Toast.makeText(ShareActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()){
            case R.id.cancel_share:
                this.finish();
                break;
            case R.id.face_rl:
                if(imm.isActive(content_et)){
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                addFace_hide_ll.setVisibility(View.VISIBLE);
                addPhoto_hide_ll.setVisibility(View.GONE);
                break;

            case R.id.photo_rl:
               if(imm.isActive(content_et)){
                   imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                addFace_hide_ll.setVisibility(View.GONE);
                addPhoto_hide_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.mention_rl:

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
                }).showMentionPopWindow();
               /* mentionPopUpWindow.getMentionPopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });*/

                break;

            case R.id.topic_rl:
                if(imm.isActive(content_et)){
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                OkHttpUtils.get("http://"+getString(R.string.url)+":8080/portal/topic/find.do",
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                //解析数据
                                Gson gson=new Gson();
                                ServerResponse<List<Topic>> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<List<Topic>>>(){}.getType());
                                if(serverResponse.getStatus() == 0){
                                    for (Topic topic : serverResponse.getData()) {
                                        recentTopics.add(topic.getName());
                                    }
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(ShareActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                );
                if(recentTopics != null){

                    final TopicPopUpWindow topicPopUpWindow =
                            new TopicPopUpWindow(ShareActivity.this, recentTopics);
                    topicPopUpWindow.showTopicPopWindow();
                    topicPopUpWindow.getTopicPopUpWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if(topicPopUpWindow.getSelectedTopic()!=null){
                                int selectionStart = content_et.getSelectionStart();// 光标位置
                                Editable editable = content_et.getText();// 原先内容
                                // 在光标位置插入内容// 话题后面插入空格,至关重要
                                /*//变色
                                SpannableString spannableString = new SpannableString("#" +  topicPopUpWindow.getSelectedTopic() + "# ");
                                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), 0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                */
                                editable.insert(selectionStart, "#" +  topicPopUpWindow.getSelectedTopic() + "# ");
                                content_et.setSelection(content_et.getSelectionStart());// 移动光标到添加的内容后面
                            }
                        }
                    });
                }else{
                    Toast.makeText(this, "数据库无话题", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.takePhoto_ll:

                startCamera();

                break;

            case R.id.fromAlbum:
                //调用相册
                final Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, REQUEST_CODE_GALLERY);

                break;

            case R.id.commit_share:
                String content = null;
                String createTime = null;
                try {
                    content = URLEncoder.encode(content_et.getText().toString(), "UTF-8");
                    createTime = URLEncoder.encode(StringUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Map<String, String> map = new HashMap<>();
                map.put("content", content);
                map.put("createTime", createTime);
                map.put("userid", userVO.getId()+"");
                map.put("status", activityStatus+"");
                OkHttpUtils.uploadMultipy("http://"+getString(R.string.url)+":8080/portal/activity/share.do", "pic", photos,  map, new OkHttpCallback() {
                    @Override
                    public void onFinish(String status, String msg) {

                        Gson gson = new Gson();
                        ServerResponse serverResponse = gson.fromJson(msg, ServerResponse.class);
                        if(serverResponse.getStatus() == 0){
                            Intent intent1 = null;
                            if("HOMEACTIVITY".equals(getIntent().getStringExtra("src"))){
                                intent1 = new Intent(ShareActivity.this, HomeActivity.class);
                                intent1.putExtra("homeFragment", getIntent().getIntExtra("homeFragment", 0));
                            }else{
                                ShareActivity.this.finish();
                            }
                            ShareActivity.this.startActivity(intent1);

                        }
                        Looper.prepare();
                        Toast.makeText(ShareActivity.this, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Looper.loop();

                    }
                });

                break;
        }


    }


    @Override
    public void onEmojiDelete() {
        String text = content_et.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                content_et.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayTextView();
                return;
            }
            content_et.getText().delete(index, text.length());
            displayTextView();
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        content_et.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        displayTextView();
    }

    private void displayTextView() {
        try {
            EmojiUtil.handlerEmojiText(content_et, null, content_et.getText().toString(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        if (emoji != null) {
            int index = content_et.getSelectionStart();
            Editable editable = content_et.getEditableText();
            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        displayTextView();
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
                lp.height = 1000;
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

    /*
     * 监控EditText改变情况，记录输入的字符数
     */
    class textWatcher implements TextWatcher {

        private CharSequence temp;
        private boolean isEdit = true;
        private int selectionStart ;
        private int selectionEnd ;
        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2,
                                  int arg3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            selectionStart = content_et.getSelectionStart();
            selectionEnd = content_et.getSelectionEnd();
            int remainCount = (200 - content_et.getText().toString().length());

            if (temp.length() > 200) {
                Toast.makeText(ShareActivity.this,
                        "您输入的的已经超过200个字符", Toast.LENGTH_SHORT)
                        .show();
                s.delete(selectionStart-1, selectionEnd);
                int tempSelection = selectionStart;
                content_et.setText(s);
                content_et.setSelection(tempSelection);
                wordCount.setTextColor(getResources().getColor(R.color.red));
            }else{
                wordCount.setTextColor(getResources().getColor(R.color.nav_text_default));
            }
            wordCount.setText("" + remainCount);

        }

    }

    private void startCamera(){



        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri(getFile()));

        startActivityForResult(intent, REQUEST_CODE_CAMERA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == REQUEST_CODE_CAMERA){
            if(resultCode == RESULT_OK){
                crop(imageUri);//裁剪图片
                /*不裁剪直接上传
                  try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    badge.setBadgeNumber(badge.getBadgeNumber() + 1);
                    LayoutInflater mInflater = LayoutInflater.from(this);
                    final View view = mInflater.inflate(R.layout.item_sharephoto,
                            photoList_ll, false);
                    final ImageView photoItem = view.findViewById(R.id.photo_item);
                    ImageView deleteItem = view.findViewById(R.id.deletephoto_iv);
                    photoItem.setImageBitmap(bitmap);
                    deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            photoList_ll.removeView(view);
                            badge.setBadgeNumber(badge.getBadgeNumber() - 1);
                        }
                    });
                    photoList_ll.addView(view);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
            }
        }else if(requestCode == PHOTO_REQUEST_CUT){
            if(data != null)
            {
                bm = data.getParcelableExtra("data");
                final File file = ViewUtil.bitmap2File(bm, getFile());
                photos.add(file.getAbsolutePath());

                badge.setBadgeNumber(badge.getBadgeNumber() + 1);
                photoCount_tv.setText((Integer.parseInt(photoCount_tv.getText().toString()) + 1) + "");

                final View view = mInflater.inflate(R.layout.item_sharephoto,
                        photoList_ll, false);
                final ImageView photoItem = view.findViewById(R.id.photo_item);
                ImageView deleteItem = view.findViewById(R.id.deletephoto_iv);
                photoItem.setImageBitmap(bm);
                deleteItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoList_ll.removeView(view);
                        badge.setBadgeNumber(badge.getBadgeNumber() - 1);
                        photoCount_tv.setText((Integer.parseInt(photoCount_tv.getText().toString()) - 1) + "");
                        photos.remove(file.getAbsolutePath());
                    }
                });
                photoList_ll.addView(view);

            }
        }else if(requestCode == REQUEST_CODE_GALLERY){
            if(resultCode == RESULT_OK && data!=null){
              /*  Uri uri = data.getData();
                Bitmap bit = null;
                try {
                    bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                final View view = mInflater.inflate(R.layout.item_sharephoto,
                        photoList_ll, false);
                final ImageView photoItem = view.findViewById(R.id.photo_item);
                ImageView deleteItem = view.findViewById(R.id.deletephoto_iv);
                photoItem.setImageBitmap(bit);
                deleteItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoList_ll.removeView(view);
                        badge.setBadgeNumber(badge.getBadgeNumber() - 1);
                    }
                });
                photoList_ll.addView(view);*/

                crop(data.getData());//裁剪图片
                /*不裁剪直接上传
                  try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    badge.setBadgeNumber(badge.getBadgeNumber() + 1);
                    LayoutInflater mInflater = LayoutInflater.from(this);
                    final View view = mInflater.inflate(R.layout.item_sharephoto,
                            photoList_ll, false);
                    final ImageView photoItem = view.findViewById(R.id.photo_item);
                    ImageView deleteItem = view.findViewById(R.id.deletephoto_iv);
                    photoItem.setImageBitmap(bitmap);
                    deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            photoList_ll.removeView(view);
                            badge.setBadgeNumber(badge.getBadgeNumber() - 1);
                        }
                    });
                    photoList_ll.addView(view);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void crop(Uri uri){

        //删除动态要删除电脑上的图片文件
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //裁剪框的比例：1：1
        if (Build.MANUFACTURER.equals("HUAWEI")) {
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("scale", true);//去除黑边
        intent.putExtra("scaleUpIfNeeded", true);//去除黑边

        intent.putExtra("outputFormat", "JPEG");//图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        intent.putExtra("return-data", true);

        //开启一个带有返回值的ACTIVITY, 请求码位PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private File getFile(){
        tempFile = new File(getExternalCacheDir(), random++ + ".png");
        if(tempFile.exists()){
            tempFile.delete();
        }
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    private Uri getImageUri(File file){
        if(Build.VERSION.SDK_INT >= 24){
            //FileProvider获取

            imageUri = FileProvider.getUriForFile(this, "com.tyut.fileprovider" ,file);

        }else {
            imageUri = Uri.fromFile(file);
        }
        return imageUri;
    }



}
