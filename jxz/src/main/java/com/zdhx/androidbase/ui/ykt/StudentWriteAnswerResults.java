package com.zdhx.androidbase.ui.ykt;

import java.util.List;

public class StudentWriteAnswerResults {
	
	private String startTime;
	private String previewImage;
	private String originalImage;
	private String questionId;
	private String wisdomClassId;
	private String questionName;
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

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	private String sex;
	private String title;

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	private String teacherName;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getWisdomClassId() {
		return wisdomClassId;
	}

	public void setWisdomClassId(String wisdomClassId) {
		this.wisdomClassId = wisdomClassId;
	}

	private List<Answer> answers;
	public StudentWriteAnswerResults() {
		super();
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public List<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	public String getPreviewImage() {
		return previewImage;
	}
	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
	public String getOriginalImage() {
		return originalImage;
	}
	public void setOriginalImage(String originalImage) {
		this.originalImage = originalImage;
	}
}
