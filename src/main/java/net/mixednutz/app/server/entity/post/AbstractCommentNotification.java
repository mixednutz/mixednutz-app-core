package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractCommentNotification<P extends Post<C>, C extends PostComment> extends AbstractNotification {

	protected P post;
	protected Long postId;
	protected C comment;
	private Long commentId;
	
	public AbstractCommentNotification(String type, Long userId, P post, C comment) {
		super(type, userId);
		this.postId = post.getId();
		this.commentId = comment.getCommentId();
	}

	public AbstractCommentNotification(String type) {
		super(type);
	}
	
	@Transient
	public P getPost() {
		return post;
	}

	@Column(name="comment_id", insertable=true, updatable=false)
	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	
	public void setComment(C comment) {
		this.comment = comment;
	}

	@Transient
	public String getUri() {
		return comment.getUri();
	}
	
	@Transient
	public ZonedDateTime getCommentDateCreated() {
		return comment.getDateCreated();
	}
	
	@Transient
	public Long getAuthorId() {
		return comment.getAuthor().getUserId();
	}
	
	@Transient
	public abstract String getPostTypeDisplayName();
	
	@Transient
	public abstract String getPostSubject();
	
}
