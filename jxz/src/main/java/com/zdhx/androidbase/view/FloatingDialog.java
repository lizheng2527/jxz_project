package com.zdhx.androidbase.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.zdhx.androidbase.R;


/**
 * 浮动对话框
 *
 * @author 吕绪文
 * @created 吕绪文 2012-8-20
 *
 * @update 2012-11-26 16:58
 * @author 宋志军 更新内容:增加wheel浮动框样式以及构造函数
 */
public class FloatingDialog extends Dialog {

	public FloatingDialog(Context context, boolean cancelable,
						  OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public FloatingDialog(Context context, int theme) {
		super(context, R.style.Theme_Transparent);
	}

	/**
	 * wheel浮动框样式
	 *
	 * @param context
	 *            上下文
	 * @param animStyleId
	 *            浮动的动画样式
	 * @param gravity
	 *            重力,默认BOTTOM
	 *
	 * @created 2012-11-26 16:58
	 * @author 宋志军
	 */
	public FloatingDialog(Context context, int animStyleId, int gravity) {
		super(context, R.style.Theme_Transparent);

		setCanceledOnTouchOutside(true);

		// 设置window属性
		LayoutParams a = getWindow().getAttributes();
		a.gravity = gravity == 0 ? Gravity.BOTTOM : gravity;
		a.dimAmount = 0; // 去背景遮盖
		a.windowAnimations = animStyleId;
		getWindow().setAttributes(a);
	}

	public FloatingDialog(Context context) {

		super(context, R.style.Theme_Transparent);

		setCanceledOnTouchOutside(true);

		// 设置window属性
		LayoutParams a = getWindow().getAttributes();
		a.gravity = Gravity.CENTER;
		a.dimAmount = 0; // 去背景遮盖
		a.windowAnimations = R.style.dialogAnim;
		getWindow().setAttributes(a);

	}

	public void showAtPosition(View targetView) {
		int[] location = new int[2];
		targetView.getLocationOnScreen(location);
		int y = location[1];
		int x = location[0];
		int height = targetView.getHeight();

		setPosition(x, y + height / 2);
		Log.d("setPosition", "x=" + x + ",y=" + (y + height / 2));

		show();
	}

	public void setPosition(int x, int y) {
		LayoutParams a = getWindow().getAttributes();
		if (-1 != x)
			a.x = x;
		if (-1 != y)
			a.y = y;
		getWindow().setAttributes(a);
	}

}
