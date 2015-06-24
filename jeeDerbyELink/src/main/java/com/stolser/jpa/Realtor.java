package com.stolser.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import com.stolser.jpa.User.UserStatusType;
import com.stolser.jpa.User.UserType;

/**
 * Realtor entity: a concrete entity; represent a real estate agent that
 * manages estate items(of type EstateItem).<br/>
 * It has a type = User.UserType.REALTOR.
 * Has an access to the back-end. Can have assigned estate items (by himself or 
 * by the admin). Can perform adding/editing/deleting assigned to him estate items.
 * */
@Entity
@Table(name="REALTORS")
public class Realtor extends User implements Serializable {
	private static final long serialVersionUID = 353L;
	
	/**
	 * A list of EstateItem elements that represent all real estate 
	 * items which are managed by this realtor. Each real estate 
	 * item can have only one manager who MUST be assigned.<br/>
	 * Adding to and removing from this list items 
	 * performed by the class's clients through public methods 
	 * <code>addManagedEstate()</code> and <code>removeManagedEstate()</code>.
	 * */
	@OneToMany(mappedBy="manager", cascade={CascadeType.PERSIST})
	private List<EstateItem> managedEstateItems;
	/**
	 * A list of phone numbers of this realtor.<br/>
	 * Adding to and removing from this list performed by the class's clients
	 * through public methods <code>addPhoneNumber</code> and 
	 * <code>removePhoneNumber</code>.
	 * */
	@ElementCollection
	@CollectionTable(name ="REALTOR_PHONES")
	@Column(name="PHONE_NUMBERS")
	private List<String> phoneNumbers;
	@Pattern(regexp="^[a-zA-Z][a-zA-Z0-9]{5,31}$")
	private String skype;
	
	
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public Realtor() {}

	public Realtor(UserType type, UserStatusType status, String login,
				String password, String firstName, String lastName,
				Date dateOfCreation) {
		super(type, status, login, password, firstName, lastName, dateOfCreation);
		
	}
/*--------- END of constructors -------------------*/

	/*-------- getters and setters --------------------*/
	public List<EstateItem> getManagedEstateItems() {
		if (managedEstateItems == null) {
			managedEstateItems = new ArrayList<EstateItem>();
		}
		return managedEstateItems;
	}

	public void setManagedEstateItems(List<EstateItem> managedEstateItems) {
		this.managedEstateItems = managedEstateItems;
	}

	public List<String> getPhoneNumbers() {
		if (phoneNumbers == null) {
			phoneNumbers = new ArrayList<String>();
		}
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	
	
/*-------- END of getters and setters -------------*/
	
	public EstateItem addManagedEstateItem(EstateItem newEstateItem) {
		getManagedEstateItems().add(newEstateItem);
		newEstateItem.setManager(this);           // now newEstateItem object references to this Realtor object
		return newEstateItem;
	}
	
	public EstateItem removeManagedEstateItem(EstateItem oldEstateItem) {
		if (this.equals(oldEstateItem.getManager())) {
			throw new IllegalArgumentException("Before removing " + oldEstateItem + 
					" from the list of managed properties of " + this + 
					" this real estate MUST be reassigned to another realtor!");
		}
		getManagedEstateItems().remove(oldEstateItem);
		return oldEstateItem;
	}
	
	public String addPhoneNumber(String newPhoneNumber) {
		getPhoneNumbers().add(newPhoneNumber);
		return newPhoneNumber;
	}
	
	public String removePhoneNumber(String oldPhoneNumber) {
		getPhoneNumbers().remove(oldPhoneNumber);
		return oldPhoneNumber;
	}


}
