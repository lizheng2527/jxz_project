package com.zdhx.androidbase.entity;

import java.io.Serializable;

public class User implements Serializable{

	private String id;
	private String name;
	private String type;
	private String displayName;
//	private String dataSourceName;
//
//	public String getDataSourceName() {
//		return dataSourceName;
//	}
//
//	public void setDataSourceName(String dataSourceName) {
//		this.dataSourceName = dataSourceName;
//	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	private String loginName;
	private String passWord;

	public User() {
		super();
	}
	public User(String id, String name, String type, String displayName) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.displayName = displayName;
	}
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				", displayName='" + displayName + '\'' +
				'}';
	}
}
