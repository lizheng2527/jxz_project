package com.zdhx.androidbase.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.zdhx.androidbase.ui.plugin.FileUtils;

/**
 * Created by lizheng on 2017/6/20.
 */

public class PhoneShareUtil {
    /**
     * 其他应用调用本分享功能
     */
    public static String shareFormOtherProg(Intent intent, Context context) {
		/* 比如通过Gallery方式来调用本分享功能 */
//        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                try {
                    // Get resource path from intent
                    Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                    // 返回路径
                    String path = FileUtils.getPathByUri4kitkat(context, uri);
//                    out.println("path-->" + path);
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (extras.containsKey(Intent.EXTRA_TEXT)) {
                return null;
            }
        }
        return null;
    }
}
