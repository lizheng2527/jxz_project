package com.zdhx.androidbase.ui.treelistview.bean;


import java.util.List;

public class TreeBean1 {


	private String id;
	private String parentId;
	private String type;
	private String name;
	private List<ChildBean> child;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ChildBean> getChild() {
		return child;
	}

	public void setChild(List<ChildBean> child) {
		this.child = child;
	}

	public static class ChildBean {
		private List<ChildBean> child;
		private String id;
		private String parentId;
		private String type;
		private String name;

		public List<ChildBean> getChild() {
			return child;
		}

		public void setChild(List<ChildBean> child) {
			this.child = child;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
