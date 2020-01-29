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
    private static final  String FILENAME="currentuser";

    private SharedPreferencesUtil(Context context){
        sharedPreferences =context.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public  static SharedPreferencesUtil getInstance(Context context){

         if(sharedPreferencesUtil==null){
             synchronized (SharedPreferencesUtil.class){
                 if(sharedPreferencesUtil==null){
                     sharedPreferencesUtil=new SharedPreferencesUtil(context);
                 }
             }
         }
         return sharedPreferencesUtil;

    }


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
