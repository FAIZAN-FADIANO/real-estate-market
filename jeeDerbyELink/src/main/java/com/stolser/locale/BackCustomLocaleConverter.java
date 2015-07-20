package com.stolser.locale;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name = "backLocaleConverter")
@RequestScoped
public class BackCustomLocaleConverter implements Converter {
	private static final Logger logger = LoggerFactory.getLogger(BackCustomLocaleConverter.class);
	
	@ManagedProperty(value = "#{backLocale}")
	private BackLocale backLocale;
	
	private List<CustomLocale> locales;

	public BackCustomLocaleConverter() {}

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		
		locales = backLocale.getLocales();
		if(value != null && value.trim().length() == 2) {
            try {	
            	CustomLocale currentLocale = new CustomLocale();
            	for (CustomLocale customLocale : locales) {
        			if (customLocale.getLangCode().equals(value)) {
        				currentLocale = customLocale;
        				break;
        			}
        		}
     	
            	return currentLocale;
            	
            } catch(NumberFormatException e) {
            	logger.error("Conversion Error. Not a valid customLocale.", e);
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR
                		, "Conversion Error.", "Not a valid customLocale."));
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

}
