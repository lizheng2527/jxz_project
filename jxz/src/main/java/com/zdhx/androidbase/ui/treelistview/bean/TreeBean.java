package com.zdhx.androidbase.ui.treelistview.bean;


import com.zdhx.androidbase.ui.treelistview.utils.annotating.TreeNodeHeadPortraitUrl;
import com.zdhx.androidbase.ui.treelistview.utils.annotating.TreeNodeId;
import com.zdhx.androidbase.ui.treelistview.utils.annotating.TreeNodeLabel;
import com.zdhx.androidbase.ui.treelistview.utils.annotating.TreeNodeLeafSize;
import com.zdhx.androidbase.ui.treelistview.utils.annotating.TreeNodePid;
import com.zdhx.androidbase.ui.treelistview.utils.annotating.TreeNodeTerminalId;

public class TreeBean {
	@TreeNodeId
	private String id;		//标识
	@TreeNodePid
	private String pId;	//指向父节点的标识
	@TreeNodeLabel
	private String label;	//名字
	@TreeNodeTerminalId
	private String contactId;	//
	@TreeNodeHeadPortraitUrl
	private String type;
	@TreeNodeLeafSize
	private String LeafSize;

	public TreeBean() {
	}

	public TreeBean(String id, String pId, String label) {
		super();
		this.id = id;
		this.pId = pId;
		this.label = label;
	}
	

	public TreeBean(String id, String pId, String label, String contactId,
					String type) {
		super();
		this.id = id;
		this.pId = pId;
		this.label = label;
		this.contactId = contactId;
		this.type = type;
	}

	public TreeBean(String id, String pId, String label, String contactId,
					String type, String leafSize) {
		super();
		this.id = id;
		this.pId = pId;
		this.label = label;
		this.contactId = contactId;
		this.type = type;
		LeafSize = leafSize;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLeafSize() {
		return LeafSize;
	}

	public void setLeafSize(String leafSize) {
		LeafSize = leafSize;
	}

	@Override
	public String toString() {
		return "TreeBean{" +
				"id='" + id + '\'' +
				", pId='" + pId + '\'' +
				", label='" + label + '\'' +
				", contactId='" + contactId + '\'' +
				", type='" + type + '\'' +
				", LeafSize='" + LeafSize + '\'' +
				'}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contactId == null) ? 0 : contactId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeBean other = (TreeBean) obj;
		if (contactId == null) {
			if (other.contactId != null)
				return false;
		} else if (!contactId.equals(other.contactId))
			return false;
		return true;
	}

}
