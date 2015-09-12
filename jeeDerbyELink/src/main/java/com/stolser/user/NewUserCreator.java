package com.stolser.user;

import static com.stolser.MessageFromProperties.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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

import com.stolser.jpa.User;


@ManagedBean(name = "newUserCreator")
@ViewScoped
public class NewUserCreator implements Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(NewUserCreator.class);
	
	private User newUser;
	private String userTypeLabel;
	private String passwordRepeat;
	@EJB 
	private UserFacade userEJB;

	public NewUserCreator() {}
	
	@PostConstruct
	private void init() {
		ExternalContext externalContext = FacesContext
				.getCurrentInstance().getExternalContext();
		String userTypeParam = externalContext.getRequestParameterMap().get("usertype");
		logger.trace("newUserTypeLabel = " + userTypeParam);
		/*For creating a new user of both types (ADMIN and REALTOR) the same addNewUser.xhtml
		 * page used but with different GET parameter usertype.*/
		if ( !(userTypeParam == null) ) {
			switch (userTypeParam) {
			case "admin":
				newUser = UserFactory.createAdmin();
				break;
			case "realtor":
				newUser = UserFactory.createRealtor();
				break;
			default:
				logger.error("An attempt to create a user of "
						+ "type {} from the Admin Panel", userTypeParam);
				try {
					externalContext.redirect(externalContext.getRequestContextPath() + 
							"/adminPanel/accessDenied.jsf");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				externalContext.redirect(externalContext.getRequestContextPath() + 
						"/adminPanel/accessDenied.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String addNewUser() {
		try{
			newUser = userEJB.addNewUser(newUser);
			
		} catch(Exception e) {
			String failureMessage = createMessageText("userNotBeenCreatedMessage", newUser);
			addMessageToFacesContext(createErrorFacesMessage(failureMessage));
			logger.error(failureMessage, e);
			
			return null;
		}
		
		String successMessage = createMessageText("userBeenCreatedMessage", newUser);
		addMessageToFacesContext(createInfoFacesMessage(successMessage));
		logger.info(successMessage);
		
		return null;
	}
	
	public void passwordRepeatValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String firstPassword = getNewUser().getPassword();
		String repeatPassword = value.toString();
		
		List<FacesMessage> firstPasswordInputMessages = FacesContext.getCurrentInstance()
				.getMessageList("addNewUserForm:passwordFirst");
		
		if (firstPasswordInputMessages.size() > 0) {
			FacesMessage newMessage = createErrorFacesMessage(
					createMessageText("passwordRepeatNotCorrectMessage"));
				
			throw new ValidatorException(newMessage);
		} else if ( !repeatPassword.equals(firstPassword) ) {
			FacesMessage newMessage = createErrorFacesMessage(
					createMessageText("passwordRepeatRequiredMessage"));
			
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
				userTypeLabel = createMessageText("userAdminLabel");
				break;
			case REALTOR:
				userTypeLabel = createMessageText("userRealtorLabel");
				break;
			default:
				break;
			}
		} else {
			userTypeLabel = "";
		}
		
		return userTypeLabel;
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
}
