package net.mixednutz.api.nodeinfo.server;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class NodeinfoResponse {
	
	private List<Link> links;

	public NodeinfoResponse() {
		super();
	}

	public NodeinfoResponse(List<Link> links) {
		super();
		this.links = links;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public static class Link {
		private String rel;
		private String href;
		
		public Link(String rel, String href) {
			this.rel = rel;
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

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}
	}
	
}
