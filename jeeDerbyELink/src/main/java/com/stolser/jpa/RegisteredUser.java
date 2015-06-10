package com.stolser.jpa;

import java.io.Serializable;
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
	
	@Enumerated(EnumType.STRING)
	private EstateItem.HomeAreaUnitType homeAreaUnit;
	@Enumerated(EnumType.STRING)
	private EstateItem.PlotAreaUnitType plotAreaUnit;
	@Enumerated(EnumType.STRING)
	private EstateItem.PriceCurrencyType priceCurrency;
	@ManyToMany
    @JoinTable( name = "REGIST_USERS_ESTATE",
            	joinColumns = @JoinColumn(name = "REGISTERED_USER_ID"),
            	inverseJoinColumns = @JoinColumn(name = "ESTATE_ITEM_ID"))
	private List<EstateItem> favoriteList;
/*--------- END of entity properties --------------*/

/*--------- constructors --------------------------*/	
	public RegisteredUser() {}
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

	public List<EstateItem> getFavoriteList() {
		return favoriteList;
	}

	public void setFavoriteList(List<EstateItem> favoriteList) {
		this.favoriteList = favoriteList;
	}
	
/*-------- END of getters and setters -------------*/


}
