
package com.zdhx.androidbase.util.volley;

import android.content.Context;

import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

/**
 * 
 * @author 马海明
 * @created 马海明 2015年5月6日
 */
public class LoadingDialogResultListenerImpl implements ResultLinstener {

//	private LoadingDialog dialog;
	private ECProgressDialog mDialog;
	private String tag;
	private Context context;
	/**
	 * 
	 */
	public LoadingDialogResultListenerImpl(Context context, String msg) {
		this.context = context;
		mDialog = new ECProgressDialog(context);
		mDialog.setPressText(msg);
		mDialog.show();
//		dialog = new LoadingDialog(context, R.style.dialog, msg);
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.show();
//		dialog.setOnCancelListener(new OnCancelListener() {
//
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				// VolleyUtils.cancelService(tag);
//			}
//		});
	}

	@Override
	public void onSuccess(String response) {
		if (mDialog != null) {
			mDialog.dismiss();
		}
		System.out.println(response);
	}

	@Override
	public void onServerError() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
		ToastUtil.showMessage("服务器错误");

	}

	@Override
	public void onIOError() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
		ToastUtil.showMessage("无法访问服务器,可能网络异常");
	}

	@Override
	public void onSetTag(String tag) {
		this.tag = tag;
	}

	@Override
	public void onTimeOutError() {

		if (mDialog != null) {
			mDialog.dismiss();
		}
		ToastUtil.showMessage("访问超时，请重试");
	}

	@Override
	public void onNoConnectionError() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
		ToastUtil.showMessage("连接错误，可能访问地址有误");
	}

	@Override
	public void onError() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}
}
