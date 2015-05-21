package com.stolser;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.stolser.beans.FrontLocaleBean;

@FacesConverter("customLocaleConverter")
public class CustomLocaleConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() == 2) {
            try {
            	FrontLocaleBean frontLocale = (FrontLocaleBean) fc.getExternalContext().getApplicationMap().get("frontLocale");
        		CustomLocale currentLocale = new CustomLocale();
            	for (CustomLocale customLocale : frontLocale.getLocales()) {
        			if (customLocale.getLangCode().equals(value)) {
        				currentLocale = customLocale;
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

}
