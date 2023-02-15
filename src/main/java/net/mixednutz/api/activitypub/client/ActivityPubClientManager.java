package net.mixednutz.api.activitypub.client;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.w3c.activitypub.client.ActivityPubClient;
import org.w3c.activitystreams.Link;
import org.w3c.activitystreams.model.ActivityImpl;
import org.w3c.activitystreams.model.ActorImpl;
import org.w3c.activitystreams.model.BaseObjectOrLink;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.UserProfile;
import net.mixednutz.app.server.manager.FollowerManager;
import net.mixednutz.app.server.manager.UserKeyManager;
import net.mixednutz.app.server.repository.UserProfileRepository;

@Service
public class ActivityPubClientManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(ActivityPubClientManager.class);
	
	private RestTemplateBuilder restTemplateBuilder;
	private FollowerManager followerManager;
	private UserProfileRepository userProfileRepository;
	private UserKeyManager userKeyManager;
	
	@Autowired
	public ActivityPubClientManager(RestTemplateBuilder restTemplateBuilder,
			FollowerManager followerManager, UserProfileRepository userProfileRepository,
			UserKeyManager userKeyManager) {
		super();
		this.restTemplateBuilder = restTemplateBuilder;
		this.followerManager = followerManager;
		this.userProfileRepository = userProfileRepository;
		this.userKeyManager = userKeyManager;
	}

	public ActorImpl getActor(URI actorUri) {
		return new ActivityPubClient(restTemplateBuilder, null).getActor(actorUri);
	}
	
	public void sendActivity(User user, ActivityImpl activity) {
		List<URI> followers = followerManager.getFollowers(user).stream()
			.map(f->userProfileRepository.findById(f.getId().getFollowerId()).orElse(new UserProfile()))
			.filter(p->p.getActivityPubActorUri()!=null)
			.map(p->URI.create(p.getActivityPubActorUri()))
			.collect(Collectors.toList());
		
		Optional<Link> publicCollection = activity.getTo().stream()
			.filter(to->(to instanceof Link))
			.map(to->(Link)to)
			.filter(to->BaseObjectOrLink.PUBLIC.equals(to.getHref().toString()))
			.findAny();
		
		if (publicCollection.isPresent()) {
			for (URI followerUri: followers) {
				ActorImpl actor = null;
				try {
					actor = getActor(followerUri);
				} catch (HttpClientErrorException e) {
					LOG.warn("Unable to get actor from follower URI {}", followerUri, e);
				}
				//TODO check for shared inbox and group sending together
				if (actor!=null && actor.getInbox()!=null) {
//					sendActivity(actor.getInbox(), activity, user);
				}
			}
		} else {
//			throw new RuntimeException("Cannot determine destination Inbox from addessing!");
		}
		
	}
	
	public void sendActivity(URI destinationInbox, ActivityImpl activity, User user) {
		ActivityPubClient client = new ActivityPubClient(restTemplateBuilder, 
				(request, body)->{
					userKeyManager.signRequest(request, user, 
							activity.getActor().isLink() ? 
									((Link)activity.getActor()).getHref() : 
										((ActorImpl)activity.getActor()).getId(), 
							body);
				});
		client.sendActivity(destinationInbox, activity);		
	}
	
	
	
}
