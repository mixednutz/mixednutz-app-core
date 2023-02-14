package net.mixednutz.api.activitypub.impl;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.activitystreams.model.BaseObjectOrLink;
import org.w3c.activitystreams.model.ImageImpl;
import org.w3c.activitystreams.model.LinkImpl;
import org.w3c.activitystreams.model.Note;
import org.w3c.activitystreams.model.activity.Accept;
import org.w3c.activitystreams.model.activity.Create;
import org.w3c.activitystreams.model.activity.Follow;
import org.w3c.activitystreams.model.actor.Person;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mixednutz.api.activitypub.ActivityPubManager;
import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.IUserSmall;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.manager.UserKeyManager;
import net.mixednutz.app.server.repository.UserProfileRepository;

@Service
public class ActivityPubManagerImpl implements ActivityPubManager {
	
	@Autowired
	private UserProfileRepository profileRepository;
	
	@Autowired
	private UserKeyManager userKeyManager;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	private NetworkInfo networkInfo;
	
	public URI getActorUri(String username) {
		return UriComponentsBuilder
				.fromHttpUrl(networkInfo.getBaseUrl()+URI_PREFIX+USER_ACTOR_ENDPOINT)
				.buildAndExpand(Map.of("username",username)).toUri();
	}
	
	@Override
	public Note toNote(ITimelineElement element, String authorUsername, boolean isRoot) {
		Note note = new Note();
		if (isRoot) initRoot(note);
		
		String itemurl = element.getUrl();
		String itemid = element.getUri();
		if (element instanceof InternalTimelineElement) {
			InternalTimelineElement ite = (InternalTimelineElement)element;
			if (ite.getLatestSuburi()!=null) {
				itemurl = ite.getLatestSuburl();
			}
			if (ite.getLatestSuburi()!=null) {
				itemid= ite.getLatestSuburi();
			}
		}
		note.setUrl(itemurl);
		note.setId(URI.create(networkInfo.getBaseUrl()+NOTE_URI_PREFIX+itemid));
		StringBuffer summaryBuffer = new StringBuffer();
		if (element.getTitle()!=null) {
			summaryBuffer.append(element.getTitle());
		} else {
			summaryBuffer.append(element.getDescription());
		}
		StringBuffer descBuffer = new StringBuffer();
		if (element.getTitle()!=null) {
			descBuffer.append("<strong>"+element.getTitle()+"</strong>");
		}
		if (element.getDescription()!=null) {
			descBuffer.append("<p>"+element.getDescription()+"</p>");
		}
		if (element instanceof InternalTimelineElement) {
			InternalTimelineElement ite = (InternalTimelineElement)element;
			if (ite.getLatestSubdescription()!=null) {
				
				descBuffer.append("<p>");
				if (ite.getLatestSubtitle()!=null) {
					descBuffer.append(ite.getLatestSubtitle()).append(" : ");
					summaryBuffer.append(" - ").append(ite.getLatestSubtitle());
				}
				descBuffer.append(ite.getLatestSubdescription());
				descBuffer.append("</p>");
			}
		}		
		note.setContent(descBuffer.toString()); 
		note.setSummary(summaryBuffer.toString());
		note.setPublished(element.getPostedOnDate());
		note.setAttributedTo(new LinkImpl(getActorUri(authorUsername)));
		switch (element.getVisibility().getVisibilityType()) {
		case ALL_FOLLOWERS:
			//TODO this is the followers collection
			break;
		case ALL_FRIENDS:
			//TODO collection of actors
			break;
		case FRIEND_GROUPS:
			//TODO collection of actors
			break;
		case SELECT_FOLLOWERS:
			//TODO collection of actors
			break;
		case PRIVATE:
			note.setTo(List.of(new LinkImpl(getActorUri(authorUsername))));
			break;
		case ALL_USERS:
		case WORLD:
			note.setTo(List.of(new LinkImpl(BaseObjectOrLink.PUBLIC)));
			break;
		}
		//TODO implement tags (when search feature is available in mixednutz)
		return note;
	}
	
	public void initRoot(BaseObjectOrLink root) {
		root.setContext(List.of(BaseObjectOrLink.CONTEXT));
	}

	@Override
	public Person toPerson(IUserSmall user, User nativeUser, HttpServletRequest request, 
			URI id, URI userOutbox, URI userInbox, URI followers, URI following, boolean isRoot) {
		
		Person person = new Person();
		if (isRoot) initRoot(person);
		person.setId(id);
		person.setUrl(user.getUrl());
		person.setName(user.getDisplayName());
		person.setPreferredUsername(user.getUsername());
		person.setInbox(userInbox);
		person.setOutbox(userOutbox);
		person.setFollowers(followers);
		person.setFollowing(following);
		if (user.getAvatar()!=null) {
			ImageImpl icon = new ImageImpl();
			icon.setUrl(user.getAvatar().getSrc());
			icon.setName(user.getAvatar().getAlt());
			person.setIcon(List.of(icon));
		}
		person.setPublished(nativeUser.getMemberSince());

		profileRepository.findById(nativeUser.getUserId()).ifPresent(profile->{
			person.setSummary(profile.getBio());
		});
		
		userKeyManager.setPublicKeyPem(nativeUser, person);
				
		return person;
	}

	public Create toCreate(ITimelineElement element, Note note, String username) {
		
		Create create = new Create();
		initRoot(create);
		create.setActor(new LinkImpl(getActorUri(username)));
		create.setTo(note.getTo());
		String itemuri = element.getUri();
		if (element instanceof InternalTimelineElement) {
			InternalTimelineElement ite = (InternalTimelineElement)element;
			if (ite.getLatestSuburi()!=null) {
				itemuri = ite.getLatestSuburi();
			}
		}
		create.setObject(new LinkImpl(note.getId()));
		create.setId(URI.create(networkInfo.getBaseUrl()+CREATE_URI_PREFIX+itemuri));
		return create;
	}
	
	@Override
	public Create toCreateNote(ITimelineElement element, String authorUsername) {
		toNote(element, authorUsername, false);
		return toCreate(element, toNote(element, authorUsername, false), authorUsername);
	}

	public Accept toAccept(String username, final Follow follow) {
		Follow followCopy;
		try {
			//copy and remove context
			followCopy = objectMapper.treeToValue(objectMapper.valueToTree(follow), Follow.class);
			followCopy.setContext(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		Accept accept = new Accept();
		initRoot(accept);
		accept.setActor(new LinkImpl(getActorUri(username)));
		accept.setObject(followCopy);
		if (follow.getId()!=null) {
			accept.setId(URI.create(networkInfo.getBaseUrl()+URI_PREFIX+"/Accept"+follow.getId().getPath()));	
		}
		
		return accept;
	}

}
