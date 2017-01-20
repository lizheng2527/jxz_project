package com.zdhx.androidbase.util;

import android.content.Context;

import com.zdhx.androidbase.view.LoadingDialog;

public class ProgressUtil {

    private static LoadingDialog dialog = null;

    public static LoadingDialog show(Context context, String msg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(msg);
        } else {
            dialog = new LoadingDialog(context, 0, msg);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.show();
            System.out.println("LoadingDialog--show()");
        }

        return dialog;
    }

    public static void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
