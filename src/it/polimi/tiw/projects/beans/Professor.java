package it.polimi.tiw.projects.beans;

public class Professor {
	
	private String role = "professor";
	private int professorId;
	private String name;
	private String surname;

	public Integer getId() {
		return professorId;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}
	
	public String getRole() {
		return this.role;
	}
	
	public void setId(Integer i) {
		professorId = i;
	}

	public void setName(String n) {
		name = n;
	}

	public void setSurname(String s) {
		surname = s;
	}
	
}
