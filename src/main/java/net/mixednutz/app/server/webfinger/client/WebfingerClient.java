package net.mixednutz.app.server.webfinger.client;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import net.mixednutz.app.server.webfinger.WebfingerResponse;

import static net.mixednutz.app.server.webfinger.WebfingerSettings.*;

@Component
public class WebfingerClient {

	private RestTemplate rest;
	
	@Autowired
	public WebfingerClient(RestTemplateBuilder restTemplateBuilder) {
		super();
		this.rest = restTemplateBuilder.build();
	}

	public ResponseEntity<WebfingerResponse> webfinger(String preferredName, String host) {
		URI resource = URI.create("acct:"+preferredName+"@"+host);
		
		URI endpoint = UriComponentsBuilder.fromUriString("https://"+host+WEBFINGER_ENDPOINT)
			.queryParam("resource", resource.toString())
			.build().toUri();
		
		return rest.getForEntity(endpoint, WebfingerResponse.class);		
	}
	
}
