package com.stolser.locale;

import java.util.Locale;

public class CustomLocale {
	
	private Locale locale;
	private String label;
	private String icon;
	private String description;

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
//-----------------------
	
	@Override
	public String toString(){
		return locale.getLanguage();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass())
			return false;
		CustomLocale other = (CustomLocale) obj;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		return true;
	}

}
