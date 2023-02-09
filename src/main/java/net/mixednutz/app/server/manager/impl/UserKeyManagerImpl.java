package net.mixednutz.app.server.manager.impl;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Transactional
@Service
public class UserKeyManagerImpl implements UserKeyManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserKeyManagerImpl.class);
	
	@Autowired
	UserKeyRepository userKeyRepository;
	
	private KeyPairGenerator generator;
	private KeyFactory keyFactory;
	
	
	DateTimeFormatter httpDateFormatter =
				DateTimeFormatter
					.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
					.withZone(ZoneId.of("GMT"));
	@PostConstruct
	public void init() throws Exception {
		generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		keyFactory = KeyFactory.getInstance("RSA");
		
	}
	
	public void generateKeyPair(User user) {
		KeyPair pair = generator.generateKeyPair();
		
		UserKey userKey = new UserKey();
		userKey.setUser(user);
		userKey.setUserId(user.getUserId());
		userKey.setPrivateKey(pair.getPrivate().getEncoded());
		userKey.setPublicKey(pair.getPublic().getEncoded());
		userKeyRepository.save(userKey);
	}
	
	private PrivateKey getPrivateKey(UserKey userKey) throws InvalidKeySpecException {
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(userKey.getPrivateKey());
		return keyFactory.generatePrivate(privateKeySpec);
	}
	
	private String publicKeyBytesToPem(byte[] key) {
		return new StringBuffer()
				.append("-----BEGIN PUBLIC KEY-----")
					.append(System.lineSeparator())
				.append(Base64.getMimeEncoder().encodeToString(key))
					.append(System.lineSeparator())
				.append("-----END PUBLIC KEY-----")
				.toString();
	}
	
	private byte[] publicKeyPemToBytes(String pem) {
		String publicKeyPEM = pem
			      .replace("-----BEGIN PUBLIC KEY-----", "")
			      .replaceAll(System.lineSeparator(), "")
			      .replace("-----END PUBLIC KEY-----", "");
		return Base64.getMimeDecoder().decode(publicKeyPEM);
	}
	
	private Optional<String> getPublicKeyPem(User user) {
		return userKeyRepository.findById(user.getUserId())
				.map(userKey->publicKeyBytesToPem(userKey.getPublicKey()));
	}
	
	public PublicKey getPublicKey(String pem) {
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				publicKeyPemToBytes(pem));
		try {
			return keyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
	
	public void signRequest(User user, HttpMethod method, HttpHeaders headers, URI actorUri, URI destination,
			byte[] body)  {
		
		// HOST & PATH
		String path = destination.getPath();
		String host = destination.getHost();
		if (destination.getPort()!=-1) {
			host+=":"+destination.getPort();
		}
		
		// DATE
		ZonedDateTime date = ZonedDateTime.now();
		headers.setDate(date);
		
		// DIGEST
		String digestHeader = null;
		if (body!=null) {
			digestHeader = "SHA-256=";
			try {
				digestHeader+=Base64.getEncoder().encodeToString(
						MessageDigest.getInstance("SHA-256").digest(body));
			}catch(NoSuchAlgorithmException ignore){}
		}
		
		if(digestHeader!=null) {
			headers.set("Digest", digestHeader);
		}
		
		// STRING TO SIGN
		StringBuffer strToSign = new StringBuffer()
			.append("(request-target): ")
				.append(method.name().toLowerCase()).append(" ")
				.append(path).append('\n')
			.append("host: ").append(host).append('\n')
			.append("date: ")
				.append(httpDateFormatter.format(date));			
		if(digestHeader!=null) {
			strToSign.append('\n')
			.append("digest: ").append(digestHeader);
		}
		
		// SIGN STRING
		Signature sig;
		byte[] signature;
		try {
			sig = Signature.getInstance("SHA256withRSA");
			Optional<UserKey> userKey = userKeyRepository.findById(user.getUserId());
			if (userKey.isPresent()) {
				PrivateKey s = getPrivateKey(userKey.get());
				sig.initSign(s);
			} else {
				throw new RuntimeException("No private key found for user "+user.getUserId());
			}
			LOG.info("Signature string: \n{}", strToSign);
			sig.update(strToSign.toString().getBytes(StandardCharsets.UTF_8));
			signature = sig.sign();
		} catch (Exception e) {
			LOG.error("Exception while signing request", e);
			throw new RuntimeException(e);
		}
		
		// SIGNATURE HEADER
		String sigHeader = new StringBuffer()
				.append("keyId=\""+actorUri.toString()+"#"+UserKeyManager.KEY_NAME+"\",")
				.append("headers=\"(request-target) host date"+(digestHeader!=null ? " digest\"," : "\","))
				.append("algorithm=\"rsa-sha256\",")
				.append("signature=\"").append(Base64.getEncoder()
						.encodeToString(signature)).append("\"")
				.toString();
		LOG.info("Signature: {}", sigHeader);
		headers.set("Signature", sigHeader);
	}
	
	public void verfiyRequest(HttpMethod method, HttpHeaders headers, ActorImpl actor, URI destination) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		String sigHeader = headers.getFirst("Signature");
		if(sigHeader==null) {
			throw new RuntimeException("Request is missing Signature header");
		}
		String[] parts = sigHeader.split(",");
		if(parts.length<=1) {
			throw new RuntimeException("Signature header has invalid format");
		}
		
		Map<String, String> values = new HashMap<>();
		for (String part: parts) {
			String[] pair = part.split("=");
			String value = pair[1].replace("\"", "");
			values.put(pair[0], value);
		}
		
		if(!values.containsKey("algorithm") ||
				!"rsa-sha256".equalsIgnoreCase(values.get("algorithm"))) {
			throw new RuntimeException("Signature header is missing algorithm");
		}
		if(!values.containsKey("keyId"))
			throw new RuntimeException("Signature header is missing keyId field");
		if(!values.containsKey("signature"))
			throw new RuntimeException("Signature header is missing signature field");
		if(!values.containsKey("headers"))
			throw new RuntimeException("Signature header is missing headers field");
		
		System.out.println(values.get("signature"));
		byte[] signature=Base64.getDecoder().decode(values.get("signature"));
		List<String> sigHeaders = Arrays.asList(values.get("headers").split(" "));
		
		if(!sigHeaders.contains("(request-target)"))
			throw new RuntimeException("(request-target) is not in signed headers");
		if(!sigHeaders.contains("date"))
			throw new RuntimeException("date is not in signed headers");
		if(!sigHeaders.contains("host"))
			throw new RuntimeException("host is not in signed headers");
		
		long unixtime=headers.getDate();
		long now=Instant.now().toEpochMilli();
		long diff=now-unixtime;
		if(diff>30000L)
			throw new RuntimeException("Date is too far in the future (difference: "+diff+"ms)");
		if(diff<-30000L)
			throw new RuntimeException("Date is too far in the past (difference: "+diff+"ms)");

		//TODO get live actor
		
		List<String> sigParts=new ArrayList<>();
		for(String header:sigHeaders){
			String value;
			if(header.equals("(request-target)")){
				value=method.name().toLowerCase()+" "+destination.getPath();
			}else{
				value=headers.getFirst(header);
			}
			sigParts.add(header+": "+value);
		}
		String sigStr=String.join("\n", sigParts);
		Signature sig=Signature.getInstance("SHA256withRSA");
		sig.initVerify(getPublicKey(actor.getPublicKey().getPublicKeyPem()));
		sig.update(sigStr.getBytes(StandardCharsets.UTF_8));
		if(!sig.verify(signature)){
			LOG.info("Failed signature header: {}", sigHeader);
			LOG.info("Failed signature string: \n{}", sigStr);
			throw new RuntimeException("Signature failed to verify");
		}
	}
	
	

}
