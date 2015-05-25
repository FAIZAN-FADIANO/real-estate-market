package com.stolser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.stolser.beans.FrontLocaleBean;

@ManagedBean(name = "customLocaleConverter")
@RequestScoped
public class CustomLocaleConverter implements Converter {
	
	@ManagedProperty(value = "#{frontLocale}")
	private FrontLocaleBean frontLocale;
	
	private List<CustomLocale> locales;

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		
		locales = frontLocale.getLocales();
		
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
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", 
                												"Not a valid customLocale."));
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
	
	private void addNewCustomMessage(String message) {
		FacesMessage newMessage = new FacesMessage(message);
		newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, newMessage);
	}

	public FrontLocaleBean getFrontLocale() {
		return frontLocale;
	}

	public void setFrontLocale(FrontLocaleBean frontLocale) {
		this.frontLocale = frontLocale;
	}

}
