package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.util.DensityUtil;

import java.util.List;

public class TweekVoSpinnerAdapter extends ArrayAdapter<TweekVo>{
	
	public TweekVoSpinnerAdapter(Context context,
			List<TweekVo> area) {
		super(context, android.R.layout.simple_list_item_1, area);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setTextSize(DensityUtil.dip2px(14));
		tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
		tv.setText(getItem(position).getName());
		return item;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items1, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setText(getItem(position).getName());
		return item;
	}
}
