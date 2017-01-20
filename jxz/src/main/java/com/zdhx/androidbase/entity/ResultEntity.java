package com.zdhx.androidbase.entity;

import java.io.Serializable;

/**   
 * @Title: ResultEntity.java 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 立思辰合众   
 * @date 2016-5-10 下午12:10:01 
 */
public class ResultEntity<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pageNo;
	private int pageSize;
	private int total;
	private int result;
	private String msg;
	private T data;
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}


}
