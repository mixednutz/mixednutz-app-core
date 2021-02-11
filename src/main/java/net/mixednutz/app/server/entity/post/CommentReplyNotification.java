package net.mixednutz.app.server.entity.post;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue(CommentReplyNotification.TYPE)
public class CommentReplyNotification extends AbstractCommentReplyNotification<AbstractPostComment> {

	public static final String TYPE = "NewCommentReply";
	
	public CommentReplyNotification(Long userId, AbstractPostComment inReplyTo, AbstractPostComment comment) {
		super(TYPE, userId, inReplyTo, comment);
	}

	public CommentReplyNotification() {
		super(TYPE);
	}

	@ManyToOne
	@JoinColumn(name="comment_id", insertable=false, updatable=false)
	public AbstractPostComment getComment() {
		return comment;
	}
	@ManyToOne
	@JoinColumn(name="inReplyTo_id", insertable=false, updatable=false)
	public AbstractPostComment getInReplyTo() {
		return inReplyTo;
	}
	@Column(name="inReplyTo_id", insertable=true, updatable=false)
	public Long getInReplyToId() {
		return inReplyToId;
	}
	
	public void setInReplyTo(AbstractPostComment inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	public void setInReplyToId(Long inReplyToId) {
		this.inReplyToId = inReplyToId;
	}

}
