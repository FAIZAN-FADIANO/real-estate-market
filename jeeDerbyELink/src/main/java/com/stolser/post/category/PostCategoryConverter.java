package com.stolser.post.category;

import static com.stolser.MessageFromProperties.*;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.PostCategory;

@Named("postCategoryConverter")
@ViewScoped
public class PostCategoryConverter implements Converter, Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(PostCategoryConverter.class);
	
	@Inject
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
