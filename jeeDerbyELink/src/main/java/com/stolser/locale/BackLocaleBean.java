package com.stolser.locale;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.CustomLocale;
import com.stolser.PropertiesLoader;

@ManagedBean (name = "backLocale")
@SessionScoped
public class BackLocaleBean implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(BackLocaleBean.class);
	private static final long serialVersionUID = 1L;
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
	
	private List<CustomLocale> locales = new ArrayList<>();
	private CustomLocale currentLocale;
	
	@PostConstruct
    private void init() {
		logger.trace("FacesContext.getCurrentInstance() = " + FacesContext.getCurrentInstance());
		
		propSystemMap = propLoader.getPropSystemMap();
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		logger.trace("currentLocal = " + currentLocal);
		
		Properties currentProperties = propSystemMap.get(currentLocal);
		
		currentLocale = new CustomLocale(new Locale("en", "US")
				, "English"
				, "langIcons/en-icon.png"
				, currentProperties.getProperty("langDescriptionUS"));
		
		locales.add(currentLocale);
		
		locales.add(new CustomLocale(new Locale("ru", "RU")
				, "\u0420\u0443\u0441\u0441\u043A\u0438\u0439"
				, "langIcons/ru-icon.png"
				, currentProperties.getProperty("langDescriptionUS")));
		
		/*locales.add(new CustomLocale(new Locale("de", "DE"), "German", "langIcons/de-icon.png", 
				"The language of Goethe and Sigmund Freud."));*/
	}

	public BackLocaleBean() {}
	
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
