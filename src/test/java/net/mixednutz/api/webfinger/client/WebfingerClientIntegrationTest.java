package net.mixednutz.api.webfinger.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.ietf.webfinger.WebfingerResponse;
import org.ietf.webfinger.WebfingerResponse.Link;
import org.ietf.webfinger.client.WebfingerClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;


public class WebfingerClientIntegrationTest {

	
	private static final String PREFERRED_NAME = "festaindctest";
	private static final String HOST = "universeodon.com";
	private static final String EXPECTED_ACTOR_URI = "https://universeodon.com/users/festaindctest";
	
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
