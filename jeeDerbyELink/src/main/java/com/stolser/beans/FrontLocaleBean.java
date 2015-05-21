package com.stolser.beans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

@ManagedBean (name = "frontLocale")
@SessionScoped
public class FrontLocaleBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Map<String, Object> locales;
	private String currentLocale = "en";
	//private Locale myLocale;
	
	static{
		locales = new LinkedHashMap<String, Object>(); // country name, locale
		locales.put("English", Locale.ENGLISH);
		locales.put("\u0420\u0443\u0441\u0441\u043A\u0438\u0439", new Locale("ru"));
		locales.put("German", Locale.GERMAN);
		
	}
	 
	public void localeChanged(ValueChangeEvent e) {
		
		for (Map.Entry<String, Object> entry : locales.entrySet()) { 
			if(entry.getValue().toString().equals(currentLocale)){ 
				FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale)entry.getValue()); 
				
			}
		}
		
/*		String newLocaleValue = e.getNewValue().toString(); 
		for (Map.Entry<String, Object> entry : countries.entrySet()) { 
			if(entry.getValue().toString().equals(newLocaleValue)){ 
				FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale)entry.getValue()); 
				myLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			}
		}
*/	}
	
	public Map<String, Object> getLocales() {
		return locales;
	}


	public void setLocales(Map<String, Object> locales) {
		FrontLocaleBean.locales = locales;
	}


	public String getcurrentLocale() {
		return currentLocale;
	}


	public void setcurrentLocale(String locale) {
		this.currentLocale = locale;
	}


	/*public Locale getMyLocale() {
		return myLocale;
	}*/

	public FrontLocaleBean() {
		
	}

}
