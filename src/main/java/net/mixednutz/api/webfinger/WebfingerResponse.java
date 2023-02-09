package net.mixednutz.api.webfinger;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WebfingerResponse {
	
	private URI subject;
	private List<Link> links;
	
	public WebfingerResponse() {
		super();
	}

	public WebfingerResponse(URI subject, List<Link> links) {
		super();
		this.subject = subject;
		this.links = links;
	}

	public URI getSubject() {
		return subject;
	}

	public void setSubject(URI subject) {
		this.subject = subject;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public static class Link {
		private String rel;
		private String type;
		private String href;
		
		public Link(String rel, String type, String href) {
			this.rel = rel;
			this.type = type;
			this.href = href;
		}

		public Link() {
		}

		public String getRel() {
			return rel;
		}

		public void setRel(String rel) {
			this.rel = rel;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}
	}
	
	public static class WebfingerError extends WebfingerResponse {

		private String error;
		
		public WebfingerError(String error) {
			super();
			this.error = error;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}
		
	}

}
