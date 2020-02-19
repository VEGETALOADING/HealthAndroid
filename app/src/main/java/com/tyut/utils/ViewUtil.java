package com.tyut.utils;

import android.os.Handler;
import android.os.Message;

public class ViewUtil {

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
}
