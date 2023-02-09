package net.mixednutz.api.activitypub.client;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.activitystreams.model.ActorImpl;

public class ActivityPubClient {
	
	private RestTemplate rest;
	
	@Autowired
	public ActivityPubClient(RestTemplateBuilder restTemplateBuilder) {
		super();
		this.rest = restTemplateBuilder.build();
	}

	public ActorImpl getActor(URI actorUri) {
		ResponseEntity<ActorImpl> response = rest.getForEntity(actorUri, ActorImpl.class);
		return response.getBody();
	}
	
}
