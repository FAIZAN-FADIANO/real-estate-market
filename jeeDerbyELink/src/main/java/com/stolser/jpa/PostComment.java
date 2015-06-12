package com.stolser.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * PostComment entity: a concrete entity that represent one comment for
 * a particular post. Comment can be added only by 
 * <span style="text-decoration:underline;">registered users</span>.<br/>
 * After adding from the front-end they have a status = new, and NOT displayed.<br/> 
 * After moderation (by changing their status = active) they are displayed.
 * */
@Entity
@NamedQueries({
	@NamedQuery(name="PostComment.findByStatus", 
			query="select c from PostComment c where c.status = :status")
})
@Table(name="POST_COMMENTS")
public class PostComment implements Serializable {
	private static final long serialVersionUID = 349L;
	
	@Id
	@Column(name="COMMENT_PK")
	@TableGenerator(name="postCommentIdGenerator",
					table="SEQUENCE_STORAGE",
					pkColumnName="SEQUENCE_NAME",
					pkColumnValue="POST_COMMENTS.COMMENT_PK",
					valueColumnName="SEQUENCE_VALUE",
					initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="postCommentIdGenerator")
	private int id;
	
	/**
	 * Defines comments' visibility on the front-end.
	 * */
	@NotNull
	private PostCommentStatusType status;
	@ManyToOne
	@JoinColumn(name="AUTHOR_ID")
	private User author;
	@NotNull
	private String text;
	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name="CREATION_DATE")
	private Date dateOfCreation;
	/**
	 * A post (of type Post) to which this postComment belongs to.
	 * */
	@NotNull
	@ManyToOne
	@JoinColumn(name="POST_ID")
	private Post post;
	@Version
	private int version;
/*--------- END of entity properties --------------*/
	
/*--------- constructors --------------------------*/	
	public PostComment() {}
/*--------- END of constructors -------------------*/
	
	/**
	 * NEW – is assigned only to newly created comments and is used for moderation.
	 * Comments are not displayed.<br/>
	 * ACTIVE - is displayed.<br/>
	 * NOT_ACTIVE – is NOT displayed.<br/>
	 * */
	enum PostCommentStatusType {
		NEW, ACTIVE, NOT_ACTIVE
	}
	
/*-------- getters and setters --------------------*/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PostCommentStatusType getStatus() {
		return status;
	}

	public void setStatus(PostCommentStatusType status) {
		this.status = status;
	}

	public User getAuthorId() {
		return author;
	}

	public void setAuthorId(User authorId) {
		this.author = authorId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public Post getPostId() {
		return post;
	}

	public void setPostId(Post postId) {
		this.post = postId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
/*-------- END of getters and setters -------------*/


}















