/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.zdhx.androidbase.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * 时间转换工具类
 * 
 * @author 容联•云通讯 Modify By Li.Xin @ 立思辰合众
 * @date 2014-12-10
 * @version 4.0
 */
public class QQFaceUtil {
	/**
	 * 返回QQ表情
	 * @param ctx
	 * @param fileName
	 * @return
	 */
	public static Drawable loadImageFromAsserts(final Context ctx, String fileName) {
		try {
			InputStream is = ctx.getResources().getAssets().open(fileName);
			return Drawable.createFromStream(is, null);
		} catch (IOException e) {
			if (e != null) {
				e.printStackTrace();
			}
		} catch (OutOfMemoryError e) {
			if (e != null) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
