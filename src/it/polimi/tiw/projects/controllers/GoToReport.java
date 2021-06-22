package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.IllegalFormatException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.projects.beans.OrderType;
import it.polimi.tiw.projects.beans.Report;
import it.polimi.tiw.projects.dao.ReportDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToReport
 */
@WebServlet("/GoToReport")
public class GoToReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToReport() {
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

		Integer lastReportIndex = null;
		Integer appelloId = null;
		Date appelloDate = null;
		String courseName = null;
		
		try {
			lastReportIndex = Integer.parseInt(request.getParameter("lastReportIndex"));
			appelloId = Integer.parseInt(request.getParameter("appelloId"));
			appelloDate = Date.valueOf(request.getParameter("appelloDate"));
			courseName = request.getParameter("courseName");
		} catch(IllegalArgumentException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter value");
			return;
		}
		
		ReportDAO reportDao = new ReportDAO(connection);
		Report report = null;
		try {
			report = reportDao.getLastReport(lastReportIndex);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure while getting last report created");
			return;
		}
		
		String path = "/WEB-INF/Report.html";
		ServletContext servletContext = getServletContext();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
