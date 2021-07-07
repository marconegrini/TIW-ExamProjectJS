package it.polimi.tiw.projects.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import it.polimi.tiw.projects.beans.Exam;
import it.polimi.tiw.projects.beans.ExamGrade;
import it.polimi.tiw.projects.beans.ExamGradeList;
import it.polimi.tiw.projects.beans.OrderType;
import it.polimi.tiw.projects.dao.ExamDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

/**
 * Servlet implementation class UpdateGrade
 */
@WebServlet("/UpdateSingleGrade")
public class UpdateSingleGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateSingleGrade() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Integer examid = null;
		try {
			examid = Integer.parseInt(request.getParameter("examid"));
			System.out.println(examid);
		} catch(IllegalArgumentException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid exam id input parameter");
			return;
		}
		
		String grade = null;
		try {
			grade = request.getParameter("grade");
		} catch(IllegalArgumentException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid grade input parameter");
			return;
		}
		
		ExamDAO examDao = new ExamDAO(connection);
		try {
			if(examDao.checkGradeInsertion(examid)) {
				examDao.insertGrade(grade, examid);
			}
		} catch (SQLException sqle) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Cannot update grade due to a database failure");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("Grade updated correctly");

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
