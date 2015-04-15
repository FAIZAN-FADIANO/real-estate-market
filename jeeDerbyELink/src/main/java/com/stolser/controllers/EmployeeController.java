package com.stolser.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.*;
/*import javax.inject.Named;*/

import com.stolser.ejb.EmployeeEJB;
import com.stolser.jpa.Employee;

@ManagedBean(name = "employeeController")
@RequestScoped
public class EmployeeController {
	
	@EJB
    private EmployeeEJB employeeEJB;
    private Employee employee = new Employee();
    private List<Employee> employeeList = new ArrayList<>();
    
	public EmployeeController() {}
	
	public List<Employee> getEmployeeList() {
        employeeList = employeeEJB.findEmployees();
        return employeeList;
    }
 
   public String viewEmployee(){
        return "listEmployee.xhtml" + "?faces-redirect=true";
    }
   
    public String addNewEmployee() {
        employee = employeeEJB.addNew(employee);
        employeeList = employeeEJB.findEmployees();
        return "listEmployee.xhtml" + "?faces-redirect=true";
    }

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

}