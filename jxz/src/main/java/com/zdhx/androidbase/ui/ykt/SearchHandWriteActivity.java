package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;

import java.util.HashMap;

import static com.zdhx.androidbase.ui.ykt.YKTFragment.courses;
import static com.zdhx.androidbase.ui.ykt.YKTFragment.tweekVosForStudent;

public class SearchHandWriteActivity extends BaseActivity{

	private Context context;


	private final int SHOWWEEKCODE = 0;

	private Spinner weekSpinner,eClassSpinner,booksSpinner;

	private ImageView backImg;

	private TextView commitBtn;

	private LinearLayout eclassLinear;
	private TextView centerLine;
	private TextView belowLine;
	private LinearLayout booksLinear;
	public static HashMap<String,Integer> selectWeekMap = new HashMap();
	public static HashMap<String,Integer> selectEclassMap = new HashMap();
	public static HashMap<String,Integer> selectBooksMap = new HashMap();
	private HashMap<String,Integer> nowSelectWeekMap = new HashMap();
	private HashMap<String,Integer> nowSelectEclassMap = new HashMap();
	private HashMap<String,Integer> nowSelectBooksMap = new HashMap();
	private WeekAdapter weekAdapter0;
	private WeekAdapter weekAdapter1;
	private WeekAdapter weekAdapter2;


