package com.stolser.locale;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("customLocaleConverter")
@RequestScoped
public class CustomLocaleConverter implements Converter, Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(CustomLocaleConverter.class);
	
	@Inject
	private FrontLocale frontLocale;
	
	private List<CustomLocale> locales;

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		locales = frontLocale.getLocales();
		
		if(isValueValid(value)) {
            try {
            	return getCurrentLocaleByValue(value);
            	
            } catch(IllegalArgumentException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                		"Conversion Error.", "Not a valid customLocale."));
            }
        }
        else {
            return null;
        }
    }

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return ((CustomLocale) object).getLangCode();
        }
        else {
            return null;
        }
    }   
	
/*	private void addNewCustomMessage(String message) {
		FacesMessage newMessage = new FacesMessage(message);
		newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
	}*/

	public FrontLocale getFrontLocale() {
		return frontLocale;
	}

	public void setFrontLocale(FrontLocale frontLocale) {
		this.frontLocale = frontLocale;
	}
	
	private boolean isValueValid(String value) {
		return (value != null) 
				&& (value.trim().length() == 2);
	}
	
	private CustomLocale getCurrentLocaleByValue(String value) {
		CustomLocale currentLocale = null;
    	for (CustomLocale customLocale : locales) {
			if (customLocale.getLangCode().equals(value)) {
				currentLocale = customLocale;
				break;
			}
		}
    	
    	if (currentLocale == null) {
    		logger.error("Cannot map value = {} to CustomLocale.", value);
			throw new IllegalArgumentException("Cannot map string value to CustomLocale.");
		}
	
    	return currentLocale;
	}


}
