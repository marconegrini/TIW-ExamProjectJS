package it.polimi.tiw.projects.filters;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StudentChecker implements Filter{
	
	public StudentChecker() {
		
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		System.out.print("Student filter executing ...\n");

		// java.lang.String loginpath = "/index.html";
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		HttpSession s = req.getSession();
		
		if (s.getAttribute("student") == null) {
			res.setStatus(403);
			res.setHeader("Location", loginpath);
			System.out.print("Student login checker FAILED...\n");
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
