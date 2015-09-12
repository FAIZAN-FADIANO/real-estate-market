package com.stolser.post.category;

import static com.stolser.MessageFromProperties.addMessageToFacesContext;
import static com.stolser.MessageFromProperties.createErrorFacesMessage;
import static com.stolser.MessageFromProperties.createInfoFacesMessage;
import static com.stolser.MessageFromProperties.createMessageText;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.Post;
import com.stolser.jpa.PostCategory;
import com.stolser.jpa.PostCategory.PostCategoryStatusType;
import com.stolser.post.PostFacade;

@ManagedBean(name = "postCategoryEditor")
@ViewScoped
public class PostCategoryEditor implements Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(PostCategoryEditor.class);
	
	@EJB
	private PostCategoryFacade categoryFacade;
	@EJB
	private PostFacade postFacade;
	private PostCategory category;
	private PostCategory newParentCategory;
	/*contains all categories except this category*/
	private List<PostCategory> allPostCategories;
	private List<PostCategoryStatusType> postCategoryStatuses;
		
	public PostCategoryEditor() {}

	@PostConstruct
	private void init() {
		logger.trace("init()");
		ExternalContext externalContext = FacesContext
				.getCurrentInstance().getExternalContext();
		String categoryIdParam = externalContext.getRequestParameterMap().get("cat_id");
		if ( !(categoryIdParam == null) ) {
			int categoryID = Integer.parseInt(categoryIdParam);
			List<PostCategory> categoriesInDBWithSuchId = categoryFacade.getPostCategoriesFindById(categoryID);
			if (categoriesInDBWithSuchId.size() == 1) {
				category = categoriesInDBWithSuchId.get(0);
				newParentCategory = category.getParentCategory();
			} else {
				try {
					externalContext.redirect(externalContext.getRequestContextPath() + 
							"/adminPanel/noEntityInDbWithSuchId.jsf");
					category = new PostCategory();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} else {
			try {
				externalContext.redirect(externalContext.getRequestContextPath() + 
						"/adminPanel/accessDenied.jsf");
				category = new PostCategory();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		allPostCategories = categoryFacade.getPostCategoriesFindAll();
		allPostCategories.remove(category);
		
		postCategoryStatuses = Arrays.asList(PostCategoryStatusType.values());
	}
	
	public String updatePostCategory() {
		try{
			category = categoryFacade.updatePostCategoryInDB(category, newParentCategory);
			
		} catch(Exception e) {
			String errorMessage = createMessageText("updatePostCatErr", category);
			addMessageToFacesContext(createErrorFacesMessage(errorMessage));
			logger.error(errorMessage, e);
			
			category = categoryFacade.refreshPostCategoryFromDB(category);
			
			return null;
		}
		
		String successMessage = createMessageText("updatePostCatSuccessMessage", category);
		addMessageToFacesContext(createInfoFacesMessage(successMessage));
		logger.debug(successMessage);
		
		return null;
	}
	
	public String cancelUpdatingPostCategory() {
		return "listingOfAllPostCategories?faces-redirect=true";
	}

	public String deletePostCategory() {
		Collection<PostCategory> subCategories = category.getSubCategories();
		if (subCategories.size() != 0) {
			addMessageToFacesContext(createErrorFacesMessage("You cannot delete a category "
					+ "with subcategories!"));
			return null;
		}
		
		List<Post> postsInThisCategory = postFacade.getPostsFindByCategory(category);
		if (postsInThisCategory.size() != 0) {
			addMessageToFacesContext(createErrorFacesMessage("You cannot delete a category "
					+ "that contains posts!"));
			return null;
		}
		
		try{
			categoryFacade.removePostCategoryFromDB(category);
			
		} catch(Exception e) {
			String errorMessage = createMessageText("deletePostCatErr", category);
			addMessageToFacesContext(createErrorFacesMessage(errorMessage));
			logger.error(errorMessage, e);
			
			return null;
		}
		
		String successMessage = createMessageText("deletePostCatSuccessMessage", category);
		addMessageToFacesContext(createInfoFacesMessage(successMessage));
		logger.debug(successMessage);
		
		return "listingOfAllPostCategories?faces-redirect=true";
	}
	
	public PostCategory getCategory() {
		return category;
	}

	public PostCategory getNewParentCategory() {
		return newParentCategory;
	}

	public void setNewParentCategory(PostCategory newParentCategory) {
		this.newParentCategory = newParentCategory;
	}

	public List<PostCategory> getAllPostCategories() {
		return allPostCategories;
	}

	public List<PostCategoryStatusType> getPostCategoryStatuses() {
		return postCategoryStatuses;
	}
	
	
}
