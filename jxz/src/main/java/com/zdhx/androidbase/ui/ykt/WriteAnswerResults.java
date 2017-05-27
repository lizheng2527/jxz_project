package com.zdhx.androidbase.ui.ykt;

public class WriteAnswerResults {
	
	private String previewImage;
	private String originalImageQuestion;
	private String originalImage;
	private String studentId;
	private String studentName;
	private String previewImageQuestion;
	private String dateTime;
	private String originalImageAttachmentId;
	private String previewImageAttachmentId;
	private String questionName;

	public String getReName() {
		return reName;
	}

	public void setReName(String reName) {
		this.reName = reName;
	}

	private String reName;

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

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

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	private boolean isQuestion = false;

	public boolean isQuestion() {
		return isQuestion;
	}

	public void setQuestion(boolean question) {
		isQuestion = question;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	private String teacherName;
	private int index;
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	private boolean isSelectde = false;

	public boolean isSelectde() {
		return isSelectde;
	}

	public void setSelectde(boolean selectde) {
		isSelectde = selectde;
	}

	public WriteAnswerResults() {
		super();
	}
	public String getPreviewImage() {
		return previewImage;
	}
	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
	public String getOriginalImageQuestion() {
		return originalImageQuestion;
	}
	public void setOriginalImageQuestion(String originalImageQuestion) {
		this.originalImageQuestion = originalImageQuestion;
	}
	public String getOriginalImage() {
		return originalImage;
	}
	public void setOriginalImage(String originalImage) {
		this.originalImage = originalImage;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getPreviewImageQuestion() {
		return previewImageQuestion;
	}
	public void setPreviewImageQuestion(String previewImageQuestion) {
		this.previewImageQuestion = previewImageQuestion;
	}

}
