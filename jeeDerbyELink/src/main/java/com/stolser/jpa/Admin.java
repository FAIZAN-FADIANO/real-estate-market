package com.stolser.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Admin entity: a concrete entity. This entity can have a type:
 * <ul>
 * <li>User.UserType.SUPER_ADMIN - cannot be deleted and created from the Admin Panel.
 * So, in the system can be only one user with the type = SUPER_ADMIN.<br/>
 * Only this user can create/delete other users.;</li>
 * <li>User.UserType.ADMIN - access to all possible functionality on 
 * the back-end excluding manipulating users.<br/> 
 * On the front-end see all types of messages (including errors).</li>
 * </ul>
 * 
 * */
@Entity
@Table(name="ADMINS")
public class Admin extends User implements PostAuthor, Serializable {
	private static final long serialVersionUID = 352L;
	
	public Admin() {}

	public Admin(UserType type, UserStatusType status, String login,
			String password, String firstName, String lastName,
			Date dateOfCreation) {
		super(type, status, login, password, firstName, lastName, dateOfCreation);
	}

}







