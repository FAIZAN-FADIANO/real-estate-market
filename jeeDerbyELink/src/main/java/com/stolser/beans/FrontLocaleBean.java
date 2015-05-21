package com.stolser.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import com.stolser.CustomLocale;

@ManagedBean (name = "frontLocale")
@SessionScoped
public class FrontLocaleBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<CustomLocale> locales = new ArrayList<>();
	private CustomLocale currentLocale;
	
	
/*	private static Map<String, Object> locales;
	private String currentLocale = "en";*/
	
	@PostConstruct
    private void init() {
		currentLocale = new CustomLocale(Locale.ENGLISH, "English", "langIcons/en-icon.png", 
				"The language of William Shakespeare and Bruce Willis.");
		
		locales.add(currentLocale);
		locales.add(new CustomLocale(new Locale("ru"), "\u0420\u0443\u0441\u0441\u043A\u0438\u0439", 
				"langIcons/ru-icon.png", "The language of Dmitri Mendeleev and Sergey Shnurov."));
		locales.add(new CustomLocale(Locale.GERMAN, "German", "langIcons/de-icon.png", 
				"The language of Goethe and Sigmund Freud."));
	}
	
/*	static{
		locales = new LinkedHashMap<String, Object>(); // country name, locale
		locales.put("English", Locale.ENGLISH);
		locales.put("\u0420\u0443\u0441\u0441\u043A\u0438\u0439", new Locale("ru"));
		locales.put("German", Locale.GERMAN);
		
	}*/
	
//constructors	
	public FrontLocaleBean() {}
	
	
//---------------------
	 
	public void localeChanged(ValueChangeEvent e) {
		
		FacesContext.getCurrentInstance().getViewRoot().setLocale(currentLocale.getLocale());
		
/*		for (Map.Entry<String, Object> entry : locales.entrySet()) { 
			if(entry.getValue().toString().equals(currentLocale)){ 
				FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale)entry.getValue()); 
				
			}
		}*/
		
/*		String newLocaleValue = e.getNewValue().toString(); 
		for (Map.Entry<String, Object> entry : countries.entrySet()) { 
			if(entry.getValue().toString().equals(newLocaleValue)){ 
				FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale)entry.getValue()); 
				myLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			}
		}
*/	}

	public CustomLocale getCurrentLocale() {
		return currentLocale;
	}

	public void setCurrentLocale(CustomLocale currentLocale) {
		this.currentLocale = currentLocale;
	}

	public List<CustomLocale> getLocales() {
		return locales;
	}
	




}
