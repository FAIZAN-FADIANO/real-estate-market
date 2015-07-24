package com.stolser.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import javax.faces.context.FacesContext;
import javax.persistence.EntityExistsException;
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
import com.stolser.jpa.User.UserStatusType;
import com.stolser.jpa.User.UserType;
import com.stolser.post.PostFacade;

import org.slf4j.*;
/**
 * Session Bean implementation class UserFacade
 */
@Stateless
public class UserFacade {
	static private final Logger logger = LoggerFactory.getLogger(UserFacade.class);
	
	@PersistenceContext(unitName = "derby")
	private EntityManager entityManager;
	
	@EJB
	private PostFacade postFacade;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
	
	@PostConstruct
	private void init() {
		propSystemMap = propLoader.getPropSystemMap();
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

    	checkSuperAdminUniqueness(foundUsers, type);
    	
    	return foundUsers;
    }
    
    public List<User> getUsersFindByStatus(User.UserStatusType status) {
    	TypedQuery<User> query = entityManager.
    			createNamedQuery("User.findByStatus", User.class).setParameter("status", status);
    	return query.getResultList();
    }
    
    public List<User> getUsersFindByStatusNot(User.UserStatusType status) {
    	TypedQuery<User> query = entityManager.
    			createNamedQuery("User.findByStatusNot", User.class).setParameter("status", status);
    	return query.getResultList();
    }
    
    public List<User> getUsersFindByTypeAndStatus(UserType type, UserStatusType status) {
    	TypedQuery<User> query = entityManager
    			.createNamedQuery("User.findByTypeAndStatus", User.class)
    			.setParameter("type", type).setParameter("status", status);
    	List<User> foundUsers = query.getResultList();
    	
    	checkSuperAdminUniqueness(foundUsers, type);
    	
    	return foundUsers;
    }
    
