package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;

import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class RifiutaEsame
 */
@WebServlet("/RifiutaEsame")
@MultipartConfig
public class RifiutaEsame extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public RifiutaEsame() {
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
		// TODO Auto-generated method stub
		Integer examid = null;
		
		try {
			examid = Integer.parseInt(request.getParameter("examid"));
		} catch (IllegalArgumentException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameter value");
			return;
		}
		System.out.println(examid);
		ExamDAO examDao = new ExamDAO(connection);
		try {
			examDao.rifiuta(examid);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Database failure while rejecting grade");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("Grade rejected");
	}

}
