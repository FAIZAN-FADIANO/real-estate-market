package com.stolser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class PropertiesLoader {
	static private final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

	private Map<String, Properties> propSystemMap;
	 
    @PostConstruct
    private void init() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        propSystemMap = new HashMap<>();
                
        /* Loading all files for resource bundle system.properties*/
        String basicPathSystem = "stolser/i18n/general/system/system";
        Properties propSystemEn = new Properties();
        Properties propSystemRu = new Properties();
        Properties propSystemDe = new Properties();
        
        try {
        	propSystemEn.load(classLoader.getResourceAsStream(basicPathSystem + ".properties"));
        	propSystemRu.load(classLoader.getResourceAsStream(basicPathSystem + "_ru.properties"));
        	propSystemDe.load(classLoader.getResourceAsStream(basicPathSystem + "_de.properties"));
        	propSystemMap.put("en", propSystemEn);
        	propSystemMap.put("ru", propSystemRu);
        	propSystemMap.put("de", propSystemDe);
        	
        } catch (IOException e) {
        	logger.error("Exception arose during loading system/system.properties.", e);
        }
    }
 
    public PropertiesLoader() {}

	public Map<String, Properties> getPropSystemMap() {
		return propSystemMap;
	}
}
