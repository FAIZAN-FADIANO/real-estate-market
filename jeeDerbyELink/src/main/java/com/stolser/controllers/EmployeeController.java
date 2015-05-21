package com.stolser.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.*;

import com.stolser.ejb.EmployeeEJB;
import com.stolser.jpa.Employee;

@ManagedBean(name = "employeeController")
@RequestScoped
public class EmployeeController {
	
	//property "employee"
    private Employee employee = new Employee();
    
	@EJB
    private EmployeeEJB employeeEJB;
		
    private List<Employee> employeeList = new ArrayList<>();
    
    private Date date;
/*------------action controller methods-------------------------------------------------- */   
    
    public String addNewEmployee() {
        employee = employeeEJB.addNew(employee);
        employeeList = employeeEJB.findEmployees();
        return "listEmployee.xhtml" + "?faces-redirect=true";
    }
    
    public String viewEmployee(){
        return "listEmployee.xhtml" + "?faces-redirect=true";
    }
  
/*-------placeholders for results data------------------------------------------------------- */    
   
    public List<Employee> getEmployeeList() {
        employeeList = employeeEJB.findEmployees();
        return employeeList;
    }
 

/*---------------getters and setters----------------------------------------------- */  
	public EmployeeEJB getEmployeeEJB() {
		return employeeEJB;
	}

	public void setEmployeeEJB(EmployeeEJB employeeEJB) {
		this.employeeEJB = employeeEJB;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}