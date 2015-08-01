package com.stolser.user;

import static com.stolser.MessageFromProperties.*;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.User;

@ManagedBean(name = "userConverter")
@ViewScoped
public class UserConverter implements Converter {
	static private final Logger logger = LoggerFactory.getLogger(UserConverter.class);
	
	@ManagedProperty(value = "#{usersListing}")
	private UsersListing usersListing;
	private List<User> usersList;
	
	@PostConstruct
	private void init() {
		usersList = usersListing.getUsersList();
	}

	public UserConverter() {}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, 
			String value) {

		if(isValueValid(value)) {
            try {	
            	User selectedAssignee = usersList.stream()
            			.filter(user -> value.equals(((User)user).getLogin()))
            			.findFirst().get();
            	
            	logger.trace("selectedAssingee = {}.", selectedAssignee);
         	
            	return selectedAssignee;
            	
            } catch(Exception e) {
            	String errorMsg = "Conversion Error. Not a valid login.";
            	logger.error(errorMsg, e);
            	
                throw new ConverterException(createErrorFacesMessage(errorMsg));
            }
        } else {
            return null;
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if(isValueValid(object)) {
			return ((User) object).getLogin();
        } else { 
            return null;
        }
	}

	public UsersListing getUsersListing() {
		return usersListing;
	}

	public void setUsersListing(UsersListing usersListing) {
		this.usersListing = usersListing;
	}
	
	private boolean isValueValid(Object object) {
		return (object != null) 
				&& ( ! "".equals(object));
	}
}
