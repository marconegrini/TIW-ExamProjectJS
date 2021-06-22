package it.polimi.tiw.projects.beans;

public class StudentReport {
	private String name;
	private String surname;
	private Integer studentId;
	private String grade;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}
	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public Integer getStudentId() {
		return studentId;
	}
	
	public String getGrade() {
		return grade;
	}

}
