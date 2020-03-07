package com.tyut.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.HashMap;

/***
 SharedPreferences的单例模式，支持不同的命名
 */
public class SPSingleton {
    private static volatile HashMap<String, SPSingleton> instanceMap = new HashMap<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //是否是执行apply的模式，false表示为commit保存数据
    private boolean isApplyMode = false;
    private static final String DEFAULT = "default";
    public static final String USERINFO="currentuser";
    public static final String SETTINGDATA="settingdata";

    private SPSingleton(String name, Context context) {
        if (DEFAULT.equals(name)) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    public static SPSingleton get(Context context, String name) {
        if (instanceMap.get(name) == null) {
            synchronized (SPSingleton.class) {
                if (instanceMap.get(name) == null) {
                    instanceMap.put(name, new SPSingleton(name, context));
                }
            }
        }
        //这里每次get操作时强制将保存模式改为commit的方式
        instanceMap.get(name).isApplyMode = false;
        return instanceMap.get(name);
    }

    public static SPSingleton get(Context context) {
        return get(context, DEFAULT);
    }

    // 如果用apply模式的话，得要先调用这个方法，
    // 然后链式调用后续的存储方法，最后以commit方法结尾
    public SPSingleton applyMode() {
        isApplyMode = true;
        return this;
    }

    public void commit() {
        editor.commit();
    }



    private void save() {
        if (isApplyMode) {
            editor.apply();
        } else {
            editor.commit();
        }
    }


    public SPSingleton putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        save();
        return this;
    }
    public SPSingleton putInt(String key, int value) {
        editor.putInt(key, value);
        save();
        return this;
    }
    public SPSingleton putString(String key, String value) {
        editor.putString(key, value);
        save();
        return this;
    }


    public Object readObject(String key,Class clazz){
        String str=  sharedPreferences.getString(key,"");
        Gson gson=new Gson();
        return gson.fromJson(str,clazz);
    }
    public String readString(String key) {
        return sharedPreferences.getString(key, "");
    }
    public int readInt(String key) {
        return sharedPreferences.getInt(key, 000);
    }
    public boolean readBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void delete(String key) {
        editor.remove(key);
        save();
    }
    public void clear(){
        editor.clear().commit();
    }




}

