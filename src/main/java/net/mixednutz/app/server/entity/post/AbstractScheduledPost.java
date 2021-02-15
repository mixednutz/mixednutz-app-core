package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="ScheduledPost")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class AbstractScheduledPost implements ScheduledPost  {
		
    private Long id;
    private String type;
    
	private ZonedDateTime publishDate;
	private Long[] externalFeedId;
	private boolean emailFriendGroup;
	
	private Boolean published=false;
	
	public abstract Post<?> post();
	
	public AbstractScheduledPost(String type) {
		super();
		this.type = type;
	}
	
	@Id
	@Column(name="comment_id")
	@GeneratedValue(generator="system-native")
	@GenericGenerator(name="system-native", strategy = "native")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="type",insertable=false, updatable=false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(nullable=false)
	public ZonedDateTime getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(ZonedDateTime publishDate) {
		this.publishDate = publishDate;
	}
	@Transient
	//TODO Fix this later
	public Long[] getExternalFeedId() {
		return externalFeedId;
	}
	public void setExternalFeedId(Long[] externalFeedId) {
		this.externalFeedId = externalFeedId;
	}
	public boolean isEmailFriendGroup() {
		return emailFriendGroup;
	}
	public void setEmailFriendGroup(boolean emailFriendGroup) {
		this.emailFriendGroup = emailFriendGroup;
	}

	public Boolean isPublished() {
		return published!=null?published:false;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

}
