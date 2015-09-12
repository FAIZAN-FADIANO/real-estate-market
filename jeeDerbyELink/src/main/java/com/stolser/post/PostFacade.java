package com.stolser.post;

import static com.stolser.MessageFromProperties.createMessageText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.stolser.jpa.Post;
import com.stolser.jpa.PostAuthor;
import com.stolser.jpa.PostCategory;
import com.stolser.jpa.User;

/**
 * Session Bean implementation class PostFacade
 */
@Stateless
public class PostFacade {

	@PersistenceContext(unitName = "derby")
	private EntityManager entityManager;
	
    public PostFacade() {}
    
    public List<Post> getPostsFindAll() {
    	TypedQuery<Post> query = entityManager
    			.createNamedQuery("Post.findAll", Post.class);
    	return query.getResultList();
    }
    
/**
 * Returns either an empty list or a list that contains only one found Post instance.
 * */
    public List<Post> getPostsFindById(Integer id) {
    	Post foundPost = entityManager.find(Post.class, id);
    	if (foundPost == null) {
			return Collections.emptyList();
		}
    	
    	List<Post> foundPosts = new ArrayList<Post>();
    	foundPosts.add(foundPost);
    	return foundPosts; 
    }
    
    public List<Post> getPostsFindByCategory(PostCategory category) {
    	TypedQuery<Post> query = entityManager.
    			createNamedQuery("Post.findByCategory", Post.class).setParameter("category", category);
    	return query.getResultList();
    }
    
    public List<Post> getPostsFindByStatus(Post.PostStatusType status) {
    	TypedQuery<Post> query = entityManager.
    			createNamedQuery("Post.findByStatus", Post.class).setParameter("status", status);
    	return query.getResultList();
    }
    
    public List<Post> getPostsFindByAuthor(PostAuthor author) {
    	TypedQuery<Post> query = entityManager.
    			createNamedQuery("Post.findByAuthor", Post.class).setParameter("author", author);
    	return query.getResultList();
    }
    
    public List<Post> getPostsFindByLinkName(String linkName) {
    	TypedQuery<Post> query = entityManager.
    			createNamedQuery("Post.findByLinkName", Post.class).setParameter("linkName", linkName);
    	return query.getResultList();
    }
    
    public Post addNewPost(Post postToAdd) {
    	postToAdd = systemRestrictionCheck(postToAdd);
    	persistEntity(postToAdd);

    	return postToAdd;
    }
    
    
    
    
    
    private Post systemRestrictionCheck(Post postToCheck) {
    	
    	checkRequiredPropertyForNullity(postToCheck);
    	checkPostLinkNameUniqueness(postToCheck);

    	return postToCheck;
    }
    
    private void checkRequiredPropertyForNullity(Post postToCheck) {
    	boolean isAnyRequiredPropertyIsNull = (postToCheck.getCategory() == null) 
    			|| (postToCheck.getStatus() == null)
    			|| (postToCheck.getAuthor() == null) 
    			|| (postToCheck.getLinkName() == null) 
        		|| (postToCheck.getTitle() == null) 
        		|| (postToCheck.getText() == null)
        		|| (postToCheck.getDateOfCreation() == null);
    	if (isAnyRequiredPropertyIsNull) {
			throw new RuntimeException(createMessageText("requiredPropsViolationErr"));
    	}
    }
    
    private void checkPostLinkNameUniqueness(Post postToCheck) {
    	String linkNameOfPostToCheck = postToCheck.getLinkName();
    	List<Post> postsInDBWithSuchLinkName = getPostsFindByLinkName(linkNameOfPostToCheck);
    	if (postsInDBWithSuchLinkName.size() != 0) {
			int IDofPostToCheck = postToCheck.getId();
			int IDofPostInDB = postsInDBWithSuchLinkName.get(0).getId();
			if (IDofPostToCheck != IDofPostInDB) {
				throw new RuntimeException(createMessageText("addPostLinkNameViolationErr", 
						linkNameOfPostToCheck));
			}
    	}
    }
    
    private <T> T persistEntity(T entity) {
		try {
			entityManager.persist(entity);
			
			return entity;
			
		} catch (Exception eee) {
			throw new RuntimeException(createMessageText("failureDuringPersistanceMessage"), eee);
		}
	}
}