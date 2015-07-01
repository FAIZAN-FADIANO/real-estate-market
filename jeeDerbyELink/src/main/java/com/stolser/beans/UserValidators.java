package com.stolser.beans;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.stolser.PropertiesLoader;

@ManagedBean(name = "userValidators")
@SessionScoped
public class UserValidators {
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;

	public UserValidators() {}
	
	@PostConstruct
	private void init() {
		propSystemMap = propLoader.getPropSystemMap();
	}
	
	public void loginValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String LOGIN_PATTERN = "^[a-zA-Z0-9-]{5,15}$";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(LOGIN_PATTERN);
	    
		String enteredLogin = value.toString();
		matcher = pattern.matcher(enteredLogin);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage("value = " + value + " " + getSystemProperties()
					.getProperty("loginValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
/**
 * Validates user password on matching the pattern.
 * */
	public void passwordValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String PASSWORD_PATTERN = "^.{5,15}$";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(PASSWORD_PATTERN);
	    
		String enteredPassword = value.toString();
		matcher = pattern.matcher(enteredPassword);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage("value = " + value + " " + getSystemProperties()
					.getProperty("passwordValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
	public void firstLastNameValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String NAME_PATTERN = "^[a-zA-Z-]{1,20}$";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(NAME_PATTERN);
	    
		String enteredName = value.toString();
		matcher = pattern.matcher(enteredName);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("firstLastNameValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
	public void emailValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String EMAIL_PATTERN = "([a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." 
			+ "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
			+ "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)|(^$)";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    
		String enteredEmail = value.toString();
		matcher = pattern.matcher(enteredEmail);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("emailValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
	public void skypeValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		final String SKYPE_PATTERN = "(^[a-zA-Z][a-zA-Z0-9]{5,31}$)|(^$)";
	    Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(SKYPE_PATTERN);
	    
		String enteredSkype = value.toString();
		matcher = pattern.matcher(enteredSkype);
		
		if( !matcher.matches() ) {
			FacesMessage newMessage = new FacesMessage(getSystemProperties()
					.getProperty("skypeValidatorMessage"));
				newMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				
			throw new ValidatorException(newMessage);
		}
	}
	
/**
 * Returns appropriate Properties object for current local on the front-end
 * */
	private Properties getSystemProperties() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		Properties currentProperties = propSystemMap.get(currentLocal);
		return currentProperties;
	}

}