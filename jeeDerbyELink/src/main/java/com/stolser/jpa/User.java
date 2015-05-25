package com.stolser.jpa;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "USER")
@SequenceGenerator(name="SEQMYCLASSID", sequenceName="SEQMYCLASSID")
@NamedQuery(name = "findAllUsers", query = "SELECT e FROM User e")
public class User implements Serializable {
	
	private static final long serialVersionUID = 347L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQMYCLASSID")
	private long id;
	@NotNull
	private UserType type;
	@NotNull
	private UserStatus status;
	@NotNull
	private String login;
	@NotNull
	private String password;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	private String email;

//constructors
	public User() {}
//-----------------
	
	enum UserType {
		SUPER_ADMIN, ADMIN, REALTOR, CLIENT
	}
	
	enum UserStatus {
		REGISTERED, ACTIVE, NOT_ACTIVE
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}



















