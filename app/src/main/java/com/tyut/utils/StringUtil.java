package com.tyut.utils;

import com.tyut.MainActivity;
import com.tyut.vo.NutritionVO;
import com.tyut.vo.UserVO;
import com.tyut.vo.Weight;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StringUtil {
    public static String divison(float a,int b) {
        // TODO 自动生成的方法存根

        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数

        return df.format(a/b);

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

    public static java.sql.Date utilDate2Sql() {
        java.util.Date nDate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(nDate);
        return java.sql.Date.valueOf(sDate);  //转型成java.sql.Date类型
    }

    public static String convertDatetime(Date datetime, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(datetime);
    }

    public static float getBMI(String weight, String height){
        String h = StringUtil.divison(Float.parseFloat(height), 100);
        return StringUtil.keepDecimal((Float.parseFloat(weight) /  Float.parseFloat(h) / Float.parseFloat(h)), 1);
    }

    public static NutritionVO getNutritionData(UserVO vo){
        Integer year = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(vo.getBirthday().substring(0, 4));
        Integer basic = null;

        Integer carb = null;
        Integer protein = null;
        Integer fat = null;
        Integer hot = null;
        Float standardWeight = null;

        if(vo.getGender() == 0){
            basic = (int)Math.round((450 + 3.1 * Float.parseFloat(vo.getHeight()) + 9.2 * Float.parseFloat(vo.getWeight()) - 4.3 * year) * 1.3);
            standardWeight = StringUtil.keepDecimal((float) ((Float.parseFloat(vo.getHeight()) - 70) * 0.6), 1);

        }else{
            basic = (int)Math.round((90 + 4.8 * Float.parseFloat(vo.getHeight()) + 13.4 * Float.parseFloat(vo.getWeight()) -5.7 * year) * 1.3);
            standardWeight = StringUtil.keepDecimal((float) ((Float.parseFloat(vo.getHeight()) - 80) * 0.7), 1);

        }
        if(vo.getGoal() == 1){
            hot = basic;
            protein = Math.round(Float.parseFloat(vo.getWeight()) * 2 * 1.3f);
            fat = (int) Math.round(hot * 0.2 / 9);
            carb = (int) Math.round((hot * 0.8 - protein * 4) / 4);
        }  else if(vo.getGoal() == 0){

            hot = basic - 200;
            protein = Math.round(Float.parseFloat(vo.getWeight()) * 2 * 1.4f);
            fat = (int) Math.round(hot * 0.2 / 9);
            carb = (int) Math.round((hot * 0.8 - protein * 4) / 4);

        } else if(vo.getGoal() == 2){

            hot = basic + 200;
            protein = Math.round(Float.parseFloat(vo.getWeight()) * 2 * 1.5f);
            fat = (int) Math.round(hot * 0.2 / 9);
            carb = (int) Math.round((hot * 0.8 - protein * 4) / 4);
        }
        return new NutritionVO( hot,  carb,  protein,  fat,  standardWeight);

    }


    public static List<String> getRecengtDateList(){
        List<String> list = new ArrayList<String>();

        SimpleDateFormat formatDate = new SimpleDateFormat("MM月-dd日");
        Calendar calendar = Calendar.getInstance();
        Date beginDate = new Date();

        for(int i = 5;i>0;i--) {
            calendar.setTime(beginDate);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - i);
            String strDay =  formatDate.format(calendar.getTime());
            list.add(strDay);
        }
        list.add("今天");

        calendar.setTime(beginDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        String strDay =  formatDate.format(calendar.getTime());
        list.add(strDay);
        return  list;
    }

    public static List<String> getRecengtDateList(String date){
        List<String> list = new ArrayList<String>();

        SimpleDateFormat formatDate = new SimpleDateFormat("MM月-dd日");
        Calendar calendar = Calendar.getInstance();
        Date beginDate = string2Date(date, "yyyy-MM-dd");

        for(int i = 5;i>=-1;i--) {
            calendar.setTime(beginDate);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - i);
            String strDay =  formatDate.format(calendar.getTime());
            list.add(strDay);
        }
        return  list;
    }

    public static List<String> getRecengtDateListWithYear(){
        List<String> list = new ArrayList<String>();

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date beginDate = new Date();

        for(int i = 5;i>-2;i--) {
            calendar.setTime(beginDate);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - i);
            String strDay =  formatDate.format(calendar.getTime());
            list.add(strDay);
        }
        return  list;
    }
    public static List<String> getRecengtDateListWithYear(String date){
        List<String> list = new ArrayList<String>();

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date beginDate = string2Date(date, "yyyy-MM-dd");

        for(int i = 5;i>-2;i--) {
            calendar.setTime(beginDate);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - i);
            String strDay =  formatDate.format(calendar.getTime());
            list.add(strDay);
        }
        return list;
    }

    public static Date string2Date(String strDate,String format){

        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(format);
        DateTime dateTime= dateTimeFormatter.parseDateTime(strDate);
        return dateTime.toDate();
    }

    public static String getNextDay(String date){

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(string2Date(date, "yyyy-MM-dd"));
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return formatDate.format(calendar.getTime());
    }
    public static String getLastDay(String date){

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(string2Date(date, "yyyy-MM-dd"));
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return formatDate.format(calendar.getTime());
    }

    //获取当前时间  xx月xx日
    public static String getCurrentDate(String pattern){

        //"MM月-dd日"
        SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
        return  formatDate.format(calendar.getTime());

    }

    public static String transferDate2Db(String date){
        return date.substring(0, 2)+"-"+date.substring(3, 5);
    }

    public static int getDaysOfMonth(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy年MM月").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }



    public static void main(String[] args) {

        System.out.println(getLastDay("2020-02-14"));
    }








}
