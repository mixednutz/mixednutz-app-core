package net.mixednutz.api.nodeinfo.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import software.diaspora.nodeinfo.server.NodeinfoSchema;

public class NodeinfoSchemaImpl implements NodeinfoSchema {
	
	private String version = "2.1"; //Node Info Version
	private Map<String, String> software = new LinkedHashMap<>();
	private List<String> protocols = List.of("activitypub");
	private Map<String, List<String>> services = new LinkedHashMap<>();
	private boolean openRegistrations;
	private Map<String, Object> usage = new LinkedHashMap<>();
	private Map<String, Object> metadata = new LinkedHashMap<>();
	
	public NodeinfoSchemaImpl() {
		software.put("name", "mixednutz");
		services.put("inbound", new ArrayList<>());
		services.put("outbound", new ArrayList<>());
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Map<String, String> getSoftware() {
		return software;
	}
	public void setSoftware(Map<String, String> software) {
		this.software = software;
	}
	public List<String> getProtocols() {
		return protocols;
	}
	public void setProtocols(List<String> protocols) {
		this.protocols = protocols;
	}
	public boolean isOpenRegistrations() {
		return openRegistrations;
	}
	public void setOpenRegistrations(boolean openRegistrations) {
		this.openRegistrations = openRegistrations;
	}
	public Map<String, List<String>> getServices() {
		return services;
	}
	public void setServices(Map<String, List<String>> services) {
		this.services = services;
	}
	public Map<String, Object> getUsage() {
		return usage;
	}
	public void setUsage(Map<String, Object> usage) {
		this.usage = usage;
	}
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

}
