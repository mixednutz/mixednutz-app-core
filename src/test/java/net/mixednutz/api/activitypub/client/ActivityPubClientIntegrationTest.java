package net.mixednutz.api.activitypub.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.w3c.activitypub.client.ActivityPubClient;
import org.w3c.activitypub.util.ProblemHandler;
import org.w3c.activitystreams.model.ActorImpl;
import org.w3c.activitystreams.model.BaseObjectOrLink;
import org.w3c.activitystreams.model.LinkImpl;
import org.w3c.activitystreams.model.Note;
import org.w3c.activitystreams.model.activity.Create;
import org.w3c.activitystreams.model.activity.Follow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.mixednutz.app.server.entity.Role;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.UserKey;
import net.mixednutz.app.server.manager.FollowerManager;
import net.mixednutz.app.server.manager.UserKeyManager;
import net.mixednutz.app.server.manager.impl.UserKeyManagerImpl;
import net.mixednutz.app.server.repository.UserKeyRepository;
import net.mixednutz.app.server.repository.UserProfileRepository;
import net.mixednutz.app.server.util.HttpSignaturesUtil;

public class ActivityPubClientIntegrationTest {

	private static final String REMOTE_ACTOR_URI = "https://universeodon.com/users/festaindctest";
	private static final String REMOTE_INBOX = "https://universeodon.com/users/festaindctest/inbox";
	private static final String REMOTE_SHARED_INBOX = "https://universeodon.com/inbox";
	
	UserKeyRepository userKeyRepository;
	UserKeyManager userKeyManager;
	
	User localUser;
	UserKey localUserKey;
	
	RestTemplateBuilder restTemplateBuilder;
	
	ObjectMapper objectMapper;
	
	public Jackson2ObjectMapperBuilderCustomizer customizer() {
	    return new Jackson2ObjectMapperBuilderCustomizer() {
	        @Override
	        public void customize(Jackson2ObjectMapperBuilder builder) {
	        	builder.modulesToInstall(new ProblemHandlerModule());
	        }
	    };
	}
	
	@SuppressWarnings("serial")
	public class ProblemHandlerModule extends SimpleModule {

		@Override
	    public void setupModule(SetupContext context) {
	        // Required, as documented in the Javadoc of SimpleModule
	        super.setupModule(context);
	        context.addDeserializationProblemHandler(new ProblemHandler());
	    } 
		
	}

	
	@BeforeEach
	public void setup() throws Exception {
		localUser = setupLocalUser();
		
		userKeyRepository = mock(UserKeyRepository.class);
		userKeyManager=new UserKeyManagerImpl(userKeyRepository);
		
		
		when(userKeyRepository.findById(eq(localUser.getUserId())))
			.thenReturn(Optional.of(localUserKey));
		
		Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
		customizer().customize(b);
		objectMapper = b.build();
		JavaTimeModule javaTimeModule=new JavaTimeModule();
		objectMapper.registerModule(javaTimeModule);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setPrettyPrint(false);
		messageConverter.setObjectMapper(objectMapper);
		restTemplateBuilder = new RestTemplateBuilder()
				.messageConverters(messageConverter)
				;
	}
	
	
	@Disabled
	@Test
	public void test_getActor() {
		ActivityPubClientManager client = new ActivityPubClientManager(restTemplateBuilder, null, null, null);
		ActorImpl actor = client.getActor(URI.create(REMOTE_ACTOR_URI));
		assertNotNull(actor);
		assertEquals(REMOTE_INBOX, actor.getInbox().toString());
		assertNotNull(actor.getPublicKey());
		assertNotNull(actor.getEndpoints());
		assertTrue(actor.getEndpoints().containsKey("sharedInbox"));
		System.out.println("PreferredName " + actor.getPreferredUsername());
		System.out.println("Inbox: " + actor.getInbox());
		System.out.println("Followers: " + actor.getFollowers());
		System.out.println("Shared Inbox: " + actor.getEndpoints().get("sharedInbox"));
	}
	
	@Disabled
	@Test
	public void test_getActorsFollowers() {
		ActivityPubClient client = new ActivityPubClient(restTemplateBuilder, null);
		
		String followers = client.getFollowers(URI.create(REMOTE_ACTOR_URI));
		System.out.println(followers);
	}
	
