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

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pgyersdk.crash.PgyCrashManager;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.User;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.PreferenceUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RunnableWrap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import volley.Request;
import volley.RequestQueue;
import volley.toolbox.Volley;


public class ECApplication extends Application {
    private static ECApplication instance;

    private static RequestQueue mVolleyQueue = null;

//    private static HashMap<String, ParameterValue> userInfoMap = new HashMap<>();

//    private List<Activity> activityList = new ArrayList<>();

    public HashMap<String, ParameterValue> loginUrlMap = new HashMap<>();

    public static ArrayList<File> jxzFiles = new ArrayList<>();
    private static ArrayList<String> jxzFileNames = new ArrayList<>();

    private static String userAuth = "no";

//    private static final String APP_ID = "wx0e662ccb34bb7889";
//
//    private final String SHARESDKAPP_KEY = "1e69404689338";

    /**
     * 单例，返回一个实例
     *
     * @return instance
     */
    public static synchronized  ECApplication getInstance() {
        if (instance == null) {
            LogUtil.w("[ECApplication] instance is null.");
        }
        return instance;
    }


    /**
     * 返回是否有发布重要活动的权限
     * @return 是否有权限“no“，“yes”
     */
    public String getUserAuth() {

        return userAuth;
    }

    public void setUserAuth(String userAutl){
        ECApplication.userAuth = userAutl;
    }
    public ImageLoader loader;
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
        ImagePipelineConfig frescoConfig = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        Fresco.initialize(this,frescoConfig);
        getFileFromJxz();
        ShareSDK.initSDK(this);
    }

    /*public void showShare(File file) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
//        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(file.getPath());//确保SDcard下面存在此张图片
//         url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(showUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
// 启动分享GUI
        oks.show(this);
    }*/


    /**
     * 初始化时遍历jxz文件夹，将所有文件放入集合中用来以后比较
     */
    private void getFileFromJxz() {
        new ProgressThreadWrap(this, new RunnableWrap() {
            @Override
            public void run() {
                File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File dir = new File(idr+"/jxz");
                if (!dir.exists()){
                    dir.mkdir();
                }
                File[] files = dir.listFiles();
                if (files != null&&files.length>0){
                    for (File file : files) {
                        jxzFiles.add(file);
                        jxzFileNames.add(file.getName());
                    }
                }
            }
        }).start();
    }

    public String getDownloadJxzDir(){
        File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(idr+"/jxz");
        if (!dir.exists()){
            dir.mkdir();
        }
        return dir.getAbsolutePath();
    }

  /*  public List<File> getJxzFile (){

        return jxzFiles;
    }*/

    public void addJxzFiles(String fileName,File file){
        jxzFileNames.add(fileName);
        jxzFiles.add(file);
    }

    public boolean isExistFile(String fileName){

        return jxzFileNames.contains(fileName);
    }


    public String getAddress(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("address", SystemConst.DEFAULT_SERVER);
    }
    private User userForYKT;
    public User getUserForYKT(){

        return userForYKT;
    }
    public void saveUserInfoForYKT(User user){

        userForYKT = user;
    }

    /*public boolean getLoggingSwitch() {
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
    }*/

    /*public boolean getAlphaSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("ALPHA");
            LogUtil.w("[ECApplication - getAlpha] Alpha is: " + b);
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }*/

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

    public static void addRequest(Request<?> req, String tag, boolean shouldCache) {
        req.setTag(tag);
        req.setShouldCache(shouldCache);
        getRequestQueue().add(req);
    }

    /**
     * 存储当前用户
     * @param user 登录用户
     */
    public void saveUser(User user) {
        PreferenceUtil.save(user, "user");
    }
    /**
     * 存储当前用户
     * @param user 登录用户
     */
    public void saveUsers(User user) {
        PreferenceUtil.save(user, user.getLoginName());
    }

    /**
     * 获取当前用户实体
     *
     * @return user
     */
    public User getCurrentUser(){

        return PreferenceUtil.find("user", User.class);
    }
    /**
     * 获取当前用户实体
     *
     * @return User
     */
    public User getUserValue(String key){

        return PreferenceUtil.find(key, User.class);
    }

    /**
     * 获取当前用户实体
     *
     * @return User
     */
    public List<User> getAllUser(){

        return PreferenceUtil.findAll(User.class);
    }

    public HashMap<String,ParameterValue> getLoginUrlMap(){

        if (loginUrlMap == null||loginUrlMap.size() == 0){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
            String sys_auto_authenticate = preferences.getString("sys_auto_authenticate", "");
            String sys_username = preferences.getString("sys_username", "");
            String sys_password = preferences.getString("sys_password", "");
            loginUrlMap.put("sys_auto_authenticate",new ParameterValue(sys_auto_authenticate));
            loginUrlMap.put("sys_username",new ParameterValue(sys_username));
            loginUrlMap.put("sys_password",new ParameterValue(sys_password));
        }
        return loginUrlMap;
    }

    public void setLoginUrlMap(String key,String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString(key,value).commit();
    }

//    public HashMap<String,ParameterValue> getCurrentUserMap(){
//
//        return userInfoMap;
//    }
//
//
//    public void setCurrentUserMap(HashMap<String,ParameterValue> map){
//
//        userInfoMap = map;
//    }
    /**
     * 存储数据（sharePreference）
     */
    public void saveValue(String key,String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString(key,value).commit();
    }

    /**
     * 获取数据（sharePreference）
     * @param key key
     * @return value or ""
     */
    public String getValue(String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString(key, "");
    }

//    /**
//     * 将当前activity添加到容器中
//     *
//     * @param activity
//     */
   /* public void addActivity(Activity activity){
        activityList.add(activity);
        System.out.println("activityList"+activityList.size());
    }*/

//    /**
//     * 从容器中移除当前activity
//     * @param activity
//     */
//    public void removeActivity(Activity activity){
//        activityList.remove(activity);
//        System.out.println("activityList"+activityList.size());
//    }

//    /**
//     * 遍历activity集合，退出所有！
//     */
//    public void exit(){
//        for (Activity activity : activityList){
//            activity.finish();
//        }
//        System.exit(0);
//    }
}
