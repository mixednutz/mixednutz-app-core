package net.mixednutz.api.webfinger.server;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.mixednutz.api.activitypub.ActivityPubManager;
import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.api.webfinger.WebfingerResponse;
import net.mixednutz.api.webfinger.WebfingerResponse.Link;
import net.mixednutz.app.server.controller.exception.UserNotFoundException;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.repository.UserRepository;

@Component
public class WebfingerServer {
		
	private static final Logger LOG = LoggerFactory.getLogger(WebfingerServer.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NetworkInfo networkInfo;
	
	@Autowired
	private ActivityPubManager activityPubManager;
	
	
	public WebfingerResponse handleWebfingerRequest(URI resource) throws BadRequestException {
		LOG.info("Webfinger lookup for {}",resource);
		String scheme = resource.getScheme();
		URI actorUri = null;
		if ("acct".equals(scheme)) {
			String acc = resource.getSchemeSpecificPart();
			String[] part = acc.split("@");
			if (part.length!=2) {
				throw new BadRequestException("Resource expected to be acct:username@host: "+resource.toString());
			}
			final String username = part[0];
			final String host = part[1];
			LOG.info("Webfinger lookup for username: {} host: {}",username, host);
			
			if (!networkInfo.getHostName().equalsIgnoreCase(host)) {
				throw new BadRequestException("Resource host not found on this server: "+host);
			}
			
			User profileUser = userRepository.findByUsername(username)
					.orElseThrow(new Supplier<UserNotFoundException>(){
						@Override
						public UserNotFoundException get() {
							throw new UserNotFoundException("User "+username+" not found");
						}});
			
			resource = URI.create("acct:"+profileUser.getUsername()+"@"+networkInfo.getHostName());
			actorUri = activityPubManager.getActorUri(profileUser.getUsername());
		} else {
			throw new BadRequestException("Resource expected to be acct:username@host: "+resource.toString());
		}
		
		WebfingerResponse response = new WebfingerResponse(resource, List.of(
				new Link("self","application/activity+json",actorUri.toString())));
		
		return response;
	}

	public static class BadRequestException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3536512365470851068L;

		public BadRequestException(String message) {
			super(message);
		}
		
	}
}
