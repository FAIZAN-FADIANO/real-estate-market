package com.stolser.jpa;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;
/**
 * Apartment entity: a concrete entity that represents an apartment, flat.
 * */
@Entity
@NamedQueries({
	@NamedQuery(name="Apartment.findAll", query="select a from Apartment a"),
	@NamedQuery(name="Apartment.findByStatus", 
		query="select a from Apartment a where a.status = :status"),
	@NamedQuery(name="Apartment.findByPurpose", 
		query="select a from Apartment a where a.purpose = :purpose"),
	@NamedQuery(name="Apartment.findByHomeAreaTotalRange", 
		query="select a from Apartment a where a.homeAreaTotal BETWEEN :fromArea AND :upToArea"),
	@NamedQuery(name="Apartment.findBySalePriceRange", 
		query="select a from Apartment a where a.salePrice BETWEEN :fromPrice AND :upToPrice"),
	@NamedQuery(name="Apartment.findByMonthlyRentRange", 
		query="select a from Apartment a where a.monthlyRent BETWEEN :fromRent AND :upToRent"),
	@NamedQuery(name="Apartment.findByYearOfConstruction", 
		query="select a from Apartment a where a.yearOfConstruction = :year"),
	@NamedQuery(name="Apartment.findByNumberOfBeds", 
		query="select a from Apartment a where a.numberOfBeds = :number")	
})
@Table(name="APARTMENTS")
public class Apartment extends EstateItem implements Serializable {
	private static final long serialVersionUID = 357L;

	private int floorNumber;
	@Min(value=0)	
	private int numberOfParkingPlaces;
	private String householdServicesDescription;
	@Version
	private int version;
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public Apartment() {}
/*--------- END of constructors -------------------*/

/*-------- getters and setters --------------------*/
	public int getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	public int getNumberOfParkingPlaces() {
		return numberOfParkingPlaces;
	}

	public void setNumberOfParkingPlaces(int numberOfParkingPlaces) {
		this.numberOfParkingPlaces = numberOfParkingPlaces;
	}

	public String getHouseholdServicesDescription() {
		return householdServicesDescription;
	}

	public void setHouseholdServicesDescription(String householdServicesDescription) {
		this.householdServicesDescription = householdServicesDescription;
	}
	
/*-------- END of getters and setters -------------*/


}
