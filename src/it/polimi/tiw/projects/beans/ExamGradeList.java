package it.polimi.tiw.projects.beans;

import java.util.List;

public class ExamGradeList {
	
	private List<ExamGrade> examgradelist;
	
	public void setExamGradeList(List<ExamGrade> examgradelist) {
		this.examgradelist = examgradelist;
	}
	
	public List<ExamGrade> getExamGradeList(){
		return this.examgradelist;
	}

}
