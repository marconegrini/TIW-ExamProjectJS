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

public class ProfessorChecker implements Filter{
	
	public ProfessorChecker() {
		
	}
		
	public void destroy() {
			
	}
		
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		System.out.print("Professor filter executing ...\n");

		// java.lang.String loginpath = "/index.html";
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();
		String loginpath = req.getServletContext().getContextPath() + "/index.html";

		if (s.getAttribute("professor") == null) {
			res.setStatus(403);
			res.setHeader("Location", loginpath);
			System.out.print("Professor login checker FAILED...\n");
			return;
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}



