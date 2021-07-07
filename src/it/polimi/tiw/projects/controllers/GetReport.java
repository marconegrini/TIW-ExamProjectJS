package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.IllegalFormatException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.projects.beans.OrderType;
import it.polimi.tiw.projects.beans.Report;
import it.polimi.tiw.projects.dao.ReportDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToReport
 */
@WebServlet("/GetReport")
@MultipartConfig
public class GetReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetReport() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());		
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Integer lastReportIndex = null;
		
		try {
			lastReportIndex = Integer.parseInt(request.getParameter("lastReportIndex"));
		} catch(IllegalArgumentException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameter value");
			return;
		}
		
		ReportDAO reportDao = new ReportDAO(connection);
		Report report = null;
		try {
			report = reportDao.getLastReport(lastReportIndex);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Database failure while getting last report created");
			return;
		}
		
		Gson gson = new Gson();
		String jsonMessage = gson.toJson(report);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonMessage);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
