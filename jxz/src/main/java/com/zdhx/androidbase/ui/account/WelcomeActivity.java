package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.base.BaseActivity;

/**
 * @Title: WelcomeActivity.java
 * @Package com.xinyulong.seagood.ui.account
 * @Description: TODO(过渡页面)
 * @author Li.xin @ 立思辰合众
 * @date 2016-5-3 下午9:22:06
 */
public class WelcomeActivity extends BaseActivity {
	
	private Activity context;

	@Override
	protected int getLayoutId() {
		return R.layout.welcom;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		initUI();
	}

	private void initUI() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
					goIntent();
			}
		}, 2000);
	}

	private void goIntent() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }
	
//	public void login() {
//		VolleyUtils.requestService(SystemConst.LOGIN_URL, Params.getLoginParams("1", ECApplication.getInstance().getCurrentUser().getLoginName(), ECApplication.getInstance().getCurrentUser().getLoginPsw()), new ResultListenerImpl(
//				this) {
//			@Override
//			public void onSuccess(String response) {
//				super.onSuccess(response);
//				LoginVo vo = GsonUtil.deser(response, LoginVo.class);
//				if (vo == null) {
//					doToast(context.getResources().getString(R.string.msg_wso_error));
//					return;
//				}
//				if (vo.getResult() == 1) {
//					if (vo.getList() != null && vo.getList().size() > 0) {
//					} else {
//						User user = ECApplication.getInstance().getCurrentUser();
//						user.setLogin(false);
//						ECApplication.getInstance().saveUser(user);
//						doToast("登录失败，请重试");
//					}
//					goIntent();
//					finish();
//				} else {
//					User user = ECApplication.getInstance().getCurrentUser();
//					user.setLogin(false);
//					ECApplication.getInstance().saveUser(user);
//					doToast(vo.getMsg());
//					goIntent();
//					finish();
//				}
//			}
//
//			@Override
//			public void onError() {
//				super.onError();
//				goIntent();
//			}
//		},false);
//	}
	
	
}