    public List<User> getUsersFindByTypeAndStatusExclude(UserType type, 
    						UserStatusType status,
    						List<Integer> excludedUsers) {

    	TypedQuery<User> query = entityManager
    			.createNamedQuery("User.findByTypeAndStatusExclude", User.class)
    			.setParameter("type", type).setParameter("status", status)
    			.setParameter("excludedUsers", excludedUsers);
    	List<User> foundUsers = query.getResultList();
    	
    	checkSuperAdminUniqueness(foundUsers, type);
    	
    	return foundUsers;
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
 * Tries to persist a new user. Throw an exception if some checks are not passed. 
 * */
    public User addNewUser(User userToAdd) {
    	userToAdd = systemRestrictionCheck(userToAdd);
    	persistEntity(userToAdd);

    	return userToAdd;
    }
    
/**
 * Tries to update (merge) a user passed as a parameter. 
 * Throws an exception if:
 * <ul>
 * <li>there is no user in the DB with such id;</li>
 * <li>some checks are not passed;</li>
 * </ul> 
 * */
    public User updateUserInDB(User userToUpdate) {
    	List<User> usersInDB = getUsersFindById(userToUpdate.getId());
    	if (usersInDB.size() == 0) {
			throw new RuntimeException(getSystemProperties()
					.getProperty("noUserInDBWithID"));
		}
    	
    	userToUpdate = systemRestrictionCheck(userToUpdate);
    	
    	return mergeEntity(userToUpdate);
    }
    
/**
 * Refreshes user object passed as a parameter by the data from the DB.
 * Throws an exception if there is no user in DB with such id.
 * */
    public User refreshUserFromDB(User userToRefresh) {
    	List<User> usersInDB = getUsersFindById(userToRefresh.getId());
    	if (usersInDB.size() == 0) {
			throw new RuntimeException(getSystemProperties()
					.getProperty("noUserInDBWithID"));
		}
    	
    	User userFromDB = usersInDB.get(0);
    	return userFromDB;
    }
/**
 * After successful completion of this method the user has status = 
 * User.UserStatusType.DISCARDED and is put into the Recycle Bin on
 * the back-end. Only such users can be removed from the DB.
 * */
    public Admin discardAdmin(Admin adminToDiscard, Admin adminAssignee) {
    	
    	checkIfNull(adminAssignee);
    	   	
    	User.UserType adminToDiscardType = adminToDiscard.getType();
    	if (adminToDiscardType == User.UserType.SUPER_ADMIN) {
    		String errorMsg = getSystemProperties().getProperty("discardSuperAdminViolationErr");
    		logger.error(errorMsg);
			throw new RuntimeException(errorMsg);
		}
    	
/*    	List<Post> adminToDiscardPosts = postFacade.getPostsFindByAuthor(adminToDiscard);
    	adminToDiscardPosts.forEach(post -> post.setAuthor(adminAssignee));*/
    	adminToDiscard.setStatus(User.UserStatusType.DISCARDED);
    	
    	try {
    		adminToDiscard = (Admin)updateUserInDB(adminToDiscard);
    		logger.trace("User {} has been discarded.", adminToDiscard);
    		
		} catch (Exception e) {
			logger.error("An exception occurred during discarding a user ({})."
					, adminToDiscard, e);
		}
    	
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
/*    	List<EstateItem> managedEstateItems = realtorToDiscard.getManagedEstateItems();
    	for (EstateItem estateItem: managedEstateItems) {
			realtorAssignee.addManagedEstateItem(estateItem);
		}*/
    	
    	realtorToDiscard.setManagedEstateItems(null);
    	realtorToDiscard.setStatus(User.UserStatusType.DISCARDED);
    	    	
    	try {
    		realtorToDiscard = (Realtor)updateUserInDB(realtorToDiscard);
    		logger.trace("User {} has been discarded.", realtorToDiscard);
    		
		} catch (Exception e) {
			logger.error("An exception occurred during discarding a user ({}).", realtorToDiscard, e);
		}
    	
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
    	
    	try {
    		updateUserInDB(regUserToDiscard);
    		logger.trace("User {} has been discarded.", regUserToDiscard);
		} catch (Exception e) {
			logger.error("An exception occurred during discarding a user ({}).", regUserToDiscard, e);
		}
    	
    	return regUserToDiscard;
    }
    
    public void removeUsersFromDB(List<User> usersToRemove) {
    	
    	for (Iterator<User> iterator = usersToRemove.iterator(); iterator.hasNext();) {
			User currentUser = (User) iterator.next();
			removeUser(currentUser);
		}
    	
    }
/**
 * Only users with status = User.UserStatusType.DISCARDED can be removed from the DB.
 * */
    private void removeUser(User userToRemove) {
    	
    	checkIfStatusIsDiscarded(userToRemove);
    	userToRemove = getUsersFindById(userToRemove.getId()).get(0);
    	
    	try {
    		entityManager.remove(userToRemove);
    		
		} catch (RuntimeException e) {
			logger.error("An exception occured during removing a user ({}).", userToRemove, e);
			
			throw new RuntimeException("An exception occured during removing a user.", e);
		}
    }
    
/**
 * The method performs several checks on uniqueness and required properties.
 * Throws an Exception if some restrictions are violated or
 * returns the same object.
 * */
    private User systemRestrictionCheck(User userToCheck) {
    	
    	checkRequiredPropertyForNullity(userToCheck);
    	checkSuperAdminUniqueness(userToCheck);
    	checkLoginUniqueness(userToCheck);
    	checkIfUserTypeValid(userToCheck);

    	return userToCheck;
    }
   
	private <T> T persistEntity(T entity) {
		try {
			entityManager.persist(entity);
			return entity;
			
		} catch (Exception eee) {
			throw new RuntimeException(getSystemProperties()
					.getProperty("failureDuringPersistanceMessage"), eee);
		}
	}
	
	private <T> T mergeEntity(T entity) {
		try {
			logger.trace("Before merging...(entity.hashcode = {})", entity.hashCode());
			return entityManager.merge(entity);
			
		} catch (Exception eee) {
			String errorMsg = getSystemProperties()
					.getProperty("failureDuringMergingMessage");
			logger.error(errorMsg, eee);
			
			throw new RuntimeException(errorMsg, eee);
		}
	}

	private <T> T refreshEntity(T entity) {
		try {
			entityManager.refresh(entity);
			return entity;
			
		} catch (Exception eee) {
			throw new RuntimeException(getSystemProperties()
					.getProperty("failureDuringRefreshingMessage"), eee);
		}
	}
	
	private boolean checkSuperAdminUniqueness(List<User> foundUsers, UserType type) {
    	if (type == User.UserType.SUPER_ADMIN) {
    		int foundUsersSize = foundUsers.size();
    		if ( foundUsersSize > 1) {
    			throw new NonUniqueResultException(MessageFormat.format(
    					getSystemProperties().getProperty("nonUniqueSuperAdminErr"), foundUsersSize));
    		}
    	}
    	
    	return true;
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
    
    private void checkIfNull(User assignee) {
    	if (assignee == null) {
    		String errorMsg = getSystemProperties().getProperty("discardAdminNullErr");
    		logger.error(errorMsg);
			throw new NullPointerException(errorMsg);
		}
    }

    private void checkIfStatusIsDiscarded(User userToRemove) {
    	User.UserStatusType userToRemoveStatus = userToRemove.getStatus(); 
    	if (userToRemoveStatus != User.UserStatusType.DISCARDED) {
			String errorMsg = MessageFormat.format(getSystemProperties()
					.getProperty("removeNotDiscardedUserErr"), userToRemoveStatus);
			logger.error(errorMsg);
			
    		throw new RuntimeException(errorMsg);
		}
    }
    
    private void checkRequiredPropertyForNullity(User userToCheck) {
    	boolean isAnyRequiredPropertyIsNull = (userToCheck.getType() == null) 
    			|| (userToCheck.getStatus() == null)
    			|| (userToCheck.getLogin() == null) 
    			|| (userToCheck.getPassword() == null) 
        		|| (userToCheck.getFirstName() == null) 
        		|| (userToCheck.getLastName() == null)
        		|| (userToCheck.getDateOfCreation() == null);
    	if (isAnyRequiredPropertyIsNull) {
			throw new RuntimeException(getSystemProperties()
					.getProperty("requiredPropsViolationErr"));
    	}

    }
    
    private void checkSuperAdminUniqueness(User userToCheck) {
    	User.UserType newUserType = userToCheck.getType();
    	
    	if (newUserType == User.UserType.SUPER_ADMIN) {
			List<User> foundUsers = getUsersFindByType(newUserType);	
			
			if (foundUsers.size() != 0) {
				/*In the DB there is a user with type SUPER_ADMIN. If it has 
				 * the same id as the id of the userToCheck than OK.
				 * If these ids are different then throw an exception.*/
				int IDofUserToCheck = userToCheck.getId();
    			int IDofUserInDB = foundUsers.get(0).getId();
				if (IDofUserToCheck != IDofUserInDB) {
					throw new RuntimeException(getSystemProperties()
							.getProperty("addSuperAdminViolationErr"));
				}
			}
		}
    }
    
    private void checkLoginUniqueness(User userToCheck) {
    	String LoginOfUserToCheck = userToCheck.getLogin();
    	List<User> usersInDBWithSuchLogin = getUsersFindByLogin(LoginOfUserToCheck);
    	if (usersInDBWithSuchLogin.size() != 0) {
    		/*In the DB there is a user with such login. If it has 
			 * the same id as the id of the userToCheck than OK.
			 * If these ids are different then throw an exception.*/
			int IDofUserToCheck = userToCheck.getId();
			int IDofUserInDB = usersInDBWithSuchLogin.get(0).getId();
			if (IDofUserToCheck != IDofUserInDB) {
				throw new RuntimeException(MessageFormat.format(getSystemProperties()
						.getProperty("addUserLoginViolationErr"), LoginOfUserToCheck));
			}
    	}
    }
    
    private void checkIfUserTypeValid(User userToCheck) {
    	User.UserType newUserType = userToCheck.getType();
    	switch (newUserType) {
		case SUPER_ADMIN:
		case ADMIN:
			if (!(userToCheck instanceof Admin)) {
				throw new RuntimeException(MessageFormat.format(
						getSystemProperties().getProperty("addUserTypeViolationErr"), 
						User.UserType.SUPER_ADMIN + " or " + User.UserType.ADMIN, "Admin"));
			}
			break;
		case REALTOR:
			if (!(userToCheck instanceof Realtor)) {
				throw new RuntimeException(MessageFormat.format(
						getSystemProperties().getProperty("addUserTypeViolationErr"), 
						User.UserType.REALTOR, "Realtor"));
			}
			break;
		case REGISTERED_USER:
			if (!(userToCheck instanceof RegisteredUser)) {
				throw new RuntimeException(MessageFormat.format(
						getSystemProperties().getProperty("addUserTypeViolationErr"), 
						User.UserType.REGISTERED_USER, "RegisteredUser"));
			}
			break;
		default:
			throw new RuntimeException("A new user type is invalid.");
		}
    }
    
}






