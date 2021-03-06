package com.stolser.user;

import static com.stolser.MessageFromProperties.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.stolser.jpa.Admin;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.User;
import com.stolser.jpa.User.UserStatusType;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.slf4j.*;

@Named("usersListing")
@ViewScoped
public class UsersListing implements Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(UsersListing.class);

	private List<User> usersList;
	/** Current user for whom extra info will be displayed in the popup window.*/
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
	private Boolean isDiscardUserButtonVisible;
	/** Contains flags (true/false values) for each user from the usersList list. Only users 
	 * with the corresponding value true in this list will be deleted from the DB after
	 * successful confirmation.*/
	private List<Boolean> usersToDeleteFromDBFlags;
	private List<User> usersToDeleteFromDB;
	private Boolean isDeleteUsersButtonVisible;

	@Inject
	private UserFacade userEJB;
	private String userStatusFilter;

	public UsersListing() {}

	@PostConstruct
	private void init() {
		populateUsersListFromStatusFilter();
		populateUserStatusLabels();
		populateUsersTableColumnVisibility();

		isDiscardUserButtonVisible = false;
	}

	public UserFacade getUserEJB() {
		return userEJB;
	}

	public void setUserEJB(UserFacade userEJB) {
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

	public List<Boolean> getUsersTableColumnVisibility() {
		return usersTableColumnVisibility;
	}

	public void onUserTableToggle(ToggleEvent e) {
		usersTableColumnVisibility.set((Integer) e.getData(), 
				e.getVisibility() == Visibility.VISIBLE);
	}

	public void userStatusChanged(ValueChangeEvent event){
		userForUpdateStatusOld = (UserStatusType)event.getOldValue();
		userForUpdateStatusNew = (UserStatusType)event.getNewValue();

		FacesContext context = FacesContext.getCurrentInstance();
		User userForUpdateCurrent = context.getApplication()
				.evaluateExpressionGet(context, "#{user}", User.class);
		int userForUpdateCurrentId = userForUpdateCurrent.getId();

		/*We need to assign a chosen user to the userForUpdate var only if 
		 * the status of this particular user hasn't already been changed during this
		 * view. Otherwise userForUpdate contain an object managed by EntityManager
		 * that was returned by the UserFacade#mergeEntity() method and if we try 
		 * to change the status of the same object again (on the same view) we 
		 * will get javax.persistence.OptimisticLockException.*/
		if ((userForUpdate == null) || (userForUpdate.getId() != userForUpdateCurrentId)) {
			userForUpdate = userForUpdateCurrent;
		} 
	}

	public void activateDisableUserOK() {
		try {
			userForUpdate.setStatus(userForUpdateStatusNew);
			userForUpdate = userEJB.updateUserInDB(userForUpdate);

			String successMessage = createMessageText("activateDisableSuccessMessage", 
					userForUpdate, userForUpdate.getStatus());
			addMessageToFacesContext(createInfoFacesMessage(successMessage));
			logger.debug(successMessage);

		} catch (Exception e) {
			String errorMessage = createMessageText("activateDisableErrMessage", 
					userForUpdate, userForUpdate.getStatus());
			addMessageToFacesContext(createErrorFacesMessage(errorMessage));
			logger.error(errorMessage, e);

			userForUpdate.setStatus(userForUpdateStatusOld);
		}
	}

	public void possibleAssigneesSearch() {
		User.UserType userForUpdateType = userForUpdate.getType();
		List<Integer> selectedUser = Arrays.asList(userForUpdate.getId());
		possibleAssignees = userEJB
				.getUsersFindByTypeAndStatusExclude(userForUpdateType, 
						UserStatusType.ACTIVE, selectedUser);
	}

	public void discardUserOK() {
		User.UserType userForUpdateType = userForUpdate.getType();
		try {
			switch (userForUpdateType) {
			case ADMIN:
				userForUpdate = userEJB.discardAdmin((Admin)userForUpdate,
						(Admin)userAssignee);
				break;
			case REALTOR:
				userForUpdate = userEJB.discardRealtor((Realtor)userForUpdate,
						(Realtor)userAssignee);
				break;
			default:
				break;
			}
			
			String successMessage = createMessageText("discardSuccessMessage", 
					userForUpdate);
			addMessageToFacesContext(createInfoFacesMessage(successMessage));
			logger.debug(successMessage);

		} catch (Exception e) {
			String errorMessage = createMessageText("discardErrMessage", 
					userForUpdate);
			addMessageToFacesContext(createErrorFacesMessage(errorMessage));
			logger.error(errorMessage, e);
		}
	}

	public void changeUserStatusCancel() {
		usersList.stream()
			.filter(user -> user.getId() == userForUpdate.getId())
			.findFirst()
				.get()
				.setStatus(userForUpdateStatusOld);

		userForUpdate.setStatus(userForUpdateStatusOld);
		isDiscardUserButtonVisible = false;
		possibleAssignees = null;
		userAssignee = null;
	}

	public Boolean userAssigneeSelectedCheck(ValueChangeEvent event) {
		Object newValue = event.getNewValue();
		isDiscardUserButtonVisible = isNotNullAndNotEmpty(newValue);

		return isDiscardUserButtonVisible;
	}

	public void userDeleteTogglerClicked(ValueChangeEvent event){
		FacesContext context = FacesContext.getCurrentInstance();
		Integer currentRowIndex = context.getApplication()
				.evaluateExpressionGet(context, "#{currentRow}", Integer.class);
		boolean isSelectedCurrentUser = (boolean)event.getNewValue();

		usersToDeleteFromDBFlags.set(currentRowIndex, isSelectedCurrentUser);

		logger.trace("usersToDeleteFromDBFlags = {}", usersToDeleteFromDBFlags);
	}

	public void isAllUsersToDeleteToggler(ValueChangeEvent event){
		boolean isSelectedAllUsers = (boolean)event.getNewValue();

		for (int i = 0; i < usersList.size(); i++) {
			usersToDeleteFromDBFlags.set(i, isSelectedAllUsers);
		}

		logger.trace("usersToDeleteFromDBFlags = {}", usersToDeleteFromDBFlags);
	}

	public void extractUsersToDeleteFromDB() {
		usersToDeleteFromDB.clear();
		for (int i = 0; i < usersToDeleteFromDBFlags.size(); i++) {
			if (usersToDeleteFromDBFlags.get(i)) {
				usersToDeleteFromDB.add(usersList.get(i));
			}
		}
		logger.trace("usersToDeleteFromDB = {}", usersToDeleteFromDB);
	}

	public void deleteUsersPermanentlyOK() {
		try {
			userEJB.removeUsersFromDB(usersToDeleteFromDB);

			String successMessage = createMessageText("deleteUsersFromDBSuccessMessage", 
					usersToDeleteFromDB);
			addMessageToFacesContext(createInfoFacesMessage(successMessage));
			logger.debug(successMessage);

			usersList.removeAll(usersToDeleteFromDB);
			usersToDeleteFromDB.clear();

			usersToDeleteFromDBFlags.clear();
			for (int i = 0; i < usersList.size(); i++) {
				usersToDeleteFromDBFlags.add(i, false);
			}

		} catch (Exception e) {
			String errorMessage = createMessageText("deleteUsersFromDBErrorMsg");
			addMessageToFacesContext(createErrorFacesMessage(errorMessage));
			logger.error(errorMessage);
		}
	}

	public User getUserForUpdate() {
		return userForUpdate;
	}

	public UserStatusType getUserForUpdateStatusNew() {
		return userForUpdateStatusNew;
	}

	public UserStatusType getUserForUpdateStatusOld() {
		return userForUpdateStatusOld;
	}

	public List<User> getPossibleAssignees() {
		return possibleAssignees;
	}

	public User getUserAssignee() {
		return userAssignee;
	}

	public void setUserAssignee(User userAssignee) {
		this.userAssignee = userAssignee;
	}

	public Boolean getIsDiscardUserButtonVisible() {
		return isDiscardUserButtonVisible;
	}

	public List<User> getUsersToDeleteFromDB() {
		return usersToDeleteFromDB;
	}

	public boolean getIsDeleteUsersButtonVisible() {
		isDeleteUsersButtonVisible = false;
		for (Boolean flag : usersToDeleteFromDBFlags) {
			if (flag) {
				isDeleteUsersButtonVisible = true;
				break;
			}
		}
		return isDeleteUsersButtonVisible;
	}

	public String getUserStatusFilter() {
		return userStatusFilter;
	}

	public List<Boolean> getUsersToDeleteFromDBFlags() {
		return usersToDeleteFromDBFlags;
	}
	
	private void populateUsersListFromStatusFilter() {
		userStatusFilter = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("userstatus");
		if ( !(userStatusFilter == null) ) {
			try {
				switch (userStatusFilter) {
				case "notdiscarded":
					usersList = userEJB.getUsersFindByStatusNot(UserStatusType.DISCARDED);
					break;
				case "discarded":
					usersList = userEJB.getUsersFindByStatus(UserStatusType.DISCARDED);
					usersToDeleteFromDB = new ArrayList<>();
					usersToDeleteFromDBFlags = new ArrayList<>();
					for (int i = 0; i < usersList.size(); i++) {
						usersToDeleteFromDBFlags.add(i, false);
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				logger.error("Exception occured during getting users list the status = {}.", 
						userStatusFilter, e);
			}			
		} else {
			userStatusFilter = "";
		}
		logger.trace("A new usersList's been created with params: userstatus = {}.", 
				userStatusFilter);
	}
	
	private void populateUserStatusLabels() {
		userStatusLabels = Arrays.asList(User.UserStatusType.values());
	}
	
	private void populateUsersTableColumnVisibility() {
		usersTableColumnVisibility = Arrays.asList(true, true, true, true, true, true, true);
	}
	
	private boolean isNotNullAndNotEmpty(Object newValue) {
		return (newValue != null) 
				&& ( !"".equals(newValue));
	}
}
