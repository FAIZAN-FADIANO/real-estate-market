package com.stolser.post.category;

import static com.stolser.MessageFromProperties.createMessageText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.Post;
import com.stolser.jpa.PostCategory;
import com.stolser.jpa.User;
import com.stolser.jpa.PostCategory.PostCategoryStatusType;

@Stateless
public class PostCategoryFacade {
	static private final Logger logger = LoggerFactory.getLogger(PostCategoryFacade.class);
	@PersistenceContext(unitName = "derby")
	private EntityManager entityManager;
	
	public PostCategoryFacade() {}
	
	public List<PostCategory> getPostCategoriesFindAll() {
    	TypedQuery<PostCategory> query = entityManager
    			.createNamedQuery("PostCategory.findAll", PostCategory.class);
    	return query.getResultList();
    }
	
	public List<PostCategory> getPostCategoriesFindById(Integer id) {
		PostCategory foundPostCategory = entityManager.find(PostCategory.class, id);
    	if (foundPostCategory == null) {
			return Collections.emptyList();
		}
    	
    	List<PostCategory> foundPostCategories = new ArrayList<PostCategory>();
    	foundPostCategories.add(foundPostCategory);
    	return foundPostCategories; 
    }
	
	public List<PostCategory> getPostCategoriesFindBySystemName(String systemName) {
		TypedQuery<PostCategory> query = entityManager
    			.createNamedQuery("PostCategory.findBySystemName", PostCategory.class)
    			.setParameter("systemName", systemName);
		
		return query.getResultList(); 
	}
	
	public List<PostCategory> getPostCategoriesFindByStatus(PostCategoryStatusType status) {
		TypedQuery<PostCategory> query = entityManager
				.createNamedQuery("PostCategory.findByStatus", PostCategory.class)
				.setParameter("status", status);
		
		return query.getResultList(); 
	}
	
	public List<PostCategory> getPostCategoriesFindByParentCategory(PostCategory parentCategory) {
		TypedQuery<PostCategory> query = entityManager
				.createNamedQuery("PostCategory.findByParentCategory", PostCategory.class)
				.setParameter("parentCategory", parentCategory);
		
		return query.getResultList(); 
	}
	
	public PostCategory addNewPostCategory(PostCategory postCategoryToAdd) {
    	systemRestrictionCheck(postCategoryToAdd);
    	PostCategory persistedCategory = persistEntity(postCategoryToAdd);
    	
    	updateNewParent(persistedCategory);
    	
    	return persistedCategory;
    }

	public PostCategory updatePostCategoryInDB(PostCategory categoryToUpdate, 
											   PostCategory newParentCategory) {
		systemRestrictionCheck(categoryToUpdate);
		
    	List<PostCategory> categoriesInDB = getPostCategoriesFindById(categoryToUpdate.getId());
    	if (categoriesInDB.size() == 0) {
			throw new RuntimeException(createMessageText("noPostCategoryInDBWithID"));
		}
    	
    	PostCategory oldParentCategory = categoryToUpdate.getParentCategory();
    	
    	categoryToUpdate.setParentCategory(newParentCategory);
    	PostCategory updatedCategory = mergeEntity(categoryToUpdate);
    	
    	if ( ((newParentCategory != null) && (! newParentCategory.equals(oldParentCategory))) 
    		 || ((newParentCategory == null) && (oldParentCategory != null)) ) {
    		updateNewParent(updatedCategory);
    		updateOldParent(updatedCategory, oldParentCategory);
		}
    	
    	return updatedCategory;
    }
	
	public PostCategory updatePostCategoryInDB(PostCategory categoryToUpdate) {
		systemRestrictionCheck(categoryToUpdate);
		
    	List<PostCategory> categoriesInDB = getPostCategoriesFindById(categoryToUpdate.getId());
    	if (categoriesInDB.size() == 0) {
			throw new RuntimeException(createMessageText("noPostCategoryInDBWithID"));
		}
    	
    	return mergeEntity(categoryToUpdate);
	}
    
    public PostCategory refreshPostCategoryFromDB(PostCategory categoryToRefresh) {
    	List<PostCategory> categoriesInDB = getPostCategoriesFindById(categoryToRefresh.getId());
    	if (categoriesInDB.size() == 0) {
			throw new RuntimeException(createMessageText("noPostCategoryInDBWithID"));
		}
    	
    	PostCategory categoryFromDB = categoriesInDB.get(0);
    	return categoryFromDB;
    }
    
    public void removePostCategoryFromDB(PostCategory categoryToRemove) {
    	
    	categoryToRemove = getPostCategoriesFindById(categoryToRemove.getId()).get(0);
    	PostCategory parentCategoryToRemove = categoryToRemove.getParentCategory();
    	
    	entityManager.remove(categoryToRemove);
    	updateOldParent(categoryToRemove, parentCategoryToRemove);
    }
    
    
    
    

	
	
	
    private void updateNewParent(PostCategory changedCategory) {
    	PostCategory parentCategory = changedCategory.getParentCategory();
    	if (parentCategory != null) {
			PostCategory parentCategoryInDB = 
					getPostCategoriesFindBySystemName(parentCategory.getSystemName()).get(0);
			parentCategoryInDB.getSubCategories().add(changedCategory);
			
			mergeEntity(parentCategoryInDB);
			
			logger.trace("{} has been updated.", parentCategoryInDB);
		}
    }
    
    private void updateOldParent(PostCategory changedCategory, PostCategory oldParentCategory) {
    	if (oldParentCategory != null) {
			PostCategory oldPatentCategoryInDB = 
					getPostCategoriesFindById(oldParentCategory.getId()).get(0);
			oldPatentCategoryInDB.getSubCategories().remove(changedCategory);
			
			mergeEntity(oldPatentCategoryInDB);
			
			logger.trace("{} has been updated.", oldPatentCategoryInDB);
		}
    }
    
	private void systemRestrictionCheck(PostCategory postCategoryToCheck) {
    	
    	checkRequiredPropertyForNullity(postCategoryToCheck);
    	checkCategorySystemNameUniqueness(postCategoryToCheck);

    }
	
	private void checkRequiredPropertyForNullity(PostCategory postCategoryToCheck) {
    	boolean isAnyRequiredPropertyIsNull = (postCategoryToCheck.getSystemName() == null) 
    			|| (postCategoryToCheck.getStatus() == null)
        		|| (postCategoryToCheck.getTitle() == null);
    	if (isAnyRequiredPropertyIsNull) {
			throw new RuntimeException(createMessageText("requiredPropsViolationErr"));
    	}
    }
	
	private void checkCategorySystemNameUniqueness(PostCategory postCategoryToCheck) {
    	String systemNameOfCategoryToCheck = postCategoryToCheck.getSystemName();
    	List<PostCategory> categoriesInDBWithSuchSystemName = 
    			getPostCategoriesFindBySystemName(systemNameOfCategoryToCheck);
    	if (categoriesInDBWithSuchSystemName.size() != 0) {
			int IDofCategoryToCheck = postCategoryToCheck.getId();
			int IDofCategoryInDB = categoriesInDBWithSuchSystemName.get(0).getId();
			if (IDofCategoryToCheck != IDofCategoryInDB) {
				throw new RuntimeException(createMessageText("addPostCategorySystemNameViolationErr", 
						systemNameOfCategoryToCheck));
			}
    	}
    }
	
	private <T> T persistEntity(T entity) {
		entityManager.persist(entity);
		
		return entity;
	}
	
	private <T> T mergeEntity(T entity) {
		return entityManager.merge(entity);
	}
}
