package com.zdhx.androidbase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.zdhx.androidbase.entity.User;
import com.zdhx.androidbase.util.PreferenceUtil;
import com.zdhx.androidbase.view.IosAlertDialog;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.UUID;

public final class SystemEnv {

    private static Context context = ECApplication.getInstance().getApplicationContext();
    private static SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    private static TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    /**
     * 系统退出(带提示框)
     */
    public static void systemOut(Activity context) {
        IosAlertDialog dialog = new IosAlertDialog(context).builder();
        dialog.setTitle("系统提示");
        dialog.setMsg("是否确定退出应用?");
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemEnv.systemOut();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();

    }

    /**
     * 系统退出(不带提示框)
     */
    public static void systemOut() {
        AppManager.getAppManager().AppExit();
    }

    /**
     * 系统退出标记
     */
    private static final String ISFINISHED = "ISFINISHED";

    public static boolean isFinished() {
        return pref.getBoolean(ISFINISHED, false);
    }

    public static void setFinish(boolean isFinish) {
        Editor et = pref.edit();
        et.putBoolean(ISFINISHED, isFinish);
        et.commit();
    }

    /**
     * 获取手机系统的版本
     *
     * @return
     */
    public static String getSystemVersion() {

        if (Build.VERSION.RELEASE == null) {
            return "Android";
        }
        return "Android" + Build.VERSION.RELEASE;
    }

    /**
     * 获取手机唯一标示码
     *
     * @return
     */
    public static String getUniqueCode() {
        String code = "";

        if (TextUtils.isEmpty(code)) {
            code = getIMEI();
        }

        if (TextUtils.isEmpty(code)) {
            code = getCurrentMachineSerialNo();
        }
        return code;
    }

    /**
     * 获取手机设备号IMEI<br>
     * 即international mobile Equipment identity手机唯一标识码
     *
     * @return
     */
    public static String getIMEI() {
        return tm.getDeviceId();
    }

    /**
     * 获取当前设备组合的唯一标示
     *
     * @return
     */
    public static String getCurrentMachineSerialNo() {

        String cpuNo = getCPUSerialNo();
        String androidId = getAndroidID();

        boolean cpuIsEmpty = cpuNo == null || cpuNo.equals("");
        boolean androidIdIsEmpty = androidId == null || androidId.equals("");

        if (cpuIsEmpty) {
            cpuNo = "";
        }

        if (androidIdIsEmpty) {
            androidId = "";
        }

        long mostSigBits = 0L;
        long leastSigBits = 0L;

        UUID uuid;

        mostSigBits = cpuNo.hashCode();
        leastSigBits = androidId.hashCode();

        uuid = new UUID(mostSigBits, leastSigBits);
        String MachineSerialNo = uuid.toString();

        return MachineSerialNo;
    }

    private static String getAndroidID() {
        String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }

    private static String getCPUSerialNo() {
        String str = "", strCPU = "", cpuAddress = "";

        try {

            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");

            InputStreamReader ir = new InputStreamReader(pp.getInputStream());

            LineNumberReader input = new LineNumberReader(ir);

            for (int i = 1; i < 100; i++) {

                str = input.readLine();

                if (str != null) {

                    if (str.indexOf("Serial") > -1) {
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        cpuAddress = strCPU.trim();
                        break;
                    }

                } else {
                    break;
                }

            }

            input.close();
            ir.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return cpuAddress;

    }

    /**
     * 当前版本是否第一次运行
     *
     * @return
     */
    public static boolean isFirstRun() {
        return pref.getBoolean(SystemEnv.getVersionName(), true);
    }

    public static void setFirstRun(boolean isFirst) {
        pref.edit().putBoolean(SystemEnv.getVersionName(), isFirst).commit();
    }

    /**
     * 存放快捷方式名称
     **/
    private static final String SHORTCUT = "SHORTCUT";

    /**
     * 存快捷方式名称
     *
     * @param shortCut
     */
    public static void saveShortCut(String shortCut) {
        Editor et = pref.edit();
        et.putString(SHORTCUT, shortCut);
        et.commit();
    }

    /**
     * 取快捷方式名称
     *
     * @return
     */
    public static String getShorCut() {
        return pref.getString(SHORTCUT, "");
    }

    /**
     * 内部版本号
     *
     * @return
     * @author 王磊杰 2012-12-05
     */
    public static Integer getVersionCode() {
        PackageInfo info = getAppPackageInfo();
        if (info == null) {
            return 1;
        }
        return info.versionCode;
    }

    /**
     * 外部版本号
     *
     * @return
     */
    public static String getVersionName() {
        PackageInfo info = getAppPackageInfo();
        if (info == null) {
            return "1.0";
        }
        return info.versionName;
    }

    /**
     * 存放导出报表的邮箱地址
     **/
    private static final String EMAILADDRESS = "EMAILADDRESS";

    /**
     * 存导出报表的邮箱地址
     *
     * @return
     */
    public static void saveEmailAddress(String emailAddress) {
        Editor et = pref.edit();
        et.putString(EMAILADDRESS, emailAddress);
        et.commit();
    }

    /**
     * 取导出报表的邮箱地址
     *
     * @return
     */
    public static String getEmailAddress() {
        return pref.getString(EMAILADDRESS, "");
    }

    private static PackageInfo getAppPackageInfo() {
        try {

            Context context = ECApplication.getInstance().getApplicationContext();
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String USERNAME = "USER_NAME";
    private static final String PASSWORD = "PASSWORD";

    private static final String ANONYMOUS_ACCOUNT = "Anonymous_Account";

    public static String getAnonymousAccount() {
        return pref.getString(ANONYMOUS_ACCOUNT, "-1");
    }

    public static void setAnonymousAccount(String userid) {
        Editor et = pref.edit();
        et.putString(ANONYMOUS_ACCOUNT, userid);
        et.commit();
    }

    public static void rememberUserNameAndPassword(String userName, String password) {
        Editor et = pref.edit();
        et.putString(USERNAME, userName);
        et.putString(PASSWORD, password);
        et.commit();
    }

    public static String getRememberUserName() {
        return pref.getString(USERNAME, "");
    }

    public static String getRememberUserPassword() {
        return pref.getString(PASSWORD, "");
    }

    /**
     * 登录用户信息存取
     */
    private static final String USER = "USER";

    public static void saveUser(User user) {
        PreferenceUtil.save(user, USER);
    }

    public static User getUser() {
        User user = PreferenceUtil.find(USER, User.class);
        return user;
    }

    public static void deleteUser() {
        PreferenceUtil.deleteAll(User.class);
    }

    private static final String STATUSBarHeight = "statusBarHeight";

    public static void setStatusBarHeight(int statusBarHeight) {
        Editor et = pref.edit();
        et.putInt(STATUSBarHeight, statusBarHeight);
        et.commit();
    }

    public static int getStatusBarHeight() {
        return pref.getInt(STATUSBarHeight, 0);
    }


    public static final String GOODS_FILTER = "GOODS_FILTER";

}
