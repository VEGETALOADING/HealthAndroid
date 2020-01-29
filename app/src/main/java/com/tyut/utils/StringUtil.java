package com.tyut.utils;

import com.tyut.MainActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtil {
    public static String divison(int a,int b) {
        // TODO 自动生成的方法存根

        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数

        return df.format((float)a/b);

    }
    public static float keepDecimal(float num, int count){

        // 设置位数
        int scale = count;
        // 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        int roundingMode = 4;
        BigDecimal bd = new BigDecimal((float) num);
        bd = bd.setScale(scale, roundingMode);
        return bd.floatValue();

    }

    public static Object positive(float num){
        if(num > 0){
            return num;
        }else{
            return -num;
        }
    }

    public static void main(String[] args) {


        System.out.println(positive(50.25f));
    }


}
