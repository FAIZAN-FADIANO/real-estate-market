package com.stolser.ejb;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.stolser.PropertiesLoader;
import com.stolser.jpa.Admin;
import com.stolser.jpa.EstateItem;
import com.stolser.jpa.Post;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.RegisteredUser;
import com.stolser.jpa.User;

/**
 * Session Bean implementation class UserEJB
 */
@Stateless
public class UserFacadeEJB {
	
	@PersistenceContext(unitName = "derby")
	private EntityManager entityManager;
	
	@EJB
	private PostFacadeEJB postFacade;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
	
	@PostConstruct
	private void init() {
		propSystemMap = propLoader.getPropSystemMap();
		/*List<User> superAdminUsers = getUsersFindByType(User.UserType.SUPER_ADMIN);
		if (superAdminUsers.size() == 0) {
			User superAdmin = new Admin(User.UserType.SUPER_ADMIN, User.UserStatusType.ACTIVE, 
					"superadmin", "12345", "Oleg", "Stoliarov", new Date());
			persistEntity(superAdmin);
		}*/
	}
	
	public <T> T persistEntity(T entity) {
		entityManager.persist(entity);
		return entity;
	}
	
	public <T> T mergeEntity(T entity) {
		return entityManager.merge(entity);
	}
	  
    public List<User> getUsersFindAll() {
    	TypedQuery<User> query = entityManager
    			.createNamedQuery("User.findAll", User.class);
    	return query.getResultList();
    }
/**
 * Returns either an empty list or a list that contains only one found User instance.
 * */
    public List<User> getUsersFindById(Integer id) {
    	User foundUser = entityManager.find(User.class, id);
    	if (foundUser == null) {
			return Collections.emptyList();
		}
    	
    	List<User> foundUsers = new ArrayList<User>();
    	foundUsers.add(foundUser);
    	return foundUsers; 
    }
/**
 * In the DB can be maximum one user with type = User.UserType.SUPER_ADMIN.
 * Otherwise, will be thrown an <code>javax.persistence.NonUniqueResultException</code>.
 * */
    public List<User> getUsersFindByType(User.UserType type) {
    	TypedQuery<User> query = entityManager
    			.createNamedQuery("User.findByType", User.class)
    			.setParameter("type", type);
    	List<User> foundUsers = query.getResultList();
    	if (type == User.UserType.SUPER_ADMIN) {
    		int foundUsersSize = foundUsers.size();
        	if ( foundUsersSize > 1) {
    			throw new NonUniqueResultException(MessageFormat.format(
    					getSystemProperties().getProperty("nonUniqueSuperAdminErr"), foundUsersSize));
    		}
		}
    	return foundUsers;
    }
    
