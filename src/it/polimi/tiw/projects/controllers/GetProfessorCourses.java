package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import java.util.List;
import it.polimi.tiw.projects.beans.Professor;
import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.Course;
import it.polimi.tiw.projects.dao.CourseDAO;
import it.polimi.tiw.projects.dao.ProfessorDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetProfessorCourses")
@MultipartConfig
public class GetProfessorCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetProfessorCourses() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
		Professor professor = (Professor) request.getSession().getAttribute("professor");
		
		//String chosenCourse = request.getParameter("courseId");
		ProfessorDAO prof = new ProfessorDAO(connection);
		List<Course> courses = null;
		//List<Appello> appelli = null;
		//Integer chosenCourseId = 0;
		//String chosenCourseName = "";
		try {
			courses = prof.findCourses(professor.getId().toString());
		} catch (SQLException e) {
			// throw new ServletException(e);
			//e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Failure in professor's courses database extraction");
		}
		if(courses == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		} else {
			Gson gson = new Gson();
			String json = gson.toJson(courses);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
