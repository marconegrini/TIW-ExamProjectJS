package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.text.SimpleDateFormat;

import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.enumerations.Status;

public class ExamDAO {

	private Connection con;

	public ExamDAO(Connection connection) {
		this.con = connection;
	}

	public Exam findExamById(String examId) throws SQLException {
		Exam exam = new Exam();
		String query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND E.examId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, examId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Student student = new Student();
					Appello appello = new Appello();

					student.setId(result.getInt("studentId"));
					student.setName(result.getString("name"));
					student.setSurname(result.getString("surname"));
					student.setEmail(result.getString("email"));
					student.setCorsoDiLaurea(result.getString("corsoDiLaurea"));

					appello.setAppelloId(result.getInt("appelloId"));
					appello.setCourseId(result.getInt("courseId"));
					appello.setDate(result.getDate("date"));

					exam.setExamId(result.getInt("examId"));
					exam.setStudentId(student.getId());
					exam.setStudentName(student.getName());
					exam.setStudentSurname(student.getSurname());
					exam.setStudentEmail(student.getEmail());
					exam.setCorsoDiLaurea(student.getCorsoDiLaurea());
					exam.setStudent(student);
					exam.setAppelloId(appello.getAppelloId());
					exam.setAppelloDate(appello.getDate());
					exam.setAppello(appello);
					exam.setStatus(Status.valueOf(result.getString("status")));
					exam.setGrade(result.getString("grade"));
				}
			}
		}

		return exam;
	}
	
	public Exam findStudentExam(Integer appelloid, Integer studentid) throws SQLException {
		Exam exam = new Exam();
		String query = "SELECT S.studentId, S.name, S.surname, S.email, S.corsoDiLaurea, A.courseId, A.appelloId, A.date, E.examId, E.status, E.grade FROM exams AS E, students AS S, appelli AS A  WHERE E.student = S.studentId AND E.appelloId = A.appelloId AND S.studentId = ? AND A.appelloId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, studentid);
			pstatement.setInt(2, appelloid);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Student student = new Student();
					Appello appello = new Appello();

					student.setId(result.getInt("studentId"));
					student.setName(result.getString("name"));
					student.setSurname(result.getString("surname"));
					student.setEmail(result.getString("email"));
					student.setCorsoDiLaurea(result.getString("corsoDiLaurea"));

					appello.setAppelloId(result.getInt("appelloId"));
					appello.setCourseId(result.getInt("courseId"));
					appello.setDate(result.getDate("date"));

					exam.setExamId(result.getInt("examId"));
					exam.setStudentId(student.getId());
					exam.setStudentName(student.getName());
					exam.setStudentSurname(student.getSurname());
					exam.setStudentEmail(student.getEmail());
					exam.setCorsoDiLaurea(student.getCorsoDiLaurea());
					exam.setStudent(student);
					exam.setAppelloId(appello.getAppelloId());
					exam.setAppelloDate(appello.getDate());
					exam.setAppello(appello);
					exam.setStatus(Status.valueOf(result.getString("status")));
					exam.setGrade(result.getString("grade"));
				}
			}
		}

		return exam;
	}
	
	public boolean checkGradeInsertion(Integer examid) throws SQLException {
		Integer examNumber = 0;
		String query = "SELECT COUNT(*) FROM exams WHERE examid = ? AND (status = 'NONINSERITO' OR status = 'INSERITO')";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, examid);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				examNumber = result.getInt("COUNT(*)");
			}
		}
		if(examNumber == 1)
			return true;
		else if(examNumber == 0)
			return false;
		else throw new SQLException();
	}

	public void insertGrade(String grade, Integer examId) throws SQLException {
		String query = "UPDATE exams SET grade = ?, status = 'INSERITO' WHERE examId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, grade);
			pstatement.setInt(2, examId);
			pstatement.executeUpdate();
		}
	}

	public boolean checkPublishAvailability(Integer appelloId) throws SQLException {
		Integer examNumber = null;
		String query = "SELECT COUNT(*) FROM exams WHERE status = 'INSERITO' AND appelloId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, appelloId);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				examNumber = result.getInt("COUNT(*)");
			}
		}
		System.out.println("Exams to publish: " + examNumber);
		if (examNumber > 0)
			return true;
		return false;
	}

	public void pubblica(Integer appelloId) throws SQLException {
		String query = "UPDATE exams SET status = 'PUBBLICATO' WHERE appelloId = ? AND status = 'INSERITO'";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, appelloId);
			pstatement.executeUpdate();
		}
	}

	public void verbalizza(Integer appelloId) throws SQLException {
		String query = "UPDATE exams SET status = 'VERBALIZZATO' WHERE appelloId = ? AND (status = 'PUBBLICATO' OR STATUS = 'RIFIUTATO')";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, appelloId);
			pstatement.executeUpdate();
		}
	}	
	
	public void rifiuta(Integer examid) throws SQLException{
		String query = "UPDATE exams SET status = 'RIFIUTATO' WHERE examId = ? AND status = 'PUBBLICATO'";
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setInt(1, examid);
			pstatement.executeUpdate();
		}
	}

}
