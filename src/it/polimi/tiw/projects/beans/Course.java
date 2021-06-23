	package it.polimi.tiw.projects.beans;

public class Course {
	
	private int courseId;
	private String code;
	private String name;
	private int professorId;
	

	public int getCourseId() {
		return courseId;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}

	public int getProfessorId() {
		return professorId;
	}

	public void setCourseId(Integer i) {
		courseId = i;
	}
	
	public void setCode(String c) {
		code = c;
	}

	public void setName(String n) {
		name = n;
	}
	
	public void setProfessorId(Integer n) {
		professorId = n;
	}

}
