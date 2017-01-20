package com.zdhx.androidbase.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.SystemEnv;
import com.zdhx.androidbase.ui.account.WelcomeActivity;

public final class UIUtil {
    private static Toast toast;

    private UIUtil() {
    }

    public static void hideInputMethod(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (context.getWindow() != null) {
            Window win = context.getWindow();
            if (win.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(win
                        .getCurrentFocus().getWindowToken(), 0);
            } else {
                win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }
    }

    public static void disableView(View view) {
        setViewState(view, false);
    }

    public static void enableView(View view) {
        setViewState(view, true);
    }

    private static void setViewState(View view, boolean isEnabled) {
        view.setEnabled(isEnabled);
        view.setFocusable(isEnabled);
        view.setClickable(isEnabled);
    }

    /**
     * 删除桌面快捷方式
     */
    public static void delShortcut(Activity act) {

        String appName = SystemEnv.getShorCut();

        if (TextUtils.isEmpty(appName)) {
            Log.d("TAG_SHORTCUT", "桌面快捷方式不存在");
            return;
        }

        Intent shortcut = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");

        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);

        // 程序入口activity
        Intent intent = new Intent(act, WelcomeActivity.class);

        // 下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        act.sendBroadcast(shortcut);

        SystemEnv.saveShortCut("");
        Log.d("TAG_SHORTCUT", "快捷方式已删除");

    }

    /**
     * 创建桌面快捷方式
     */
    public static void addShortCut(Activity act) {

        if (!TextUtils.isEmpty(SystemEnv.getShorCut())) {
            Log.d("TAG_SHORTCUT", "桌面快捷方式已经存在:" + SystemEnv.getShorCut());
            return;
        }

        String appName = act.getString(R.string.app_name);

        // 创建快捷方式的Intent
        Intent shortcut = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建 ，如果重复的话就会有多个快捷方式
        shortcut.putExtra("duplicate", false);
        // 快捷图标的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(act,
                R.drawable.ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        // 程序入口activity
        Intent intent = new Intent(act, WelcomeActivity.class);
        // 当应用程序卸载时桌面上的快捷方式会删除
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        // 发送广播
        act.sendBroadcast(shortcut);

        SystemEnv.saveShortCut(appName);
        Log.d("TAG_SHORTCUT", "桌面快捷方式创建成功:" + appName);
    }

    private static Context context = ECApplication.getInstance();

    /**
     * 动态设置listView的高度
     *
     * @param listView
     * @param offsetHeight 高度偏移量：用于修正最后一个item高度测量不准确造成数据悬殊不全的BUG
     * @param offsetCount  显示item个数的偏移量：当需要增加footView或者headerView的时候这设置该参数
     */
    public static void setListViewHeight(ListView listView, int offsetHeight,
                                         int offsetCount) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int size = listAdapter.getCount() + offsetCount;

        for (int i = 0; i < size; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        totalHeight += offsetHeight;

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 弹出键盘
     *
     * @param editText
     */
    public static void showPan(EditText editText) {
        InputMethodManager manager = (InputMethodManager) editText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏键盘
     */
    public static void hidePan(Context mcontext, View v) {
        InputMethodManager imm = (InputMethodManager) mcontext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 自定义Toast
     *
     * @param string
     */
   /* public static void doToast(String string) {
        toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        if (toast==null) {
            toast= Toast.makeText(context, string, Toast.LENGTH_SHORT);
        }else{
            toast.cancel();
            toast= Toast.makeText(context, string, Toast.LENGTH_SHORT);
        }
        toast.show();

    }*/

    /**
     * 自定义Toast
     *
     * @param resId
     */
    /*public static void doToast(int resId) {
       toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        if (toast==null) {
            toast= Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        }else{
            toast.cancel();
            toast= Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        }
        toast.show();
    }*/


    /**
     * 获取手机屏幕宽度像素
     *
     * @return
     */
    public static int getWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取手机屏幕高度像素
     *
     * @return
     */
    public static int getHeight() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Dip to Px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * Px To Dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 屏蔽输入框的复制粘贴功能
     * @param editText 输入框引用对象
     */
    public static void screenCopyAndPaste(EditText editText) {
        //判断Android版本是否大于等于11,以此调用不同的api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }else{
            editText.setLongClickable(false);
        }
    }
}
