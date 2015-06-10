package com.stolser.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

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
	
	@OneToMany(mappedBy="manager")
	private List<EstateItem> managedRealEstate;
	@ElementCollection
	@CollectionTable(name ="REALTOR_PHONES")
	@Column(name="PHONE_NUMBERS")
	private List<String> phoneNumbers;
	private String skype;
	private String photo;
	
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public Realtor() {}
/*--------- END of constructors -------------------*/

/*-------- getters and setters --------------------*/
	public List<EstateItem> getManagedRealEstate() {
		return managedRealEstate;
	}

	public void setManagedRealEstate(List<EstateItem> managedRealEstate) {
		this.managedRealEstate = managedRealEstate;
	}

	public List<String> getPhoneNumbers() {
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
/*-------- END of getters and setters -------------*/


}
