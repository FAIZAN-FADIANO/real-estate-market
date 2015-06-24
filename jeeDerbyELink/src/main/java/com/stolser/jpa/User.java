package com.stolser.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * User entity: An abstract entity, and the root of an inheritance hierarchy.
 * Has one of the types defined in the <code>UserType</code> enum type. Each
 * type determines access rights on the back-end and messages and UI elements on
 * the front-end.
 * */
@Entity
@NamedQueries({
		@NamedQuery(name = "User.findAll", query = "select u from User u"),
		@NamedQuery(name = "User.findByType", query = "select u from User u where u.type = :type"),
		@NamedQuery(name = "User.findByStatus",
				query = "select u from User u where u.status = :status"),
		@NamedQuery(name = "User.findByLogin",
				query = "select u from User u where u.login = :login") })
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIM_TYPE")
abstract public class User implements Serializable {
	private static final long serialVersionUID = 351L;
	
	@Id
	@Column(name = "USER_PK")
	@TableGenerator(name = "userIdGenerator", table = "SEQUENCE_STORAGE",
			pkColumnName = "SEQUENCE_NAME", pkColumnValue = "USERS.USER_PK",
			valueColumnName = "SEQUENCE_VALUE", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "userIdGenerator")
	private int id;
	@NotNull
	@Column(name = "USER_TYPE")
	@Enumerated(EnumType.STRING)
	private UserType type;
	@NotNull
	@Enumerated(EnumType.STRING)
	private UserStatusType status;
	/**
	 * <span style="color:red";>Value MUST be unique.</span> Will be used in a
	 * URL.
	 * */
	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9-]{5,15}$")
	private String login;
	@NotNull
	@Pattern(regexp = "^.{5,15}$")
	private String password;
	@NotNull
	@Pattern(regexp = "^[a-zA-Z-]{1,20}$")
	@Column(name = "FIRST_NAME")
	private String firstName;
	@NotNull
	@Pattern(regexp = "^[a-zA-Z-]{1,20}$")
	@Column(name = "LAST_NAME")
	private String lastName;
	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = "CREATION_DATE")
	private Date dateOfCreation;
	@Temporal(TemporalType.DATE)
	@Past
	private Date birthday;
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." + "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
			+ "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	private String email;
	private String photo;
	@Version
	private int version;
	/*--------- END of entity properties --------------*/

	

	/*--------- constructors --------------------------*/
	public User() {
	}

	public User(UserType type, UserStatusType status, String login, String password,
			String firstName, String lastName, Date dateOfCreation) {
		super();
		this.type = type;
		this.status = status;
		this.login = login;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfCreation = dateOfCreation;
	}

	/*--------- END of constructors -------------------*/

	public enum UserType {
		SUPER_ADMIN, ADMIN, REALTOR, REGISTERED_USER
	}

	public enum UserStatusType {
		ACTIVE, NOT_ACTIVE, DISCARDED;
	}

	/*-------- getters and setters ---------*/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public UserStatusType getStatus() {
		return status;
	}

	public void setStatus(UserStatusType status) {
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

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/*-------- END of getters and setters ---------*/

	@Override
	public String toString() {
		return lastName + " " + firstName + " (" + login + ")";
	}

}
