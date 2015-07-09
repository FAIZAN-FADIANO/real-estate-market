package com.stolser.beans;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.PropertiesLoader;
import com.stolser.ejb.UserFacadeEJB;
import com.stolser.jpa.Admin;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.User;

@ManagedBean(name = "newUserCreating")
@ViewScoped
public class NewUserCreating {
	private static final Logger logger = LoggerFactory.getLogger(NewUserCreating.class);
	
	private User newUser;
	private String userTypeLabel;
	private String passwordRepeat;
	
	@EJB 
	private UserFacadeEJB userEJB;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
	
	public NewUserCreating() {}
	
	@PostConstruct
	private void init() {
		String userTypeParam = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("usertype");
		if ( !(userTypeParam == null) ) {
			switch (userTypeParam) {
			case "admin":
				newUser = new Admin();
				newUser.setType(User.UserType.ADMIN);
				break;
			case "realtor":
				newUser = new Realtor();
				newUser.setType(User.UserType.REALTOR);
				break;
			default:
				break;
			}
		}
		
		propSystemMap = propLoader.getPropSystemMap();
	}

	public String addNewUser() {
		
		User newUser = getNewUser();
		newUser.setStatus(User.UserStatusType.ACTIVE);
		newUser.setDateOfCreation(Calendar.getInstance().getTime());
		newUser.setPhoto(getUserDefaultPhotoPath());
		
		try{
			userEJB.addNewUser(newUser);
		} catch(Exception e) {
			String failureMessage = MessageFormat.format(getSystemProperties()
					.getProperty("userNotBeenCreatedMessage"), newUser);
			FacesMessage newMessage = new FacesMessage(failureMessage);
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			logger.error(failureMessage, e);
			
			return null;
		}
		
		String successMessage = MessageFormat.format(getSystemProperties()
				.getProperty("userBeenCreatedMessage"), newUser);
		FacesMessage newMessage = new FacesMessage(successMessage);
		newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
		logger.info(successMessage);
		
		return null;
	}
	
/**
 * Checks whether the second password value on the addNewUser.xhtml page 
 * matches the first password value. 
 * */
	public void passwordRepeatValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String firstPassword = getNewUser().getPassword();
		String repeatPassword = value.toString();
		
		List<FacesMessage> messages = FacesContext.getCurrentInstance()
				.getMessageList("addNewUserForm:passwordFirst");
		
		if (messages.size() > 0) {
			/*There are messages for the first password input field.*/
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("passwordRepeatNotCorrectMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		} else if ( !repeatPassword.equals(firstPassword) ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
				.getProperty("passwordRepeatRequiredMessage"));
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			
			throw new ValidatorException(newMessage);
		}
	}
	
	public User getNewUser() {
		return newUser;
	}

	public void setNewUser(User newUser) {
		this.newUser = newUser;
	}

	public String getNewUserTypeLabel() {
		
		User.UserType userType = newUser.getType();
		switch (userType) {
		case ADMIN:
			userTypeLabel = getSystemProperties().getProperty("userAdminLabel");
			break;
		case REALTOR:
			userTypeLabel = getSystemProperties().getProperty("userRealtorLabel");
			break;
		default:
			String errorMsg = "An attempt to create a user of type " + userType + 
					" from the Admin Panel";
			logger.error(errorMsg);
			assert false : errorMsg;
		}
		
		return userTypeLabel;
	}
	
	public String getUserDefaultPhotoPath() {
		String userPhotoPath = "/images/unknownUser.jpg";
		return userPhotoPath;
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}
	
	public UserFacadeEJB getUserEJB() {
		return userEJB;
	}

	public void setUserEJB(UserFacadeEJB userEJB) {
		this.userEJB = userEJB;
	}
	
/**
 * Returns appropriate Properties object for current local on the front-end
 * */
    private Properties getSystemProperties() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale().toString();
		Properties currentProperties = propSystemMap.get(currentLocal);
		return currentProperties;
	}
	
}