	@Disabled
	@Test
	public void test_sendActivity_Single() throws JsonProcessingException {
		String testhost = "https://tfemily.com";
		String localActorUriStr = testhost+"/activitypub/Emily";
		String localFollowersUriStr = testhost+"/activitypub/Emily/followers";
		String replyTo = "https://universeodon.com/@festaindctest/109846300859849576";
		String remoteActorStr = REMOTE_ACTOR_URI;
		//String replyTo = "https://mastodon.social/@Gargron/100254678717223630";
		String destinationInbox = REMOTE_INBOX;
		
		ActivityPubClientManager client = new ActivityPubClientManager(restTemplateBuilder, 
				null, null, userKeyManager);
		
		URI localActorUri = URI.create(localActorUriStr);
		URI remoteActorUri = URI.create(remoteActorStr);
		
		ZonedDateTime publishedDate = ZonedDateTime.now();
		Create activity = new Create();
	    activity.setContext(List.of(BaseObjectOrLink.CONTEXT));
	    activity.setPublished(publishedDate);
	    activity.setId(URI.create("https://tfemily.com/activitypub/Create/Emily/journal/2023/2/8/tv-review-quantum-leap-2022-let-them-play"));
	    activity.setActor(new LinkImpl(localActorUri));
	    activity.setTo(List.of(new LinkImpl(BaseObjectOrLink.PUBLIC), new LinkImpl(remoteActorUri)));
//	    activity.setTo(List.of(new LinkImpl(BaseObjectOrLink.PUBLIC)));
	    activity.setCc(List.of(new LinkImpl(URI.create(localFollowersUriStr))));
	    Note note = new Note();
	    note.setId(URI.create("https://tfemily.com/activitypub/Note/Emily/journal/2023/2/8/tv-review-quantum-leap-2022-let-them-play"));
	    note.setPublished(publishedDate);
	    note.setAttributedTo(new LinkImpl(localActorUri));
//	    note.setInReplyTo(new LinkImpl(replyTo));
	    note.setContent("<p>Hello world 1</p>");
	    note.setTo(activity.getTo());
	    note.setCc(activity.getCc());
//	    activity.setObject(new LinkImpl(URI.create("https://tfemily.com/activitypub/Note/Emily/journal/2023/2/8/tv-review-quantum-leap-2022-let-them-play")));
		
		
//		Follow activity = new Follow();
//		activity.setContext(List.of(BaseObjectOrLink.CONTEXT));
//	    activity.setId(URI.create(testhost+"/my-first-follow"));
//	    activity.setActor(new LinkImpl(localActorUri));
//	    activity.setObject(new LinkImpl(remoteActorUri));
	    
	    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(activity));
		
		client.sendActivity(URI.create(destinationInbox), activity, localUser);
	}
	
	public void test_sendActivity_Multi() {
		String testhost = "https://tfemily.com";
		String actorUriStr = testhost+"/activitypub/Emily";
		String replyTo = "https://mastodon.social/@Gargron/100254678717223630";
//		String destinationInbox = "https://mastodon.social/inbox";
		
		FollowerManager followerManager = mock(FollowerManager.class);
		UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);
		UserKeyRepository userKeyRepository = mock(UserKeyRepository.class);
		UserKeyManager userKeyManager=new UserKeyManagerImpl(userKeyRepository);
		
		ActivityPubClientManager client = new ActivityPubClientManager(new RestTemplateBuilder(), 
				followerManager, userProfileRepository, userKeyManager);
		
		URI actorUri = URI.create(actorUriStr);
		
		User user = null;
		
		Create activity = new Create();
	    activity.setContext(List.of(BaseObjectOrLink.CONTEXT));
	    activity.setId(URI.create(testhost+"/create-hello-world"));
	    activity.setActor(new LinkImpl(actorUri));
	    Note note = new Note();
	    note.setId(URI.create(testhost+"/hello-world"));
	    note.setPublished(ZonedDateTime.now());
	    note.setAttributedTo(new LinkImpl(actorUri));
	    note.setInReplyTo(new LinkImpl(replyTo));
	    note.setContent("<p>Hello world</p>");
	    note.setTo(List.of(new LinkImpl(BaseObjectOrLink.PUBLIC)));
	    activity.setObject(note);
		
		client.sendActivity(user, activity);
	}
	
	private void prettyPrint(Object activity) {
		ObjectMapper mapper = new ObjectMapper();
	    JavaTimeModule javaTimeModule=new JavaTimeModule();
        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's) 
//        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        mapper.registerModule(javaTimeModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    String activityStr;
		try {
			activityStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activity);
			System.out.println("PAYLOAD (pretty print):");
			System.out.println(activityStr);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}
	
	private User setupLocalUser() throws Exception {
		User user = new User();
		user.setUserId(1L);
		user.setUsername("admin");
		user.getRoles().add(new Role(user, "ROLE_ADMIN"));
		
//		KeyPair pair = HttpSignaturesUtil.generateKeyPair();
		File privatePemFile = new File("/home/apfesta/Downloads/private(1).pem");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		String privateKeyStr = new String(Files.readAllBytes(privatePemFile.toPath()), StandardCharsets.UTF_8);
	    privateKeyStr = privateKeyStr
	      .replace("-----BEGIN PRIVATE KEY-----", "")
	      .replaceAll(System.lineSeparator(), "")
	      .replace("-----END PRIVATE KEY-----", "");
	    PKCS8EncodedKeySpec keySpec2 = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(privateKeyStr));
	    PrivateKey privateKey = keyFactory.generatePrivate(keySpec2);

		
		localUserKey = new UserKey();
		localUserKey.setUser(user);
		localUserKey.setUserId(user.getUserId());
		localUserKey.setPrivateKey(privateKey.getEncoded());
//		localUserKey.setPublicKey(pair.getPublic().getEncoded());
		
		return user;
	}
	
}
