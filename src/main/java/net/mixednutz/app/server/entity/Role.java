/**
 * 
 */
package net.mixednutz.app.server.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;


/**
 * @author Andy
 *
 */
@Entity
@Table(name="Role_Authority")
public class Role implements GrantedAuthority {
	
	private RolePK id;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Role() {
		super();
	}
	private Role(RolePK id) {
		super();
		this.id = id;
	}
	public Role(User user, String authority) {
		this(new RolePK(user, authority));
	}
	
	@EmbeddedId
	public RolePK getId() {
		return id;
	}
	public void setId(RolePK id) {
		this.id = id;
	}
	
	@Transient
	public String getAuthority() {
		return getId().getAuthority();
	}
	
	public int hashCode() {
		if (getId()!=null) {
			return this.getId().hashCode();
		}
        return super.hashCode();
    }

    public String toString() {
        return this.getAuthority();
    }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o != null && o instanceof GrantedAuthority) {
			String rhsRole = ((GrantedAuthority) o).getAuthority();
			
			if (rhsRole == null) {
				return -1;
			}
			
			return getAuthority().compareTo(rhsRole);
		}
		return -1;
	}

	@Embeddable
	public static class RolePK implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -5970441618608620233L;
		
		private String authority;
		private User user;
		
		public RolePK(User user, String authority) {
			super();
			this.authority = authority;
			this.user = user;
		}
		
		public RolePK() {
			super();
		}
		
		@Column(name="role")
		public String getAuthority() {
			return authority;
		}
		public void setAuthority(String authority) {
			this.authority = authority;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		
		@ManyToOne()
		@JoinColumn(name="account_id")
		public User getUser() {
			return this.user;
		}
		
		public void setUser(User user) {
			this.user = user;
		}

		@Override
		public String toString() {
			return String.valueOf(this.authority);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof RolePK) {
				RolePK other = (RolePK) obj;
				return (other.authority==this.authority);
			}
			return false;
		}
		
	}

}
