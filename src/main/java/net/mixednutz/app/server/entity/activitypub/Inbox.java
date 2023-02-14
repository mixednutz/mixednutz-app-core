package net.mixednutz.app.server.entity.activitypub;

import java.net.URI;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="ActivityPub_Inbox")
public class Inbox {
	
	private Long id;
	
	private ZonedDateTime recievedDate;
	
	private String headers;
	
	private String payload;
	
	private URI activityId;
	
	private String type;
	
	private URI actor;
	
	private boolean processed = false;

	@Id
	@GeneratedValue(generator="system-native")
	@GenericGenerator(name="system-native", strategy = "native")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public URI getActivityId() {
		return activityId;
	}

	public void setActivityId(URI activityId) {
		this.activityId = activityId;
	}

	public ZonedDateTime getRecievedDate() {
		return recievedDate;
	}

	public void setRecievedDate(ZonedDateTime recievedDate) {
		this.recievedDate = recievedDate;
	}

	@Lob
	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	@Lob
	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public URI getActor() {
		return actor;
	}

	public void setActor(URI actor) {
		this.actor = actor;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	

}
