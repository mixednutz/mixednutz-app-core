package net.mixednutz.app.server.entity;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mixednutz.api.core.model.Action;
import net.mixednutz.api.core.model.AlternateLink;
import net.mixednutz.api.core.model.ReactionCount;
import net.mixednutz.api.core.model.ReshareCount;
import net.mixednutz.api.core.model.TagCount;
import net.mixednutz.api.model.IGroupSmall;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.IUser;
import net.mixednutz.api.model.IVisibility;

public class InternalTimelineElement implements ITimelineElement {
	
	/**
	 * Unique value to relative to the timeline page.
	 */
	private Long id;
	
	/**
	 * Resource Identifier of element.  
	 * Also Internet location of the Machine-readable version of the Element.
	 * @return
	 */
	private String uri;
	
	/**
	 * Internet location of the UI version of the Element
	 * @return
	 */
	private String url;
	
	/**
	 * Optional latest sub url for grouped posts
	 * @return
	 */
	private String latestSuburl;
	
	/**
	 * Optional latest sub uri for grouped posts
	 * @return
	 */
	private String latestSuburi;
	
	/**
	 * Optional URL to element this is in reply to.
	 */
	private String inReplyToUrl;
	
	/**
	 * Optional URL to element this is in reply to.
	 */
	private String inReplyToUri;
		
	/**
	 * Possible additional actions (outside of normal CRUD actions) that can be performed
	 */
	private List<Action> actions;
	
	/**
	 * The type of element
	 */
	private Type type;
	
	/**
	 * Who can see this element;
	 */
	private IVisibility visibility;

	/**
	 * User who posted this element
	 */
	private IUser postedByUser;
	
	/**
	 * Optional group this element was posted to
	 */
	private IGroupSmall postedToGroup;

	/**
	 * The date created
	 */
	private ZonedDateTime postedOnDate;
	
	/**
	 * The date updated or the last post in the conversation
	 */
	private ZonedDateTime updatedOnDate;
	
	/**
	 * Optional Title
	 */
	private String title;
	
	/**
	 * Optional latest subtitle for grouped posts that have titles
	 */
	private String latestSubtitle;

	/**
	 * Optional titles of element this is in reply to.
	 */
	private String inReplyToTitle;
	
	/**
	 * Optional short description (may be truncated)
	 */
	private String description;
	
	/**
	 * Optional short sub-description (may be truncated) for grouped posts that have descriptions
	 */
	private String latestSubdescription;

	/**
	 * Optional Alternate data.
	 */
	private Collection<AlternateLink> alternateLinks;

	/**
	 * Optional count of reactions this element has received
	 */
	private List<ReactionCount> reactions;

	/**
	 * Optional count of tags this element has received
	 */
	private List<TagCount> tags;

	/**
	 * Optional count of times this element has been reshared by network.
	 */
	private List<ReshareCount> reshares;
	
	/**
	 * Optional list of comments about this element
	 */
	private List<InternalTimelineElement> comments;
	
	private Map<String, Object> additionalData;
	
	public InternalTimelineElement() {
		super();
	}

	public InternalTimelineElement(Type type) {
		super();
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Long getProviderId() {
		return this.id;
	}

	@Override
	public Long getPaginationId() {
		return this.id;
	}
	
	@Override
	public Long getReference() {
		return this.id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getLatestSuburl() {
		return latestSuburl;
	}

	public void setLatestSuburl(String suburl) {
		this.latestSuburl = suburl;
	}

	public String getLatestSuburi() {
		return latestSuburi;
	}

	public void setLatestSuburi(String latestSuburi) {
		this.latestSuburi = latestSuburi;
	}

	public String getInReplyToUrl() {
		return inReplyToUrl;
	}

	public void setInReplyToUrl(String inReplyToUrl) {
		this.inReplyToUrl = inReplyToUrl;
	}

	public String getInReplyToUri() {
		return inReplyToUri;
	}

	public void setInReplyToUri(String inReplyToUri) {
		this.inReplyToUri = inReplyToUri;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public IVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(IVisibility visibility) {
		this.visibility = visibility;
	}

	public IUser getPostedByUser() {
		return postedByUser;
	}

	public void setPostedByUser(IUser postedByUser) {
		this.postedByUser = postedByUser;
	}

	public IGroupSmall getPostedToGroup() {
		return postedToGroup;
	}

	public void setPostedToGroup(IGroupSmall postedToGroup) {
		this.postedToGroup = postedToGroup;
	}

	public ZonedDateTime getPostedOnDate() {
		return postedOnDate;
	}

	public void setPostedOnDate(ZonedDateTime postedOnDate) {
		this.postedOnDate = postedOnDate;
	}

	public ZonedDateTime getUpdatedOnDate() {
		return updatedOnDate;
	}

	public void setUpdatedOnDate(ZonedDateTime updatedOnDate) {
		this.updatedOnDate = updatedOnDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLatestSubtitle() {
		return latestSubtitle;
	}

	public void setLatestSubtitle(String latestSubtitle) {
		this.latestSubtitle = latestSubtitle;
	}

	public String getInReplyToTitle() {
		return inReplyToTitle;
	}

	public void setInReplyToTitle(String inReplyToTitle) {
		this.inReplyToTitle = inReplyToTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLatestSubdescription() {
		return latestSubdescription;
	}

	public void setLatestSubdescription(String latestSubdescription) {
		this.latestSubdescription = latestSubdescription;
	}

	public Collection<AlternateLink> getAlternateLinks() {
		return alternateLinks;
	}

	public void setAlternateLinks(Collection<AlternateLink> alternateLinks) {
		this.alternateLinks = alternateLinks;
	}

	public List<ReactionCount> getReactions() {
		return reactions;
	}

	public void setReactions(List<ReactionCount> reactions) {
		this.reactions = reactions;
	}

	public List<TagCount> getTags() {
		return tags;
	}

	public void setTags(List<TagCount> tags) {
		this.tags = tags;
	}

	public List<ReshareCount> getReshares() {
		return reshares;
	}

	public void setReshares(List<ReshareCount> reshares) {
		this.reshares = reshares;
	}

	public List<InternalTimelineElement> getComments() {
		return comments;
	}

	public void setComments(List<InternalTimelineElement> comments) {
		this.comments = comments;
	}

	public Map<String, Object> getAdditionalData() {
		if (additionalData==null) {
			additionalData = new HashMap<>();
		}
		return additionalData;
	}

	public void setAdditionalData(Map<String, Object> additionalData) {
		this.additionalData = additionalData;
	}


	/**
	 * The type can be a custom type created by the remote network,
	 */
	public static class Type implements ITimelineElement.Type {

		String name;
		String namespace;
		String id;

		public Type() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Type(String name, String namespace, String id) {
			super();
			this.name = name;
			this.namespace = namespace;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

}