    public List<User> getUsersFindByStatus(User.UserStatusType status) {
    	TypedQuery<User> query = entityManager.
    			createNamedQuery("User.findByStatus", User.class).setParameter("status", status);
    	return query.getResultList();
    }
/**
 * The <code>login</code> property MUST be unique.<br/>
 * The method uses <code>getResultList()</code> and then performs a check how many 
 * elements have been retrieved:<br/>
 * - if 0 or 1 - returns an empty or a list with only one user;<br/>
 * - if more than 1 - throws an <code>javax.persistence.NonUniqueResultException</code>.
 * */
    public List<User> getUsersFindByLogin(String login) {
    	TypedQuery<User> query = entityManager.
    			createNamedQuery("User.findByLogin", User.class).setParameter("login", login);
    	List<User> foundUsers = query.getResultList();
    	int foundUsersSize = foundUsers.size();
    	if ( foundUsersSize > 1) {
			throw new NonUniqueResultException(MessageFormat.format(
					getSystemProperties().getProperty("nonUniqueLoginErr"), foundUsersSize, login));
		}
    	return foundUsers;
    }
/**
 * The method performs several checks on uniqueness and required properties 
 * and persists a new user object only if those checks are passed. 
 * */
    public User addNewUser(User userToAdd) {
    	if ((userToAdd.getType() == null) || (userToAdd.getStatus() == null) ||
    		(userToAdd.getLogin() == null) || (userToAdd.getPassword() == null) ||
    		(userToAdd.getFirstName() == null) || (userToAdd.getLastName() == null) ||
    		(userToAdd.getDateOfCreation() == null)) {
			throw new RuntimeException(getSystemProperties().getProperty("requiredPropsViolationErr"));
		}
    	
    	User.UserType newUserType = userToAdd.getType();
    	if (newUserType == User.UserType.SUPER_ADMIN) {
			List<User> foundUsers = getUsersFindByType(newUserType);
			if (foundUsers.size() != 0) {
				throw new RuntimeException(getSystemProperties().getProperty("addSuperAdminViolationErr"));
			}
		}
    	
    	String newUserLogin = userToAdd.getLogin();
    	List<User> usersInDBWithSuchLogin = getUsersFindByLogin(newUserLogin);
    	if (usersInDBWithSuchLogin.size() != 0) {
			throw new RuntimeException(MessageFormat.format(
					getSystemProperties().getProperty("addUserLoginViolationErr"), newUserLogin));
		}
    	
    	switch (newUserType) {
		case SUPER_ADMIN:
		case ADMIN:
			if (!(userToAdd instanceof Admin)) {
				throw new RuntimeException(MessageFormat.format(
						getSystemProperties().getProperty("addUserTypeViolationErr"), 
						User.UserType.SUPER_ADMIN + " or " + User.UserType.ADMIN, "Admin"));
			}
			break;
		case REALTOR:
			if (!(userToAdd instanceof Realtor)) {
				throw new RuntimeException(MessageFormat.format(
						getSystemProperties().getProperty("addUserTypeViolationErr"), 
						User.UserType.REALTOR, "Realtor"));
			}
			break;
		case REGISTERED_USER:
			if (!(userToAdd instanceof RegisteredUser)) {
				throw new RuntimeException(MessageFormat.format(
						getSystemProperties().getProperty("addUserTypeViolationErr"), 
						User.UserType.REGISTERED_USER, "RegisteredUser"));
			}
			break;
		default:
			assert false;
			break;
		}
    	
    	persistEntity(userToAdd);
    	return userToAdd;
    }
/**
 * After successful completion of this method the user has status = 
 * User.UserStatusType.DISCARDED and is put into the Recycle Bin on
 * the back-end. Only such users can be removed from the DB.
 * */
    public Admin discardAdmin(Admin adminToDiscard, Admin adminAssignee) {
    	//adminToDiscard = (Admin) getUsersFindById(adminToDiscard.getId()).get(0);
    	
    	if (adminAssignee == null) {
			throw new NullPointerException(getSystemProperties().getProperty("discardAdminNullErr"));
		}
    	
    	User.UserType adminToDiscardType = adminToDiscard.getType();
    	if (adminToDiscardType == User.UserType.SUPER_ADMIN) {
			throw new RuntimeException(getSystemProperties().getProperty("discardSuperAdminViolationErr"));
		}
    	
    	List<Post> adminToDiscardPosts = postFacade.getPostsFindByAuthor(adminToDiscard);
    	adminToDiscardPosts.forEach(post -> post.setAuthor(adminAssignee));
    	adminToDiscard.setStatus(User.UserStatusType.DISCARDED);
    	
    	return adminToDiscard;
    }
/**
 * After successful completion of this method the user has status = 
 * User.UserStatusType.DISCARDED and is put into the Recycle Bin on
 * the back-end. Only such users can be removed from the DB.
 * */  
    public Realtor discardRealtor(Realtor realtorToDiscard, Realtor realtorAssignee) {
    	//realtorToDiscard = (Realtor) getUsersFindById(realtorToDiscard.getId()).get(0);
    	//realtorAssignee = (Realtor) getUsersFindById(realtorAssignee.getId()).get(0);
    	    	
    	List<EstateItem> managedEstateItems = realtorToDiscard.getManagedEstateItems();
    	for (EstateItem estateItem : managedEstateItems) {
			realtorAssignee.addManagedEstateItem(estateItem);
		}
    	realtorToDiscard.setManagedEstateItems(null);
    	realtorToDiscard.setStatus(User.UserStatusType.DISCARDED);
    	
    	return realtorToDiscard;
    }
/**
 * After successful completion of this method the user has status = 
 * User.UserStatusType.DISCARDED and is put into the Recycle Bin on
 * the back-end. Only such users can be removed from the DB.
 * */   
    public RegisteredUser discardRegisteredUser(RegisteredUser regUserToDiscard) {
    	
    	regUserToDiscard.setFavoriteEstateItems(null);
    	regUserToDiscard.setStatus(User.UserStatusType.DISCARDED);
    	
    	return regUserToDiscard;
    }
/**
 * Only users with status = User.UserStatusType.DISCARDED can be removed from the DB.
 * */
    public void removeUser(User userToRemove) {
    	User.UserStatusType userToRemoveStatus = userToRemove.getStatus(); 
    	if (userToRemoveStatus != User.UserStatusType.DISCARDED) {
			throw new RuntimeException(MessageFormat.format(
					getSystemProperties().getProperty("removeNotDiscardedUserErr"), userToRemoveStatus));
		}
    	
    	userToRemove = getUsersFindById(userToRemove.getId()).get(0);
    	entityManager.remove(userToRemove);
    }
/**
 * Returns appropriate Properties object for current local on the fron-end
 * */
    private Properties getSystemProperties() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		Properties currentProperties = propSystemMap.get(currentLocal);
		return currentProperties;
	}

}






