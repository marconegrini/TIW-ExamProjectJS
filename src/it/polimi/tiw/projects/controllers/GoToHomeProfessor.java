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
import javax.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.List;
import it.polimi.tiw.projects.beans.Professor;
import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.Course;
import it.polimi.tiw.projects.dao.CourseDAO;
import it.polimi.tiw.projects.dao.ProfessorDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GoToHomeProfessor")
public class GoToHomeProfessor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToHomeProfessor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
		Professor professor = (Professor) request.getSession().getAttribute("professor");
		
		String chosenCourse = request.getParameter("courseId");
		ProfessorDAO prof = new ProfessorDAO(connection);
		List<Course> courses = null;
		List<Appello> appelli = null;
		Integer chosenCourseId = 0;
		String chosenCourseName = "";
		try {
			courses = prof.findCourses(professor.getId().toString());
			if (chosenCourse == null) {
				chosenCourseId = prof.findDefaultCourse(professor.getId().toString());
			} else {
				chosenCourseId = Integer.parseInt(chosenCourse);
			}
			CourseDAO cDao = new CourseDAO(connection);
			appelli = cDao.findAppelli(chosenCourseId.toString());
			for(Course c : courses) 
				if(c.getCourseId() == chosenCourseId)
					chosenCourseName = c.getName();
		} catch (SQLException e) {
			// throw new ServletException(e);
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in professor's courses database extraction");
		}
		
		String path = "/WEB-INF/HomeProfessor.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("courses", courses);
		ctx.setVariable("chosenCourseId", chosenCourseId);
		ctx.setVariable("chosenCourseName", chosenCourseName);
		ctx.setVariable("appelli", appelli);

		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
