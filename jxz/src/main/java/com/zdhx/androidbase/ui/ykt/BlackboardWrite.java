package com.zdhx.androidbase.ui.ykt;

public class BlackboardWrite {

	private String previewImageAttachmentId;
	private String originalImageAttachmentId;
	private String originalImage;
	private String previewImage;
	private String name;
	private String dateTime;
	private String teacherName;
	private boolean loading;

	public String getReName() {
		return reName;
	}

	public void setReName(String reName) {
		this.reName = reName;
	}

	private String reName;

	public String getPreviewImageAttachmentId() {
		return previewImageAttachmentId;
	}

	public void setPreviewImageAttachmentId(String previewImageAttachmentId) {
		this.previewImageAttachmentId = previewImageAttachmentId;
	}

	public String getOriginalImageAttachmentId() {
		return originalImageAttachmentId;
	}

	public void setOriginalImageAttachmentId(String originalImageAttachmentId) {
		this.originalImageAttachmentId = originalImageAttachmentId;
	}



	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public BlackboardWrite() {
		super();
	}
	public String getOriginalImage() {
		return originalImage;
	}
	public void setOriginalImage(String originalImage) {
		this.originalImage = originalImage;
	}
	public String getPreviewImage() {
		return previewImage;
	}
	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
}
