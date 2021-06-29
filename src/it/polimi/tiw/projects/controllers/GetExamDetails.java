package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.util.List;

import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class ExamDetails
 */
@WebServlet("/GetExamDetails")
public class GetExamDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
	public GetExamDetails() {
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
		
		Exam exam = null;
		ExamDAO examDao = new ExamDAO(connection);
		try {
			exam = examDao.findExamById(request.getParameter("examId"));
		} catch (SQLException sqle) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in exam database extraction");
		}
		if(exam == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("CourseId must not be null");
		} else {
			Gson gson = new Gson();
			String json = gson.toJson(exam);
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
