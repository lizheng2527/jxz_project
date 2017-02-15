package com.zdhx.androidbase.entity;

import java.io.Serializable;

public class ImageUrlBean implements Serializable{
	private String showUrl;
	private String downLoadUrl;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ImageUrlBean(String showUrl, String downLoadUrl) {
		this.showUrl = showUrl;
		this.downLoadUrl = downLoadUrl;
	}

	public ImageUrlBean() {
	}

	public String getShowUrl() {
		return showUrl;
	}

	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}

	public String getDownLoadUrl() {
		return downLoadUrl;
	}

	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}
}
