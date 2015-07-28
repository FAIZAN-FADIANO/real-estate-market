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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.User;
import com.stolser.user.LoginBean;

@WebFilter(
		description = "An authorization filter for the Admin Panel", 
		urlPatterns = { 
				"/adminPanel/*",
				"/adminLogin.jsf"
		})
public class AuthAdminPanelFilter implements Filter {
	private ServletRequest request;
	private ServletResponse response;
	private FilterChain chain;
	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private HttpSession session;
	private LoginBean loginBean;
	private User loggedInUser;
	private String requestedURI;
	private User.UserType loggedInUserType;

	private final Logger logger = LoggerFactory.getLogger(AuthAdminPanelFilter.class);

	public AuthAdminPanelFilter() {}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		this.request = request;
		this.response = response;
		this.chain = chain;
		
		httpRequest = (HttpServletRequest) request;
		httpResponse = (HttpServletResponse) response;
		session = httpRequest.getSession(false);
		loginBean = (session != null) 
				? (LoginBean)session.getAttribute("loginBean") 
				: null;
		loggedInUser = (loginBean != null) 
				? loginBean.getLoggedInUser() 
				: null;
		requestedURI = httpRequest.getRequestURI();
		
		verifyAccessToAdminLoginPage("/adminLogin.");
		verifyAccessToAdminPanelDirectory("/adminPanel/");

	}

	public void destroy() {}

	public void init(FilterConfig fConfig) throws ServletException {}

	private void redirectTo(String uri) throws IOException {
		try {
			httpResponse.sendRedirect(httpRequest.getContextPath() + uri);
		} catch (IOException e) {
			logger.error("An exception occured during redirecting to {}", uri, e);
			throw new IOException("An exception occured during redirecting.");
		}
	}
	
	private boolean isRequestedURIContain(String uriPart) {
		return (requestedURI.indexOf(uriPart) >= 0);
	}
	
	private boolean isUserLoggedInAdminPanel() {
		return loggedInUser != null;
	}
	
	private boolean isLoggedInUserSuperAdmin() {
		return loggedInUserType == User.UserType.SUPER_ADMIN;
	}
	
	private boolean isLoggedInUserAdmin() {
		return loggedInUserType == User.UserType.ADMIN;
	}
	
	private boolean isLoggedInUserRealtor() {
		return loggedInUserType == User.UserType.REALTOR;
	}
	
	private boolean isUserTypeParamValid(String param) {
		return ("admin".equals(param) 
				|| ("realtor".equals(param)));
	}
	
	private void verifyAccessToAdminLoginPage(String pageName) 
			throws IOException, ServletException {
		if (isRequestedURIContain(pageName)) {
			if (isUserLoggedInAdminPanel()) {
				redirectTo("/adminPanel/home.jsf");
			} else {
				chain.doFilter(request, response);
			}
		}
	}
	
	private void verifyAccessToAdminPanelDirectory(String pageName) 
			throws IOException, ServletException {
		if (isRequestedURIContain(pageName)) {
			if (isUserLoggedInAdminPanel()) {
				loggedInUserType = loggedInUser.getType();
				
				verifyAccessToUserListingPage("/userListing.");
				verifyAccessToMonitorPage("/adminPanelMonitor.");
				verifyAccessToCreateUserPage("/addNewUser.");
				
				chain.doFilter(request, response);
			} else {
				/* User hasn't logged in but asking for a page with restricted access, 
				 * so redirect user to the Login Page.*/
				redirectTo("/adminLogin.jsf");
			}
		}
	}
	
	private void verifyAccessToUserListingPage(String pageName) throws IOException {
		/* Processing a request to the userListing.xhtml page. This request can
		 * contain different parameters that define what type of users with what 
		 * status to show. */
		if (isRequestedURIContain(pageName)) {
			String userStatusParam = (String)httpRequest.getParameter("userstatus");
			if (("discarded".equals(userStatusParam)) 
					&& ( ! isLoggedInUserSuperAdmin() )) {
				redirectTo("/adminPanel/accessDenied.jsf");
			}
		}
	}
	
	private void verifyAccessToMonitorPage(String pageName) throws IOException {
		if ( (isRequestedURIContain(pageName)) 
				&& (isLoggedInUserRealtor())) {
			redirectTo("/adminPanel/accessDenied.jsf");
		}
	}
	
	private void verifyAccessToCreateUserPage(String pageName) throws IOException {
		
		if (isRequestedURIContain(pageName)) {
			/*String userTypeParam = (String) httpRequest.getParameter("usertype");*/
			
			if ( ! isLoggedInUserSuperAdmin()) {
				redirectTo("/adminPanel/accessDenied.jsf");
			}

		}
	}

}
