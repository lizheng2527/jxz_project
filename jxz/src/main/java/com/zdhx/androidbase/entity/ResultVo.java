
package com.zdhx.androidbase.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 接口返回值json解析vo基类 ，解析继承此类即可
 */
public class ResultVo<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int pageNo;
	private int pageSize;
	private int total;
	private int result;
	private String msg;
	private List<T> data;

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

	public List<T> getList() {
		return data;
	}

	public void setList(List<T> data) {
		this.data = data;
	}

	public boolean isHasNextPage() {
		int lastIndex = pageNo * pageSize;
		if (lastIndex >= total)
			return false;
		return true;

	}

}
