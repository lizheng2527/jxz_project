/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.zdhx.androidbase;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pgyersdk.crash.PgyCrashManager;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.User;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.PreferenceUtil;
import com.zdhx.volley.Request;
import com.zdhx.volley.RequestQueue;
import com.zdhx.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;

public class ECApplication extends Application {
    private static ECApplication instance;

    private static RequestQueue mVolleyQueue = null;

    private static HashMap<String, ParameterValue> userInfoMap = new HashMap<>();

    private List<Activity> activityList = new ArrayList<Activity>();

    public HashMap<String, ParameterValue> loginUrlMap = new HashMap<>();

    /**
     * 单例，返回一个实例
     * @return
     */
    public static synchronized  ECApplication getInstance() {
        if (instance == null) {
            LogUtil.w("[ECApplication] instance is null.");
        }
        return instance;
    }
    private ImageLoader loader;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        PgyCrashManager.register(this); //注册蒲公英Crash反馈
        ShareSDK.initSDK(instance);     //注册分享
        DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder() //
                .considerExifParams(true) // 调整图片方向
                .resetViewBeforeLoading(true) // 载入之前重置ImageView
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_picture_loading) // 载入时图片设置为黑色
                .showImageOnFail(R.drawable.ic_picture_loadfailed) // 加载失败时显示的图片
                .delayBeforeLoading(0) // 载入之前的延迟时间
                .build(); //
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultDisplayImageOptions).memoryCacheExtraOptions(200, 400)
                .threadPoolSize(5).build();
        loader = ImageLoader.getInstance();
        loader.init(config);
    }

    public String getAddress(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        String address = preferences.getString("address", SystemConst.DEFAULT_SERVER);
        return address;
    }

    /**
     * 返回配置文件的日志开关
     * @return
     */
    public boolean getLoggingSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("LOGGING");
            LogUtil.w("[ECApplication - getLogging] logging is: " + b);
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean getAlphaSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("ALPHA");
            LogUtil.w("[ECApplication - getAlpha] Alpha is: " + b);
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static RequestQueue getRequestQueue() {
        if (mVolleyQueue == null) {
            mVolleyQueue = Volley.newRequestQueue(getInstance());
        }
        return mVolleyQueue;
    }

    public static void addRequest(Request<?> req, String tag,boolean shouldCache) {
        req.setTag(tag);
        req.setShouldCache(shouldCache);
        getRequestQueue().add(req);
    }

    /**
     * 存储当前用户
     * @param user
     */
    public void saveUser(User user) {
        PreferenceUtil.save(user, "user");
    }
    /**
     * 获取当前用户实体
     * @return
     */
    public User getCurrentUser(){

        return PreferenceUtil.find("user", User.class);
    }

    public HashMap<String,ParameterValue> getLoginUrlMap(){
        return loginUrlMap;
    }

    public void setLoginUrlMap(String key,ParameterValue value){
        loginUrlMap.put(key,value);
    }

    public HashMap<String,ParameterValue> getCurrentUserMap(){
        return userInfoMap;
    }


    public void setCurrentUserMap(HashMap<String,ParameterValue> map){
        userInfoMap = map;
    }
    /**
     * 存储数据（sharePreference）
     */
    public void saveValue(String key,String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString(key,value).commit();
    }

    /**
     * 获取数据（sharePreference）
     * @param key
     * @return
     */
    public String getValue(String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString(key, "");
    }

    /**
     * 将当前activity添加到容器中
     * @param activity
     */
    public void addActivity(Activity activity){
        activityList.add(activity);
        System.out.println("activityList"+activityList.size());
    }

    /**
     * 从容器中移除当前activity
     * @param activity
     */
    public void removeActivity(Activity activity){
        activityList.remove(activity);
        System.out.println("activityList"+activityList.size());
    }

    /**
     * 遍历activity集合，退出所有！
     */
    public void exit(){
        for (Activity activity : activityList){
            activity.finish();
        }
        System.exit(0);
    }

    /**
     * 当前用户的请求条件
     * @return  */
    public String getUserUrl(){
        String url = null;
        if (userInfoMap != null){
            url = "sys_auto_authenticate=true&sys_username="+getCurrentUserMap().get("sys_username").getValues().get(0)+"&sys_password="+getCurrentUserMap().get("sys_username").getValues().get(0);
        }
        return url;
    }


}
