package net.mixednutz.api.activitypub.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.activitystreams.model.BaseObjectOrLink;
import org.w3c.activitystreams.model.ImageImpl;
import org.w3c.activitystreams.model.LinkImpl;
import org.w3c.activitystreams.model.Note;
import org.w3c.activitystreams.model.activity.Create;
import org.w3c.activitystreams.model.actor.Person;

import net.mixednutz.api.activitypub.ActivityPubManager;
import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.IUserSmall;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.Visibility;
import net.mixednutz.app.server.repository.UserProfileRepository;

@Service
public class ActivityPubManagerImpl implements ActivityPubManager {
	
	@Autowired
	private UserProfileRepository profileRepository;
	
	private String getBaseUrl(HttpServletRequest request) {
		try {
			URL baseUrl = new URL(
					request.getScheme(), 
					request.getServerName(), 
					request.getServerPort(), 
					"");
			return baseUrl.toExternalForm();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Something's wrong with creating the baseUrl!", e);
		}
	}
	
	@Autowired
	private NetworkInfo networkInfo;

	@Override
	public Note toNote(ITimelineElement element, Visibility visibility, boolean isRoot) {
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
		note.setId(URI.create(networkInfo.getBaseUrl()+URI_PREFIX+itemid));
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
		note.setAttributedTo(new LinkImpl(element.getPostedByUser().getUrl()));
		switch (visibility.getVisibilityType()) {
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
			note.setTo(new LinkImpl(element.getPostedByUser().getUrl()));
			break;
		case ALL_USERS:
		case WORLD:
			note.setTo(new LinkImpl(BaseObjectOrLink.PUBLIC));
			break;
		}
		//TODO implement tags (when search feature is available in mixednutz)
		return note;
	}
	
	public void initRoot(BaseObjectOrLink root) {
		root.set_Context(BaseObjectOrLink.CONTEXT);
	}

	@Override
	public Person toPerson(IUserSmall user, User nativeUser, HttpServletRequest request, URI userOutbox, boolean isRoot) {
		
		Person person = new Person();
		if (isRoot) initRoot(person);
		person.setId(URI.create(user.getUrl()));
		person.setUrl(user.getUrl());
		person.setName(user.getDisplayName());
		person.setPreferredUsername(user.getUsername()+"@"+networkInfo.getHostName());
		person.setInbox("inbox");
		person.setOutbox(userOutbox);
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
		return person;
	}

	public Create toCreate(ITimelineElement element, String username, HttpServletRequest request) {
		String baseUrl = getBaseUrl(request);
		Create create = new Create();
		create.setActor(new LinkImpl(baseUrl+URI_PREFIX+"/"+username));
		String itemuri = element.getUri();
		if (element instanceof InternalTimelineElement) {
			InternalTimelineElement ite = (InternalTimelineElement)element;
			if (ite.getLatestSuburi()!=null) {
				itemuri = ite.getLatestSuburi();
			}
		}
		create.setObject(new LinkImpl(baseUrl+URI_PREFIX+itemuri));
		create.setId(URI.create(networkInfo.getBaseUrl()+URI_PREFIX+"/Create"+itemuri));
		return create;
	}

}
