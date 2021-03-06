package com.stolser.locale;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("backLocaleConverter")
@RequestScoped
public class BackCustomLocaleConverter implements Converter, Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory
			.getLogger(BackCustomLocaleConverter.class);
	
	@Inject
	private BackLocale backLocale;
	private List<CustomLocale> locales;

	public BackCustomLocaleConverter() {}

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		locales = backLocale.getLocales();
		
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
        } else {
            return null; 
        }
	}

	public BackLocale getBackLocale() {
		return backLocale;
	}

	public void setBackLocale(BackLocale backLocale) {
		this.backLocale = backLocale;
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
