package net.mixednutz.api.activitypub.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.w3c.activitystreams.model.ActorImpl;

public class ActivityPubClientIntegrationTest {

	private static final String ACTOR_URI = "x";
	
	@Disabled
	@Test
	public void test() {
		ActivityPubClient client = new ActivityPubClient(new RestTemplateBuilder());
		ActorImpl actor = client.getActor(URI.create(ACTOR_URI));
		assertNotNull(actor);
		assertNotNull(actor.getInbox());
		assertNotNull(actor.getPublicKey());
		System.out.println("PreferredName " + actor.getPreferredUsername());
		System.out.println("Inbox: " + actor.getInbox());
	}
	
}
