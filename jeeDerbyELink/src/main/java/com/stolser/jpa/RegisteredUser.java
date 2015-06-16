package com.stolser.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

/**
 * RegisteredUser entity: a concrete entity - a registered user of the front-end, 
 * doesn't have an access to the back-end.<br/>
 * Have access to Private Panel with some extra functionality.
 * */
@Entity
@Table(name="REGISTERED_USERS")
public class RegisteredUser extends User implements Serializable {
	private static final long serialVersionUID = 354L;
	
	@Column(name="HOME_AREA_UNIT")
	@Enumerated(EnumType.STRING)
	private EstateItem.HomeAreaUnitType homeAreaUnit;
	@Column(name="PLOT_AREA_UNIT")
	@Enumerated(EnumType.STRING)
	private EstateItem.PlotAreaUnitType plotAreaUnit;
	@Column(name="PRICE_CURRENCY")
	@Enumerated(EnumType.STRING)
	private EstateItem.PriceCurrencyType priceCurrency;
	/**
	 * Adding to and removing from this list performed by the class's clients
	 * through public methods <code>addFavoriteEstateItem</code> and 
	 * <code>removeFavoriteEstateItem</code>.
	 * */
	@ManyToMany
    @JoinTable( name = "REGIST_USERS_ESTATE",
            	joinColumns = @JoinColumn(name = "REGISTERED_USER_ID"),
            	inverseJoinColumns = @JoinColumn(name = "ESTATE_ITEM_ID"))
	private List<EstateItem> favoriteEstateItems;
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public RegisteredUser() {}
	
	public RegisteredUser(UserType type, UserStatusType status, String login,
			String password, String firstName, String lastName,
			Date dateOfCreation) {
		super(type, status, login, password, firstName, lastName, dateOfCreation);
	}
/*--------- END of constructors -------------------*/

/*-------- getters and setters --------------------*/
	
	public EstateItem.HomeAreaUnitType getHomeAreaUnit() {
		return homeAreaUnit;
	}

	public void setHomeAreaUnit(EstateItem.HomeAreaUnitType homeAreaUnit) {
		this.homeAreaUnit = homeAreaUnit;
	}

	public EstateItem.PlotAreaUnitType getPlotAreaUnit() {
		return plotAreaUnit;
	}

	public void setPlotAreaUnit(EstateItem.PlotAreaUnitType plotAreaUnit) {
		this.plotAreaUnit = plotAreaUnit;
	}

	public EstateItem.PriceCurrencyType getPriceCurrency() {
		return priceCurrency;
	}

	public void setPriceCurrency(EstateItem.PriceCurrencyType priceCurrency) {
		this.priceCurrency = priceCurrency;
	}

	public List<EstateItem> getFavoriteEstateItems() {
		if (favoriteEstateItems == null) {
			favoriteEstateItems = new ArrayList<EstateItem>();
		}
		return favoriteEstateItems;
	}

	public void setFavoriteEstateItems(List<EstateItem> favoriteEstateItems) {
		this.favoriteEstateItems = favoriteEstateItems;
	}
	
/*-------- END of getters and setters -------------*/

	public EstateItem addFavoriteEstateItem(EstateItem newEstateItem) {
		getFavoriteEstateItems().add(newEstateItem);
		return newEstateItem;
	}
	
	public EstateItem removeFavoriteEstateItem(EstateItem oldEstateItem) {
		getFavoriteEstateItems().remove(oldEstateItem);
		return oldEstateItem;
	}

}
