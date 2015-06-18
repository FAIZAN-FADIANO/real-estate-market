package com.stolser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PropertiesLoader.<br/>
 * Each bundle resource is loaded as a Map whose keys are 
 * language codes (en/ru/de) and values are objects of 
 * the Properties type with loaded info from appropriate
 * .properties files. 
 */
@Singleton
@Startup
public class PropertiesLoader {

	private Map<String, Properties> propLoggingMap;
	private Map<String, Properties> propUserMap;
	 
    @PostConstruct
    public void init() {
        propLoggingMap = new HashMap<>();
        propUserMap = new HashMap<>();
        
        ClassLoader classLoader = this.getClass().getClassLoader();
         
        // Loading all files for resource bundle logging.properties
        String basicPathLogging = "stolser/i18n/general/logging";
        Properties propLoggingEn = new Properties();
        Properties propLoggingRu = new Properties();
        Properties propLoggingDe = new Properties();
        
        try {
        	propLoggingEn.load(classLoader.getResourceAsStream(basicPathLogging + ".properties"));
        	propLoggingRu.load(classLoader.getResourceAsStream(basicPathLogging + "_ru.properties"));
        	propLoggingDe.load(classLoader.getResourceAsStream(basicPathLogging + "_de.properties"));
        	propLoggingMap.put("en", propLoggingEn);
        	propLoggingMap.put("ru", propLoggingRu);
        	propLoggingMap.put("de", propLoggingDe);
        	
		} catch (IOException e) {
			System.out.println("Exception arose during loading logging.properties.");
		}
        
        // Loading all files for resource bundle entity/user.properties
        String basicPathUser = "stolser/i18n/general/entity/user";
        Properties propUserEn = new Properties();
        Properties propUserRu = new Properties();
        Properties propUserDe = new Properties();
        
        try {
        	propUserEn.load(classLoader.getResourceAsStream(basicPathUser + ".properties"));
        	propUserRu.load(classLoader.getResourceAsStream(basicPathUser + "_ru.properties"));
        	propUserDe.load(classLoader.getResourceAsStream(basicPathUser + "_de.properties"));
        	propUserMap.put("en", propUserEn);
        	propUserMap.put("ru", propUserRu);
        	propUserMap.put("de", propUserDe);
        	
        } catch (IOException e) {
        	System.out.println("Exception arose during loading entity/user.properties.");
        }
         
    }
 
    public PropertiesLoader() {}

	public Map<String, Properties> getPropLoggingMap() {
		return propLoggingMap;
	}

	public Map<String, Properties> getPropUserMap() {
		return propUserMap;
	}




}
