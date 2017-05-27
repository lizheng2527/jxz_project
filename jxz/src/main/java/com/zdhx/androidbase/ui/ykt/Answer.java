package com.zdhx.androidbase.ui.ykt;

public class Answer {
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

	private String originalImage;
	private String previewImage;
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
