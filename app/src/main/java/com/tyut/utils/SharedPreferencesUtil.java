package com.tyut.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

//单例
public class SharedPreferencesUtil {

    private static SharedPreferencesUtil  sharedPreferencesUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public SharedPreferencesUtil(Context context, String fileName){
        sharedPreferences =context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

       /*
        由于要存储不同文件，停用单例模式，失去以下优点：
        第一、控制资源的使用，通过线程同步来控制资源的并发访问；
        第二、控制实例产生的数量，达到节约资源的目的。
        第三、作为通信媒介使用，也就是数据共享，它可以在不建立直接关联的条件下，
        让多个不相关的两个线程或者进程之间实现通信。
        */

    /*public  static SharedPreferencesUtil getInstance(Context context, String fileName){

         if(sharedPreferencesUtil==null){
             synchronized (SharedPreferencesUtil.class){
                 if(sharedPreferencesUtil==null){
                     sharedPreferencesUtil=new SharedPreferencesUtil(context, fileName);
                 }
             }
         }
         sharedPreferencesUtil=new SharedPreferencesUtil(context, fileName);
         return sharedPreferencesUtil;

    }*/


    public  void  putBoolean(String key,boolean value){
        editor.putBoolean(key, value);
        editor.commit();
    }
    public  void  putString(String key,String value){
        editor.putString(key, value);
        editor.commit();
    }
    public  void  putInt(String key, Integer value){
        editor.putInt(key, value);
        editor.commit();
    }


    public boolean  readBoolean(String key){
       return  sharedPreferences.getBoolean(key,false);
    }

    public String  readString(String key){
        return  sharedPreferences.getString(key,"");
    }

    public int  readInt(String key){
        return  sharedPreferences.getInt(key,000);
    }

    public Object  readObject(String key,Class clazz){
        String str=  sharedPreferences.getString(key,"");
        Gson gson=new Gson();
        return gson.fromJson(str,clazz);
    }

    public  void  delete(String key){
        editor.remove(key).commit();
    }

    public void clear(){
        editor.clear().commit();
    }

}
