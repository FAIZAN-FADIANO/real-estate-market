package com.stolser.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.stolser.PropertiesLoader;
import com.stolser.ejb.UserFacadeEJB;
import com.stolser.jpa.Admin;
import com.stolser.jpa.User;

@ManagedBean(name="loginBean")
@SessionScoped
public class LoginBean implements Serializable{
	private static final long serialVersionUID = 1L;

	private String enteredLogin;
	private String enteredPassword;
	
	@EJB
	private UserFacadeEJB userFacade;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propLoggingMap;
	
	private User loggedInUser;
	
	public LoginBean() {}
	
	@PostConstruct
	private void init() {
		propLoggingMap = propLoader.getPropLoggingMap();
	}

	public String getEnteredLogin() {
		return enteredLogin;
	}

	public void setEnteredLogin(String enteredLogin) {
		this.enteredLogin = enteredLogin;
	}

	public String getEnteredPassword() {
		return enteredPassword;
	}

	public void setEnteredPassword(String enteredPassword) {
		this.enteredPassword = enteredPassword;
	}
	
	public User getLoggedInUser() {
		return loggedInUser;
	}

	public String adminPanelValidation() {
		List<User> usersInDB = userFacade.getUsersFindByLogin(getEnteredLogin());
		if (usersInDB.size() == 0) {	
			// there are no users in the DB with such login
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							propLogging().getProperty("invalidLoginErrSum"),
							propLogging().getProperty("invalidLoginErrDetail")));
			return null;
		}
		
		User user = usersInDB.get(0);
		String userLogin = user.getLogin();
		String userPassword = user.getPassword();
		User.UserType userType = user.getType();
		User.UserStatusType userStatusType = user.getStatus();
		
		if (!(userPassword.equals(getEnteredPassword()))) {  
			// the entered password doesn't match the password in the DB
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							propLogging().getProperty("invalidPassErrSum"),
							propLogging().getProperty("invalidPassErrDetail")));
			return null;
			
		}
		
		if (userType == User.UserType.REGISTERED_USER) {
			// registered users don't have access to the Admin Panel
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_WARN, 
							propLogging().getProperty("invalidTypeErrSum"), 
							propLogging().getProperty("invalidTypeErrDetail")));
			return null;
		}
		
		if (userStatusType != User.UserStatusType.ACTIVE) {
			// the user is NOT active --> they don't have NO permissions
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_WARN, 
							propLogging().getProperty("invalidStatusErrSum"), 
							propLogging().getProperty("invalidStatusErrDetail")));
			return null;
		}
		
		System.out.println("inside adminPanelValidation(), before session...");
        // get Http Session and store username
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("username", userLogin);
        session.setAttribute("usertype", userType);
        loggedInUser = user;
        System.out.println("... before redirect to userListing");
        return "/adminPanel/home?faces-redirect=true";
	}
	
	public String adminPanelLogout() {
        HttpSession session = SessionUtil.getSession();
        session.invalidate();
        return "/adminLogin?faces-redirect=true";
    }
	
/**
 * Returns appropriate Properties object for current local on the fron-end
 * */
	private Properties propLogging() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		Properties currentProperties = propLoggingMap.get(currentLocal);
		return currentProperties;
	}
	
	/*public String creatSuperAdmin() {
		List<User> superAdminUsers = userFacade.getUsersFindByType(User.UserType.SUPER_ADMIN);
		if (superAdminUsers.size() == 0) {
			User superAdmin = new Admin(User.UserType.SUPER_ADMIN, User.UserStatusType.ACTIVE, 
					"superadmin", "12345", "Oleg", "Stoliarov", new Date());
			userFacade.persistEntity(superAdmin);
		}
		
		return "userListing";
	}*/
	

}









