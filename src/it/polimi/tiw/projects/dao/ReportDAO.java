package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import it.polimi.tiw.projects.beans.Report;
import it.polimi.tiw.projects.beans.StudentReport;

public class ReportDAO {
	
	private Connection con;

	public ReportDAO(Connection connection) {
		this.con = connection;
	}
	
	public boolean checkReportAvailability() throws SQLException {
		Integer examId = null;
		String query = "SELECT COUNT(*) FROM exams WHERE status = 'PUBBLICATO'";
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			try(ResultSet result = pstatement.executeQuery();){
				result.next();
				examId = result.getInt("COUNT(*)");	
			}
		}
		System.out.println(examId);
		if(examId > 0) 
			return true;
		return false;
	}
	
	
	public String createReport(Integer appelloId) throws SQLException{
		Date datetime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(datetime);
		
		String query = "INSERT INTO report(datetime, appello) VALUES (?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setString(1, currentTime);
			pstatement.setInt(2, appelloId);
			pstatement.executeUpdate();
		}
		
		return currentTime;
	}
	
	public void insertExamsIntoReport(String datetime) throws SQLException{
		String query = "INSERT INTO examreport(reportId, examId) SELECT R.reportId, E.examId FROM report AS R, exams AS E WHERE R.datetime = ? AND E.status = 'VERBALIZZATO' AND E.examId NOT IN (SELECT examId FROM examreport)";
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setString(1, datetime);
			pstatement.executeUpdate();
		}
	}
	
	public Report getLastReport(Integer reportId) throws SQLException{
		Report report = new Report();
		List<StudentReport> students = new ArrayList<StudentReport>();
		
		String query = "SELECT R.reportId, R.datetime, R.appello, E.grade, S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea FROM report AS R, examreport AS ER, exams AS E, students AS S WHERE R.reportId = ER.reportId AND R.reportId = ? AND ER.examId = E.examId AND E.student = S.studentId";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, reportId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					StudentReport studentReport = new StudentReport();
					
					report.setReportId(result.getInt("reportId"));
					report.setDateTime(result.getString("datetime"));
					report.setAppelloId(result.getInt("appello"));
					
					studentReport.setStudentId(result.getInt("studentId"));
					studentReport.setName(result.getString("name"));
					studentReport.setSurname(result.getString("surname"));
					studentReport.setGrade(result.getString("grade"));
					
					students.add(studentReport);
				}
			}
		}
		
		report.setStudents(students);
		return report;
	}
	
	public Integer getLastReportIndex() throws SQLException {
		Integer reportId = null;
		String query = "SELECT MAX(reportId) FROM report";
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			try(ResultSet result = pstatement.executeQuery();){
				result.next();
				reportId = result.getInt("MAX(reportId)");
			}
		}
		return reportId;
	}

}
