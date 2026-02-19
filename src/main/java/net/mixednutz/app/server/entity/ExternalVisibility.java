package net.mixednutz.app.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * A visibility list (or role) maintained by an external system.
 * 
 * (ie. Patreon tiers)
 * 
 * @author apfesta
 *
 */
@Entity
@Table(name = "x_visibility_list")
public class ExternalVisibility {
	
	private String id; //unique primary key
	private String providerId; //the provider's ID
	private String providerListId; //The provider's ID for this list.
	private String name; //the name of the list
	private String providerUri; //link for the user to join/subscribe this list
	
	/**
	 * Default Constructor for JPA instantiation
	 */
	public ExternalVisibility() {
		super();
	}

	/**
	 * Constructor for static {@link #of(String, String, String)} instantiation
	 * 
	 * @param providerId
	 * @param providerListId
	 * @param name
	 */
	private ExternalVisibility(String providerId, String providerListId, String name, String providerUri) {
		super();
		this.providerId = providerId;
		this.providerListId = providerListId;
		this.name = name;
		this.providerUri = providerUri;
	}
	
	public static ExternalVisibility of(String providerId, String providerListId, String name, String providerUri) {
		return new ExternalVisibility(providerId, providerListId, name, providerUri);
	}
	
	@Id
	@Column(name = "external_visibility_id")
	@GeneratedValue(strategy = GenerationType.TABLE)
	@GenericGenerator(name = "system-native", strategy = "native")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getProviderListId() {
		return providerListId;
	}
	public void setProviderListId(String providerListId) {
		this.providerListId = providerListId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProviderUri() {
		return providerUri;
	}
	public void setProviderUri(String providerUri) {
		this.providerUri = providerUri;
	}
	
}
