package com.ccwchina.calendar;

import java.util.Date;
import java.util.List;

public class CourseCalendar {
	private String courseCalendarId;
	private Date classDate;
	private Integer classTimeId;
	private String classTimeName;
	private Integer pricePerPerson;
	private Integer seatLeft;
	private Integer courseLocationId;
	private String courseLocationName;
	private Integer courseTrunkTypeId;
	private String courseTrunkTypeName;
	private String fontColor;
	private String backgroundColor;
	private Integer courseBranchTypeId;
	private String courseBranchTypeName;
	private List<Course> courseList;
	
	public String getCourseCalendarId() {
		return courseCalendarId;
	}
	public void setCourseCalendarId(String courseCalendarId) {
		this.courseCalendarId = courseCalendarId;
	}
	public Date getClassDate() {
		return classDate;
	}
	public void setClassDate(Date classDate) {
		this.classDate = classDate;
	}
	public Integer getClassTimeId() {
		return classTimeId;
	}
	public void setClassTimeId(Integer classTimeId) {
		this.classTimeId = classTimeId;
	}
	public String getClassTimeName() {
		return classTimeName;
	}
	public void setClassTimeName(String classTimeName) {
		this.classTimeName = classTimeName;
	}
	public Integer getPricePerPerson() {
		return pricePerPerson;
	}
	public void setPricePerPerson(Integer pricePerPerson) {
		this.pricePerPerson = pricePerPerson;
	}
	public Integer getSeatLeft() {
		return seatLeft;
	}
	public void setSeatLeft(Integer seatLeft) {
		this.seatLeft = seatLeft;
	}
	public Integer getCourseLocationId() {
		return courseLocationId;
	}
	public void setCourseLocationId(Integer courseLocationId) {
		this.courseLocationId = courseLocationId;
	}
	public String getCourseLocationName() {
		return courseLocationName;
	}
	public void setCourseLocationName(String courseLocationName) {
		this.courseLocationName = courseLocationName;
	}
	public Integer getCourseTrunkTypeId() {
		return courseTrunkTypeId;
	}
	public void setCourseTrunkTypeId(Integer courseTrunkTypeId) {
		this.courseTrunkTypeId = courseTrunkTypeId;
	}
	public String getCourseTrunkTypeName() {
		return courseTrunkTypeName;
	}
	public void setCourseTrunkTypeName(String courseTrunkTypeName) {
		this.courseTrunkTypeName = courseTrunkTypeName;
	}
	public String getFontColor() {
		return fontColor;
	}
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public Integer getCourseBranchTypeId() {
		return courseBranchTypeId;
	}
	public void setCourseBranchTypeId(Integer courseBranchTypeId) {
		this.courseBranchTypeId = courseBranchTypeId;
	}
	public String getCourseBranchTypeName() {
		return courseBranchTypeName;
	}
	public void setCourseBranchTypeName(String courseBranchTypeName) {
		this.courseBranchTypeName = courseBranchTypeName;
	}
	public List<Course> getCourseList() {
		return courseList;
	}
	public void setCourseList(List<Course> courseList) {
		this.courseList = courseList;
	}
	
}
