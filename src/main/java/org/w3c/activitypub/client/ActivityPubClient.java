package org.w3c.activitypub.client;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.activitystreams.model.ActivityImpl;
import org.w3c.activitystreams.model.ActorImpl;

public class ActivityPubClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(ActivityPubClient.class);
	
	private RestTemplateBuilder restTemplateBuilder;
	private RequestSigner requestSigner;

	public ActivityPubClient(RestTemplateBuilder restTemplateBuilder, RequestSigner requestSigner) {
		super();
		this.restTemplateBuilder = restTemplateBuilder;
		this.requestSigner = requestSigner;
	}

	public interface RequestSigner {
		void signRequest(HttpRequest request, byte[] body);
	}
	
	public ActorImpl getActor(URI actorUri) {
		RestTemplate rest = restTemplateBuilder.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(MediaType.parseMediaTypes(
				"application/ld+json; profile=\"w3.org/ns/activitystreams\""));
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		
		try {
			ResponseEntity<ActorImpl> response = rest.exchange(actorUri, HttpMethod.GET, 
					requestEntity, ActorImpl.class);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			LOG.error("Error sending activity", e);
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			throw new RuntimeException(e);
		}
		

	}
	
	public void sendActivity(URI destinationInbox, ActivityImpl activity) {
		
		// INTERCEPTOR
		RestTemplate rest = restTemplateBuilder.additionalInterceptors(
				(request, body, execution) -> {
					requestSigner.signRequest(request, body);
					return execution.execute(request, body);
				}).build();
		

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(ActivityImpl.APPLICATION_ACTIVITY);
		headers.setAccept(MediaType.parseMediaTypes(
				"application/ld+json; profile=\"https://www.w3.org/ns/activitystreams\""));
		HttpEntity<ActivityImpl> requestEntity = new HttpEntity<>(activity, headers);
		
		try {
			ResponseEntity<String> response = rest.exchange(
					destinationInbox, HttpMethod.POST, 
					requestEntity, String.class);
			
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
		} catch (HttpClientErrorException e) {
			LOG.error("Error sending activity", e);
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			throw new RuntimeException(e);
		}
		
	}

	
}
