package com.stolser.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "EMPLOYEE")
@NamedQuery(name = "findAllEmployees", query = "SELECT e FROM Employee e")
public class Employee implements Serializable {

	private static final long serialVersionUID = 343L;

	@Id
	@SequenceGenerator(name="SEQMYCLASSID", sequenceName="SEQMYCLASSID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQMYCLASSID")
	private long id;
	
	@NotNull
	private String firstName;
	
	@NotNull
	private String lastName;
	
	@NotNull
    //@Pattern(regexp = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message = "{invalid.phonenumber}")
    private String phone;
	
	@NotNull
    /*@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
                  	+ "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9]"
                  	+ "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9]"
                  	+ "(?:[a-z0-9-]*[a-z0-9])?", message = "{invalid.email}")*/
    private String email;
	
	public Employee() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
