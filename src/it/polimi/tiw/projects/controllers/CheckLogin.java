package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;

import it.polimi.tiw.projects.beans.Professor;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ProfessorDAO;
import it.polimi.tiw.projects.dao.StudentDAO;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.enumerations.Role;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CheckLogin")
@MultipartConfig
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public CheckLogin() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {	
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
		
		if (usrn == null || pwd == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		
		UserDAO user = new UserDAO(connection);
		User u = null;
		try {
			u = user.checkCredentials(usrn, pwd);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
 		}
		
		String path;	
		
		if (u == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Incorrect credentials");
			
		} else {
			
			Gson gson = new Gson();
			Student stud = null;
			Professor prof = null;
			String json = null;
			
			if(u.getRole().equals("STUDENT")) {
				
				StudentDAO student = new StudentDAO(connection);
				try {
					stud = student.checkStudent(u.getUserId().toString(), u.getUsername(), u.getPassword());
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Internal server error, retry later");
		 		}
				if (stud != null) {
					json = gson.toJson(stud);
				} 
				
			} else {
				ProfessorDAO professor = new ProfessorDAO(connection);
				try {
					prof = professor.checkProfessor(u.getUserId().toString(), u.getUsername(), u.getPassword());
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Internal server error, retry later");
		 		}
				if (prof != null) {
					json = gson.toJson(prof);
				} 
			}
			
			if(json != null) {
				if(stud == null)
					request.getSession().setAttribute("professor", prof);
				else request.getSession().setAttribute("student", stud);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(json);
			}
				
		}
		
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
