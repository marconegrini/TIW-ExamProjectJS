package it.polimi.tiw.projects.beans;

import java.util.List;

public class Report {

	private Integer reportId;
	private String dateTime;
	private Integer appelloId;
	private List<StudentReport> students;
	
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public void setAppelloId(Integer appelloId) {
		this.appelloId = appelloId;
	}
	
	public void setStudents(List<StudentReport> students) {
		this.students = students;
	}
	
	public Integer getReportId() {
		return reportId;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	public Integer getAppelloId() {
		return appelloId;
	}
	
	public List<StudentReport> getStudents(){
		return students;
	}
}
