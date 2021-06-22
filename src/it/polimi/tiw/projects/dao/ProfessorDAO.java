package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Course;
import it.polimi.tiw.projects.beans.Professor;
import it.polimi.tiw.projects.beans.Student;

public class ProfessorDAO {
	
	private Connection con;

	public ProfessorDAO(Connection connection) {
		this.con = connection;
	}

	public Professor checkProfessor(String id, String usrn, String pwd) throws SQLException {
		String query =	"SELECT  professorId, name, surname FROM professors WHERE professorId = ? AND profUser = ? AND profPass = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, id);
			pstatement.setString(2, usrn);
			pstatement.setString(3, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Professor professor = new Professor();
					professor.setId(result.getInt("professorId"));
					professor.setName(result.getString("name"));
					professor.setSurname(result.getString("surname"));
					return professor;
				}
			}
		}
	}
	
	public List<Course> findCourses(String professorId) throws SQLException {
		List<Course> courses = new ArrayList<Course>();
		String query = "SELECT courseId, code, name, professor FROM courses WHERE professor = ? ORDER BY name ASC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, professorId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Course course = new Course();
					course.setCourseId(result.getInt("courseId"));
					course.setCode(result.getString("code"));
					course.setName(result.getString("name"));
					course.setProfessorId(result.getInt("professor"));
					courses.add(course);
				}
			}
		}
		return courses;
	}
	
	public Integer findDefaultCourse(String professorId) throws SQLException {
		String query = "SELECT courseId, code, name, professor FROM courses WHERE professor = ? ORDER BY name ASC LIMIT 1";
		Integer cid = 0;
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, professorId);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				cid = result.getInt("courseId");
			}
		}
		return cid;
	}
}
