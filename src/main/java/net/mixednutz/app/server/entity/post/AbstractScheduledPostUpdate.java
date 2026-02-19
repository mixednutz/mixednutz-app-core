package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.mixednutz.app.server.entity.Visibility;

@Entity
@Table(name="ScheduledUpdate")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class AbstractScheduledPostUpdate implements ScheduledPostUpdate {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private Long id;
    private String type;
    
	private ZonedDateTime effectiveDate;
	
	/* Changable Post fields */
	private Visibility visibility;
	
	/* Transient fields */
	
	private Long[] externalFeedId;
	private Map<String,Object> externalFeedData = new HashMap<>();
	
	private Boolean executed=false;
	
	public abstract Post<?> post();
	public abstract Post<?> inReplyTo();
		
	public AbstractScheduledPostUpdate(String type) {
		super();
		this.type = type;
	}
	
	protected AbstractScheduledPostUpdate(String type, ZonedDateTime effectiveDate) {
		this(type);
		this.effectiveDate = effectiveDate;
	}
	
	@Id
	@GeneratedValue(generator="system-native")
	@GenericGenerator(name="system-native", strategy = "native")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="type",insertable=false, updatable=false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Transient
	public String getUri() {
		return post().getUri();
	}

	@Column(nullable=false)
	public ZonedDateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(ZonedDateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	
	@Transient
	public Long[] getExternalFeedId() {
		return externalFeedId;
	}
	@Column(name="externalFeedIds")
	public String getExternalFeedIdString() {
		if (externalFeedId!=null) {
			return Arrays.stream(externalFeedId).map(String::valueOf).collect(Collectors.joining(","));
		}
		return null;
	}
	public void setExternalFeedId(Long[] externalFeedId) {
		this.externalFeedId = externalFeedId;
	}
	public void setExternalFeedIdString(String externalFeedIdString) {
		if (externalFeedIdString!=null) {
			this.externalFeedId = Arrays.stream(externalFeedIdString.split(",")).map(Long::parseLong).toArray(Long[]::new);
		}
	}
	
	private String toJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private <T> T fromJson(String str, TypeReference<T> clazz) {
		try {
			return objectMapper.readValue(str, clazz);	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Transient
	public Map<String, Object> getExternalFeedData() {
		return externalFeedData;
	}
	@Lob
	@Column(name="externalFeedData")
	public String getExternalFeedDataString() {
		if (externalFeedData!=null) {
			return toJson(externalFeedData);
		}
		return null;
	}

	public void setExternalFeedData(Map<String, Object> externalFeedData) {
		this.externalFeedData = externalFeedData;
	}
	public void setExternalFeedDataString(String externalFeedDataString) {
		if (externalFeedDataString!=null) {
			this.externalFeedData = this.fromJson(externalFeedDataString, 
					new TypeReference<Map<String, Object>>(){});
		}
	}

	public Boolean isExecuted() {
		return executed!=null?executed:false;
	}

	public void setExecuted(Boolean executed) {
		this.executed = executed;
	}

}
