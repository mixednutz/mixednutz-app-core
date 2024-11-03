package net.mixednutz.api.webfinger.server;

import java.net.URI;
import java.util.function.Supplier;

import org.ietf.webfinger.server.WebfingerApplicationCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.mixednutz.api.activitypub.ActivityPubManager;
import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.app.server.controller.exception.UserNotFoundException;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.repository.UserRepository;

@Component
public class WebfingerApplicatonCallbackImpl implements WebfingerApplicationCallback {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NetworkInfo networkInfo;
	
	@Autowired
	private ActivityPubManager activityPubManager;

	@Override
	public boolean isHostSupported(URI resource, String host) {
		return networkInfo.getHostName().equalsIgnoreCase(host);
	}
	
	protected User getUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(new Supplier<UserNotFoundException>(){
					@Override
					public UserNotFoundException get() {
						throw new UserNotFoundException("User "+username+" not found");
					}});
	}

	@Override
	public URI getActorUri(URI resource, String host, String username) {
		User profileUser = getUser(username);
		
		return activityPubManager.getActorUri(profileUser.getUsername());
	}

	@Override
	public URI newResource(URI resource, String host, String username) {
		User profileUser = getUser(username);
		
		return URI.create("acct:"+profileUser.getUsername()+"@"+networkInfo.getHostName());
	}

}
