package com.stolser.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
    			throw new NonUniqueResultException("In the DB must be only one user " + 
    						"with type = SUPER_ADMIN, but there are " + foundUsersSize + 
    						" rows in the 'USERS' table with such type.");
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
			throw new NonUniqueResultException("The property User.login MUST be unique, but " + 
				"there are " + foundUsersSize + " rows in the 'USERS' table with login = " + login);
		}
    	return foundUsers;
    }
/**
 * The method performs several checks on uniqueness and persists a new user object
 * only if those checks are passed. 
 * */
    public User addNewUser(User userToAdd) {
    	User.UserType newUserType = userToAdd.getType();
    	if (newUserType == User.UserType.SUPER_ADMIN) {
			List<User> foundUsers = getUsersFindByType(newUserType);
			if (foundUsers.size() != 0) {
				throw new RuntimeException("An attempt to add a user with " +
					"type = SUPER_ADMIN and there is already a row in " + 
					"the DB with such type (violated restriction: only one user " +
						" with type = SUPER_ADMIN).");
			}
		}
    	
    	String newUserLogin = userToAdd.getLogin();
    	List<User> usersInDBWithSuchLogin = getUsersFindByLogin(newUserLogin);
    	if (usersInDBWithSuchLogin.size() != 0) {
			throw new RuntimeException("There is already a user in DB with such " + 
					"login (" + newUserLogin + ")");
		}
    	
    	switch (newUserType) {
		case SUPER_ADMIN:
		case ADMIN:
			if (!(userToAdd instanceof Admin)) {
				throw new RuntimeException("An attempt to add a new user with type " + 
						"SUPER_ADMIN or ADMIN that is not of class Admin.");
			}
			break;
		case REALTOR:
			if (!(userToAdd instanceof Realtor)) {
				throw new RuntimeException("An attempt to add a new user with type " + 
						"REALTOR that is not of class Realtor.");
			}
			break;
		case REGISTERED_USER:
			if (!(userToAdd instanceof RegisteredUser)) {
				throw new RuntimeException("An attempt to add a new user with type " + 
						"REGISTERED_USER that is not of class RegisteredUser.");
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
			throw new NullPointerException("Assignee admin cannot be null.");
		}
    	
    	User.UserType adminToDiscardType = adminToDiscard.getType();
    	if (adminToDiscardType == User.UserType.SUPER_ADMIN) {
			throw new RuntimeException("An attempt to discard a user with " + 
					"type SUPER_ADMIN that violates system restrictions.");
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
			throw new RuntimeException("An attempt to remove a user with status " + 
					userToRemoveStatus + ". Only users with status = DISCARDED can " + 
					"be removed from the DB.");
		}
    	
    	userToRemove = getUsersFindById(userToRemove.getId()).get(0);
    	entityManager.remove(userToRemove);
    }
    
    
    
    

}






