package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractCommentReplyNotification<C extends PostComment> extends AbstractNotification {

	protected C inReplyTo;
	protected Long inReplyToId;
	protected C comment;
	private Long commentId;
	
	public AbstractCommentReplyNotification(String type, Long userId, C inReplyTo, C comment) {
		super(type, userId);
		this.inReplyToId = inReplyTo.getCommentId();
		this.commentId = comment.getCommentId();
	}

	public AbstractCommentReplyNotification(String type) {
		super(type);
	}
	
	@Transient
	public C getInReplyTo() {
		return inReplyTo;
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
			
}
