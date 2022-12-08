package net.mixednutz.app.server.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Follower")
public class Follower {
	
	private FollowerPK id;
	
	private User user;
	
	private User follower;
	
	private boolean pending=true;
	
	public Follower() {
		super();
	}

	public Follower(FollowerPK id) {
		super();
		this.id = id;
	}

	public Follower(User user, User follower, boolean pending) {
		this(new FollowerPK(user.getUserId(), follower.getUserId()));
		this.user = user;
		this.follower = follower;
		this.pending = pending;
	}

	@Id
	public FollowerPK getId() {
		return id;
	}

	public void setId(FollowerPK id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="user_id", insertable=false, updatable=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name="follower_id", insertable=false, updatable=false)
	public User getFollower() {
		return follower;
	}

	public void setFollower(User follower) {
		this.follower = follower;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}
	
	@Embeddable
	public static class FollowerPK implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4505492443899473232L;
		
		private Long userId;
		private Long followerId;
		
		public FollowerPK() {
			super();
		}

		public FollowerPK(Long userId, Long followerId) {
			super();
			this.userId = userId;
			this.followerId = followerId;
		}

		@Column(name="user_id", insertable=true, updatable=true)
		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		@Column(name="follower_id", insertable=true, updatable=true)
		public Long getFollowerId() {
			return followerId;
		}

		public void setFollowerId(Long followerId) {
			this.followerId = followerId;
		}
		
		@Override
		public int hashCode() {
			return (userId.hashCode()*100)+followerId.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FollowerPK) {
				FollowerPK obj2 = (FollowerPK) obj;
				return (this.userId.equals(obj2.userId)&&
						this.followerId.equals(obj2.followerId));
			}
			return false;
		}
		
	}
	
}
