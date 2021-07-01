package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class Pubblica
 */
@WebServlet("/Pubblica")
public class Pubblica extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Pubblica() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());	
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ExamDAO examDao = new ExamDAO(connection);
		Integer appelloId = null;
		boolean badRequest = false;
		boolean sqlEx = false;
		
		try {
			appelloId = Integer.parseInt(request.getParameter("appelloId"));
		} catch (IllegalArgumentException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameter value");
			badRequest = true;
			return;
		}
		
		if(!badRequest) {
			try {
				examDao.pubblica(appelloId);
			} catch(SQLException sqle) {
				sqlEx = true;
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Database failure while updating grade");
				return;
			}
			if(!sqlEx) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println("Grades published");
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
