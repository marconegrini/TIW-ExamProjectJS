package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.IllegalFormatException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.Course;
import it.polimi.tiw.projects.beans.Professor;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.dao.CourseDAO;
import it.polimi.tiw.projects.dao.ProfessorDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class GetProfessorCourseDetails
 */
@WebServlet("/GetStudentExams")
@MultipartConfig
public class GetStudentExams extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetStudentExams() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Student student = (Student) request.getSession().getAttribute("student");
		String chosenCourse = request.getParameter("courseid");
		Integer courseId = null;
		CourseDAO cDao = new CourseDAO(connection);
		List<Appello> appelli = null;
		
		if (chosenCourse == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("CourseId must not be null");
			return;
		} else {
			try {
				courseId = Integer.parseInt(chosenCourse);
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("CourseId must be an integer");
				return;
			}
			if (courseId != null) {
				try {
					appelli = cDao.findStudentAppelli(courseId.toString(), student.getId().toString());
				} catch (SQLException e) {
					// throw new ServletException(e);
					// e.printStackTrace();
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Failure in exam sessions database extraction");
					return;
				}
				if(appelli != null) {
					Gson gson = new Gson();
					String json = gson.toJson(appelli);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(json);
				}
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

}
