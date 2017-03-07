package com.zdhx.androidbase.ui.quantity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.WorkSpaceFragment;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Title: CheckActivity.java
 * @Description: 推优审核
 * @author Li.xin @ 立思辰合众
 * @date 2016-3-30 上午10:42:16
 */
public class CheckActivity extends BaseActivity {

	private Button back;

	private RadioButton pass;
	private RadioButton unPass;
	private RadioGroup group;

	private TextView title;

	private boolean passTag = true;

	private EditText noteET;

	private Handler handler = new Handler();

	@Override
	protected int getLayoutId() {
		return R.layout.activity_cm_check;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		back = (Button) findViewById(R.id.Button01);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		title = (TextView) findViewById(R.id.TextView10);
		pass = (RadioButton) findViewById(R.id.passRB);
		unPass = (RadioButton) findViewById(R.id.unpassRB);
		group = (RadioGroup) findViewById(R.id.checkRG);
		noteET = (EditText) findViewById(R.id.noteET);
		String highQuality = (String) MainActivity.map.get("quantity_highQuality");
		String teacherFinalSelection = (String) MainActivity.map.get("quantity_teacherFinalSelection");
		if (highQuality.equals("2")){//当前为最高级别，只能选择不通过
			pass.setClickable(false);
			unPass.setClickable(false);
			unPass.setChecked(true);
			passTag = false;
			title.setText("终评审核");
		}else if (highQuality.equals("0")){//当前为最低级别，只能选择通过
			pass.setClickable(false);
			unPass.setClickable(false);
			unPass.setChecked(false);
			passTag = true;
			title.setText("预评审核");
		}else{
			if (teacherFinalSelection.equals("0")){//当前为预评级别，没有终评权限，只能选择不通过，
				pass.setClickable(false);
				unPass.setChecked(false);
				unPass.setChecked(true);
				passTag = false;
				title.setText("预评审核");
			}else{//当前为预评级别，有终评权限，都可以选择，
				pass.setClickable(true);
				unPass.setClickable(true);
				title.setText("预评审核");
			}
		}
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				if (checkedId == R.id.passRB) {
					passTag = true;//通过
				} else {
					passTag = false;//不通过
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	private HashMap<String,ParameterValue> map = new HashMap<>();
	public void onSend(View v){
		final ECProgressDialog dialog = new ECProgressDialog(CheckActivity.this,"正在上传信息");
		final String content = noteET.getText().toString().trim();
		if (content == null||content.length() == 0){
			doToast("请说明原因");
			return;
		}else{
			dialog.show();
			new ProgressThreadWrap(CheckActivity.this, new RunnableWrap() {
				@Override
				public void run() {
					String type = (String) MainActivity.map.get("quantity_type");
					String resouceId = (String) MainActivity.map.get("quantity_resouceId");
					final int position = (int) MainActivity.map.get("position");
					final WorkSpaceFragment fragment = (WorkSpaceFragment) MainActivity.map.get("fragment");
					String status;
					if (passTag){
						status = "1";
					}else{
						status = "2";
					}

					//当前资源的推优状态
					MainActivity.map.clear();
					map.putAll(ECApplication.getInstance().getLoginUrlMap());
					map.put("type",new ParameterValue(type));
					map.put("resouceId",new ParameterValue(resouceId));
					map.put("status",new ParameterValue(status));
					map.put("content",new ParameterValue(content));
					try {
						ZddcUtil.doQuantity(map);
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								fragment.onCheckRefrush(position,passTag);
								CheckActivity.this.finish();
							}
						},5);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
