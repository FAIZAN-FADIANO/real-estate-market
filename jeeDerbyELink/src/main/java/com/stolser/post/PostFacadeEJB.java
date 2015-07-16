package com.stolser.post;

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
 * Session Bean implementation class PostFacadeEJB
 */
@Stateless
public class PostFacadeEJB {

	@PersistenceContext(unitName = "derby")
	private EntityManager entityManager;
	
    public PostFacadeEJB() {}
    
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
}














