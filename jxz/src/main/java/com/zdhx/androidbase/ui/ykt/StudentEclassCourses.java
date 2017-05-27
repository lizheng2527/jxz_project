package com.zdhx.androidbase.ui.ykt;

import java.util.List;

/**
 * 教师任课的班级课程
 * @author lenovo
 *
 */
public class StudentEclassCourses {
	private Eclass eclass;
	private List<Course> courses;
	public StudentEclassCourses() {
		super();
	}
	public Eclass getEclass() {
		return eclass;
	}
	public void setEclass(Eclass eclass) {
		this.eclass = eclass;
	}
	public List<Course> getCourses() {
		return courses;
	}
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
}
