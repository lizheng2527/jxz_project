package com.zdhx.androidbase.ui.scrosearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.ScroGridAdapter;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.treadstree.ScroTreeActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.util.DateUtil;

import java.util.Calendar;


public class SelectScroActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	//回退按钮
	private ImageView backImg;

	private TextView dateFrom;
	private TextView dateTo;
	private TextView selectTree;
	private TextView studentLine;
	private Button commit;
	private LinearLayout studentSelectTree;
	private String DATAPAGERTAG = null;

	private String startDate = null;
	private String endDate = null;

	private final int SCROTREECODE = 1;

	private static TreeBean bean;


	@Override
	protected int getLayoutId() {
		return R.layout.activity_selectscro;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		backImg = (ImageView) findViewById(R.id.activity_selectscro_goback);
		dateFrom = (TextView) findViewById(R.id.activity_selectscro_date_from);
		dateFrom.setText(DateUtil.getCurrDateString());
		dateTo = (TextView) findViewById(R.id.activity_selectscro_date_to);
		dateTo.setText(DateUtil.getCurrDateString());

		selectTree = (TextView) findViewById(R.id.activity_selectscro_tree);
		if (!MainActivity.ScroSearchTag){
			selectTree.setText("昌平二毛学校");
		}else{
			if (lable != null){
				selectTree.setText(lable);
			}
		}
		studentLine = (TextView) findViewById(R.id.studentLine);
		studentSelectTree = (LinearLayout) findViewById(R.id.studentselecttree);
		if(ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			if (ScroGridAdapter.index == 0){
				studentSelectTree.setVisibility(View.GONE);
				studentLine.setVisibility(View.GONE);
			}else{
				studentSelectTree.setVisibility(View.VISIBLE);
				studentLine.setVisibility(View.VISIBLE);
			}
		}else{
			studentSelectTree.setVisibility(View.GONE);
			studentLine.setVisibility(View.GONE);
		}

		commit = (Button) findViewById(R.id.activity_selectscro_but);
		backImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}
	//点击事件分项处理
	public void onClick(View view){
		switch (view.getId()){
			case R.id.activity_selectscro_date_from:
				Calendar calendarFrom = Calendar.getInstance();
				final DatePickerDialog datePickerDialogFrom = DatePickerDialog.newInstance(SelectScroActivity.this,calendarFrom.get(java.util.Calendar.YEAR),
						calendarFrom.get(java.util.Calendar.MONTH),
						calendarFrom.get(java.util.Calendar.DAY_OF_MONTH));
				datePickerDialogFrom.show(getFragmentManager(),"DATEPICKERTAGFROM");
				DATAPAGERTAG = "DATEPICKERTAGFROM";
				break;
			case R.id.activity_selectscro_date_to:
				Calendar calendarTo = Calendar.getInstance();
				final DatePickerDialog datePickerDialogTo = DatePickerDialog.newInstance(SelectScroActivity.this,calendarTo.get(java.util.Calendar.YEAR),
						calendarTo.get(java.util.Calendar.MONTH),
						calendarTo.get(java.util.Calendar.DAY_OF_MONTH));
				datePickerDialogTo.show(getFragmentManager(),"DATEPICKERTAGTO");
				DATAPAGERTAG = "DATEPICKERTAGTO";
				break;
			case R.id.activity_selectscro_tree:
				startActivityForResult(new Intent(SelectScroActivity.this,ScroTreeActivity.class),SCROTREECODE);
				break;
			case R.id.activity_selectscro_but:
				searchScro();
				break;
		}
	}

	private void searchScro(){
//		startDate = dateFrom.getText().toString();
		endDate = dateTo.getText().toString();
		if (startDate == null&&bean == null){
			doToast("请选择搜索条件");
			return;
		}
		MainActivity.map.put("startDate",startDate);
		MainActivity.map.put("endDate",endDate);
		MainActivity.map.put("ScroTreeBean",bean);
		this.finish();
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

	}
	private int oldYear;
	private int oldMonth;
	private int oldDay;
	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
		String month = monthOfYear+1+"";
		String day = null;
		if (monthOfYear+1<10){
			month = "0"+month;
		}
		if (dayOfMonth<10){
			day = "0"+dayOfMonth;
		}else{
			day = dayOfMonth+"";
		}
		if (DATAPAGERTAG.equals("DATEPICKERTAGFROM")){
			oldYear = year;
			oldMonth = monthOfYear;
			oldDay = dayOfMonth;
			dateFrom.setText(year+"-"+month+"-"+day);
			startDate = dateFrom.getText().toString().trim();
		}
		if (DATAPAGERTAG.equals("DATEPICKERTAGTO")){
			if (oldYear-year>0){
				doToast("选择时间有误");
				return ;
			}else if (oldYear-year==0){
				if (oldMonth - monthOfYear >0){
					doToast("选择时间有误");
					return ;
				}else if (oldMonth - monthOfYear == 0){
					if (oldDay-dayOfMonth>=0){
						doToast("选择时间有误");
						return ;
					}
				}
			}
			dateTo.setText(year+"-"+month+"-"+day);
			endDate = dateTo.getText().toString().trim();
		}
	}

	private static String lable = "昌平二毛学校";
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SCROTREECODE){
			bean = (TreeBean) MainActivity.map.get("ScroTreeBean");
			if (bean != null){
				MainActivity.map.remove("ScroTreeBean");
				lable = bean.getLabel();
				selectTree.setText(lable);
			}
		}
	}
}
