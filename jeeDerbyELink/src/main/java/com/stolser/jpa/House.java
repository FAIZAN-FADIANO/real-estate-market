package com.stolser.jpa;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;
/**
 * House entity: a concrete entity that represents a house with optional plot around it.
 * */
@Entity
@NamedQueries({
	@NamedQuery(name="House.findAll", query="select h from House h"),
	@NamedQuery(name="House.findByStatus", 
		query="select h from House h where h.status = :status"),
	@NamedQuery(name="House.findByPurpose", 
		query="select h from House h where h.purpose = :purpose"),
	@NamedQuery(name="House.findByHomeAreaTotalRange", 
		query="select h from House h where h.homeAreaTotal BETWEEN :fromArea AND :upToArea"),
	@NamedQuery(name="House.findByPlotAreaRange", 
		query="select h from House h where h.plotArea BETWEEN :fromArea AND :upToArea"),
	@NamedQuery(name="House.findBySalePriceRange", 
		query="select h from House h where h.salePrice BETWEEN :fromPrice AND :upToPrice"),
	@NamedQuery(name="House.findByMonthlyRentRange", 
		query="select h from House h where h.monthlyRent BETWEEN :fromRent AND :upToRent"),
	@NamedQuery(name="House.findByYearOfConstruction", 
		query="select h from House h where h.yearOfConstruction = :year"),
	@NamedQuery(name="House.findByNumberOfBeds", 
		query="select h from House h where h.numberOfBeds = :number")	
})
@Table(name="HOUSES")
public class House extends EstateItem implements Serializable {
	private static final long serialVersionUID = 358L;
	
	@Column(name="GARAGE_ROOMINESS")
	@Min(value=0)	
	private int garageRoominess;
	@Column(name="PLOT_AREA")
	private double plotArea;
	@Column(name="POOL_AVAILABILITY")
	private boolean isTherePool;
	@Version
	private int version;
	
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public House() {}
/*--------- END of constructors -------------------*/

/*-------- getters and setters --------------------*/
	
	public int getGarageRoominess() {
		return garageRoominess;
	}

	public void setGarageRoominess(int garageRoominess) {
		this.garageRoominess = garageRoominess;
	}

	public double getPlotArea() {
		return plotArea;
	}

	public void setPlotArea(double plotArea) {
		this.plotArea = plotArea;
	}

	public boolean isTherePool() {
		return isTherePool;
	}

	public void setTherePool(boolean isTherePool) {
		this.isTherePool = isTherePool;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
/*-------- END of getters and setters -------------*/
}
