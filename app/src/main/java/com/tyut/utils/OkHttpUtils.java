package com.tyut.utils;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpUtils {

    //private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20,  TimeUnit.SECONDS)
            .readTimeout(20,  TimeUnit.SECONDS)
            .build();

    /**
     * get请求.
     * @param url
     * @param callback
     * */
    public static void get(String url, OkHttpCallback callback) {
        callback.url = url;
        Request request = new Request.Builder().url(url).build();
        CLIENT.newCall(request).enqueue(callback);
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * post请求.
     * */
    public static void post(String url, String json, OkHttpCallback callback) {
        callback.url = url;
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        CLIENT.newCall(request).enqueue(callback);
    }

    /**
     *
     * */
    public static void downFile(String url,final String saveDir, OkHttpCallback callback) {
        callback.url = url;
        Request request = new Request.Builder().url(url).build();
        CLIENT.newCall(request).enqueue(callback);

    }

    /**
     *图片上传
     **/
    //String url图片上传接口
    public static void upload(String url, String filePath, Map<String, String> params, OkHttpCallback callback){

        java.io.File file = new File(filePath);


        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //文件上传三个参数（1.参数名）
                .addFormDataPart("userpic",
                        file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                file));

        if(params!=null) {
            Set<String> strings = params.keySet();

            for (String string : strings) {
                String key = string;
                String value = params.get(key);
                builder.addFormDataPart(string, params.get(string));
            }
        }
        okhttp3.RequestBody formBody = builder.build();

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).post(formBody).build();
        CLIENT.newCall(request).enqueue(callback);
    }

    //多图片上传
    public static void uploadMultipy(String url, String fileParam, List<String> filePaths, Map<String, String> params, OkHttpCallback callback){

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (String filePath : filePaths) {
            java.io.File file = new File(filePath);
            //文件上传三个参数（1.参数名）
            builder.addFormDataPart(fileParam,
                    file.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            file));
        }
        if(params!=null) {

            Set<String> strings = params.keySet();
            for (String string : strings) {

                String value = params.get(string);

                builder.addFormDataPart(string, params.get(string));
            }
        }
        okhttp3.RequestBody formBody = builder.build();

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).post(formBody).build();
        CLIENT.newCall(request).enqueue(callback);
    }


}

