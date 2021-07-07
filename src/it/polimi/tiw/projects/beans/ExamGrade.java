package it.polimi.tiw.projects.beans;

public class ExamGrade {
	private Integer key;
	private String value;
	
	public void setExamid(Integer key) {
		this.key = key;
	}
	
	public void setGrade(String value) {
		this.value = value;
	}
	
	public Integer getExamid() {
		return this.key;
	}
	
	public String getGrade() {
		return this.value;
	}	
}
