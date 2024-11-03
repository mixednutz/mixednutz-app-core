package net.mixednutz.api.activitypub.client;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ietf.webfinger.WebfingerResponse;
import org.ietf.webfinger.client.WebfingerClient;
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
	private WebfingerClient webfingerClient;
	
	@Autowired
	public ActivityPubClientManager(RestTemplateBuilder restTemplateBuilder,
			FollowerManager followerManager, UserProfileRepository userProfileRepository,
			UserKeyManager userKeyManager, WebfingerClient webfingerClient) {
		super();
		this.restTemplateBuilder = restTemplateBuilder;
		this.followerManager = followerManager;
		this.userProfileRepository = userProfileRepository;
		this.userKeyManager = userKeyManager;
		this.webfingerClient = webfingerClient;
	}
	
	public ActorImpl getActor(String fediverseUsername) {
		String[] parts = extractUsernameAndHost(fediverseUsername);
		String preferedName = parts[0];
		String host = parts[1];
		WebfingerResponse wf = webfingerClient.webfinger(preferedName, host);
		WebfingerResponse.Link link = wf.getLinks().stream()
				.filter(l->"application/activity+json".equals(l.getType()))
				.findFirst().orElseThrow(()->new RuntimeException("Unable to get actor URI from webfinger "+fediverseUsername));
		return getActor(URI.create(link.getHref()));
	}
	
	protected String[] extractUsernameAndHost(String fediverseUsername) {
		String username;
		if (fediverseUsername.startsWith("@")) {
			username = fediverseUsername.substring(1);
		} else {
			username = fediverseUsername;
		}
		return username.split("@");
	}

	public ActorImpl getActor(URI actorUri) {
		return new ActivityPubClient(restTemplateBuilder, null).getActor(actorUri);
	}
	
	protected Set<URI> getInboxes(List<URI> followers) {
		
		return followers.stream()
			.map(followerUri->{
				Optional<ActorImpl> actor;
				try {
					actor = Optional.of(getActor(followerUri));
				} catch (HttpClientErrorException e) {
					LOG.warn("Unable to get actor from follower URI {}", followerUri, e);
					actor = Optional.empty();
				}
				return actor;
			})
			.flatMap(Optional::stream)
			.map(actor->{
				if (actor.getEndpoints().containsKey("sharedInbox")) {
					URI sharedInbox = actor.getEndpoints().get("sharedInbox");
					return sharedInbox;
				} else {
					return actor.getInbox();
				}
			
			})
			.collect(Collectors.toSet());
	}
	
	/**
	 * Sends to all user's followers
	 * 
	 * @param user
	 * @param activity
	 */
	public void sendActivity(User user, ActivityImpl activity) {
		
		List<URI> followers = followerManager.getAllFollowers(user).stream()
			.map(f->userProfileRepository.findById(f.getId().getFollowerId()).orElse(new UserProfile()))
			.filter(p->p.getActivityPubActorUri()!=null)
			.map(p->URI.create(p.getActivityPubActorUri()))
			.collect(Collectors.toList());
		
		boolean hasPublicCollection = activity.getTo().stream()
			.filter(to->(to instanceof Link))
			.map(to->(Link)to)
			.anyMatch(to->BaseObjectOrLink.PUBLIC.equals(to.getHref().toString()));
		
		if (hasPublicCollection) {
			Set<URI> inboxes = getInboxes(followers);
			LOG.info("Sending activity to {} inboxes", inboxes.size());
			inboxes.forEach(inbox->{
				try {
					sendActivity(inbox, activity, user);
				} catch (Exception e) {
					// Log and swallow error
					LOG.error("Unable to post to inbox {}",inbox, e);
				}
			});
			
		} else {
//			throw new RuntimeException("Cannot determine destination Inbox from addessing!");
		}
		
	}
	
	/**
	 * Sends to a single inbox.
	 * 
	 * @param destinationInbox
	 * @param activity
	 * @param user
	 */
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
