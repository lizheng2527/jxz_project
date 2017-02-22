package com.zdhx.androidbase.ui.treadssearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.WorkSpaceFragment;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.treadstree.ScroTreeActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.util.LogUtil;

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

	private LinearLayout eclassLinear;
	private TextView belowLine;

	private TextView eClassTreeBtn;

	private RadioGroup passReview;

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
		belowLine = (TextView) findViewById(R.id.belowLine);
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
		eclassLinear = (LinearLayout) findViewById(R.id.search_eclass_linear);
		eClassTreeBtn = (TextView) findViewById(R.id.search_eclass_tree);
		passReview = (RadioGroup) findViewById(R.id.reviewpass);
		RadioButton passed = (RadioButton) findViewById(R.id.passed);
		RadioButton prepassed = (RadioButton) findViewById(R.id.prepassed);
		RadioButton passfail = (RadioButton) findViewById(R.id.passfail);
		if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			if (status != null){
				switch (status){
					case "2"://通过
						status = "2";
						passed.setChecked(true);
						prepassed.setChecked(false);
						passfail.setChecked(false);
						break;
					case "0"://待审核
						status = "0";
						passed.setChecked(false);
						prepassed.setChecked(true);
						passfail.setChecked(false);
						break;
					case "1"://审核失败
						status = "1";
						passed.setChecked(false);
						prepassed.setChecked(true);
						passfail.setChecked(true);
						break;
				}
			}
		}

		if (lableForBooks != null&&eclassOrBooks != null){
			selectTree.setText(lableForBooks);
		}
		if (lableForEclass != null&&eclassOrBooks != null){
			eClassTreeBtn.setText(lableForEclass);
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

		if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			if (WorkSpaceFragment.selectIndexTag == 0){//教师备课资源
				eclassLinear.setVisibility(View.GONE);
				belowLine.setVisibility(View.GONE);
				passReview.setVisibility(View.GONE);
			}else{//学生生成资源
				eclassLinear.setVisibility(View.VISIBLE);
				belowLine.setVisibility(View.VISIBLE);
				passReview.setVisibility(View.VISIBLE);
			}

		}else{
			eclassLinear.setVisibility(View.GONE);
			belowLine.setVisibility(View.GONE);
			passReview.setVisibility(View.GONE);
		}

		passReview.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId){
					case R.id.passed://通过
						nowStatus = "2";
						break;
					case R.id.prepassed://待审核
						nowStatus = "0";
						break;
					case R.id.passfail://审核失败
						nowStatus = "1";
						break;
				}
			}
		});
		eClassTreeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickTree(v,"eClass");
			}
		});
	}
	private String nowStatus;
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
	private String eclassIds;
	public static String status;
	public static String oldStatus = "2";
	private static TreeBean treeBean;
	private static TreeBean treeBeanForEclass;

	public static void setTreeBeanToNull (){
		if (treeBean != null){
			treeBean = null;
		}
		if (lableForBooks != null){
			lableForBooks = null;
		}
		if (eclassOrBooks != null){
			eclassOrBooks = null;
		}
	}

	public static void setTreeBeanForEclassToNull(){
		if (treeBeanForEclass != null){
			treeBeanForEclass = null;
		}
		if (lableForEclass != null){
			lableForEclass = null;
		}
	}
	/**
	 * 点击搜索资源
	 */
	private void searchTreads(){
		name = searchName.getText().toString();
		if (name == null||name.equals("")){
			if (treeBean == null||treeBean.getId() == null||treeBean.getType() == null){
				if (treeBeanForEclass == null||treeBeanForEclass.getId() == null){
					if (nowStatus.equals(oldStatus)){
						doToast("请选择搜索条件..");
						return;
					}
				}
			}
		}
		MainActivity.map.put("name",name);
		status = nowStatus;
		MainActivity.map.put("status",status);
		if (treeBean != null){
			clickId = treeBean.getId();
			type = treeBean.getType();
			MainActivity.map.put("clickId",clickId);
			MainActivity.map.put("type",type);
		}
		if (treeBeanForEclass != null){
			eclassIds = treeBeanForEclass.getId();
			MainActivity.map.put("eclassIds",eclassIds);
		}
		oldStatus = status;
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
		if (tag.equals("books")){
			MainActivity.map.put("SearchWorkTreeCode","SearchWorkTreeCode");
		}else{
			MainActivity.map.put("SearchWorkTreeCode","SearchWorkForEclassTree");
		}
		startActivityForResult(new Intent(context,ScroTreeActivity.class),WORKTREEACTIVITYCODE);
	}
	//	private List<TreeBean> list = new ArrayList<>();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == WORKTREEACTIVITYCODE){
			if (eclassOrBooks != null&&eclassOrBooks.equals("books")){
				TreeBean bean = (TreeBean) MainActivity.map.get("ScroTreeBean");
				if (bean != null){
					MainActivity.map.remove("ScroTreeBean");
					lableForBooks = bean.getLabel();
					selectTree.setText(lableForBooks);
					treeBean = bean;
				}
			}else{
				TreeBean bean = (TreeBean) MainActivity.map.get("ScroTreeBean");
				if (bean != null){
					MainActivity.map.remove("ScroTreeBean");
					lableForEclass = bean.getLabel();
					eClassTreeBtn.setText(lableForEclass);
					treeBeanForEclass = bean;
					LogUtil.w(treeBeanForEclass.toString());
				}
			}
		}
	}
	private static String lableForBooks ;
	private static String lableForEclass ;
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		WorkTreeActivity.parentPositionMap.clear();
//		WorkTreeActivity.positionMap.clear();
	}
}
