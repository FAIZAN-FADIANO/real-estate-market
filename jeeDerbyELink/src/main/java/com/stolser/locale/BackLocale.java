package com.stolser.locale;

import static com.stolser.MessageFromProperties.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("backLocale")
@SessionScoped
public class BackLocale implements Serializable {
	static private final Logger logger = LoggerFactory.getLogger(BackLocale.class);
	static private final long serialVersionUID = 1L;
	
	private List<CustomLocale> locales = new ArrayList<>();
	private CustomLocale currentLocale;
	
	@PostConstruct
    private void init() {
		currentLocale = new CustomLocale(new Locale("en", "US"),
				"English",
				"langIcons/en-icon.png",
				createMessageText("langDescriptionUS"));
		locales.add(currentLocale);
		locales.add(new CustomLocale(new Locale("ru", "RU"),
				"\u0420\u0443\u0441\u0441\u043A\u0438\u0439",
				"langIcons/ru-icon.png",
				createMessageText("langDescriptionRU")));
		
		/*locales.add(new CustomLocale(new Locale("de", "DE"), "German", "langIcons/de-icon.png", 
				createMessageText("langDescriptionDE")));*/
	}

	public BackLocale() {}
	
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
