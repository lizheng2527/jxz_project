package com.zdhx.androidbase.ui.ykt;

import java.util.List;

/**
 * 教师任课的班级课程
 * @author lenovo
 *
 */
public class TeacherEclassCourses {
	private String id;
	private String name;
	private List<Course> courses;
	public TeacherEclassCourses() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Course> getCourses() {
		return courses;
	}
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
}
