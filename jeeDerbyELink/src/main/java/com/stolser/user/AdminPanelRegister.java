package com.stolser.user;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import com.stolser.jpa.User;

/**
 * Session Bean implementation class AdminPanelRegister
 */
@Singleton
@LocalBean
public class AdminPanelRegister {

	private List<User> loggedInUsers;
	
    public AdminPanelRegister() {}
    
    @PostConstruct
    private void init() {
    	loggedInUsers = new ArrayList<>();
    }

	public List<User> getLoggedInUsers() {
		return loggedInUsers;
	}

}
