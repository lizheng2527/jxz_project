package com.zdhx.androidbase.ui.quantity;

import android.content.DialogInterface;
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
import com.zdhx.androidbase.ui.account.WorkSpaceGridAdapter;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Title: CheckActivity.java
 * @Description: 资源审核
 * @author
 * @date 2016-3-30 上午10:42:16
 */
public class PrePassActivity extends BaseActivity {

	private Button back;

	private RadioButton pass;
	private RadioButton unPass;
	private RadioGroup group;

	private TextView title;

	private boolean passTag = true;

	private EditText noteET;

	private Handler handler = new Handler();

	private WorkSpaceFragment fragment;
	private int position;
	private String status;
	private String resouceIds;

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
		title.setText("资源审核");
		pass = (RadioButton) findViewById(R.id.passRB);
		unPass = (RadioButton) findViewById(R.id.unpassRB);
		group = (RadioGroup) findViewById(R.id.checkRG);
		noteET = (EditText) findViewById(R.id.noteET);
		//获取批量审核的ids
		resouceIds = (String) MainActivity.map.get("ids");
		//获取单个审核的id
//		position = (int) MainActivity.map.get("prePassPosition");
		fragment = (WorkSpaceFragment) MainActivity.map.get("WorkSpaceFragment");
		MainActivity.map.clear();
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
		final ECProgressDialog dialog = new ECProgressDialog(PrePassActivity.this,"信息校验中..");
		dialog.show();
		final String content = noteET.getText().toString().trim();
		if (content == null||content.length() == 0){
			doToast("请说明原因");
			return;
		}else{
			new ProgressThreadWrap(PrePassActivity.this, new RunnableWrap() {
				@Override
				public void run() {

					if (passTag){
						status = "2";
					}else{
						status = "1";
					}
					map.put("resouceId",new ParameterValue(resouceIds));
					map.put("content",new ParameterValue(content));
					map.put("status",new ParameterValue(status));
					map.putAll(ECApplication.getInstance().getLoginUrlMap());
					try {
						ZddcUtil.doCheck(map);
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								//判断当前如果是批量选择的，并且选择数量大于一个，提示他是否还要继续审核，如果继续审核，视图显示多选框，否则隐藏
								if (resouceIds.contains(",")){
									ECAlertDialog.buildAlert(PrePassActivity.this, "是否继续审核？", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											MainActivity.showSelectBatchLinear(false);
											WorkSpaceFragment.isBatchSelect = false;
											fragment.workSpaceReFreshDatas(WorkSpaceGridAdapter.index,1);
//											fragment.notifyForSelect();
											PrePassActivity.this.finish();
										}
									}, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											MainActivity.showSelectBatchLinear(true);
											WorkSpaceFragment.isBatchSelect = true;
											fragment.workSpaceReFreshDatas(WorkSpaceGridAdapter.index,1);
//											fragment.notifyForSelect();
											PrePassActivity.this.finish();
										}
									}).show();
								}else{
									MainActivity.showSelectBatchLinear(false);
									WorkSpaceFragment.isBatchSelect = false;
									fragment.workSpaceReFreshDatas(WorkSpaceGridAdapter.index,1);
//									fragment.notifyForSelect();
									PrePassActivity.this.finish();
								}
								fragment.getBatchSelectMap().clear();
							}
						},10);
					} catch (IOException e) {
						doToast("审核失败");
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
