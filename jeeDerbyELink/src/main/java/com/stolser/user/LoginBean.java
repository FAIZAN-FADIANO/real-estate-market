package com.stolser.user;

import static com.stolser.MessageFromProperties.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.stolser.jpa.Realtor;
import com.stolser.jpa.User;
/**
 * Used for managing sessions and updating logged in user's info. 
 * */
@ManagedBean(name="loginBean")
@SessionScoped
public class LoginBean implements Serializable {
	
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(LoginBean.class);
	
	private String enteredLogin;
	private String enteredPassword;
	
	private String passwordRepeat;
	private UploadedFile uploadedUserPhoto;
	/*Used for deleting phone numbers on the myProfile.xhtml*/
	private List<Boolean> arePhoneNumbersDeleted;
	
	@EJB
	private UserFacade userFacade;
	
/**
 * Is NOT null only for users that have permissions to access the Admin Panel 
 * (active users with type != User.UserType.REGISTERED_USER). These users also can
 * access the User Private Panel.
 * */
	private User loggedInUser;
	@EJB
	private AdminPanelRegister adminPanelRegister; 
	
	public LoginBean() {}
	
	@PostConstruct
	private void init() {
		arePhoneNumbersDeleted = new ArrayList<Boolean>();
	}

	public String adminPanelValidation() {
		
		Marker logInMarker = MarkerFactory.getMarker("adminPanelLoggingIn");
		
		List<User> usersInDB = userFacade.getUsersFindByLogin(enteredLogin);
		User user = usersInDB.get(0);
		String userLogin = user.getLogin();
		String userPassword = user.getPassword();
		User.UserType userType = user.getType();
		User.UserStatusType userStatusType = user.getStatus();
		
		if (!(userPassword.equals(enteredPassword))) {  
			String messageSummary = createMessageText("invalidPassErrSum");
			String messageDetail = createMessageText("invalidPassErrDetail");
			addMessageToFacesContext("loggingForm:passwordInput", 
				createErrorFacesMessage(messageSummary, messageDetail));
			
			logger.info(logInMarker, "For login = {} incorrect password ({}) entered.",
					userLogin, enteredPassword);
			
			return null;	
		} 
		
		if (userType == User.UserType.REGISTERED_USER) {
			/*registered users don't have access to the Admin Panel*/
			String messageSummary = createMessageText("invalidTypeErrSum");
			String messageDetail = createMessageText("invalidTypeErrDetail");
			addMessageToFacesContext(
					createErrorFacesMessage(messageSummary,	messageDetail));
			
			return null;
		}
		
		if (userStatusType != User.UserStatusType.ACTIVE) {
			/*the user is NOT active --> they don't have NO permissions*/
			String messageSummary = createMessageText("invalidStatusErrSum"); 
			String messageDetail = createMessageText("invalidStatusErrDetail");
			addMessageToFacesContext(
					createWarnFacesMessage(messageSummary, messageDetail));
			
			return null;
		}
		
        logger.info(logInMarker, "A user ({}) has been logged in.", user);
        
        loggedInUser = user;
        /* For the use on the myProfile.xhtml page.*/
        passwordRepeat = loggedInUser.getPassword();
        
        if (loggedInUser instanceof Realtor) {
        	List<String> phoneNumbers = ((Realtor)loggedInUser).getPhoneNumbers();
        	arePhoneNumbersDeleted.clear(); 
        	for (int i = 0; i < phoneNumbers.size(); i++) {
				arePhoneNumbersDeleted.add(false);
			} 
		}
        
        addUserToAdminPanelRegister(loggedInUser);
        
        return "/adminPanel/home?faces-redirect=true";
	}
	
	public String adminPanelLogout() {
		
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
	            .getExternalContext().getSession(false);
        session.setAttribute("loginBean", null);
        session.setAttribute("adminMainMenuBean", null);
        
        adminPanelRegister.getLoggedInUsers().remove(loggedInUser);
        loggedInUser = null;
        
        logger.trace("loggedInUser = {}", loggedInUser);
   
        return "/adminLogin?faces-redirect=true";
    }
	
