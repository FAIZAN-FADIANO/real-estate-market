package com.stolser.jpa;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * PostCategory entity: a concrete entity that defines a category for 
 * a Post entity. Can have a parent category of the same type, otherwise 
 * is considered as one of root categories. 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="PostCategory.findAll", query="select c from PostCategory c"),
	@NamedQuery(name="PostCategory.findBySystemName",
		query="select c from PostCategory c where c.systemName = :systemName"),
	@NamedQuery(name="PostCategory.findByStatus",
		query="select c from PostCategory c where c.status = :status"),
	@NamedQuery(name="PostCategory.findByParentCategory",
		query="select c from PostCategory c where c.parentCategory = :parentCategory")
})
@Table(name="POST_CATEGORIES")
public class PostCategory implements Serializable {
	private static final long serialVersionUID = 348L;
	
	@Id
	@Column(name="CATEGORY_PK")
	@TableGenerator(name="postCategoryIdGenerator",
					table="SEQUENCE_STORAGE",
					pkColumnName="SEQUENCE_NAME",
					pkColumnValue="POST_CATEGORIES.CATEGORY_PK",
					valueColumnName="SEQUENCE_VALUE",
					initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, 
					generator="postCategoryIdGenerator")
	private int id;
	
	/**
	 * <span style="color:red";>Value MUST be unique.</span> Will be used in a URL.
	 * */
	@Column(name="SYSTEM_NAME")
	@Pattern(regexp="(^[a-zA-Z]{3,30}$)|(^$)")
	@NotNull
	private String systemName;
	
	/**
	 * By default is active (visible on the front-end).
	 * If <code>status = NOT_ACTIVE</code> then neither this category's posts nor
	 * any subcategory's posts are displayed on the front-end.
	 * */
	@Column(name="STATUS")
	@Enumerated(EnumType.STRING)
	@NotNull
	private PostCategoryStatusType status;
	@NotNull
	private String title;
	
	@ManyToOne
	@JoinColumn(name="PARENT_CATEGORY")
	private PostCategory parentCategory;
	@OneToMany(mappedBy="parentCategory")
	private Collection<PostCategory> subCategories;
	
	@Column(name="DESCRIPTION")
	private String description;
	@Version
	private int version;

	public PostCategory() {}

	public PostCategory(String systemName, PostCategoryStatusType status, String title) {
		super();
		this.systemName = systemName;
		this.status = status;
		this.title = title;
	}

	public enum PostCategoryStatusType {
		ACTIVE, NOT_ACTIVE;
	}
	
/*-------- getters and setters ---------*/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public PostCategoryStatusType getStatus() {
		return status;
	}

	public void setStatus(PostCategoryStatusType status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PostCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(PostCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Collection<PostCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(Collection<PostCategory> subCategories) {
		this.subCategories = subCategories;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
/*-------- END of getters and setters ---------*/

	@Override
	public String toString() {
		return title + " (" + systemName + "; id = " + id + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		PostCategory other = (PostCategory) obj;
		if (id != other.id)	return false;
		
		return true;
	}
}
