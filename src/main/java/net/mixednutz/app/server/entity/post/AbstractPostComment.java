package net.mixednutz.app.server.entity.post;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="Comment")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class AbstractPostComment extends AbstractComment
	 implements PostComment {

	private Long commentId;
	private AbstractPostComment inReplyTo;
	private Long inReplyToId;
	private List<AbstractPostComment> replies;
	private String type;

	public AbstractPostComment(String type) {
		super();
		this.type = type;
	}

	@Id
	@Column(name="comment_id")
	@GeneratedValue(generator="system-native")
	@GenericGenerator(name="system-native", strategy = "native")
	public Long getCommentId() {
		return commentId;
	}
	
	@Column(name="type",insertable=false, updatable=false)
	public String getType() {
		return type;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	
	@ManyToOne
	@JoinColumn(name="inReplyTo_id")
	public AbstractPostComment getInReplyTo() {
		return inReplyTo;
	}
	@Column(name="inReplyTo_id", insertable=false, updatable=false)
	public Long getInReplyToId() {
		if (inReplyToId==null && inReplyTo!=null) {
			return inReplyTo.getCommentId();
		}
		return inReplyToId;
	}
	@OneToMany(mappedBy="inReplyTo")
	public List<AbstractPostComment> getReplies() {
		return replies;
	}

	public void setInReplyTo(AbstractPostComment inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public void setInReplyToId(Long inReplyToId) {
		this.inReplyToId = inReplyToId;
	}

	public void setReplies(List<AbstractPostComment> replies) {
		this.replies = replies;
	}

	@Override
	public <C extends Comment> void setParentComment(C parentComment) {
		this.inReplyTo = (AbstractPostComment) parentComment;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
