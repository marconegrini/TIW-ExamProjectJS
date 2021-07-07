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

import com.google.gson.Gson;

import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetExamResult")
@MultipartConfig
public class GetExamResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetExamResult() {
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
		Integer appelloid = null;
		try {
			appelloid = Integer.parseInt(request.getParameter("appelloid"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		
		Student student = (Student) request.getSession().getAttribute("student");
		Integer studentid = student.getId();
		
		ExamDAO examDao = new ExamDAO(connection);
		Exam exam = null;
		try {
			exam = examDao.findStudentExam(appelloid, studentid);
		} catch(SQLException sqle) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Database failure while retrieving student exam from database");
		}
		
		Gson gson = new Gson();
		String jsonExam = gson.toJson(exam);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonExam);
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