	@Override
	protected int getLayoutId() {
		return R.layout.activity_searchhandwrite;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		eclassLinear = (LinearLayout) findViewById(R.id.eclassLinear);
		booksLinear = (LinearLayout) findViewById(R.id.booksLinear);
		centerLine = (TextView) findViewById(R.id.centerLine);
		belowLine = (TextView) findViewById(R.id.belowLine);
		context = this;
		backImg = (ImageView) findViewById(R.id.search_treads_back);
		backImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		weekSpinner = (Spinner) findViewById(R.id.search_handwrite_spinner);
		weekAdapter0 = new WeekAdapter(0);
		weekSpinner.setAdapter(weekAdapter0);

		eClassSpinner = (Spinner) findViewById(R.id.search_handwrite_spinner_eclass);
		weekAdapter1 = new WeekAdapter(1);
		eClassSpinner.setAdapter(weekAdapter1);

		//教材只给学生使用(课程)
		if (ECApplication.getInstance().getUserForYKT().getType().equals("0")){
			booksSpinner = (Spinner) findViewById(R.id.search_handwrite_spinner_books);
			weekAdapter2 = new WeekAdapter(2);
			booksSpinner.setAdapter(weekAdapter2);
			booksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					nowSelectBooksMap.put("true",position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
			if (selectWeekMap.size() != 0&&selectBooksMap.get("true") != null){
				booksSpinner.setSelection(selectBooksMap.get("true"));
			}
		}
		//设置选择周的显示
		if (ECApplication.getInstance().getUserForYKT().getType().equals("2")&&selectWeekMap.size() != 0&&selectWeekMap.get("true")<=YKTFragment.tweekVos.size()){
			weekSpinner.setSelection(selectWeekMap.get("true"));
		}
		if (ECApplication.getInstance().getUserForYKT().getType().equals("0")&&selectWeekMap.size() != 0&&selectWeekMap.get("true")<=YKTFragment.tweekVosForStudent.size()){
			weekSpinner.setSelection(selectWeekMap.get("true"));
		}
		weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				nowSelectWeekMap.put("true",position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		//设置选择班级的显示
		if (ECApplication.getInstance().getUserForYKT().getType().equals("2")&&selectEclassMap.size() != 0){
			eClassSpinner.setSelection(selectEclassMap.get("true"));
		}

		eClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				nowSelectEclassMap.put("true",position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		commitBtn = (TextView) findViewById(R.id.search_handwrite_commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commit();
			}
		});

		if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
			eclassLinear.setVisibility(View.VISIBLE);
			centerLine.setVisibility(View.VISIBLE);
			belowLine.setVisibility(View.GONE);
			booksLinear.setVisibility(View.GONE);
		}else if (ECApplication.getInstance().getUserForYKT().getType().equals("0")){
			eclassLinear.setVisibility(View.GONE);
			booksLinear.setVisibility(View.VISIBLE);
			centerLine.setVisibility(View.GONE);
			belowLine.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * index:
	 * “0”是显示周的spinner
	 * “1”是显示课程的spinner
	 * "2"是显示学生教材的spinner
	 */
	private class WeekAdapter extends BaseAdapter{

		private int index;

		public WeekAdapter(int index){
			this.index = index;
		}
		@Override
		public int getCount() {
			if (index == 0&&ECApplication.getInstance().getUserForYKT().getType().equals("2")){
				return YKTFragment.tweekVos.size();
			}
			else if (index == 0&&ECApplication.getInstance().getUserForYKT().getType().equals("0")){
				return YKTFragment.tweekVosForStudent.size();
			}
			else if (index == 1&&ECApplication.getInstance().getUserForYKT().getType().equals("2")){
				return YKTFragment.courses.size();
			}
//			else if (index == 2&&ECApplication.getInstance().getUserForYKT().getType().equals("0")){
//				return YKTFragment.coursesForStudent.getCourses().size();
//			}
			else {
				return YKTFragment.coursesForStudent.getCourses().size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (index == 0&&ECApplication.getInstance().getUserForYKT().getType().equals("2")){
				return YKTFragment.tweekVos.get(position);
			}
			else if (index == 0&&ECApplication.getInstance().getUserForYKT().getType().equals("0")){
				return tweekVosForStudent.get(position);
			}
			else if (index == 1&&ECApplication.getInstance().getUserForYKT().getType().equals("2")){
				return courses.get(position);
			}
			else if (index == 2){
				return YKTFragment.coursesForStudent.getCourses().get(position);
			}
			else{
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(context,R.layout.activity_handwrite_spinner_item,null);
			TextView tv = (TextView) view.findViewById(R.id.search_handwrite_spinner_item);
			switch (index){
				case 0:
					if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
						tv.setText(YKTFragment.tweekVos.get(position).getName());
					}else{
						tv.setText(YKTFragment.tweekVosForStudent.get(position).getName());
					}
					if (nowSelectWeekMap.get("true") != null && position == nowSelectWeekMap.get("true")){
						tv.setTextColor(getResources().getColorStateList(R.color.title_color));
					}else{
						tv.setTextColor(getResources().getColorStateList(R.color.gray));
					}

					break;
				case 1:

					if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
						tv.setText(YKTFragment.courses.get(position).getName());
						if (nowSelectEclassMap.get("true") != null && position == nowSelectEclassMap.get("true")){
							tv.setTextColor(getResources().getColorStateList(R.color.title_color));
						}else{
							tv.setTextColor(getResources().getColorStateList(R.color.gray));
						}
					}
					break;
				case 2:
					tv.setText(YKTFragment.coursesForStudent.getCourses().get(position).getName());
					if (nowSelectBooksMap.get("true") != null && position == nowSelectBooksMap.get("true")){
						tv.setTextColor(getResources().getColorStateList(R.color.title_color));
					}else{
						tv.setTextColor(getResources().getColorStateList(R.color.gray));
					}
					break;
			}
			return view;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * 提交搜索
	 */
	private void commit() {
		MainActivity.map.put("searchHandWriteOK","OK");
		if (ECApplication.getInstance().getUserForYKT().getType().equals("2")&&nowSelectWeekMap.size()>0){
			selectWeekMap.put("true",nowSelectWeekMap.get("true"));
		}
		if (ECApplication.getInstance().getUserForYKT().getType().equals("2")&&nowSelectEclassMap.size()>0){
			selectEclassMap.put("true",nowSelectEclassMap.get("true"));
		}
		if (ECApplication.getInstance().getUserForYKT().getType().equals("0")&&nowSelectWeekMap.size()>0){
			selectWeekMap.put("true",nowSelectWeekMap.get("true"));
		}
		if (ECApplication.getInstance().getUserForYKT().getType().equals("0")&&nowSelectBooksMap.size()>0){
			selectBooksMap.put("true",nowSelectBooksMap.get("true"));
		}
		this.finish();
	}
}
