package net.mixednutz.api.hostmeta.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.api.webfinger.WebfingerSettings;

@Component
public class HostMetaServer {

	private static final Logger LOG = LoggerFactory.getLogger(HostMetaServer.class);
	
	@Autowired
	private NetworkInfo networkInfo;

	public Xrd handleHostMetaRequest() {
		LOG.info("Host-meta lookup");
		Link link = new Link("lrdd",
				"http://" + networkInfo.getHostName() + WebfingerSettings.WEBFINGER_ENDPOINT + "?resource={uri}");
		return new Xrd(List.of(link));
	}

	public static class Xrd {

		private List<Link> links;

		public Xrd(List<Link> links) {
			super();
			this.links = links;
		}

		public Xrd() {
			super();
		}

		public List<Link> getLinks() {
			return links;
		}

		public void setLinks(List<Link> links) {
			this.links = links;
		}

	}

	public static class Link {
		private String rel;
		private String template;

		public Link(String rel, String template) {
			super();
			this.rel = rel;
			this.template = template;
		}

		public Link() {
			super();
		}

		public String getRel() {
			return rel;
		}

		public void setRel(String rel) {
			this.rel = rel;
		}

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

	}

}
