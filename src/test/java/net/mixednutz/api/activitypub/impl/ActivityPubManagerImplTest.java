package net.mixednutz.api.activitypub.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.activitystreams.model.Note;

import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.api.core.model.Visibility;
import net.mixednutz.app.server.entity.InternalTimelineElement;

public class ActivityPubManagerImplTest {
	
	ActivityPubManagerImpl manager;
	
	@BeforeEach
	public void setup() {
		manager = new ActivityPubManagerImpl();
		manager.setNetworkInfo(networkInfo());
	}
	
	private static final String BASEURL = "https://mixednutz.net";
	private static final String TITLE= "Title";
	private static final String SUBTITLE = "Subtitle";
	private static final String DESCRIPTION = "Description";
	private static final String SUBDESCRIPTION = "Subdescription";
	private static final String URI = "/uri";
	private static final String SUBURI= "/uri/suburi";
	private static final String URL = BASEURL + URI;
	private static final String SUBURL = BASEURL + SUBURI;
	
	private NetworkInfo networkInfo() {
		NetworkInfo networkInfo = new NetworkInfo();
		networkInfo.setBaseUrl(BASEURL);
		return networkInfo;
	}
	
	@Test
	public void test_toNote() {
		InternalTimelineElement element = new InternalTimelineElement();
		element.setPostedOnDate(ZonedDateTime.now());
		element.setVisibility(Visibility.toWorld());
		element.setUri(URI);
		element.setUrl(URL);
		element.setTitle(TITLE);
		element.setDescription(DESCRIPTION);
		
		Note note = manager.toNote(element, "Emily", false);
		
		assertEquals("Title", 
				note.getSummary());
		assertEquals("<strong><a href=\"https://mixednutz.net/uri\">Title</a></strong><p>Description</p>", 
				note.getContent());
		assertEquals("https://mixednutz.net/activitypub/Note/uri", 
				note.getId().toString());
		assertEquals("https://mixednutz.net/uri", 
				note.getUrl());
	}
	
	@Test
	public void test_toNote_2() {
		InternalTimelineElement element = new InternalTimelineElement();
		element.setPostedOnDate(ZonedDateTime.now());
		element.setVisibility(Visibility.toWorld());
		element.setUri(URI);
		element.setUrl(URL);
		element.setTitle(TITLE);
		element.setDescription(DESCRIPTION);
		element.setLatestSuburi(SUBURI);
		element.setLatestSuburl(SUBURL);
		element.setLatestSubtitle(SUBTITLE);
		element.setLatestSubdescription(SUBDESCRIPTION);
		
		Note note = manager.toNote(element, "Emily", false);
		
		System.out.println(note.getContent());
		assertEquals("Title - Subtitle", 
				note.getSummary());
		assertEquals("<strong><a href=\"https://mixednutz.net/uri\">Title</a></strong><p>Description</p>"
				+ "<p><a href=\"https://mixednutz.net/uri/suburi\">Subtitle</a> : Subdescription</p>", 
				note.getContent());
		assertEquals("https://mixednutz.net/activitypub/Note/uri/suburi", 
				note.getId().toString());
		assertEquals("https://mixednutz.net/uri/suburi", 
				note.getUrl());
	}
	

}
