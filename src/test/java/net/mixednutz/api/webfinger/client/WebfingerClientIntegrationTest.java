package net.mixednutz.api.webfinger.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import net.mixednutz.api.webfinger.WebfingerResponse;
import net.mixednutz.api.webfinger.WebfingerResponse.Link;


public class WebfingerClientIntegrationTest {

	
	private static final String PREFERRED_NAME = "x";
	private static final String HOST = "host";
	private static final String EXPECTED_ACTOR_URI = "actor";
	
	@Disabled
	@Test
	public void test() {
		WebfingerClient client = new WebfingerClient(new RestTemplateBuilder());
		WebfingerResponse response = client.webfinger(PREFERRED_NAME, HOST);
		assertNotNull(response);
		Link link = response.getLinks().stream()
				.filter(l->"application/activity+json".equals(l.getType()))
				.findFirst().get();
		assertEquals(EXPECTED_ACTOR_URI,link.getHref());
	}
	
}
