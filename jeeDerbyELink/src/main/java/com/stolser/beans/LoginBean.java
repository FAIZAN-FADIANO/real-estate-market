package com.stolser.beans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.stolser.PropertiesLoader;
import com.stolser.ejb.UserFacadeEJB;
import com.stolser.jpa.Admin;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.User;
/**
 * Used for managing sessions and updating logged in user's info. 
 * Contains public methods:<br>
 * <ul>
 * <li>adminPanelValidation();</li>
 * <li>adminPanelLogout();</li>
 * <li>updateLoggedInUser();</li>
 * <li>passwordRepeatValidator();</li>
 * <li>uploadedPhotoHandler();</li>
 * <li>getUserPhotoPath();</li>
 * <li></li>
 * </ul>
 * */
@ManagedBean(name="loginBean")
@SessionScoped
public class LoginBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(LoginBean.class);
	
	private String enteredLogin;
	private String enteredPassword;
	
	private String passwordRepeat;
	private UploadedFile uploadedUserPhoto;
	private List<Boolean> arePhoneNumbersDeleted;
	
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
	
	public LoginBean() {}
	
	@PostConstruct
	private void init() {
		propSystemMap = propLoader.getPropSystemMap();
		arePhoneNumbersDeleted = new ArrayList<Boolean>();
	}
/**
 * Verifies user's login, password, type and status and if everything is OK
 * redirects to the adminLogin.xhtml page.
 * */
	public String adminPanelValidation() {
		Marker logInMarker = MarkerFactory.getMarker("adminPanelLoggingIn");
		
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
			
			logger.info(logInMarker, "For login = {} incorrect password ({}) entered.",
					userLogin, userPassword);
			
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
		
        logger.info(logInMarker, "A user ({}) has been logged in.", user);
        
        loggedInUser = user;
        /* For the use on the myProfile.xhtml page.*/
        setPasswordRepeat(loggedInUser.getPassword());
        
        if (loggedInUser instanceof Realtor) {
        	List<String> phoneNumbers = ((Realtor)loggedInUser).getPhoneNumbers();
        	arePhoneNumbersDeleted.clear(); 
        	for (int i = 0; i < phoneNumbers.size(); i++) {
				arePhoneNumbersDeleted.add(false);
			} 
		}
        
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
			/*Removing empty phone numbers*/
			if (loggedInUser instanceof Realtor) {
	        	List<String> phoneNumbers = ((Realtor)loggedInUser).getPhoneNumbers();
	        	List<String> phoneNumbersWithoutEmpty = new ArrayList<>();
	        	for (int i = 0; i < phoneNumbers.size(); i++) {
	        		String currentPhoneNumber = phoneNumbers.get(i);
	        		if ( !"".equals(currentPhoneNumber) ) {
	        			phoneNumbersWithoutEmpty.add(currentPhoneNumber);
					}
				}
	        	((Realtor)loggedInUser).setPhoneNumbers(phoneNumbersWithoutEmpty);
			}
			/*-------------------------------*/
			
			loggedInUser = userFacade.updateUserInDB(loggedInUser);
			
		} catch(Exception e) {
			String errorMessage = MessageFormat.format(getSystemProperties()
					.getProperty("updateUserErr"), loggedInUser);
			FacesMessage newMessage = new FacesMessage(errorMessage);
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			logger.error(errorMessage, e);
			
			loggedInUser = userFacade.refreshUserFromDB(loggedInUser);
			
			return null;
		}
		
		String successMessage = MessageFormat.format(getSystemProperties()
				.getProperty("updateUserSuccessMessage"), loggedInUser);
		FacesMessage newMessage = new FacesMessage(successMessage);
		newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
		logger.debug(successMessage);
		
		return null;
	}
	
/**
 * Checks whether the second password value on the myProfile.xhtml page 
 * matches the first password value. 
 * The method is used for already logged in user.
 * */
	public void passwordRepeatValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String firstPassword = getLoggedInUser().getPassword();
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
	}

/**
 * Tries to create a new image on the server with a unique name for each user 
 * in the /applications/uploads/images/folder or throws an Exception. 
 */
	public void uploadedPhotoHandler(FileUploadEvent e) {
		UploadedFile uploadedFile = e.getFile();
		/* get the absolute path of the destination folder for uploaded images*/
		String separator = File.separator;
		String imageUploadedFolderPath = System.getProperty("com.sun.aas.instanceRoot") 
				+ separator + "applications" + separator + "uploads" 
				+ separator + "images";
		File imageUploadedFolder = new File(imageUploadedFolderPath);
		/* get the name of the newly created image. All user's photos are unique.*/
		String newImageName = "photoOfUser-" + getLoggedInUser().getLogin() + ".jpg";
		File uploadedPhoto = new File(imageUploadedFolder, newImageName);
		
		try(InputStream input = uploadedFile.getInputstream()) {
			Files.copy(input, uploadedPhoto.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("photoUploadedSuccessMes"));
			newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			logger.debug("A new file with the full path = {} has been uploaded.",
					uploadedPhoto);
			
			getLoggedInUser().setPhoto("/images/" + newImageName);
			
		} catch (IOException ioe) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("photoUploadedFailureMes"));
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			
			logger.error("A new file with the full path = {} has NOT been uploaded.",
					uploadedPhoto, ioe);
		}
		
	}
	
/**
 * Action listener for addNewPhoneNumber button on the myProfile.xhtml.
 * */
	public void addNewPhoneNumber(ActionEvent event) {
		((Realtor) loggedInUser).getPhoneNumbers().add("");
		arePhoneNumbersDeleted.add(false);
	}
/**
 * Action listener for deletePhoneNumber button on the myProfile.xhtml.
 * */
	public void deletePhoneNumber(ActionEvent event) {
		List<String> phoneNumbers = ((Realtor) getLoggedInUser()).getPhoneNumbers();
		List<String> newPhoneNumbers = new ArrayList<>();
		for (int i = 0; i < phoneNumbers.size(); i++) {
			if ( !arePhoneNumbersDeleted.get(i) ) {
				newPhoneNumbers.add(phoneNumbers.get(i));
			}
		}
		((Realtor) getLoggedInUser()).setPhoneNumbers(newPhoneNumbers);
		arePhoneNumbersDeleted.clear();
		for (int i = 0; i < newPhoneNumbers.size(); i++) {
			arePhoneNumbersDeleted.add(false);
		}

	}
	
/*------------ getters without fields --------*/

	
/*------------ END of getters without fields --------*/
/*------------ private methods --------*/
	
/**
 * Returns appropriate Properties object for current local on the front-end
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

/*	public MenuModel getAdminPanelMenu() {
		DefaultMenuModel model = new DefaultMenuModel();
		
		return model;
	}*/
	
/*------------ END of private methods --------*/
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

	public UploadedFile getUploadedUserPhoto() {
		return uploadedUserPhoto;
	}

	public void setUploadedUserPhoto(UploadedFile uploadedUserPhoto) {
		this.uploadedUserPhoto = uploadedUserPhoto;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public List<Boolean> getArePhoneNumbersDeleted() {
		return arePhoneNumbersDeleted;
	}

	public void setArePhoneNumbersDeleted(List<Boolean> arePhoneNumbersDeleted) {
		this.arePhoneNumbersDeleted = arePhoneNumbersDeleted;
	}

}









