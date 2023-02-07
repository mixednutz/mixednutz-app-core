package net.mixednutz.app.server.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class UserKey {

	private Long userId;
	private User user;
	
	private byte[] privateKey;
	private byte[] publicKey;
	
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
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	@Lob
	@Column(name="private_key")
	public byte[] getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}
	@Lob
	@Column(name="public_key")
	public byte[] getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}
	
	
	
}
