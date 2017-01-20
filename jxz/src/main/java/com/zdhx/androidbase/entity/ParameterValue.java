package com.zdhx.androidbase.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ParameterValue {

	private List<String> values = new ArrayList<String>();

	public ParameterValue(ArrayList<File> files) {

	}

	public ParameterValue(String val) {
		addValue(val);
	}
	public void addValue(String value) {
		values.add(value);
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
}
