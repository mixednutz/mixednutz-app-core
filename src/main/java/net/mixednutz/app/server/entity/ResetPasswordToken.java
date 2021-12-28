package net.mixednutz.app.server.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

@Entity
public class ResetPasswordToken implements VerificationToken {

	private String token;
	private UserEmailAddress emailAddress;
	private User user;
	private ZonedDateTime dateCreated;
	private boolean expired;
	private ZonedDateTime viewedOn;
	
	@PrePersist
	public void onPersist() {
		this.dateCreated=ZonedDateTime.now();
	}
	
	@Id
	@Column(name="verification_token", length=25)
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@ManyToOne(optional=true)
	@JoinColumn(name="email_adddress_id")
	public UserEmailAddress getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(UserEmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
	@ManyToOne()
	@JoinColumn(name="user_id")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public ZonedDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(ZonedDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public ZonedDateTime getViewedOn() {
		return viewedOn;
	}

	public void setViewedOn(ZonedDateTime viewedOn) {
		this.viewedOn = viewedOn;
	}
	
}
