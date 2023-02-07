package net.mixednutz.api.activitypub;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.w3c.activitystreams.model.BaseObjectOrLink;
import org.w3c.activitystreams.model.Note;
import org.w3c.activitystreams.model.activity.Create;
import org.w3c.activitystreams.model.actor.Person;

import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.IUserSmall;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.Visibility;

public interface ActivityPubManager {
	
	public static final String URI_PREFIX = "/activitypub";
	public static final String USER_ACTOR_ENDPOINT = "/{username}";
	
	URI getActorUri(String username);
	
	void initRoot(BaseObjectOrLink root);

	Note toNote(ITimelineElement element, String authorUsername, Visibility visibility, boolean isRoot);
	
	Person toPerson(IUserSmall user, User nativeUser, HttpServletRequest request, 
			URI id, URI userOutbox, URI userInbox, boolean isRoot);
	
	Create toCreate(ITimelineElement element, String username, HttpServletRequest request);
	
}
