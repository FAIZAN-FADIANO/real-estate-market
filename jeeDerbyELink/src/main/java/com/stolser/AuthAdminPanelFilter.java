package com.stolser;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

import com.stolser.beans.LoginBean;
import com.stolser.jpa.User;

/**
 * Servlet Filter implementation class AuthFilter
 */
@WebFilter(
		description = "An authentication and authorization filter for the Admin Panel", 
		urlPatterns = { 
				"/adminPanel/*",
				"/adminLogin.jsf"
		})
public class AuthAdminPanelFilter implements Filter {

    public AuthAdminPanelFilter() {}


	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			HttpSession session = httpRequest.getSession(false);
			LoginBean loginBean = (session != null) ? 
						(LoginBean)session.getAttribute("loginBean") : null;
			User loggedInUser = (loginBean != null) ? loginBean.getLoggedInUser() : null;
			String requestedURI = httpRequest.getRequestURI();
			
			if (requestedURI.indexOf("/adminLogin.jsf") >= 0) {
				if (loggedInUser != null) {
					/*This is a logged in user with permissions, so redirect to the Home page 
					of the Admin Panel*/
					httpResponse.sendRedirect(httpRequest.getContextPath() + 
												"/adminPanel/home.jsf");
				} else {
					// pass the request along the filter chain
					chain.doFilter(request, response);
				}
				
			} else if (requestedURI.indexOf("/adminPanel/") >= 0) {
				if (loggedInUser != null) {
					User.UserType loggedInUserType = loggedInUser.getType();
					/*This is a logged in user with permissions, so pass 
					 * the request along the filter chain*/
					chain.doFilter(request, response);
				} else {
					/*User didn't log in but asking for a page that is not allowed, 
					 * so take user to the login page.*/
					httpResponse.sendRedirect(httpRequest.getContextPath() + 
							"/adminLogin.jsf");
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {}

}
