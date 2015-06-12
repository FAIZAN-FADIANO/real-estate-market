package com.stolser.jpa;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Utility entity: a concrete entity that is used by the PrivateEstateItem class.
 * Represents an extra feature of a concrete real estate, 
 * for example: central air-conditioning system, intercom, Internet,
 * space TV, gas supplying.
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Utility.findAll", query="select u from Utility u")
})
@Table(name="UTILITIES")
public class Utility implements Serializable {
	private static final long serialVersionUID = 347L;
	
	@Id
	@Column(name="UTILITY_PK")
	@TableGenerator(name="utilityIdGenerator",
					table="SEQUENCE_STORAGE",
					pkColumnName="SEQUENCE_NAME",
					pkColumnValue="UTILITIES.UTILITY_PK",
					valueColumnName="SEQUENCE_VALUE",
					initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="utilityIdGenerator")
	private int id;
	@NotNull
	@Column(name="TITLE")
	private String title;
	@Column(name="DESCRIPTION")
	private String description;
	@Version
	private int version;
/*--------- END of entity properties --------------*/
	
// constructors		
	public Utility() {
	}
/*--------- END of constructors --------------*/
	
/*-------- getters and setters ---------*/
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}	
/*-------- END of getters and setters ---------*/
}
















