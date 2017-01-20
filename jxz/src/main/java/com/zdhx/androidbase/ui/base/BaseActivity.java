package com.zdhx.androidbase.ui.base;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.zdhx.androidbase.util.StringUtil;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.view.TopBarView;

/**
 * @Title: BaseActivity.java
 * @Package com.xinyulong.seagood
 * @Description: TODO(用一句话描述该文件做什么)
 * @date 2016-5-3 上午10:30:17
 */
public abstract class BaseActivity extends FragmentActivity {

	protected abstract int getLayoutId();

	private CCPActivityBase mBaseActivity = new CCPActivityImpl(this);

	private static final String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mBaseActivity.init(getBaseContext(), this);
		PgyCrashManager.register(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FragmentManager fm = getSupportFragmentManager();
		int index = requestCode >> 16;
		if (index != 0) {
			index--;
			if (fm.getFragments() == null || index < 0
					|| index >= fm.getFragments().size()) {
				Log.w(TAG, "Activity result fragment index out of range: 0x"
						+ Integer.toHexString(requestCode));
				return;
			}
			Fragment frag = fm.getFragments().get(index);
			if (frag == null) {
				Log.w(TAG, "Activity result no fragment exists for index: 0x"
						+ Integer.toHexString(requestCode));
			} else {
				handleResult(frag, requestCode, resultCode, data);
			}
			return;
		}

	}

	/**
	 * 递归调用，对所有子Fragement生效
	 * 
	 * @param frag
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	private void handleResult(Fragment frag, int requestCode, int resultCode,
			Intent data) {
		frag.onActivityResult(requestCode & 0xffff, resultCode, data);
		List<Fragment> frags = frag.getChildFragmentManager().getFragments();
		if (frags != null) {
			for (Fragment f : frags) {
				if (f != null)
					handleResult(f, requestCode, resultCode, data);
			}
		}
	}

	protected void onActivityInit() {
		// CCPAppManager.setContext(this);
	}

	public void onBaseContentViewAttach(View contentView) {
		setContentView(contentView);
	}

	public TopBarView getTopBarView() {
		return mBaseActivity.getTopBarView();
	}

	public static String getUnNullString(String s, String defalt) {
		return (s == null || StringUtil.isBlank(s) || "null".equals(s)) ? defalt
				: s;
	}

	public void doToast(String string) {
		ToastUtil.showMessage(string);
	}

	public void transfer(Class<?> clazz) {
		try {
			Intent intent = new Intent(this, clazz);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, int requestCode) {
		try {
			Intent intent = new Intent(this, clazz);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, Serializable obj, String params) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, String obj, String params) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, String obj, String params,
			String obj1, String params1) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			intent.putExtra(params1, obj1);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, String str, String params,
			int requestCode) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, str);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, int requestCode, Serializable obj,
			String params) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public String getStringByUI(View view) {

		if (view instanceof EditText) {
			return ((EditText) view).getText().toString().trim();
		} else if (view instanceof TextView) {
			return ((TextView) view).getText().toString().trim();
		}
		return "";
	}

}
