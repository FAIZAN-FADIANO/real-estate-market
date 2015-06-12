package com.stolser.jpa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.stolser.jpa.User.UserStatusType;
import com.stolser.jpa.User.UserType;

/**
 * EstateItem entity: <em>abstract root entity</em> of the real estate 
 * inheritance hierarchy.<br/>
 * Can be of two types: private and business real estate. 
 * Each of them has its own subtypes and properties. 
 * Each estate item can have one of two or both status: for rent or for sale. 
 * */
@Entity
@NamedQueries({
	@NamedQuery(name="EstateItem.findAll", query="select e from EstateItem e"),
	@NamedQuery(name="EstateItem.findByStatus", 
		query="select e from EstateItem e where e.status = :status"),
	@NamedQuery(name="EstateItem.findByPurpose", 
		query="select e from EstateItem e where e.purpose = :purpose"),
	@NamedQuery(name="EstateItem.findByHomeAreaTotalRange", 
		query="select e from EstateItem e where e.homeAreaTotal BETWEEN :fromArea AND :upToArea"),
	@NamedQuery(name="EstateItem.findBySalePriceRange", 
		query="select e from EstateItem e where e.salePrice BETWEEN :fromPrice AND :upToPrice"),
	@NamedQuery(name="EstateItem.findByMonthlyRentRange", 
		query="select e from EstateItem e where e.monthlyRent BETWEEN :fromRent AND :upToRent"),
	@NamedQuery(name="EstateItem.findByYearOfConstruction", 
		query="select e from EstateItem e where e.yearOfConstruction = :year"),
	@NamedQuery(name="EstateItem.findByNumberOfBeds", 
		query="select e from EstateItem e where e.numberOfBeds = :number")	
})
@Table(name="ESTATE_ITEMS")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="DISCRIM_TYPE")
abstract public class EstateItem implements Serializable {
	private static final long serialVersionUID = 356L;

	@Id
	@Column(name="ESTATE_PK")
	@TableGenerator(name="estateItemIdGenerator",
					table="SEQUENCE_STORAGE",
					pkColumnName="SEQUENCE_NAME",
					pkColumnValue="ESTATE_ITEMS.ESTATE_PK",
					valueColumnName="SEQUENCE_VALUE",
					initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="estateItemIdGenerator")
	private int id;
	@NotNull
	@Column(name = "ESTATE_TYPE")
	@Enumerated(EnumType.STRING)
	private EstateType type;
	@NotNull
	@Column(name = "ESTATE_STATUS")
	@Enumerated(EnumType.STRING)
	private EstateStatusType status;
	@NotNull
	@ManyToOne
	@JoinColumn(name="MANAGER")
	private Realtor manager;
	@NotNull
	@Enumerated(EnumType.STRING)
	private EstatePurposeType purpose;
	@Embedded
    @AttributeOverrides({
    	@AttributeOverride(name="country", column=@Column(name="ADDR_COUNTRY")),
    	@AttributeOverride(name="state", column=@Column(name="ADDR_STATE")),
    	@AttributeOverride(name="city", column=@Column(name="ADDR_CITY")),
    	@AttributeOverride(name="street", column=@Column(name="ADDR_STREET")),
    	@AttributeOverride(name="building", column=@Column(name="ADDR_BUILDING")),
    	@AttributeOverride(name="apartment", column=@Column(name="ADDR_APT")),
    	@AttributeOverride(name="zipCode", column=@Column(name="ADDR_ZIP")),
    	@AttributeOverride(name="latitude", column=@Column(name="ADDR_LAT")),
    	@AttributeOverride(name="longitude", column=@Column(name="ADDR_LONG")),
    })
	private Address address;
	@Temporal(TemporalType.DATE)
	@Past
	@Column(name="DATE_CREATION")
	private Date dateOfCreation;
	@NotNull
	@Min(value=1)
	@Column(name="NUMBER_STOREYS")
	private int numberOfStoreys;
	@NotNull
	@Column(name="TOTAL_AREA")
	private double homeAreaTotal;
	@ManyToMany
    @JoinTable( name = "ESTATE_UTILITIES",
            	joinColumns = @JoinColumn(name = "ESTATE_ITEM_ID"),
            	inverseJoinColumns = @JoinColumn(name = "UTILITY_ID"))
	private List<Utility> extraUtilities;
	@Column(name="SALE_PRICE")
	private double salePrice;
	@Column(name="MONTH_RENT")
	private double monthlyRent;
	@Null
	@Column(name="HOUR_RENT")
	private double hourlyRent;
	@Null
	@Column(name="DAY_RENT")
	private double dailyRent;
	@Column(name="ESTATE_DESCRIPTION")
	private String estateDescription;
	@Column(name="OWNER_DESCRIPTION")
	private String ownerDescription;
	@ElementCollection
	@CollectionTable(name ="ESTATE_IMAGES")
	@Column(name="IMAGES")
	private List<String> images;
	@Column(name="CONSTRUCTION_YEAR")
	@Temporal(TemporalType.DATE)
	@Past
	private Date yearOfConstruction;
	/**
	 * Includes all types of rooms: beds, livingrooms, libraries, studies etc.
	 * */
	@Column(name="ROOMS_NUMBER")
	@Min(value=1)
	private int numberOfRooms;
	@Column(name="BEDS_NUMBER")
	@Min(value=1)
	private int numberOfBeds;
	@Column(name="BATHROOMS_NUMBER")
	@Min(value=1)
	private int numberOfBathrooms;
	@Version
	private int version;
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public EstateItem() {}
/*--------- END of constructors -------------------*/
	
