package com.stolser.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.stolser.jpa.User;

/**
 * Session Bean implementation class UserEJB
 */
@Stateless
@LocalBean
public class UserEJB {
	
	@PersistenceContext(unitName = "derby")
	private EntityManager entityManager;
	
    public List<User> findAllUsers() {
    	TypedQuery<User> query = entityManager.createNamedQuery("findAllUsers", User.class);
    	return query.getResultList();
    }
    
    public void addNewUser(User user) {
        entityManager.persist(user);
    }
    

}

