package com.stolser.post.category;

import static com.stolser.MessageFromProperties.*;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.PostCategory;
import com.stolser.user.UserConverter;

@ManagedBean(name = "postCategoryConverter")
@ViewScoped
public class PostCategoryConverter implements Converter {
	static private final Logger logger = LoggerFactory.getLogger(PostCategoryConverter.class);
	
	@ManagedProperty(value = "#{postCategoryCreator}")
	private PostCategoryCreator postCategoryCreator;
	private List<PostCategory> postCategories;
	
	@PostConstruct
	private void init() {
		postCategories = postCategoryCreator.getAllPostCategories();
	}
	
	public PostCategoryConverter() {}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, 
			String value) {

		if(isValueValid(value)) {
            try {	
            	PostCategory selectedCategory = null;
            	for (PostCategory postCategory : postCategories) {
					if (value.equals(postCategory.getSystemName())) {
						selectedCategory = postCategory;
						break;
					}
				}
         	
            	return selectedCategory;
            	
            } catch(Exception e) {
            	String errorMsg = "Conversion Error. Not a valid post category.";
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
			return ((PostCategory) object).getSystemName();
        } else { 
            return null;
        }
	}
	
	public PostCategoryCreator getPostCategoryCreator() {
		return postCategoryCreator;
	}

	public void setPostCategoryCreator(PostCategoryCreator postCategoryCreator) {
		this.postCategoryCreator = postCategoryCreator;
	}
	
	private boolean isValueValid(Object object) {
		return (object != null) 
				&& ( ! "".equals(object));
	}

}
