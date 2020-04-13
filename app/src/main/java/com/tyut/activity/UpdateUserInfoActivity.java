package com.tyut.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyut.R;
import com.tyut.utils.OkHttpCallback;
import com.tyut.utils.OkHttpUtils;
import com.tyut.utils.SPSingleton;
import com.tyut.utils.ViewUtil;
import com.tyut.view.GlideRoundTransform;
import com.tyut.vo.ServerResponse;
import com.tyut.vo.UserVO;
import com.tyut.widget.ChoosePhotoPUW;
import com.tyut.widget.DeleteActivityPUW;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateUserInfoActivity extends AppCompatActivity implements View.OnClickListener
{

    LinearLayout whole_ll;
    LinearLayout return_ll;
    LinearLayout userpic_ll;
    LinearLayout username_ll;
    LinearLayout phone_ll;
    ImageView user_pic;
    TextView user_name;
    TextView phone_status;

    private File tempFile;
    private Bitmap bm;
    private File file;

    private Uri imageUri;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int PHOTO_REQUEST_CUT = 2;
    private static final int REQUEST_CODE_GALLERY = 3;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull final Message msg) {

            switch (msg.what){
                case 0:
                    whole_ll.getForeground().setAlpha((int)msg.obj);
                    break;
                case 5:
                    Glide.with(UpdateUserInfoActivity.this)
                            .load("http://"+getString(R.string.url)+":8080/userpic/" + (String) msg.obj)
                            .transform(new GlideRoundTransform(UpdateUserInfoActivity.this, 25))
                            .into(user_pic);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuserinfo);
        return_ll = findViewById(R.id.return_ll);
        username_ll = findViewById(R.id.username_ll);
        userpic_ll = findViewById(R.id.userpic_ll);
        phone_ll = findViewById(R.id.phone_ll);

        user_pic = findViewById(R.id.user_photo);
        user_name = findViewById(R.id.user_name);
        phone_status = findViewById(R.id.phone_status);
        whole_ll = findViewById(R.id.whole_ll);

        if (whole_ll.getForeground()!=null){
            whole_ll.getForeground().setAlpha(0);
        }

        return_ll.setOnClickListener(this);
        username_ll.setOnClickListener(this);
        userpic_ll.setOnClickListener(this);
        phone_ll.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_ll:
                finish();
                /*Intent intent = new Intent(UpdateUserInfoActivity.this, HomeActivity.class);
                this.startActivity(intent);*/
                break;
            case R.id.userpic_ll:
                //换头像
                ViewUtil.changeAlpha(mHandler, 0);
                final ChoosePhotoPUW choosePhotoPUW = new ChoosePhotoPUW(UpdateUserInfoActivity.this);
                choosePhotoPUW
                        .setFromAlbum(new ChoosePhotoPUW.IFromAlbumListener() {
                            @Override
                            public void onFromAlbum(ChoosePhotoPUW puw) {
                                //调用相册
                                final Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");//相片类型
                                startActivityForResult(intent, REQUEST_CODE_GALLERY);
                            }
                        })
                        .setTakePhoto(new ChoosePhotoPUW.ITakePhotoListener() {
                            @Override
                            public void onTakePhoto(ChoosePhotoPUW puw) {
                                startCamera();

                            }
                        })
                        .showFoodPopWindow();
                choosePhotoPUW.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtil.changeAlpha(mHandler, 1);
                    }
                });
                break;
            case R.id.username_ll:
                Intent intent2 = new Intent(UpdateUserInfoActivity.this, UpdateUsernameActivity.class);
                this.startActivity(intent2);
                break;
            case R.id.phone_ll:
                Intent intent3 = new Intent(UpdateUserInfoActivity.this, UpdatePhoneActivity.class);
                this.startActivity(intent3);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //判断用户是否登录    fragment中获取view：getActivity
        boolean isLogin =  SPSingleton.get(this, SPSingleton.USERINFO).readBoolean("isLogin");

        if(isLogin == true){

            //获取用户信息
            UserVO userVO = (UserVO)  SPSingleton.get(this, SPSingleton.USERINFO).readObject("user", UserVO.class);


            user_name.setText(userVO.getUsername());
            if(userVO.getPhone() == null || "".equals(userVO.getPhone())){
                phone_status.setText("未验证");
            }else{
                phone_status.setText("已验证");
            }
            Glide.with(this)
                    .load("http://"+getString(R.string.url)+":8080/userpic/" + userVO.getUserpic())
                    .transform(new GlideRoundTransform(this, 25))
                    .into(user_pic);

        }
    }

    private void startCamera(){



        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri(getFile()));

        startActivityForResult(intent, REQUEST_CODE_CAMERA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Toast.makeText(this, "result", Toast.LENGTH_SHORT).show();
        if(requestCode == REQUEST_CODE_CAMERA){
            if(resultCode == RESULT_OK){
                crop(imageUri);//裁剪图片
            }
        }else if(requestCode == PHOTO_REQUEST_CUT){
            if(data != null)
            {
                bm = data.getParcelableExtra("data");
                File file = ViewUtil.bitmap2File(bm, getFile());
                Map<String, String> userId = new HashMap<>();
                userId.put("id",  SPSingleton.get(UpdateUserInfoActivity.this, SPSingleton.USERINFO).readInt("userid")+"");
                OkHttpUtils.upload("http://"+getString(R.string.url)+":8080/portal/user/uploadpic.do", file.getAbsolutePath(), userId, new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        Gson gson = new Gson();
                        ServerResponse<UserVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<UserVO>>(){}.getType());
                        if(serverResponse.getStatus() == 0){

                            SPSingleton util =  SPSingleton.get(UpdateUserInfoActivity.this, SPSingleton.USERINFO);

                            util.clear();
                            util.putBoolean("isLogin", true);
                            util.putString("user", gson.toJson(serverResponse.getData()));
                            util.putInt("userid", serverResponse.getData().getId());
                            Message message = new Message();
                            message.what= 5;
                            message.obj = serverResponse.getData().getUserpic();
                            mHandler.sendMessage(message);
                        }

                    }
                });
            }
        }else if(requestCode == REQUEST_CODE_GALLERY){
            if(resultCode == RESULT_OK && data!=null){
                crop(data.getData());//裁剪图片
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void crop(Uri uri){

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
        tempFile = new File(getExternalCacheDir(), "temp_photo.png");
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
