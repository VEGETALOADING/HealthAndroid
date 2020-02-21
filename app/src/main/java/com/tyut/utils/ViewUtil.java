package com.tyut.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.EditText;

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
}
