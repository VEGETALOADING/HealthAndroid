package com.tyut.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.NavUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewUtil {

    //背景变亮变暗
    public static void changeAlpha(final Handler mHandler, int x){
        if(x == 0){//背景变暗
            final int[] alpha = {0};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha[0] < 127) {
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        alpha[0] += 1; //每次加1，逐渐变暗
                        msg.obj = alpha[0];
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }else if(x == 1){
            final int[] alpha = {127};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha[0] > 0) {
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        alpha[0] -= 1; //每次加1，逐渐变暗
                        msg.obj = alpha[0];
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();

        }
    }

    //键盘自动弹出
    public static void showSoftInputFromWindow(Activity context, EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    //弹出键盘可用！！
    public static void showSoft(final EditText editText){
        Handler handle=new Handler();
        handle.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager=(InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, 0);
            }
        }, 0);
    }




    public static File bitmap2File(Bitmap bm, File file){

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