	public enum EstateType {
		APARTMENT, HOUSE;
	}
	
	public enum EstateStatusType {
		ACTIVE, NOT_ACTIVE, DISCARDED;
	}
	
	public enum EstatePurposeType {
		RENT, SALE, BOTH;
	}
	
	public enum HomeAreaUnitType {
		FOOT, SQUARE_METRE;
	}
	
	public enum PlotAreaUnitType {
		FOOT, ACRE, SQUARE_METRE, HUNDRED_SQ_METRE;
	}
	
	public enum PriceCurrencyType {
		DOLLAR, POUND, HRYVNA;
	}
	
/*-------- getters and setters --------------------*/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EstateType getType() {
		return type;
	}

	public void setType(EstateType type) {
		this.type = type;
	}

	public EstateStatusType getStatus() {
		return status;
	}

	public void setStatus(EstateStatusType status) {
		this.status = status;
	}

	public Realtor getManager() {
		return manager;
	}

	public void setManager(Realtor manager) {
		this.manager = manager;
	}

	public EstatePurposeType getPurpose() {
		return purpose;
	}

	public void setPurpose(EstatePurposeType purpose) {
		this.purpose = purpose;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public int getNumberOfStoreys() {
		return numberOfStoreys;
	}

	public void setNumberOfStoreys(int numberOfStoreys) {
		this.numberOfStoreys = numberOfStoreys;
	}

	public double getHomeAreaTotal() {
		return homeAreaTotal;
	}

	public void setHomeAreaTotal(double homeAreaTotal) {
		this.homeAreaTotal = homeAreaTotal;
	}

	public List<Utility> getExtraUtilities() {
		return extraUtilities;
	}

	public void setExtraUtilities(List<Utility> extraUtilities) {
		this.extraUtilities = extraUtilities;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public double getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(double monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public double getHourlyRent() {
		return hourlyRent;
	}

	public void setHourlyRent(double hourlyRent) {
		this.hourlyRent = hourlyRent;
	}

	public double getDailyRent() {
		return dailyRent;
	}

	public void setDailyRent(double dailyRent) {
		this.dailyRent = dailyRent;
	}

	public String getEstateDescription() {
		return estateDescription;
	}

	public void setEstateDescription(String estateDescription) {
		this.estateDescription = estateDescription;
	}

	public String getOwnerDescription() {
		return ownerDescription;
	}

	public void setOwnerDescription(String ownerDescription) {
		this.ownerDescription = ownerDescription;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public Date getYearOfConstruction() {
		return yearOfConstruction;
	}

	public void setYearOfConstruction(Date yearOfConstruction) {
		this.yearOfConstruction = yearOfConstruction;
	}

	public int getNumberOfRooms() {
		return numberOfRooms;
	}

	public void setNumberOfRooms(int numberOfRooms) {
		this.numberOfRooms = numberOfRooms;
	}

	public int getNumberOfBeds() {
		return numberOfBeds;
	}

	public void setNumberOfBeds(int numberOfBeds) {
		this.numberOfBeds = numberOfBeds;
	}

	public int getNumberOfBathrooms() {
		return numberOfBathrooms;
	}

	public void setNumberOfBathrooms(int numberOfBathrooms) {
		this.numberOfBathrooms = numberOfBathrooms;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
/*-------- END of getters and setters -------------*/


}
