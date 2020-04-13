package com.tyut.utils;

import com.tyut.vo.NutritionVO;
import com.tyut.vo.UserVO;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String divison(float a,int b) {
        // TODO 自动生成的方法存根

        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数

        return df.format(a/b);

    }
    public static String param2Json(String paramStr){
        //String paramStr = "a=a1&b=b1&c=c1";
        String[] params = paramStr.split("&");
        JSONObject obj = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length >= 2) {
                String key = param[0];
                String value = param[1];
                for (int j = 2; j < param.length; j++) {
                    value += "=" + param[j];
                }
                try {
                    obj.put(key,value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj.toString();
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

    public static String convertSharetime(String datetime) throws ParseException {

        DateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = source.parse(datetime);

        Date now = new Date();

        //计算和当前时间差多少秒
        long diff = (now.getTime() - date.getTime()) / 1000;

        if(diff<60){
            return "刚刚";
        }else if(diff < 60* 60){
            return diff / 60 +  "分钟前";
        }else if(diff < 60 * 60 * 3){
            return diff / 60 / 60 + "小时前";
        }else{  // 今天HH时mm分发布

            //构造一个date，表示今天的凌晨 00:00:00

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.set(year, month ,day, 0,0,0 );

            Date today = calendar.getTime();

            if(date.after(today)){
                return "今天" + new SimpleDateFormat("HH时mm分").format(date);
            }else{
                return new SimpleDateFormat("yyyy年MM月dd日 HH时mm分").format(date);
            }
        }
    }

    public static Map<Integer, Integer> getTopics(String str){
        String reg = "#[^#]+#";//定义正则表达式
        //Pattern patten = Pattern.compile(reg);//编译正则表达式
        Matcher matcher = Pattern.compile(reg).matcher(str);// 指定要匹配的字符串

        List<String> matchStrs = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();

        while (matcher.find()) { //此处find（）每次被调用后，会偏移到下一个匹配
            matchStrs.add(matcher.group());//获取当前匹配的值
        }

        for (int i = 0; i < matchStrs.size(); i++) {
            map.put(str.indexOf(matchStrs.get(i), 0), str.indexOf(matchStrs.get(i), 0)+matchStrs.get(i).length());
        }
        return map;
    }
    public static Map<Integer, Integer> getEmoji(String str){
        String reg = "\\[(\\S+?)\\]";//定义正则表达式
        //Pattern patten = Pattern.compile(reg);//编译正则表达式
        Matcher matcher = Pattern.compile(reg).matcher(str);// 指定要匹配的字符串

        List<String> matchStrs = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();

        while (matcher.find()) { //此处find（）每次被调用后，会偏移到下一个匹配
            matchStrs.add(matcher.group());//获取当前匹配的值
        }

        for (int i = 0; i < matchStrs.size(); i++) {
            map.put(str.indexOf(matchStrs.get(i), 0), str.indexOf(matchStrs.get(i), 0)+matchStrs.get(i).length());
        }
        return map;
    }

    public static Map<Integer, Integer> getMention(String str){
        String reg = "@([^\\s|\\/|:|@]+)";//定义正则表达式
        //Pattern patten = Pattern.compile(reg);//编译正则表达式
        Matcher matcher = Pattern.compile(reg).matcher(str);// 指定要匹配的字符串

        List<String> matchStrs = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();

        while (matcher.find()) { //此处find（）每次被调用后，会偏移到下一个匹配
            matchStrs.add(matcher.group());//获取当前匹配的值
        }

        for (int i = 0; i < matchStrs.size(); i++) {
            map.put(str.indexOf(matchStrs.get(i), 0), str.indexOf(matchStrs.get(i), 0)+matchStrs.get(i).length());
        }
        return map;
    }

    public static String getPrettyNumber(String number) {
        return BigDecimal.valueOf(Double.parseDouble(number))
                .stripTrailingZeros().toPlainString();
    }

    public static boolean checkPhoneNum(String phoneNum){

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }
    public static boolean checkPw(String password){

        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }
    public static void main(String[] args) {

        System.out.println(checkPw("122"));
    }








}
