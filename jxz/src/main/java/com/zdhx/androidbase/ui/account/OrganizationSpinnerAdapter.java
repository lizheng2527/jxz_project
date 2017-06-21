package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.Organization;

import java.util.List;


public class OrganizationSpinnerAdapter extends ArrayAdapter<Organization> {
	
	public OrganizationSpinnerAdapter(Context context, List<Organization> area) {
		super(context, android.R.layout.simple_list_item_1, area);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items_white_text, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setText(getItem(position).getSchool());
		return item;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items1, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setText(getItem(position).getSchool());
		return item;
	}
}
