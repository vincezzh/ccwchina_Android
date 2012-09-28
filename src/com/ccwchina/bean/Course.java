package com.ccwchina.bean;

import java.io.Serializable;

public class Course implements Serializable {
	private static final long serialVersionUID = 3348367329867501910L;
	private String courseId;
	private String courseNameEn;
	private String pictureOne;
	
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseNameEn() {
		return courseNameEn;
	}
	public void setCourseNameEn(String courseNameEn) {
		this.courseNameEn = courseNameEn;
	}
	public String getPictureOne() {
		return pictureOne;
	}
	public void setPictureOne(String pictureOne) {
		this.pictureOne = pictureOne;
	}
	
}
