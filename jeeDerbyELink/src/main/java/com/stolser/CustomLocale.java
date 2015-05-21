package com.stolser;

import java.util.Locale;

public class CustomLocale {
	
	private Locale locale;
	private String label;
	private String icon;
	private String description;
	
//constructors
	public CustomLocale() {	}
	
	public CustomLocale(Locale locale, String label, String icon,
			String description) {
		super();
		this.locale = locale;
		this.label = label;
		this.icon = icon;
		this.description = description;
	}

	public String getLangCode() {
		return locale.getLanguage();
	}
	
	public String getCountry() {
		return locale.getCountry();
	}
	
	
// getters for fields
	public Locale getLocale() {
		return locale;
	}

	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}

	public String getDescription() {
		return description;
	}



}
