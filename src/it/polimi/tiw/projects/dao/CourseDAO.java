package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.enumerations.Status;

public class CourseDAO {
	
	private Connection con;

	public CourseDAO(Connection connection) {
		this.con = connection;
	}

	//finds appelli related to the specified course
	public List<Appello> findAppelli(String courseId) throws SQLException {
		List<Appello> appelli = new ArrayList<Appello>();
		String query = "SELECT appelloId, courseId, date FROM appelli WHERE courseId = ? ORDER BY date DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, courseId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Appello appello = new Appello();
					appello.setCourseId(result.getInt("courseId"));
					appello.setDate(result.getDate("date"));
					appello.setAppelloId(result.getInt("appelloId"));
					appelli.add(appello);
				}
			}
		}
		return appelli;
	}
	
	public List<Appello> findStudentAppelli(String courseId, String studentId) throws SQLException {
		List<Appello> appelli = new ArrayList<Appello>();
		String query = "SELECT A.appelloId, A.courseId, A.date FROM appelli AS A JOIN exams AS E WHERE A.courseId = ? AND E.appelloId = A.appelloId AND E.student = ? ORDER BY date DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, courseId);
			pstatement.setString(2, studentId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Appello appello = new Appello();
					appello.setCourseId(result.getInt("courseId"));
					appello.setDate(result.getDate("date"));
					appello.setAppelloId(result.getInt("appelloId"));
					appelli.add(appello);
				}
			}
		}
		return appelli;
	}
	
	public List<Exam> findRegisteredStudents(Integer appelloId, String sortBy, String order) throws SQLException{
		List<Exam> registeredStudents = new ArrayList<Exam>();
		String query = null;
		if(order.equals("ASC")) {
			switch(sortBy) {
			case "studentId":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY studentId ASC";
				break;
			case "surname":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY surname ASC";
				break;
			case "name":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY name ASC";
				break;
			case "email":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY email ASC";
				break;
			case "corsoDiLaurea":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY corsoDiLaurea ASC";
				break;
			case "grade":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY grade ASC";
				break;
			case "status":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY status ASC";
				break;
			}
		} else {
			switch(sortBy) {
			case "studentId":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY studentId DESC";
				break;
			case "surname":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY surname DESC";
				break;
			case "name":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY name DESC";
				break;
			case "email":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY email DESC";
				break;
			case "corsoDiLaurea":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY corsoDiLaurea DESC";
				break;
			case "grade":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY grade DESC";
				break;
			case "status":
				query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ? ORDER BY status DESC";
				break;
			}
		}
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, appelloId.toString());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Student student = new Student();
					Appello appello = new Appello();
					Exam exam = new Exam();
					
					student.setId(result.getInt("studentId"));
					student.setName(result.getString("name"));
					student.setSurname(result.getString("surname"));
					student.setEmail(result.getString("email"));
					student.setCorsoDiLaurea(result.getString("corsoDiLaurea"));
					
					appello.setAppelloId(result.getInt("appelloId"));
					appello.setCourseId(result.getInt("courseId"));
					appello.setDate(result.getDate("date"));
					
					exam.setExamId(result.getInt("examId"));
					exam.setAppello(appello);
					exam.setStatus(Status.valueOf(result.getString("status")));
					exam.setGrade(result.getString("grade"));
					exam.setStudent(student);

					registeredStudents.add(exam);
				}
			}
		} 
		
		return registeredStudents;
	}
	
	public List<Exam> findRegisteredStudentsJS(Integer appelloId) throws SQLException{
		List<Exam> registeredStudents = new ArrayList<Exam>();
		String query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND A.appelloId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, appelloId.toString());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Student student = new Student();
					Appello appello = new Appello();
					Exam exam = new Exam();
					
					student.setId(result.getInt("studentId"));
					student.setName(result.getString("name"));
					student.setSurname(result.getString("surname"));
					student.setEmail(result.getString("email"));
					student.setCorsoDiLaurea(result.getString("corsoDiLaurea"));
					
					appello.setAppelloId(result.getInt("appelloId"));
					appello.setCourseId(result.getInt("courseId"));
					appello.setDate(result.getDate("date"));
					
					exam.setExamId(result.getInt("examId"));
					
					exam.setStudent(student);
					exam.setStudentId(student.getId());
					exam.setStudentName(student.getName());
					exam.setStudentSurname(student.getSurname());
					exam.setStudentEmail(student.getEmail());
					exam.setCorsoDiLaurea(student.getCorsoDiLaurea());
					
					exam.setAppello(appello);
					exam.setAppelloDate(appello.getDate());
					exam.setAppelloId(appello.getAppelloId());
					exam.setStatus(Status.valueOf(result.getString("status")));
					exam.setGrade(result.getString("grade"));

					registeredStudents.add(exam);
				}
			}
		} 
		return registeredStudents;
	 }
}
