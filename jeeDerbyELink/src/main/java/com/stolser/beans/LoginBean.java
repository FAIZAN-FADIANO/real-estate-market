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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
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
	private UploadedFile uploadedUserPhoto;
	
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
		
		/*FacesContext.getCurrentInstance()
		.addMessage(null, new FacesMessage("passwordRepeatValidator: success!"));*/
	}
	
	public void firstLastNameValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String NAME_PATTERN = "^[a-zA-Z-]{1,20}$";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(NAME_PATTERN);
	    
		String enteredName = value.toString();
		matcher = pattern.matcher(enteredName);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("firstLastNameValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
	public void emailValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String EMAIL_PATTERN = "([a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." 
			+ "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
			+ "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)|(^$)";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    
		String enteredEmail = value.toString();
		matcher = pattern.matcher(enteredEmail);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("emailValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
	public void skypeValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String SKYPE_PATTERN = "(^[a-zA-Z][a-zA-Z0-9]{5,31}$)|(^$)";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(SKYPE_PATTERN);
	    
		String enteredSkype = value.toString();
		matcher = pattern.matcher(enteredSkype);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("skypeValidatorMessage"));
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
			
			System.out.println("File has been uploaded. \nuserPhotoName = " + newImageName + 
					"; \nfull path = " + uploadedPhoto);
			
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("photoUploadedSuccessMes"));
			newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			
			getLoggedInUser().setPhoto("/images/" + newImageName);
			
		} catch (IOException ioe) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("photoUploadedFailureMes"));
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			
			System.out.println("the new file with name= " + newImageName + 
					" has NOT been uploaded. \nFull path= " + uploadedPhoto);
			ioe.printStackTrace();
		}
		
	}
	
/*------------ getters without fiels --------*/
/**
 * Returns the path of the photo that is stored in the DB 
 * (if it has been uploaded already), or the path to the default photo.
 * */
	public String getUserPhotoPath() {
		
		String userPhotoPath = "";
		String loggedInUserPhoto = getLoggedInUser().getPhoto();
		
		if ((loggedInUserPhoto == null) || ("".equals(loggedInUserPhoto))) {
			userPhotoPath = "/images/unknownUser.jpg";
		} else {
			userPhotoPath = loggedInUserPhoto;
		}
		
		return userPhotoPath;
	}
	
/*------------ END of getters without fiels --------*/
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

}









