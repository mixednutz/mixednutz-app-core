package net.mixednutz.app.server.manager.impl;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.activitystreams.model.ActorImpl;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.UserKey;
import net.mixednutz.app.server.manager.UserKeyManager;
import net.mixednutz.app.server.repository.UserKeyRepository;
import net.mixednutz.app.server.util.HttpSignaturesUtil;

@Transactional
@Service
public class UserKeyManagerImpl implements UserKeyManager {
	
	
	private UserKeyRepository userKeyRepository;
	
	@Autowired
	public UserKeyManagerImpl(UserKeyRepository userKeyRepository) {
		super();
		this.userKeyRepository = userKeyRepository;
	}

	public void generateKeyPair(User user) {
		KeyPair pair = HttpSignaturesUtil.generateKeyPair();
		
		UserKey userKey = new UserKey();
		userKey.setUser(user);
		userKey.setUserId(user.getUserId());
		userKey.setPrivateKey(pair.getPrivate().getEncoded());
		userKey.setPublicKey(pair.getPublic().getEncoded());
		userKeyRepository.save(userKey);
	}
	
	private Optional<String> getPublicKeyPem(User user) {
		return userKeyRepository.findById(user.getUserId())
				.map(userKey->
				HttpSignaturesUtil.publicKeyBytesToPem(userKey.getPublicKey()));
	}
	
	public void setPublicKeyPem(User user, ActorImpl actor) {
		String pem = getPublicKeyPem(user).orElseGet(() -> {
			generateKeyPair(user);
			return getPublicKeyPem(user).get();
		});
		
		actor.setPublicKey(new org.w3c.activitystreams.model.PublicKey(
				UserKeyManager.KEY_NAME,
				actor.getId(),pem));
	}
	
	public void signRequest(HttpRequest request, User user, URI actorUri, byte[] body) {
		signRequest(user, request.getMethod(), request.getHeaders(), actorUri,
				request.getURI(), body);
	}
	
	protected void signRequest(User user, HttpMethod method, HttpHeaders headers, URI actorUri, URI destination,
			byte[] body)  {
		
		PrivateKey privateKey = userKeyRepository.findById(user.getUserId())
				.map(userKey->
				HttpSignaturesUtil.getPrivateKeyFromBytes(userKey.getPrivateKey()))
				.orElseThrow(()->new RuntimeException("No Private Key Found"));
		
		HttpSignaturesUtil.signRequest(destination, method, headers, body, 
				privateKey, actorUri.toString()+"#"+UserKeyManager.KEY_NAME);
	}
	
	protected void verfiyRequest(HttpMethod method, HttpHeaders headers, 
			ActorImpl actor, URI destination) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		
		HttpSignaturesUtil.verifyRequest(
				destination, method, headers,
				keyId->HttpSignaturesUtil.getPublicKeyFromPem(actor.getPublicKey().getPublicKeyPem()));
	}
	
	

}
