package com.stolser.controllers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.stolser.PropertiesLoader;
import com.stolser.ejb.UserFacadeEJB;
import com.stolser.jpa.Admin;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.User;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.slf4j.*;

@ManagedBean(name = "userController")
@ViewScoped
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	 
	@EJB 
	private UserFacadeEJB userEJB;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
/** For displaying users on the userListing.xhtml page*/
	private List<User> usersList;
/** Current selected user from the list on the userListing.xhtml page*/
	private User selectedUser;
/** Used for a stateful columns toggler on the userListing.xhtml page*/
	private List<Boolean> usersTableColumnVisibility;
	
//-------END of properties-------
	public UserController() {}
	
	@PostConstruct
	private void init() {
		propSystemMap = propLoader.getPropSystemMap();
		usersTableColumnVisibility = Arrays.asList(true, true, true, true, true, true, true);
	}

/*---------------getters and setters----------------------------------------------- */ 

	public UserFacadeEJB getUserEJB() {
		return userEJB;
	}

	public void setUserEJB(UserFacadeEJB userEJB) {
		this.userEJB = userEJB;
	}

	public List<User> getUsersList() {
	if (usersList == null) {
        try {
        	usersList = userEJB.getUsersFindAll();

        } catch (Exception e) {
            logger.debug("Excepton during getting users list.", e);
        }
	}
		return usersList;
	}

/*---------------END of getters and setters----------------------------------------------- */ 

	public User getSelectedUser() {
		return selectedUser;
	}
	
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}
	
	public List<Boolean> getUsersTableColumnVisibility() {
		return usersTableColumnVisibility;
	}
	
	public void onUserTableToggle(ToggleEvent e) {
		logger.trace("e.getData() = " + e.getData());
		usersTableColumnVisibility.set((Integer) e.getData(), 
				e.getVisibility() == Visibility.VISIBLE);
		logger.trace("usersTableColumnVisibility = " + usersTableColumnVisibility);
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
