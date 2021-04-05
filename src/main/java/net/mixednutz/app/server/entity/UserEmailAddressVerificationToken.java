package net.mixednutz.app.server.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

@Entity
public class UserEmailAddressVerificationToken {
	
	private static final int EXPIRATION = 60 * 24;

	private String token;
	private UserEmailAddress emailAddress;
	private ZonedDateTime dateCreated;
	private ZonedDateTime expiryDate;
	private ZonedDateTime viewedOn;
	
	@PrePersist
	public void onPersist() {
		this.dateCreated=ZonedDateTime.now();
		this.expiryDate = this.calculateExpiryDate(EXPIRATION);
	}
	
	@Id
	@Column(name="verification_token", length=25)
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@ManyToOne()
	@JoinColumn(name="email_adddress_id")
	public UserEmailAddress getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(UserEmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
		
	public ZonedDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(ZonedDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public ZonedDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(ZonedDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public ZonedDateTime getViewedOn() {
		return viewedOn;
	}

	public void setViewedOn(ZonedDateTime viewedOn) {
		this.viewedOn = viewedOn;
	}

	private ZonedDateTime calculateExpiryDate(int expiryTimeInMinutes) {
		return ZonedDateTime
				.now()
				.plusMinutes(expiryTimeInMinutes);
	}
		
}
