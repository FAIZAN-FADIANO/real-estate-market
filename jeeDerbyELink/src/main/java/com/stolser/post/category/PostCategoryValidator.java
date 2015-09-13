package com.stolser.post.category;

import static com.stolser.MessageFromProperties.*;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.PostCategory;

@Named("postCategoryValidator")
@SessionScoped
public class PostCategoryValidator implements Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(PostCategoryValidator.class);
	@Inject
	private PostCategoryFacade categoryFacade;
	
	public PostCategoryValidator() {}
	
	public void systemNameValidator(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String enteredSystemName = value.toString();

		checkSystemNameForMatchingPattern(enteredSystemName);
		checkSystemNameForUniqueness(enteredSystemName);
	}
	
	
	
	
	
	
	
	
	

	private void checkSystemNameForMatchingPattern(String enteredSystemName) {
		final String SYSTEM_NAME_PATTERN = "^[a-zA-Z]{3,30}$";
	    Pattern pattern = Pattern.compile(SYSTEM_NAME_PATTERN);
	    Matcher matcher = pattern.matcher(enteredSystemName);
	    
		if( ! matcher.matches() ) {
			throw new ValidatorException(createErrorFacesMessage(
					createMessageText("catSysNameValidatorMessage")));
		}
		
	}
	
	private void checkSystemNameForUniqueness(String enteredSystemName) {
		List<PostCategory> categoriesInDB = 
				categoryFacade.getPostCategoriesFindBySystemName(enteredSystemName);
		if (categoriesInDB.size() != 0) {	
			throw new ValidatorException(createErrorFacesMessage(
					createMessageText("catSysNameExistValidatorMessage")));
		}
		
	}


}













