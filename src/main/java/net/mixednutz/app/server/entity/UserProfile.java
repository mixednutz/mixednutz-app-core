package net.mixednutz.app.server.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.mixednutz.api.model.IUserProfile;
import net.mixednutz.app.server.entity.ExternalFeeds.Oauth1AuthenticatedFeed;

@Entity
public class UserProfile implements IUserProfile {

	private Long userId;
	private User user;
	
	private String location;
	private String bio;
	private String pronouns;
	private String website;
	
	private String discordInviteCode;
	
	private Integer twitterAccountId;
	private Oauth1AuthenticatedFeed twitterAccount;
	
	private String blueskyHandle;
	
	private String deviantArtUsername;
	
	private String fediverseUsername;
	private String activityPubActorUri;
	
	
	public UserProfile() {
		super();
	}
	
	public UserProfile(User user) {
		super();
		//this.user = user; latest hibernate doesn't like this
		this.userId = user.getUserId();
	}

	@Id
	@Column(name="user_id", nullable = false, updatable=false)
	public Long getUserId() {
		return userId;
	}

	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.MERGE)
	@PrimaryKeyJoinColumn(name="user_id")
	public User getUser() {
		return user;
	}
	
	public String getLocation() {
		return location;
	}

	@Lob
	@Column(name="bio")
//	@Column(name="bio", columnDefinition="LONGTEXT")
	public String getBio() {
		return bio;
	}

	public String getPronouns() {
		return pronouns;
	}

	public String getWebsite() {
		return website;
	}

	@Column(name="twitter_feed_id", updatable=true, insertable=true)
	public Integer getTwitterAccountId() {
		return twitterAccountId;
	}

	@ManyToOne
	@JoinColumn(name="twitter_feed_id", updatable=false, insertable=false)
	@NotFound(action=NotFoundAction.IGNORE)
	public Oauth1AuthenticatedFeed getTwitterAccount() {
		return twitterAccount;
	}

	public String getBlueskyHandle() {
		return blueskyHandle;
	}

	public void setBlueskyHandle(String blueskyHandle) {
		this.blueskyHandle = blueskyHandle;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public void setPronouns(String pronouns) {
		this.pronouns = pronouns;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDiscordInviteCode() {
		return discordInviteCode;
	}

	public void setDiscordInviteCode(String discordInviteCode) {
		this.discordInviteCode = discordInviteCode;
	}

	public void setTwitterAccountId(Integer twitterAccountId) {
		this.twitterAccountId = twitterAccountId;
	}

	public void setTwitterAccount(Oauth1AuthenticatedFeed twitterAccount) {
		this.twitterAccount = twitterAccount;
	}

	public String getDeviantArtUsername() {
		return deviantArtUsername;
	}

	public void setDeviantArtUsername(String deviantArtUsername) {
		this.deviantArtUsername = deviantArtUsername;
	}

	public String getActivityPubActorUri() {
		return activityPubActorUri;
	}

	public void setActivityPubActorUri(String activityPubId) {
		this.activityPubActorUri = activityPubId;
	}

	public String getFediverseUsername() {
		return fediverseUsername;
	}

	public void setFediverseUsername(String fediverseUsername) {
		this.fediverseUsername = fediverseUsername;
	}

}
