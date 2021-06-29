package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
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

import it.polimi.tiw.projects.dao.CourseDAO;
import it.polimi.tiw.projects.enumerations.Order;
import it.polimi.tiw.projects.utils.ConnectionHandler;
import it.polimi.tiw.projects.beans.*;
/**
 * Servlet implementation class GotToRegisteredStudents
 */
@WebServlet("/GetRegisteredStudents")
@MultipartConfig
public class GetRegisteredStudents extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetRegisteredStudents() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Integer appelloId = null;
		try {
			appelloId = Integer.parseInt(request.getParameter("appelloid"));
		} catch (IllegalArgumentException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect appello date value");
			return;
		}
		CourseDAO courseDao = new CourseDAO(connection);
		List<Exam> registeredStudents = null;
		try {
			registeredStudents = courseDao.findRegisteredStudentsJS(appelloId);	
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in registered students database extraction");
		}
		if(registeredStudents == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		} else {
			Gson gson = new Gson();
			String json = gson.toJson(registeredStudents);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
