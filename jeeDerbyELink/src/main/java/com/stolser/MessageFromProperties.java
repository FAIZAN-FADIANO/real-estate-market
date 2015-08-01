package com.stolser;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageFromProperties {
	static private final Logger logger = LoggerFactory.getLogger(MessageFromProperties.class);
	static private PropertiesLoader propertyLoader;
	static private Map<String, Properties> propSystemMap;
	
	static {
		propertyLoader = PropertiesLoader.getInstance();
		propSystemMap = propertyLoader.getPropSystemMap();
	}
	
    public MessageFromProperties() {}
    
    private static Properties getSystemProperties() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale().toString();
		Properties currentProperties = propSystemMap.get(currentLocal);

		return currentProperties;
	}
    
    public static String createMessageText(String propertyName, Object... arguments) {
    	String newMessage = MessageFormat.format(getSystemProperties()
				.getProperty(propertyName), arguments);

    	return newMessage;
    }
    
    public static FacesMessage createInfoFacesMessage(String summary) {
    	FacesMessage newFacesMessage = new FacesMessage(summary);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
    	
    	return newFacesMessage;
    }
    
    public static FacesMessage createWarnFacesMessage(String summary) {
    	FacesMessage newFacesMessage = new FacesMessage(summary);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
    	
    	return newFacesMessage;
    }
    
    public static FacesMessage createErrorFacesMessage(String summary) {
    	FacesMessage newFacesMessage = new FacesMessage(summary);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
    	
    	return newFacesMessage;
    }
    
    public static FacesMessage createFatalFacesMessage(String summary) {
    	FacesMessage newFacesMessage = new FacesMessage(summary);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_FATAL);
    	
    	return newFacesMessage;
    }

    public static FacesMessage createInfoFacesMessage(String summary, String detail) {
    	FacesMessage newFacesMessage = new FacesMessage(summary, detail);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
    	
    	return newFacesMessage;
    }
    
    public static FacesMessage createWarnFacesMessage(String summary, String detail) {
    	FacesMessage newFacesMessage = new FacesMessage(summary, detail);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
    	
    	return newFacesMessage;
    }
    
    public static FacesMessage createErrorFacesMessage(String summary, String detail) {
    	FacesMessage newFacesMessage = new FacesMessage(summary, detail);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
    	
    	return newFacesMessage;
    }
    
    public static FacesMessage createFatalFacesMessage(String summary, String detail) {
    	FacesMessage newFacesMessage = new FacesMessage(summary, detail);
    	newFacesMessage.setSeverity(FacesMessage.SEVERITY_FATAL);
    	
    	return newFacesMessage;
    }
    
    public static void addMessageToFacesContext(FacesMessage message) {
    	FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public static void addMessageToFacesContext(String clientId, FacesMessage message) {
    	FacesContext.getCurrentInstance().addMessage(clientId, message);
    }
}
