package net.mixednutz.app.server.manager.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.ietf.webfinger.WebfingerResponse;
import org.ietf.webfinger.WebfingerResponse.Link;
import org.ietf.webfinger.client.WebfingerClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.activitypub.client.ActivityPubClient;
import org.w3c.activitystreams.model.ActivityImpl;
import org.w3c.activitystreams.model.ActorImpl;
import org.w3c.activitystreams.model.BaseObjectOrLink;
import org.w3c.activitystreams.model.LinkImpl;
import org.w3c.activitystreams.model.Note;
import org.w3c.activitystreams.model.activity.Create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.mixednutz.api.activitypub.client.ActivityPubClientManager;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.UserKey;
import net.mixednutz.app.server.manager.UserKeyManager;
import net.mixednutz.app.server.repository.UserKeyRepository;
import net.mixednutz.app.server.util.HttpSignaturesUtil;

public class UserKeyManagerImplTest {

	@Disabled
	@Test
	public void test() throws Exception {
		UserKeyRepository userKeyRepository = mock(UserKeyRepository.class);
		UserKeyManagerImpl manager = new UserKeyManagerImpl(userKeyRepository);
		
				
		Map<Long, UserKey> saved = new HashMap<>();
		when(userKeyRepository.save(any()))
			.thenAnswer(inv->{
				UserKey e = inv.getArgument(0, UserKey.class);
				return saved.put(e.getUserId(), e);
			});
		when(userKeyRepository.findById(anyLong()))
			.thenAnswer(inv->
				Optional.ofNullable(saved.get(inv.getArgument(0, Long.class))));
		
		User user = new User();
		user.setUserId(1L);
		
		manager.generateKeyPair(user);
		
		assertFalse(saved.isEmpty());
		
		// SHOW PUBLIC KEY PEM
		URI actorUri = URI.create("https://andrewfesta.com/activitypub/admin");
		ActorImpl actor = new ActorImpl();
		actor.setId(actorUri);
		manager.setPublicKeyPem(user, actor);
		System.out.println(actor.getPublicKey());
		
		// WRITE ACTOR JSON
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(actor));
		
	    Create activity = new Create();
	    activity.setId(URI.create("https://andrewfesta.com/create-hello-world"));
	    activity.setActor(new LinkImpl(actorUri));
	    Note note = new Note();
	    note.setId(URI.create("https://andrewfesta.com/hello-world"));
	    note.setAttributedTo(new LinkImpl(actorUri));
	    note.setInReplyTo(new LinkImpl("https://mastodon.social/@Gargron/100254678717223630"));
	    note.setContent("<p>Hello world</p>");
	    note.setTo(List.of(new LinkImpl(BaseObjectOrLink.PUBLIC)));
	    activity.setObject(note);
		
		// SIGN REQUEST
	    byte[] body = mapper.writeValueAsBytes(activity);
	    HttpHeaders headers = new HttpHeaders();
		URI destination = URI.create("https://mastodon.social/inbox");
		manager.signRequest(user, HttpMethod.POST, headers, actorUri, 
				destination, body);
		
		System.out.println(headers);
		
		headers.setHost(new InetSocketAddress(InetAddress.getByName("mastodon.social"),0));
		
