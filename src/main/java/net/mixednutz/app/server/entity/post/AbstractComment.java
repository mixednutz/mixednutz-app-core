package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import net.mixednutz.app.server.entity.User;


/**
 * @author Andy
 */
@MappedSuperclass
public abstract class AbstractComment implements Comment{
	
	private String body;
	private ZonedDateTime dateCreated;
	private ZonedDateTime dateUpdated;
	private User author;
	private Long authorId;
				
	@PrePersist
	public void onPersist() {
		this.dateCreated=ZonedDateTime.now();
	}
	
	@PreUpdate
	public void onUpdate() {
		this.dateUpdated=ZonedDateTime.now();
	}
		
	/**
	 * @return Returns the author.
	 */
	@ManyToOne()
	@JoinColumn(name="author_id", insertable=false, updatable=false)
	public User getAuthor() {
		return author;
	}
	
	@Column(name="author_id", insertable=true, updatable=false)
	public Long getAuthorId() {
		return authorId;
	}
	
	/**
	 * @return Returns the body.
	 */
	@Lob
	@Column(name="body")
//	@Column(name="body", columnDefinition="LONGTEXT")
	public String getBody() {
		return body;
	}
	
	@Column(name="timestamp")
	public ZonedDateTime getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * @param timestamp The timestamp to set.
	 */
	public void setDateCreated(ZonedDateTime timestamp) {
		this.dateCreated = timestamp;
	}

	public ZonedDateTime getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(ZonedDateTime dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

}