	public String updateLoggedInUser() {
		removeEmptyPhoneNumbers();
		
		try{
			loggedInUser = userFacade.updateUserInDB(loggedInUser);
			
		} catch(Exception e) {
			String errorMessage = createMessageText("updateUserErr", loggedInUser);
			addMessageToFacesContext(createErrorFacesMessage(errorMessage));
			logger.error(errorMessage, e);
			
			loggedInUser = userFacade.refreshUserFromDB(loggedInUser);
			
			return null;
		}
		
		String successMessage = createMessageText("updateUserSuccessMessage", loggedInUser);
		addMessageToFacesContext(createInfoFacesMessage(successMessage));
		logger.debug(successMessage);
		
		return null;
	}
	
	public void passwordRepeatValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String firstPassword = getLoggedInUser().getPassword();
		String repeatPassword = value.toString();
		
		List<FacesMessage> firstPasswordInputMessages = FacesContext.getCurrentInstance()
				.getMessageList("myProfileInfo:myProfileForm:passwordFirst");
		
		if (firstPasswordInputMessages.size() > 0) {		
			FacesMessage newMessage = createErrorFacesMessage(
					createMessageText("passwordRepeatNotCorrectMessage"));
			
			throw new ValidatorException(newMessage);
			
		} else if ( ! firstPassword.equals(repeatPassword) ) {
			FacesMessage newMessage = createErrorFacesMessage(
					createMessageText("passwordRepeatRequiredMessage"));
			
			throw new ValidatorException(newMessage);
		}
	}

/**
 * Tries to create a new image on the server with a unique name for each user 
 * in the /applications/uploads/images/folder or throws an Exception. 
 */
	public void uploadedPhotoHandler(FileUploadEvent e) {
		
		UploadedFile uploadedFile = e.getFile();
		File uploadedPhoto = getUploadedPhotoAsFile();
				
		try (InputStream input = uploadedFile.getInputstream()) {
			Files.copy(input, uploadedPhoto.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			addMessageToFacesContext(createInfoFacesMessage(
					createMessageText("photoUploadedSuccessMes")));
			logger.debug("A new file with the full path = {} has been uploaded.",
					uploadedPhoto);
			
			loggedInUser.setPhoto("/images/" + getNewImageName());
			
		} catch (IOException ioe) {
			addMessageToFacesContext(createErrorFacesMessage(
					createMessageText("photoUploadedFailureMes")));
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
	
	public boolean isUserSuperAdmin() {
		return  (loggedInUser.getType() == User.UserType.SUPER_ADMIN);
	}
	
	public boolean isUserAdmin() {
		return  (loggedInUser.getType() == User.UserType.ADMIN);
	}
	
	public boolean isUserRealtor() {
		return  (loggedInUser.getType() == User.UserType.REALTOR);
	}
	
	public AdminPanelRegister getAdminPanelRegister() {
		return adminPanelRegister;
	}

	private void addUserToAdminPanelRegister(User user) {
		List<User> loggedInUsers = adminPanelRegister.getLoggedInUsers();
		
		loggedInUsers.remove(user);
		loggedInUsers.add(user);
	}
	
	private File getUploadedPhotoAsFile() {
		/* get the absolute path of the destination folder for uploaded images*/
		String separator = File.separator;
		String imageUploadedFolderPath = System.getProperty("com.sun.aas.instanceRoot") 
				+ separator + "applications" + separator + "uploads" 
				+ separator + "images";
		File imageUploadedFolder = new File(imageUploadedFolderPath);
		/* get the name of the newly created image. All user's photos are unique.*/
		
		File uploadedPhoto = new File(imageUploadedFolder, getNewImageName());
		
		return uploadedPhoto;
	}
	
	private String getNewImageName() {
		String newImageName = "photoOfUser-" + loggedInUser.getLogin() + ".jpg";
		return newImageName;
	}
	
	private void removeEmptyPhoneNumbers() {
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