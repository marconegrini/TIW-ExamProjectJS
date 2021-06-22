package it.polimi.tiw.projects.beans;

import it.polimi.tiw.projects.enumerations.Role;

public class User {
	
	private int userId;
	private String username;
	private String password;
	private Role role;

	public Integer getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role.toString();
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setUserId(Integer id) {
		userId = id;
	}

	public void setUsername(String u) {
		username = u;
	}
	
	public void setPassword(String p) {
		password = p;
	}

	public void setRole(String r) {
		role = Enum.valueOf(Role.class, r.toUpperCase());
	}

}
