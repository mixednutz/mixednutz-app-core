package net.mixednutz.app.server.manager;

import java.net.URI;

import org.springframework.http.HttpRequest;
import org.w3c.activitystreams.model.ActorImpl;

import net.mixednutz.app.server.entity.User;

public interface UserKeyManager {
	
	public static final String KEY_NAME = "main-key";
	
	void generateKeyPair(User user);
	
	void setPublicKeyPem(User user, ActorImpl actor);
	
	void signRequest(HttpRequest request, User user, URI actorUri, byte[] body);

}
