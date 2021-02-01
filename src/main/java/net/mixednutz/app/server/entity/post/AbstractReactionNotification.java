package net.mixednutz.app.server.entity.post;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractReactionNotification<P extends Post<C>, C extends PostComment, R extends PostReaction> extends AbstractNotification {

	protected P reactedTo;
	protected Long reactedToId;
	protected R reaction;
	private Long reactionId;
	
	public AbstractReactionNotification(String type, Long userId, P reactedTo, R reaction) {
		super(type, userId);
		this.reactedToId = reactedTo.getId();
		this.reactionId = reaction.getId();
	}

	public AbstractReactionNotification(String type) {
		super(type);
	}
	
	@Column(name="reaction_id", insertable=true, updatable=false)
	public Long getReactionId() {
		return reactionId;
	}

	public void setReactionId(Long reactionId) {
		this.reactionId = reactionId;
	}
	
	public void setReaction(R reaction) {
		this.reaction = reaction;
	}

	@Transient
	public String getUri() {
		return null;
//		return reaction.getParentUri();
	}
	
	@Transient
	public abstract String getReactedToTypeDisplayName();
	
	@Transient
	public abstract String getReactedToSubject();

}
