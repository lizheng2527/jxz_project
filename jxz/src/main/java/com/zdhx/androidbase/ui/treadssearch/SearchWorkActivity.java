package com.zdhx.androidbase.ui.treadssearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.treadstree.ScroTreeActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;

import java.util.Calendar;
import java.util.HashMap;

public class SearchWorkActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

	//回退按钮
	private ImageView backImg;
	private EditText searchName;
	private TextView dateFrom;
	private TextView dateTo;
	private TextView selectTree;
	private Button commitBtn;
	private TextView title;
	private TextView eclasstree;

	private TextView books;
	private String DATAPAGERTAG = null;

	private Activity context;

	private final int WORKTREEACTIVITYCODE = 1;

	public static String eclassOrBooks;

	private String startDate = null;
	private String endDate = null;

	private LinearLayout dateVis;
	private TextView dateLine;

	private HashMap<String,ParameterValue> hashMap = new HashMap<>();
	@Override
	protected int getLayoutId() {
		return R.layout.activity_searchwork;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.search_title);
		title.setText("资源搜索");
		books = (TextView) findViewById(R.id.books);
		books.setText("教材");
		dateVis = (LinearLayout) findViewById(R.id.datevis);
		dateVis.setVisibility(View.GONE);
		dateLine = (TextView) findViewById(R.id.datelin);
		dateLine.setVisibility(View.GONE);
		eclasstree = (TextView) findViewById(R.id.eclasstree);
		backImg = (ImageView) findViewById(R.id.search_treads_back);
		searchName = (EditText) findViewById(R.id.search_treads_name);
		dateFrom = (TextView) findViewById(R.id.search_treads_from);
		dateTo = (TextView) findViewById(R.id.search_treads_to);
		selectTree = (TextView) findViewById(R.id.search_treads_tree);
		if (lable != null){
			selectTree.setText(lable);
		}
		commitBtn = (Button) findViewById(R.id.search_treads_commitBtn);
		backImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		selectTree.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickTree(v,"books");
			}
		});
	}
	//点击事件分项处理
	public void onClick(View view){
		switch (view.getId()){
			case R.id.search_treads_from:
				Calendar calendarFrom = Calendar.getInstance();
				final DatePickerDialog datePickerDialogFrom = DatePickerDialog.newInstance(SearchWorkActivity.this,calendarFrom.get(Calendar.YEAR),
						calendarFrom.get(Calendar.MONTH),
						calendarFrom.get(Calendar.DAY_OF_MONTH));
				datePickerDialogFrom.show(getFragmentManager(),"DATEPICKERTAGFROM");
				DATAPAGERTAG = "DATEPICKERTAGFROM";
				break;
			case R.id.search_treads_to:
				Calendar calendarTo = Calendar.getInstance();
				final DatePickerDialog datePickerDialogTo = DatePickerDialog.newInstance(SearchWorkActivity.this,calendarTo.get(Calendar.YEAR),
						calendarTo.get(Calendar.MONTH),
						calendarTo.get(Calendar.DAY_OF_MONTH));
				datePickerDialogTo.show(getFragmentManager(),"DATEPICKERTAGTO");
				DATAPAGERTAG = "DATEPICKERTAGTO";
				break;
			case R.id.search_treads_commitBtn://确定
				searchTreads();
				break;
		}
	}

	private String name;
	private String clickId;
	private String type;
	private TreeBean treeBean;
	/**
	 * 点击搜索资源
	 */
	private void searchTreads(){
		name = searchName.getText().toString();
		if (treeBean != null){
			clickId = treeBean.getId();
			type = treeBean.getType();
		}else{
			doToast("请选择学科");
			return;
		}
		MainActivity.map.put("name",name);
		MainActivity.map.put("clickId",clickId);
		MainActivity.map.put("type",type);
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
		{
			String month = monthOfYear + 1 + "";
			String day = null;
			if (monthOfYear + 1 < 10) {
				month = "0" + month;
			}
			if (dayOfMonth < 10) {
				day = "0" + dayOfMonth;
			} else {
				day = dayOfMonth + "";
			}
			if (DATAPAGERTAG.equals("DATEPICKERTAGFROM")) {
				oldYear = year;
				oldMonth = monthOfYear;
				oldDay = dayOfMonth;
				dateFrom.setText(year + "-" + month + "-" + day);
			}
			if (DATAPAGERTAG.equals("DATEPICKERTAGTO")) {
				if (oldYear - year > 0) {
					doToast("选择时间有误");
					return;
				} else if (oldYear - year == 0) {
					if (oldMonth - monthOfYear > 0) {
						doToast("选择时间有误");
						return;
					} else if (oldMonth - monthOfYear == 0) {
						if (oldDay - dayOfMonth >= 0) {
							doToast("选择时间有误");
							return;
						}
					}
				}
				dateTo.setText(year + "-" + month + "-" + day);
				startDate = dateFrom.getText().toString().trim();
				endDate = dateTo.getText().toString().trim();
			}
		}
	}

	/**
	 * 树列表
	 * @param v
	 */
	private void onClickTree(View v,String tag){
		eclassOrBooks = tag;
		MainActivity.map.put("SearchWorkTreeCode","SearchWorkTreeCode");
		startActivityForResult(new Intent(context,ScroTreeActivity.class),WORKTREEACTIVITYCODE);
	}
//	private List<TreeBean> list = new ArrayList<>();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == WORKTREEACTIVITYCODE){
			TreeBean bean = (TreeBean) MainActivity.map.get("ScroTreeBean");
			if (bean != null){
				MainActivity.map.remove("ScroTreeBean");
				lable = bean.getLabel();
				selectTree.setText(lable);
				treeBean = bean;
			}
		}
	}
	private static String lable ;
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		WorkTreeActivity.parentPositionMap.clear();
//		WorkTreeActivity.positionMap.clear();
	}
}