		// VERFIY
		manager.verfiyRequest(HttpMethod.POST, headers, actor, destination);
	}
	
	@Disabled
	@Test
	public void testLive() throws FileNotFoundException, IOException, Exception {
		File privatePemFile = new File("/home/apfesta/Downloads/private(1).pem");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		// LOOKUP USER AND ASSERT WEBFINGER
		URI actorUri = URI.create("https://tfemily.com/activitypub/Emily");
		ActivityPubClientManager apClient = new ActivityPubClientManager(
				new RestTemplateBuilder(), null, null, null, null);
		ActorImpl actor = apClient.getActor(actorUri);
		WebfingerClient wfClient = new WebfingerClient(new RestTemplateBuilder());
		WebfingerResponse wf = wfClient.webfinger(actor.getPreferredUsername(), "tfemily.com");
		Link link = wf.getLinks().stream()
				.filter(l->"application/activity+json".equals(l.getType()))
				.findFirst().get();
		if (!link.getHref().equals(actorUri.toString())) {
			throw new RuntimeException("Webfinger and Actor URI must match");
		}
		
		//READ PUBLIC KEY
		String publicKeyStr = actor.getPublicKey().getPublicKeyPem();
		System.out.println("PUBLIC KEY:");
		System.out.println(publicKeyStr);
		publicKeyStr = publicKeyStr
			      .replace("-----BEGIN PUBLIC KEY-----", "")
			      .replaceAll(System.lineSeparator(), "")
			      .replace("-----END PUBLIC KEY-----", "");
	    X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(publicKeyStr));
	    PublicKey publicKey = keyFactory.generatePublic(keySpec1);
	    assertNotNull(publicKey);
	    
	    //READ PRIVATE KEY
	    String privateKeyStr = new String(Files.readAllBytes(privatePemFile.toPath()), StandardCharsets.UTF_8);
	    privateKeyStr = privateKeyStr
	      .replace("-----BEGIN PRIVATE KEY-----", "")
	      .replaceAll(System.lineSeparator(), "")
	      .replace("-----END PRIVATE KEY-----", "");
	    PKCS8EncodedKeySpec keySpec2 = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(privateKeyStr));
	    PrivateKey privateKey = keyFactory.generatePrivate(keySpec2);

	    
	    Create activity = new Create();
	    activity.setContext(List.of(BaseObjectOrLink.CONTEXT));
	    activity.setId(URI.create("https://tfemily.com/create-hello-world"));
	    activity.setActor(new LinkImpl(actorUri));
	    Note note = new Note();
	    note.setId(URI.create("https://tfemily.com/hello-world"));
	    note.setPublished(ZonedDateTime.now());
	    note.setAttributedTo(new LinkImpl(actorUri));
//	    note.setInReplyTo(new LinkImpl("https://mastodon.social/@Gargron/100254678717223630"));
	    note.setInReplyTo(new LinkImpl("https://universeodon.com/@festaindctest/109846300859849576"));
	    note.setContent("<p>Test1</p>");
	    note.setTo(List.of(new LinkImpl(BaseObjectOrLink.PUBLIC)));
	    activity.setObject(note);
	    
	    ObjectMapper mapper = new ObjectMapper();
	    JavaTimeModule javaTimeModule=new JavaTimeModule();
        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's) 
//        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        mapper.registerModule(javaTimeModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    String activityStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activity);
	    System.out.println("PAYLOAD (pretty print):");
	    System.out.println(activityStr);
	    
	    ActivityPubClient client = new ActivityPubClient(new RestTemplateBuilder(),
	    		(request, body)->HttpSignaturesUtil.signRequest(
						request.getURI(), request.getMethod(), 
						request.getHeaders(), body, 
						privateKey, actorUri.toString()+"#"+UserKeyManager.KEY_NAME));
	    
	    client.sendActivity(URI.create("https://universeodon.com/users/festaindctest/inbox"), activity);
	    
	    // SIGN REQUEST
//		RestTemplate rest = new RestTemplateBuilder()
//				.additionalInterceptors((request, body, execution)->{
//					HttpSignaturesUtil.signRequest(
//							request.getURI(), request.getMethod(), 
//							request.getHeaders(), body, 
//							privateKey, actorUri.toString()+"#"+UserKeyManager.KEY_NAME);
//					System.out.println(request.getHeaders());
//					return execution.execute(request, body);
//				}).build();
//		
//		try {
//			ResponseEntity<String> response = rest.exchange(
//					"https://universeodon.com/users/festaindctest/inbox", HttpMethod.POST, 
//					new HttpEntity<ActivityImpl>(activity), String.class);
//			System.out.println(response.getStatusCode());
//			System.out.println(response.getHeaders());
//			System.out.println(response.getBody());
//		} catch (HttpClientErrorException e) {
//			System.out.println(e.getStatusCode()+" - "+e.getResponseBodyAsString());
//		}
	}
	
	
}
