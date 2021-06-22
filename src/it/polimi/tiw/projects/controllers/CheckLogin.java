package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.Professor;
import it.polimi.tiw.projects.beans.Student;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ProfessorDAO;
import it.polimi.tiw.projects.dao.StudentDAO;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.enumerations.Role;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	//motore di interpretazione del template
	private TemplateEngine templateEngine;


	public CheckLogin() {
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		
		UserDAO user = new UserDAO(connection);
		User u = null;
		try {
			u = user.checkCredentials(usrn, pwd);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			throw new ServletException(e); 
 		}
		
		String path;	
		
		if (u == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			//setting the error message variable if user doesn't exists
			ctx.setVariable("errorMsg", "Incorrect username or password");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());	
		} else {
			if(u.getRole().equals("STUDENT")) {
				StudentDAO student = new StudentDAO(connection);
				Student stud = null;
				try {
					stud = student.checkStudent(u.getUserId().toString(), u.getUsername(), u.getPassword());
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
					throw new ServletException(e); 
		 		}
				if (stud == null) {
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					//setting the error message variable if user doesn't exists
					ctx.setVariable("errorMsg", "Incorrect username or password");
					path = "/index.html";
					templateEngine.process(path, ctx, response.getWriter());
				} else {
					request.getSession().setAttribute("student", stud);
				}
			} else {
				ProfessorDAO professor = new ProfessorDAO(connection);
				Professor prof = null;
				try {
					prof = professor.checkProfessor(u.getUserId().toString(), u.getUsername(), u.getPassword());
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
					throw new ServletException(e); 
		 		}
				if (prof == null) {
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					//setting the error message variable if user doesn't exists
					ctx.setVariable("errorMsg", "Incorrect username or password");
					path = "/index.html";
					templateEngine.process(path, ctx, response.getWriter());
				} else {
					request.getSession().setAttribute("professor", prof);
				}
			}
			
			String target = (u.getRole().equals("STUDENT")) ? "/GoToHomeStudent" : "/GoToHomeProfessor";
			path = getServletContext().getContextPath() + target;
			response.sendRedirect(path);
		}
		
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
