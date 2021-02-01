package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@Entity
@Table(name="notification")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
@JsonTypeInfo(property = "type", 
	use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, 
	include = As.PROPERTY, visible=true) 
public class AbstractNotification implements PostNotification {
	
	private Long id;
	private String type;
	private ZonedDateTime dateCreated;
	private Long userId;
	
	public AbstractNotification(String type) {
		this(type, null);
	}
	public AbstractNotification(String type, Long userId) {
		super();
		this.type = type;
		this.userId = userId;
	}
	
	@PrePersist
	public void onPersist() {
		this.setDateCreated(ZonedDateTime.now());
	}
	
	@Id
	@Column(name="notification_id")
	@GeneratedValue(strategy = GenerationType.TABLE)
	@GenericGenerator(name="system-native", strategy = "native")
	public Long getId(){
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
	public ZonedDateTime getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(ZonedDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}


}
