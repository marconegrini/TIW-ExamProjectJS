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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Verbalizza() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Integer appelloId = null;
		Date appelloDate = null;
		String courseName = null;
		
		try {
			appelloId = Integer.parseInt(request.getParameter("appelloId"));
			appelloDate = Date.valueOf(request.getParameter("appelloDate"));
			courseName = request.getParameter("courseName");
		} catch (IllegalArgumentException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter value");
			return;
		}
		
		ReportDAO reportDao = new ReportDAO(connection);
		boolean verbalize = false;
		
		try {
			//checks wether or not there are Published exams 
			verbalize = reportDao.checkReportAvailability();
		} catch(SQLException sqle) {
			sqle.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure while checking report availability");
			return;
		}		
		if(!verbalize) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No exams to verbalize");
			return;
		}
		
		ExamDAO examDao = new ExamDAO(connection);
		
		try {
			examDao.verbalizza(appelloId);
		} catch(SQLException sqle) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure while verbalizing grade");
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
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure while creating report");
			return;
		}
		
		try {
			reportDao.insertExamsIntoReport(dateTime);
		} catch(SQLException sqle) {
			sqle.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure while inserting exams into report");
			return;
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToReport?appelloId=" + appelloId + "&lastReportIndex=" + lastReportIndex + "&appelloDate=" + appelloDate.toString() + "&courseName=" + courseName;
		response.sendRedirect(path);
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
