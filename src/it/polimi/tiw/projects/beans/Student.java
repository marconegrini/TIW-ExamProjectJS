package it.polimi.tiw.projects.beans;

public class Student {
	
	private Integer studentId;
	private String name;
	private String surname;
	private String email;
	private String corsoDiLaurea;
	

	public Integer getId() {
		return studentId;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getCorsoDiLaurea() {
		return corsoDiLaurea;
	}
	
	public void setId(Integer i) {
		studentId = i;
	}

	public void setName(String n) {
		name = n;
	}

	public void setSurname(String s) {
		surname = s;
	}
	
	public void setEmail(String e) {
		email = e;
	}
		
	public void setCorsoDiLaurea(String cdl) {
		corsoDiLaurea = cdl;
	}

}
