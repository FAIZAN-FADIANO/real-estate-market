package com.stolser.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.stolser.jpa.Employee;

/**
 * Session Bean implementation class EmployeeEJB
 */
@Stateless
@LocalBean
public class EmployeeEJB {
	
	@PersistenceContext(unitName = "derby")
	private EntityManager entityManager;
	
    public List<Employee> findEmployees() {
    	TypedQuery<Employee> query = entityManager.createNamedQuery("findAllEmployees", Employee.class);
    	return query.getResultList();
    }
    
    public Employee addNew(Employee employee) {
        entityManager.persist(employee);
        return employee;
 }
    

}
