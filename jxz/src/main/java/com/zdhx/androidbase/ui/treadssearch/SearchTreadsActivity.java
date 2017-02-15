package com.zdhx.androidbase.ui.treadssearch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.DateUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SearchTreadsActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
	//回退按钮
	private ImageView backImg;
	private EditText searchName;
	private TextView dateFrom;
	private TextView dateTo;
	private TextView selectTree;
	private Button commitBtn;

	private String DATAPAGERTAG = null;

	private Activity context;

	private Spinner spinner;

	private Handler handler = new Handler();

	private List<SpinnerBean> datas;

	private List<SpinnerBean.DataListBean> spinnerDatas;

	private HashMap<String,ParameterValue> sendMap;

	private String eclassId = null;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_searchtreads;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		sendMap = new HashMap<>();
		getTopBarView().setVisibility(View.GONE);
		backImg = (ImageView) findViewById(R.id.search_treads_back);
		searchName = (EditText) findViewById(R.id.search_treads_name);
		dateFrom = (TextView) findViewById(R.id.search_treads_from);
		dateFrom.setText(DateUtil.getCurrDateString());
		dateTo = (TextView) findViewById(R.id.search_treads_to);
		dateTo.setText(DateUtil.getCurrDateString());
		selectTree = (TextView) findViewById(R.id.search_treads_tree);
		commitBtn = (Button) findViewById(R.id.search_treads_commitBtn);
		spinner = (Spinner) findViewById(R.id.search_treads_spinner);
		if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
			spinner.setVisibility(View.GONE);
		}else{
			ProgressUtil.show(this,"正在加载班级数据");
		}
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0){

				}else{
					eclassId = spinnerDatas.get(position).getId();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					String json = ZddcUtil.teacherEclassTree(ECApplication.getInstance().getLoginUrlMap());
					if (json.contains("ok")){
						List<SpinnerBean> bean = new Gson().fromJson(json, new TypeToken<List<SpinnerBean>>(){}.getType());
						spinnerDatas = new ArrayList<SpinnerBean.DataListBean>();
						SpinnerBean.DataListBean d = new SpinnerBean.DataListBean();
						d.setName("请选择班级");
						spinnerDatas.add(d);
						spinnerDatas.addAll(bean.get(0).getDataList());
					}
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							spinner.setAdapter(new SpinnerAdapter());
							ProgressUtil.hide();
						}
					},5);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
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
			case R.id.search_treads_from:
				Calendar calendarFrom = Calendar.getInstance();
				final DatePickerDialog datePickerDialogFrom = DatePickerDialog.newInstance(SearchTreadsActivity.this,calendarFrom.get(java.util.Calendar.YEAR),
						calendarFrom.get(java.util.Calendar.MONTH),
						calendarFrom.get(java.util.Calendar.DAY_OF_MONTH));
				datePickerDialogFrom.show(getFragmentManager(),"DATEPICKERTAGFROM");
				DATAPAGERTAG = "DATEPICKERTAGFROM";
				break;
			case R.id.search_treads_to:
				Calendar calendarTo = Calendar.getInstance();
				final DatePickerDialog datePickerDialogTo = DatePickerDialog.newInstance(SearchTreadsActivity.this,calendarTo.get(java.util.Calendar.YEAR),
						calendarTo.get(java.util.Calendar.MONTH),
						calendarTo.get(java.util.Calendar.DAY_OF_MONTH));
				datePickerDialogTo.show(getFragmentManager(),"DATEPICKERTAGTO");
				DATAPAGERTAG = "DATEPICKERTAGTO";
				break;
			case R.id.search_treads_commitBtn:
				searchTreads();
				break;
		}
	}

	/**
	 * 提交请求参数
	 * @param
	 */
	private void searchTreads(){
		String name = null;
		name = getStringByUI(searchName);
		String startDate = null;
		startDate = getStringByUI(dateFrom);
		String endDate = null;
		endDate = getStringByUI(dateTo);
		MainActivity.map.put("name",name);
		MainActivity.map.put("startDate",startDataStr);
		MainActivity.map.put("endDate",endDataStr);
		MainActivity.map.put("eclassId",eclassId);
		this.finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

	}

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
		int month = monthOfYear+1;
		String y = year+"";
		String m;
		String d;
		if (month <10){
			m = "0"+month+"";
		}else{
			m = month+"";
		}
		if (dayOfMonth <10){
			d = "0"+dayOfMonth+"";
		}else{
			d = dayOfMonth+"";
		}
		if (DATAPAGERTAG.equals("DATEPICKERTAGFROM")){
			startDataStr = y+"-"+m+"-"+d;
			dateFrom.setText(startDataStr);
			oldYear = year;
			oldMonth = monthOfYear;
			oldDay = dayOfMonth;
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
			endDataStr = y+"-"+m+"-"+d;
			dateTo.setText(endDataStr);
		}

	}
	private int oldYear;
	private int oldMonth;
	private int oldDay;
	private int startDate;
	private String startDataStr;
	private String endDataStr;

	class SpinnerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return spinnerDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return spinnerDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null){
				vh = new ViewHolder();
				convertView = View.inflate(context,R.layout.upfile_spinner_item,null);
				vh.tv = (TextView) convertView.findViewById(R.id.spinner_text);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			vh.tv.setText(spinnerDatas.get(position).getName());
			return convertView;
		}

		class ViewHolder{
			private TextView tv;
		}
	}

	public static class SpinnerBean implements Serializable {

		/**
		 * dataList : [{"id":"20160902114416587181119937684883","name":"小学2020届-3班"},{"id":"20160902114416650446129968199807","name":"小学2020届-4班"}]
		 * status : ok
		 */

		private String status;
		private List<DataListBean> dataList;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<DataListBean> getDataList() {
			return dataList;
		}

		public void setDataList(List<DataListBean> dataList) {
			this.dataList = dataList;
		}

		public static class DataListBean {
			/**
			 * id : 20160902114416587181119937684883
			 * name : 小学2020届-3班
			 */

			private String id;
			private String name;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
		}
	}
}
