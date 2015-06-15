package com.stolser.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

import com.stolser.ejb.UserFacadeEJB;
import com.stolser.jpa.User;

@ManagedBean(name = "userController")
@RequestScoped
public class UserController {
	 
//--------properties
	private User user;
		
	@EJB 
	private UserFacadeEJB userEJB;
	
	private List<User> usersList = new ArrayList<>();
	
//-------END of properties-------

//-------action controller methods----------
	public String addNewUser(User newUser) {
		user = newUser;
		try{
			userEJB.addNewUser(newUser);
		} catch(Exception e) {
			FacesMessage newMessage = new FacesMessage("A user " + user + "has NOT been created.");
			newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, newMessage);
		}
		
		FacesMessage newMessage = new FacesMessage("A new user " + user + "has been created.");
		newMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
		
		usersList = userEJB.getUsersFindAll();
		
		return displayUsers();		
	}
	
	public String displayUsers() {
		return "userListing.xhtml" + "?faces-redirect=true";
	}
	
//-------END of action controller methods------
/*-------placeholders for results data------------------------------------------------------- */   

	public List<User> getUsersList() {
		usersList = userEJB.getUsersFindAll();
		return usersList;
	}
	
/*-------END of placeholders for results data------------------------------------------------------- */    
/*---------------getters and setters----------------------------------------------- */ 
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserFacadeEJB getUserEJB() {
		return userEJB;
	}

	public void setUserEJB(UserFacadeEJB userEJB) {
		this.userEJB = userEJB;
	}

	public void setUsersList(List<User> usersList) {
		this.usersList = usersList;
	}
/*---------------END of getters and setters----------------------------------------------- */ 

	public UserController() {}

}
