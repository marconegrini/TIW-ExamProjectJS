package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Course;
import it.polimi.tiw.projects.beans.Student;

public class StudentDAO {
	
	private Connection con;

	public StudentDAO(Connection connection) {
		this.con = connection;
	}

	public Student checkStudent(String id, String usrn, String pwd) throws SQLException {
		String query =	"SELECT  studentId, name, surname, email, corsoDiLaurea FROM students WHERE studentId = ? AND studUser = ? AND studPass = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, id);
			pstatement.setString(2, usrn);
			pstatement.setString(3, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Student student = new Student();
					student.setId(result.getInt("studentId"));
					student.setName(result.getString("name"));
					student.setSurname(result.getString("surname"));
					student.setEmail(result.getString("email"));
					student.setCorsoDiLaurea(result.getString("corsoDiLaurea"));
					return student;
				}
			}
		}
	}
	
	public List<Course> findCourses(String studentId) throws SQLException {
		List<Course> courses = new ArrayList<Course>();
		String query = "SELECT C.courseId, C.code, C.name, C.professor FROM courses AS C JOIN exams AS E JOIN appelli AS A WHERE C.courseId = A.courseId AND A.appelloId = E.appelloId AND E.student = ? ORDER BY name ASC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, studentId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Course course = new Course();
					course.setCourseId(result.getInt("courseId"));
					course.setCode(result.getString("code"));
					course.setName(result.getString("name"));
					course.setProfessorId(result.getInt("professor"));
					boolean alreadyInserted = false;
					for(Course c : courses) {
						if(c.getCourseId() == course.getCourseId())
							alreadyInserted = true;
					}
					if(!alreadyInserted)
						courses.add(course);
				}
			}
		}
		return courses;
	}
	
	public Integer findDefaultCourse(String studentId) throws SQLException {
		String query = "SELECT C.courseId, C.code, C.name, C.professor FROM courses AS C JOIN exams AS E JOIN appelli AS A WHERE C.courseId = A.courseId AND A.appelloId = E.appelloId AND E.student = ? ORDER BY name ASC";
		Integer cid = 0;
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, studentId);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				cid = result.getInt("courseId");
			}
		}
		return cid;
	}
}
