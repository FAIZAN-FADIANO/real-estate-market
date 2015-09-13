package com.stolser.post.category;

import static com.stolser.MessageFromProperties.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.PostCategory;
import com.stolser.jpa.PostCategory.PostCategoryStatusType;

@Named("postCategoryListing")
@ViewScoped
public class PostCategoryListing implements Serializable {
	static private final long serialVersionUID = 1L;
	static private final Logger logger = LoggerFactory.getLogger(PostCategoryListing.class);
	
	private List<PostCategory> postCategories;
	private List<PostCategoryStatusType> postCategoryStatusLabels;
	@Inject
	private PostCategoryFacade categoryFacade;

	private PostCategoryStatusType categoryStatusOld;
	private PostCategoryStatusType categoryStatusNew;
	private PostCategory currentCategoryWithChangedStatus;
	
	@PostConstruct
	private void init() {
		populatePostCategories();
		populatePostCategoryStatusLabels();
		
	}
	
	public PostCategoryListing() {}

	public PostCategoryFacade getCategoryFacade() {
		return categoryFacade;
	}

	public void setCategoryFacade(PostCategoryFacade categoryFacade) {
		this.categoryFacade = categoryFacade;
	}

	public List<PostCategory> getPostCategories() {
		return postCategories;
	}

	public List<PostCategoryStatusType> getPostCategoryStatusLabels() {
		return postCategoryStatusLabels;
	}
	
	public void categoryStatusChanged(ValueChangeEvent event){
		categoryStatusOld = (PostCategoryStatusType)event.getOldValue();
		categoryStatusNew = (PostCategoryStatusType)event.getNewValue();
		
		FacesContext context = FacesContext.getCurrentInstance();
		currentCategoryWithChangedStatus = context.getApplication()
				.evaluateExpressionGet(context, "#{category}", PostCategory.class);
	}
	
	public void saveNewCategoryStatusOK() {
		currentCategoryWithChangedStatus.setStatus(categoryStatusNew);
		PostCategory postCategoryInDB;
		
		try {
			postCategoryInDB =  categoryFacade.updatePostCategoryInDB(currentCategoryWithChangedStatus);
			for (int i = 0; i < postCategories.size(); i++) {
				if (postCategories.get(i).getId() == currentCategoryWithChangedStatus.getId()) {
					postCategories.set(i, postCategoryInDB);
					break;
				}
			}

			String successMessage = "The Post Category's status has been changed on the Front End.";
			addMessageToFacesContext(createInfoFacesMessage(successMessage));
			logger.debug(successMessage);

		} catch (Exception e) {
			String errorMessage = "Failure during updating the status of a post category (" + 
					currentCategoryWithChangedStatus + ").";
			addMessageToFacesContext(createErrorFacesMessage(errorMessage));
			logger.error(errorMessage, e);

		} finally {
			zeroizeStatusVariables();
		}
		
	}
	
	public void saveNewCategoryStatusCancel() {
		postCategories.stream()
		.filter(category -> category.getId() == currentCategoryWithChangedStatus.getId())
		.findFirst()
			.get()
			.setStatus(categoryStatusOld);
		
		zeroizeStatusVariables();
	}
	
	
	
	
	private void zeroizeStatusVariables() {
		categoryStatusOld = null;
		categoryStatusNew = null;
		currentCategoryWithChangedStatus = null;
	}
	
	private void populatePostCategories() {
		try {
			postCategories = categoryFacade.getPostCategoriesFindAll();
			
		} catch (Exception e) {
			logger.error("Exception occured during getting post categories from the DB.", e);
		}
	}
	
	private void populatePostCategoryStatusLabels() {
		postCategoryStatusLabels = Arrays.asList(PostCategoryStatusType.values());
	}

	public PostCategoryStatusType getCategoryStatusOld() {
		return categoryStatusOld;
	}

	public PostCategoryStatusType getCategoryStatusNew() {
		return categoryStatusNew;
	}

	public PostCategory getCurrentCategoryWithChangedStatus() {
		return currentCategoryWithChangedStatus;
	}
	

}

















