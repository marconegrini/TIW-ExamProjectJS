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
@WebServlet("/UpdateGrade")
public class UpdateGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateGrade() {
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
		
		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();
		String line = null;
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		
		String json = sb.toString();
		System.out.println(json);
		System.out.println(json);
		JsonStreamParser parser = new JsonStreamParser(json);
		ExamDAO examDao = new ExamDAO(connection);
		boolean allInserted = true;
		try {
			while(parser.hasNext()) {
				JsonElement jsonElement = parser.next();
				if(jsonElement.isJsonArray()) {
					JsonArray jsonList = jsonElement.getAsJsonArray();
					System.out.println(jsonList);
					for(JsonElement elem : jsonList) {
						JsonObject jsonObject = elem.getAsJsonObject();
						Integer examid = jsonObject.get("key").getAsInt();
						System.out.println("Exam id: " + examid);
						String grade = jsonObject.get("value").getAsString();
						System.out.println("Grade: " + grade);
						try {
							if(examDao.checkGradeInsertion(examid)) 
								examDao.insertGrade(grade, examid);
							else allInserted = false;
						} catch (SQLException sqle) {
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							response.getWriter().println("Cannot update grades due to a database failure");
							return;
						}	
					}
				}
			}
		} catch (IllegalStateException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not a json object");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		if(allInserted) 
			response.getWriter().println("Grades updated correctly");
		else response.getWriter().println("Not all grades correctly updated. Check exams status and try again.");
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
