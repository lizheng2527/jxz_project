package com.zdhx.androidbase.entity;

public class Organization {
	private String dataSourceName;
	private String school;

	public Organization() {
	}

	public Organization(String dataSourceName, String school) {

		this.dataSourceName = dataSourceName;
		this.school = school;
	}

	public String getDataSourceName() {

		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}
}
