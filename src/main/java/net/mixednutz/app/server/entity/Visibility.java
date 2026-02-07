package net.mixednutz.app.server.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;


/**
 * Common properties or fields for entities that have visibility attributes.
 * 
 * @see VisibilityType
 * @author apfesta
 *
 */
@Embeddable
public class Visibility {
	
	private VisibilityType visibilityType;
	private Set<User> selectFollowers;
//	private Set<FGroup> friendGroups;
	private Set<ExternalVisibility> externalList;

	/**
	 * Default Constructor
	 */
	public Visibility() {
		super();
	}
	/**
	 * All possible arguments constructor
	 * 
	 * @param visibilityType
	 * @param selectFollowers
	 */
	private Visibility(
			VisibilityType visibilityType, 
			Set<User> selectFollowers,
			Set<ExternalVisibility> externalList) {
		super();
		this.visibilityType = visibilityType;
		this.selectFollowers = selectFollowers;
		this.externalList = externalList;
		if (VisibilityType.SELECT_FOLLOWERS.equals(visibilityType) && 
				(selectFollowers==null||selectFollowers.isEmpty())) {
			throw new IllegalArgumentException(
					"SELECT_FOLLOWERS requires a non-empty set of selectFollowers");
		}
		if (VisibilityType.EXTERNAL_LIST.equals(visibilityType) &&
				(externalList==null||externalList.isEmpty())) {
			throw new IllegalArgumentException(
					"EXTERNAL_LIST requires a non-empty set of externalList");
		}
	}
	/**
	 * Visibility for PRIVATE, ALL_FOLLOWERS, ALL_FRIENDS, ALL_USERS, WORLD.
	 * Other visibility types use a different constructor because they require
	 * more information.
	 * 
	 * @param visibilityType
	 */
	public Visibility(VisibilityType visibilityType) {
		this(visibilityType, null, null);
	}
	
	@NotNull
	@Enumerated(value=EnumType.STRING)
	@Column(name="visibility")
	public VisibilityType getVisibilityType() {
		return visibilityType;
	}

	public void setVisibilityType(VisibilityType visibilityType) {
		this.visibilityType = visibilityType;
	}

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable
	public Set<User> getSelectFollowers() {
		return selectFollowers;
	}

	public void setSelectFollowers(Set<User> selectFollowers) {
		this.selectFollowers = selectFollowers;
	}
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable
	public Set<ExternalVisibility> getExternalList() {
		return externalList;
	}
	public void setExternalList(Set<ExternalVisibility> externalList) {
		this.externalList = externalList;
	}
	public static Visibility asPrivate() {
		return new Visibility(VisibilityType.PRIVATE);
	}
	public static Visibility toWorld() {
		return new Visibility(VisibilityType.WORLD);
	}
	public static Visibility toAllUsers() {
		return new Visibility(VisibilityType.ALL_USERS);
	}
	public static Visibility toAllFollowers() {
		return new Visibility(VisibilityType.ALL_FOLLOWERS);
	}
	public static Visibility toAllFriends() {
		return new Visibility(VisibilityType.ALL_FRIENDS);
	}
	/**
	 * VisibilityType.SELECT_FOLLOWERS visibility with set of followers.
	 * 
	 * @param selectFollowers
	 */
	public static Visibility toSelectFollowers(Set<User> selectFollowers) {
		return new Visibility(VisibilityType.SELECT_FOLLOWERS, selectFollowers, null);
	}
	/**
	 * VisibilityType.EXTERNAL_LIST visibility with set of followers.
	 * 
	 * @param selectFollowers
	 */
	public static Visibility toExternalVisibility(Set<ExternalVisibility> externalVisibility) {
		return new Visibility(VisibilityType.EXTERNAL_LIST, null, externalVisibility);
	}
	
	
}
