package net.mixednutz.api.activitypub;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.w3c.activitystreams.model.BaseObjectOrLink;
import org.w3c.activitystreams.model.Note;
import org.w3c.activitystreams.model.activity.Accept;
import org.w3c.activitystreams.model.activity.Create;
import org.w3c.activitystreams.model.activity.Follow;
import org.w3c.activitystreams.model.actor.Person;

import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.IUserSmall;
import net.mixednutz.app.server.entity.User;

public interface ActivityPubManager {
	
	public static final String URI_PREFIX = "/activitypub";
	public static final String USER_ACTOR_ENDPOINT = "/{username}";
	public static final String CREATE_URI_PREFIX = URI_PREFIX + "/Create";
	public static final String NOTE_URI_PREFIX = URI_PREFIX + "/Note";
	
	URI getActorUri(String username);
	
	void initRoot(BaseObjectOrLink root);

	Note toNote(ITimelineElement element, String authorUsernam, boolean isRoot);
	
	Person toPerson(IUserSmall user, User nativeUser, HttpServletRequest request, 
			URI id, URI userOutbox, URI userInbox, URI followers, URI following, 
			boolean isRoot);
	
	Create toCreate(ITimelineElement element, Note note, String username);
	
	Create toCreateNote(ITimelineElement element, String authorUsername);
	
	Accept toAccept(String username, Follow follow);
		
	
}
