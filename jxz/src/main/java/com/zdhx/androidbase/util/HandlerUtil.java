package com.zdhx.androidbase.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class HandlerUtil {

	public static void postMessage(final Context context, Handler handler, final String msg) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
