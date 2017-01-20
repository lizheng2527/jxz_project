package com.zdhx.androidbase.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdhx.androidbase.R;

/**
 * 自定义loading dialog
 * 
 * @author Administrator
 *
 */
public class LoadingDialog extends Dialog {
	private ImageView mImageView;
	private TextView mTextView;
	private Animation mAnimation;
	private Context mContext;
	private String mMessage;

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public LoadingDialog(Context context, int theme, String message) {
		super(context, R.style.dialog);
		mContext = context;
		mMessage = message;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comm_loading_dialog);

		mImageView = (ImageView) findViewById(R.id.loading_image);
		mTextView = (TextView) findViewById(R.id.loading_message);

		if (!TextUtils.isEmpty(mMessage)) {
			mTextView.setText(mMessage);
		} else {
			mTextView.setVisibility(View.GONE);
		}

		mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
		mImageView.setAnimation(mAnimation);
		mAnimation.startNow();
	}

	public void setMessage(String msg) {
		if (mTextView != null && msg != null) {
			mTextView.setVisibility(View.VISIBLE);
			mTextView.setText(msg);
		}
	}
}
