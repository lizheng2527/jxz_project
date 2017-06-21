package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.SystemConst;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.User;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Title: WelcomeActivity.java
 * @Package com.xinyulong.seagood.ui.account
 * @Description: TODO(过渡页面)
 * @author Li.xin @ 立思辰合众
 * @date 2016-5-3 下午9:22:06
 */
public class WelcomeActivity extends BaseActivity {

	private Activity context;
	private TextView imgWelcome;

	@Override
	protected int getLayoutId() {
		return R.layout.welcom;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		SystemConst.setTextIp(ECApplication.getInstance().getAddress());
		imgWelcome = (TextView) findViewById(R.id.textView1);
		imgWelcome.setBackgroundResource(SystemConst.welcomeId);
		getTopBarView().setVisibility(View.GONE);
		initUI();
	}

	private void initUI() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				goIntent();
			}
		},2000);
	}

	private void goIntent() {

		startActivity(new Intent(context, LoginActivity.class));
		finish();
	}


	HashMap<String,ParameterValue> loginMap;
	HashMap<String,ParameterValue> userInfoMap = new HashMap<>();
	private User userInfos;
	String loginJson;
	private void login(final String loginName,final String password) {

		ProgressUtil.show(context,"正在登陆..");
		loginMap = new HashMap<String, ParameterValue>();
		loginMap.put("loginName", new ParameterValue(loginName));
		loginMap.put("password", new ParameterValue(password));
		userInfoMap.put("sys_auto_authenticate", new ParameterValue("true"));
		userInfoMap.put("sys_username", new ParameterValue(loginName));
		userInfoMap.put("sys_password", new ParameterValue(password));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					loginJson = ZddcUtil.checkLogin(loginMap);
					if (loginJson.contains("true")) {
						String userInfoJson = ZddcUtil.getUserInfo(userInfoMap);
						if (userInfoJson.length() != 0) {
							userInfos = new Gson().fromJson(userInfoJson, User.class);
							userInfos.setLoginName(loginMap.get("loginName").getValues().get(0));
							userInfos.setPassWord(loginMap.get("password").getValues().get(0));
							LogUtil.e(userInfos.toString());
							ECApplication.getInstance().saveUser(userInfos);
//							ECApplication.getInstance().setCurrentUserMap(userInfoMap);
							ECApplication.getInstance().setLoginUrlMap("sys_auto_authenticate", "true");
							ECApplication.getInstance().setLoginUrlMap("sys_username", loginName);
							ECApplication.getInstance().setLoginUrlMap("sys_password", password);
						}
					}
					new Handler().postDelayed(new Runnable() {
						public void run() {
							ProgressUtil.hide();
							if (loginJson.contains("true")) {
								startActivity(new Intent(context, MainActivity.class));
								finish();
							} else {
								goIntent();
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ProgressUtil.hide();
					ToastUtil.showMessage("连接服务器失败");
				}
			}
		}).start();
	}

}
