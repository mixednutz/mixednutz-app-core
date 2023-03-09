package net.mixednutz.api.nodeinfo.server;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.app.server.repository.LastonlineRepository;
import net.mixednutz.app.server.repository.UserRepository;

@Component
public class NodeinfoServer {
		
	public static final String SCHEMA_URI = "/nodeinfo/2.1";
		
	@Autowired
	private NodeinfoSchema schema;
	
	@Autowired
	private NetworkInfo networkInfo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LastonlineRepository lastonlineRepository;
	
		
	public NodeinfoResponse handleNodeinfoRequest() {
		NodeinfoResponse response = new NodeinfoResponse(List.of(new NodeinfoResponse.Link(
				"http://nodeinfo.diaspora.software/ns/schema/2.1", 
				"http://"+networkInfo.getHostName()+SCHEMA_URI)));
		
		return response;
	}
	
	@Cacheable("nodeinfo-schema")
	public NodeinfoSchema handleNodeinfoSchemaRequest() {
		//add usage stats
		schema.setUsage(new LinkedHashMap<>());
		LinkedHashMap<String,Long> users = new LinkedHashMap<>();
		schema.getUsage().put("users", users);
		users.put("total", 
				userRepository.count());
		users.put("activeHalfyear", 
				lastonlineRepository.countByTimestampGreaterThan(ZonedDateTime.now().minusDays(180)));
		users.put("activeMonth", 
				lastonlineRepository.countByTimestampGreaterThan(ZonedDateTime.now().minusDays(30)));
		
		//TODO add localPosts and localComments (we don't have a global repository to make that query)
		return schema;
	}

}
