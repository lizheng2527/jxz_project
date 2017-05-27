package com.zdhx.androidbase.ui.ykt;

public class Question {
	private String id;
	private String answer;
	private String startTime;
	private String originalImage;
	private String type;

	private String previewImage;

	private String originalImageAttachmentId;
	private String previewImageAttachmentId;

	public String getOriginalImageAttachmentId() {
		return originalImageAttachmentId;
	}

	public void setOriginalImageAttachmentId(String originalImageAttachmentId) {
		this.originalImageAttachmentId = originalImageAttachmentId;
	}

	public String getPreviewImageAttachmentId() {
		return previewImageAttachmentId;
	}

	public void setPreviewImageAttachmentId(String previewImageAttachmentId) {
		this.previewImageAttachmentId = previewImageAttachmentId;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
	public Question() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getOriginalImage() {
		return originalImage;
	}
	public void setOriginalImage(String originalImage) {
		this.originalImage = originalImage;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
