package net.mixednutz.app.server.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name="LastOnline")
public class Lastonline {

	private Long userId;
	private User user;
	private ZonedDateTime timestamp;
	private String ipAddress;
	
	public Lastonline() {
		super();
	}

	public Lastonline(Long userId) {
		super();
		this.userId = userId;
	}
	
	public Lastonline(User user) {
		this(user.getUserId());
	}
	
	@PreUpdate
	@PrePersist
	public void setTimestamp() {
		this.timestamp = ZonedDateTime.now();
	}

	@Id
	@Column(name="user_id", nullable = false, updatable=false, insertable=true)
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@OneToOne
	@MapsId
	@JoinColumn(name="user_id", updatable=false, insertable=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}
	@Column(name="ip_address")
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
