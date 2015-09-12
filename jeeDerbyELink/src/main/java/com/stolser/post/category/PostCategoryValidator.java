package com.stolser.post.category;

import static com.stolser.MessageFromProperties.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.PostCategory;
import com.stolser.jpa.User;
import com.stolser.user.UserValidators;

@ManagedBean(name = "postCategoryValidator")
@SessionScoped
public class PostCategoryValidator {
	static private final Logger logger = LoggerFactory.getLogger(PostCategoryValidator.class);
	@EJB
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













