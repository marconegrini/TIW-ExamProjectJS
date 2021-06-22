package it.polimi.tiw.projects.beans;

import java.sql.Date;

public class Appello {
	private Integer appelloId;
	private int courseId;
	private Date date;
	
	public Integer getAppelloId() {
		return appelloId;
	}
	
	public int getCourseId() {
		return courseId;
	}
	
	public String getDate() {
		return date.toString();
	}
	
	public void setAppelloId(Integer appelloId) {
		this.appelloId = appelloId;
	}
	
	public void setCourseId(Integer id) {
		courseId = id;
	}
	
	public void setDate(Date d) {
		date = d;
	}
	
}
