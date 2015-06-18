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

import com.stolser.jpa.User;

/**
 * Servlet Filter implementation class AuthFilter
 */
@WebFilter(
		description = "The main authentication and authorization filter", 
		urlPatterns = { 
				"/adminPanel/*", 
				"/userPrivatePanel/*"
		})
public class AuthFilter implements Filter {

    public AuthFilter() {}


	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {

		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			HttpSession session = httpRequest.getSession(false);
			String requestedURI = httpRequest.getRequestURI();
			
			System.out.println("AuthFilter.doFilter(): username = " + session.getAttribute("username"));
			
			
			if (requestedURI.indexOf("/adminPanel/") >= 0) {
				if ((session != null) && (session.getAttribute("username") != null) &&
					(session.getAttribute("usertype") != User.UserType.REGISTERED_USER)) {
					// pass the request along the filter chain
					chain.doFilter(request, response);
				} else {
	// user didn't log in but asking for a page that is not allowed so take user to login page
					httpResponse.sendRedirect(httpRequest.getContextPath() + 
							"/adminLogin.jsf");   // Anonymous user. Redirect to login page
				}
			} else {
				// pass the request along the filter chain
				System.out.println("AuthFilter.doFilter(): Accessing the User Privat Panel.");
				chain.doFilter(request, response);
			}
		} catch (Exception e) {
			System.out.println( e.getMessage());
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
