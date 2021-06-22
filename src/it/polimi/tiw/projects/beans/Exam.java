package it.polimi.tiw.projects.beans;

import java.sql.Date;

import it.polimi.tiw.projects.enumerations.Status;

public class Exam {
	private int examId;
	private Student student;
	private Appello appello;
	private Status status;
	private String grade;
	
	public int getExamId() {
		return examId;
	}
	
	public String getName() {
		return student.getName();
	}

	public String getSurname() {
		return student.getSurname();
	}
	
	public Integer getStudentId() {
		return student.getId();
	}
	
	public String getEmail() {
		return student.getEmail();
	}
	
	public String getCorsoDiLaurea() {
		return student.getCorsoDiLaurea();
	}
	
	public Student getStudent() {
		return this.student;
	}
	
	public Integer getCourseId() {
		return this.appello.getCourseId();
	}
	
	public String getDate() {
		return this.appello.getDate().toString();
	}
	
	public Integer getAppelloId() {
		return this.appello.getAppelloId();
	}
	
	public String getStatus() {
		return this.status.toString();
	}
	
	public String getGrade() {
		return this.grade;
	}
	
	public void setExamId(int examId) {
		this.examId = examId;
	}
	
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public void setAppello(Appello appello) {
		this.appello = appello;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
}
