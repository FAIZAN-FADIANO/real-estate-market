package com.stolser.beans;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import com.stolser.PropertiesLoader;
import com.stolser.ejb.UserFacadeEJB;
import com.stolser.jpa.Admin;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.User;
import com.stolser.jpa.User.UserStatusType;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.slf4j.*;
/**
 * Contains properties and methods for the userListing.xhtml page.
 * */
@ManagedBean(name = "usersListing")
@ViewScoped
public class UsersListing {
	private static final Logger logger = LoggerFactory.getLogger(UsersListing.class);
	
/** For displaying users on the userListing.xhtml page*/
	private List<User> usersList;
/** Current selected user from the list on the userListing.xhtml page*/
	private User selectedUser;
/** Used for a stateful columns toggler on the userListing.xhtml page*/
	private List<Boolean> usersTableColumnVisibility;
	private List<UserStatusType> userStatusLabels;
/** The user whose status is going to be changed from the Table of users.*/
	private User userForUpdate;
	private UserStatusType userForUpdateStatusOld;
	private UserStatusType userForUpdateStatusNew;
/** A list of all possible users who can be chosen as an assignee for the user
 * who is going to be discarded. <br/>
 * Can be a list of all active admins (realtors) respectively.*/
	private List<User> possibleAssignees;
/**	Used to replace the user to be discarded in the existing dependencies*/
	private User userAssignee;
	
	@EJB 
	private UserFacadeEJB userEJB;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;

	
//-------END of properties-------
	public UsersListing() {}
	
	@PostConstruct
	private void init() {
		String userStatusFilter = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("userstatus");
		if ( !(userStatusFilter == null) ) {
			try {
				switch (userStatusFilter) {
				case "notdiscarded":
					usersList = userEJB.getUsersFindByStatusNot(User.UserStatusType.DISCARDED);
					break;
				case "discarded":
					usersList = userEJB.getUsersFindByStatus(User.UserStatusType.DISCARDED);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				logger.error("Exception occured during getting users list " + 
					"the status = " + userStatusFilter, e);
			}			
		} else {
			/*usersList = userEJB.getUsersFindAll();*/
		}
		logger.trace("A new usersList's been created with params: userstatus = " + userStatusFilter);
		
		userStatusLabels = Arrays.asList(User.UserStatusType.values());

		propSystemMap = propLoader.getPropSystemMap();
		usersTableColumnVisibility = Arrays.asList(true, true, true, true, true, true, true);
	}


	public UserFacadeEJB getUserEJB() {
		return userEJB;
	}

	public void setUserEJB(UserFacadeEJB userEJB) {
		this.userEJB = userEJB;
	}

	public List<User> getUsersList() {
		return usersList;
	}

	public User getSelectedUser() {
		return selectedUser;
	}
	
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}
	
	public List<UserStatusType> getUserStatusLabels() {
		return userStatusLabels;
	}

/*	public void setUserForUpdate(User userForUpdate) {
		this.userForUpdate = userForUpdate;
	}*/

	public List<Boolean> getUsersTableColumnVisibility() {
		return usersTableColumnVisibility;
	}
	
	public void onUserTableToggle(ToggleEvent e) {
		logger.trace("e.getData() = " + e.getData());
		usersTableColumnVisibility.set((Integer) e.getData(), 
				e.getVisibility() == Visibility.VISIBLE);
		logger.trace("usersTableColumnVisibility = " + usersTableColumnVisibility);
    }
	
	public void userStatusChanged(ValueChangeEvent event){

		userForUpdateStatusOld = (UserStatusType)event.getOldValue();
		userForUpdateStatusNew = (UserStatusType)event.getNewValue();
		
		FacesContext context = FacesContext.getCurrentInstance();
		userForUpdate = context.getApplication()
				.evaluateExpressionGet(context, "#{user}", User.class);
		int userForUpdateId = userForUpdate.getId();
		logger.trace("userForUpdateId = " + userForUpdateId 
				+ "\noldStatus = " + userForUpdateStatusOld
				+ "\nnewStatus = " + userForUpdateStatusNew);

/*		FacesMessage newMessage = new FacesMessage(getSystemProperties()
				.getProperty("photoUploadedFailureMes"));
		newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);*/
	}
	
	public void activateDisableUserOK() {
		
		String errorMsg;
		FacesMessage newMessage;
		try {
			userForUpdate = userEJB.updateUserInDB(userForUpdate);
		} catch (Exception e) {
			errorMsg = MessageFormat.format(getSystemProperties()
					.getProperty("activateDisableErrMessage"), userForUpdate, 
					userForUpdate.getStatus());
			newMessage = new FacesMessage(errorMsg);
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
			logger.error(errorMsg, e);
			
			userForUpdate.setStatus(userForUpdateStatusOld);
		}
		
		errorMsg = MessageFormat.format(getSystemProperties()
				.getProperty("activateDisableSuccessMessage"), userForUpdate, 
				userForUpdate.getStatus());
		newMessage = new FacesMessage(errorMsg);
		newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
		logger.trace(errorMsg);
	}
	
	public void possibleAssigneesSearch() {
		
		User.UserType userForUpdateType = userForUpdate.getType();
		possibleAssignees = userEJB
				.getUsersFindByTypeAndStatus(userForUpdateType, UserStatusType.ACTIVE);
	}
	
	public void discardUserOK() {
		FacesMessage newMessage = new FacesMessage("User " + 
				userForUpdate + " ( " + userForUpdate.getStatus()
				+ ") discarded.");
/*		FacesMessage newMessage = new FacesMessage(getSystemProperties()
				.getProperty("photoUploadedFailureMes"));
*/		newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
	}
	
	public void changeUserStatusCancel() {
		userForUpdate.setStatus(userForUpdateStatusOld);
		possibleAssignees = null;
		userAssignee = null;
	}
	
public User getUserForUpdate() {
		return userForUpdate;
	}

public UserStatusType getUserForUpdateStatusNew() {
	return userForUpdateStatusNew;
}

public List<User> getPossibleAssignees() {
	return possibleAssignees;
}

public User getUserAssignee() {
	return userAssignee;
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
