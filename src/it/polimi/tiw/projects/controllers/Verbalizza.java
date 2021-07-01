package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.IllegalFormatException;
import java.sql.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.Report;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.dao.ReportDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class Pubblica
 */
@WebServlet("/Verbalizza")
public class Verbalizza extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Verbalizza() {
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

		Integer appelloId = null;
		
		try {
			appelloId = Integer.parseInt(request.getParameter("appelloId"));
		} catch (IllegalArgumentException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameter value");
			return;
		}
		
		ReportDAO reportDao = new ReportDAO(connection);
		boolean verbalize = false;
		
		try {
			//checks wether or not there are Published exams 
			verbalize = reportDao.checkReportAvailability();
		} catch(SQLException sqle) {
			sqle.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Database failure while checking report availability");
			return;
		}		
		if(!verbalize) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("No exams to verbalize");
			return;
		}
		
		ExamDAO examDao = new ExamDAO(connection);
		
		try {
			examDao.verbalizza(appelloId);
		} catch(SQLException sqle) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Database failure while verbalizing grade");
			return;
		}
		
		String dateTime = null;
		Integer lastReportIndex = null;
		try {
			dateTime = reportDao.createReport(appelloId);
			lastReportIndex = reportDao.getLastReportIndex();
			if(lastReportIndex == null) throw new SQLException();
		} catch(SQLException sqle) {
			sqle.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Database failure while creating report");
			return;
		}
		
		try {
			reportDao.insertExamsIntoReport(dateTime);
		} catch(SQLException sqle) {
			sqle.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Database failure while inserting exams into report");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("Exams verbalized");
		
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
