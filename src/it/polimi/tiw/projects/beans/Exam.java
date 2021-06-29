package it.polimi.tiw.projects.beans;

import it.polimi.tiw.projects.enumerations.Status;

public class Exam {
	private int examId;
	private Student student;
	private int studentId;
	private String studentName;
	private String studentSurname;
	private String studentEmail;
	private String corsoDiLaurea;
	private Appello appello;
	private int appelloId;
	private String appelloDate;
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

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	
	public void setStudentSurname(String studentSurname) {
		this.studentSurname = studentSurname;
	}
	
	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}
	
	public void setCorsoDiLaurea(String corsoDiLaurea) {
		this.corsoDiLaurea = corsoDiLaurea;
	}
	
	public void setAppello(Appello appello) {
		this.appello = appello;
	}
	
	public void setAppelloId(int appelloId) {
		this.appelloId = appelloId;
	}
	
	public void setAppelloDate(String appelloDate) {
		this.appelloDate = appelloDate;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
}
