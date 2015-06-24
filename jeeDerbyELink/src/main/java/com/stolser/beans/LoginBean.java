package com.stolser.beans;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

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
	private String passwordRepeat;
	
	@EJB
	private UserFacadeEJB userFacade;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
/**
 * Is NOT null only for users that have permissions to access the Admin Panel 
 * (active users with type != User.UserType.REGISTERED_USER). These users also can
 * access the User Private Panel.
 * */
	private User loggedInUser; 
/**
 * Is NOT null only for users that have permissions to access only
 * the User Private Panel (active users with type == User.UserType.REGISTERED_USER).
 * */
	private User signedInUser;
	
	public LoginBean() {}
	
	@PostConstruct
	private void init() {
		propSystemMap = propLoader.getPropSystemMap();
	}

	public String adminPanelValidation() {
		List<User> usersInDB = userFacade.getUsersFindByLogin(getEnteredLogin());
		if (usersInDB.size() == 0) {	
			// there are no users in the DB with such login
			FacesContext.getCurrentInstance().addMessage("loggingForm:loginInput", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							getSystemProperties().getProperty("invalidLoginErrSum"),
							getSystemProperties().getProperty("invalidLoginErrDetail")));
			return null;
		}
		
		User user = usersInDB.get(0);
		String userLogin = user.getLogin();
		String userPassword = user.getPassword();
		User.UserType userType = user.getType();
		User.UserStatusType userStatusType = user.getStatus();
		
		if (!(userPassword.equals(getEnteredPassword()))) {  
			// the entered password doesn't match the password in the DB
			FacesContext.getCurrentInstance().addMessage("loggingForm:passwordInput", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							getSystemProperties().getProperty("invalidPassErrSum"),
							getSystemProperties().getProperty("invalidPassErrDetail")));
			return null;
			
		}
		
		if (userType == User.UserType.REGISTERED_USER) {
			// registered users don't have access to the Admin Panel
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_WARN, 
							getSystemProperties().getProperty("invalidTypeErrSum"), 
							getSystemProperties().getProperty("invalidTypeErrDetail")));
			return null;
		}
		
		if (userStatusType != User.UserStatusType.ACTIVE) {
			// the user is NOT active --> they don't have NO permissions
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_WARN, 
							getSystemProperties().getProperty("invalidStatusErrSum"), 
							getSystemProperties().getProperty("invalidStatusErrDetail")));
			return null;
		}
		
        loggedInUser = user;
        setPasswordRepeat(loggedInUser.getPassword());
        
        return "/adminPanel/home?faces-redirect=true";
	}
	
	public String adminPanelLogout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
	            .getExternalContext().getSession(false);
        LoginBean loginBean = (LoginBean)session.getAttribute("loginBean");
        loginBean.setLoggedInUser(null);
        
        return "/adminLogin?faces-redirect=true";
    }
	
	public String updateLoggedInUser() {
	
		try{
			loggedInUser = userFacade.updateUserInDB(loggedInUser);
			
		} catch(Exception e) {
			FacesMessage newMessage = new FacesMessage(MessageFormat.format(getSystemProperties()
					.getProperty("updateUserErr"), loggedInUser));
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			
			loggedInUser = userFacade.refreshUserFromDB(loggedInUser);
			
			return null;
		}
		
		FacesMessage newMessage = new FacesMessage(MessageFormat.format(getSystemProperties()
				.getProperty("updateUserSuccessMessage"), loggedInUser));
		newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
		
		return null;
	}
	
/**
 * Returns appropriate Properties object for current local on the fron-end
 * */
	private Properties getSystemProperties() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		Properties currentProperties = propSystemMap.get(currentLocal);
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

	public MenuModel getAdminPanelMenu() {
		DefaultMenuModel model = new DefaultMenuModel();
		
		return model;
	}

/**
 * Validates user password on matching the pattern.
 * */
	public void passwordValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String PASSWORD_PATTERN = "^.{5,15}$";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(PASSWORD_PATTERN);
	    
		String enteredPassword = value.toString();
		matcher = pattern.matcher(enteredPassword);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("loginValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
	public void passwordRepeatValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String firstPassword = loggedInUser.getPassword();
		String repeatPassword = value.toString();
		
		List<FacesMessage> messages = FacesContext.getCurrentInstance()
				.getMessageList("myProfileInfo:myProfileForm:passwordFirst");
		
		if (messages.size() > 0) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("passwordRepeatNotCorrectMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		} else if ( !firstPassword.equals(repeatPassword) ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
				.getProperty("passwordRepeatRequiredMessage"));
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			
			throw new ValidatorException(newMessage);
		}
		
		FacesContext.getCurrentInstance()
		.addMessage(null, new FacesMessage("passwordRepeatValidator: success!"));
	}
	
/*------------ getter and setters -----------*/
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
	
	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

}









