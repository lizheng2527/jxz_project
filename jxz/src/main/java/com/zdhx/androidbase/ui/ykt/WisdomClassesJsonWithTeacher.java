package com.zdhx.androidbase.ui.ykt;

import java.util.List;

public class WisdomClassesJsonWithTeacher {
	
	private String id;
	private String sex;
	private String teacehrName;
	private List<CourseWare> courseware;
	private String dateTime;
	private String name;
	private List<Question> questions1;
	private List<Question> questions2;
	private List<BlackboardWrite> blackboardWrite;
//	private String originalImageAttachmentId;
//	private String previewImageAttachmentId;

//	public String getOriginalImageAttachmentId() {
//		return originalImageAttachmentId;
//	}
//
//	public void setOriginalImageAttachmentId(String originalImageAttachmentId) {
//		this.originalImageAttachmentId = originalImageAttachmentId;
//	}
//
//	public String getPreviewImageAttachmentId() {
//		return previewImageAttachmentId;
//	}
//
//	public void setPreviewImageAttachmentId(String previewImageAttachmentId) {
//		this.previewImageAttachmentId = previewImageAttachmentId;
//	}

	public String getTeacehrName() {
		return teacehrName;
	}

	public void setTeacehrName(String teacehrName) {
		this.teacehrName = teacehrName;
	}

	public String getSex() {
		if (sex == null){
			sex = "男";
		}
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}


	public WisdomClassesJsonWithTeacher() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<CourseWare> getCourseware() {
		return courseware;
	}
	public void setCourseware(List<CourseWare> courseware) {
		this.courseware = courseware;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BlackboardWrite> getBlackboardWrite() {
		return blackboardWrite;
	}
	public void setBlackboardWrite(List<BlackboardWrite> blackboardWrite) {
		this.blackboardWrite = blackboardWrite;
	}
	public List<Question> getQuestions1() {
		return questions1;
	}
	public void setQuestions1(List<Question> questions1) {
		this.questions1 = questions1;
	}
	public List<Question> getQuestions2() {
		return questions2;
	}
	public void setQuestions2(List<Question> questions2) {
		this.questions2 = questions2;
	}
	public class CourseWare{
		private String attachmentId;

		public String getAttachmentId() {
			return attachmentId;
		}

		public void setAttachmentId(String attachmentId) {
			this.attachmentId = attachmentId;
		}

		private String url;
		private String name;
		public CourseWare() {
			super();
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
