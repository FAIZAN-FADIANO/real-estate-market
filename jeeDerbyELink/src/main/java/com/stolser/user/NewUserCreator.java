package com.stolser.user;

import java.io.IOException;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.PropertiesLoader;
import com.stolser.jpa.Admin;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.User;

@ManagedBean(name = "newUserCreator")
@ViewScoped
public class NewUserCreator {
	static private final Logger logger = LoggerFactory.getLogger(NewUserCreator.class);
	
	private User newUser;
	private String userTypeLabel;
	private String passwordRepeat;
	
	@EJB 
	private UserFacade userEJB;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
	

	public NewUserCreator() {}
	
	@PostConstruct
	private void init() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String userTypeParam = externalContext.getRequestParameterMap().get("usertype");
		logger.trace("newUserTypeLabel = " + userTypeParam);
		/*For creating a new user of both types (ADMIN and REALTOR) the same addNewUser.xhtml
		 * page used but with different GET parameter usertype.*/
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
				logger.error("An attempt to create a user of "
						+ "type {} from the Admin Panel", userTypeParam);
				try {
					externalContext.redirect(externalContext.getRequestContextPath() + "/adminPanel/accessDenied.jsf");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				externalContext.redirect(externalContext.getRequestContextPath() + "/adminPanel/accessDenied.jsf");
			} catch (IOException e) {
				e.printStackTrace();
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
		if (newUser != null) {
			User.UserType userType = newUser.getType();
			switch (userType) {
			case ADMIN:
				userTypeLabel = getSystemProperties().getProperty("userAdminLabel");
				break;
			case REALTOR:
				userTypeLabel = getSystemProperties().getProperty("userRealtorLabel");
				break;
			default:
				break;
			}
		} else {
			userTypeLabel = "";
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
	
	public UserFacade getUserEJB() {
		return userEJB;
	}

	public void setUserEJB(UserFacade userEJB) {
		this.userEJB = userEJB;
	}
	
    private Properties getSystemProperties() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale().toString();
		Properties currentProperties = propSystemMap.get(currentLocal);
		return currentProperties;
	}
	
}















