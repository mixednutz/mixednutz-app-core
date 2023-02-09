package net.mixednutz.app.server.webfinger.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;

import net.mixednutz.app.server.webfinger.WebfingerResponse;
import net.mixednutz.app.server.webfinger.WebfingerResponse.Link;


public class WebfingerClientIntegrationTest {

	@Disabled
	@Test
	public void test() {
		WebfingerClient client = new WebfingerClient(new RestTemplateBuilder());
		ResponseEntity<WebfingerResponse> response = client.webfinger("x", "host");
		assertNotNull(response.getBody());
		Link link = response.getBody().getLinks().stream()
				.filter(l->"application/activity+json".equals(l.getType()))
				.findFirst().get();
		assertEquals("actor",link.getHref());
	}
	
}
