package com.stolser.jpa;

import java.io.Serializable;
import javax.persistence.*;
/**
 * Embeddable class - represents some general address, and can be used 
 * by different classes.<br/> 
 * The EstateItem class embeds it.
 * */
@Embeddable
public class Address implements Serializable {
	private static final long serialVersionUID = 355L;
	
	private String country;
	private String state;
	private String city;
	private String street;
	private String building;
	private String apartment;
	@Column(name="ZIP_CODE")
	private String zipCode;
	private String latitude;
	private String longitude;
	
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public Address() {}
/*--------- END of constructors -------------------*/

/*-------- getters and setters --------------------*/

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getApartment() {
		return apartment;
	}

	public void setApartment(String apartment) {
		this.apartment = apartment;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
/*-------- END of getters and setters -------------*/

	@Override
	public String toString() {
		return building + " " + street + ", " + "#" + building + ", " +
				city + ", "	+ state + ", " + zipCode + ", " + country;
	}
}
