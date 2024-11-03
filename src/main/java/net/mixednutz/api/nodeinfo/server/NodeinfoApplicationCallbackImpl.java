package net.mixednutz.api.nodeinfo.server;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.app.server.repository.LastonlineRepository;
import net.mixednutz.app.server.repository.UserRepository;
import software.diaspora.nodeinfo.server.NodeinfoApplicationCallback;
import software.diaspora.nodeinfo.server.NodeinfoSchema;

@Component
public class NodeinfoApplicationCallbackImpl implements NodeinfoApplicationCallback {

	@Autowired
	private NetworkInfo networkInfo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LastonlineRepository lastonlineRepository;
	
	@Autowired
	NodeinfoSchema schema;
	
	@Override
	public String getHostName() {
		return networkInfo.getHostName();
	}

	long getTotalUsers() {
		return userRepository.count();
	}

	long getActiveUsersHalfyear() {
		return lastonlineRepository.countByTimestampGreaterThan(ZonedDateTime.now().minusDays(180));
	}

	long getActiveUsersMonth() {
		return lastonlineRepository.countByTimestampGreaterThan(ZonedDateTime.now().minusDays(30));
	}

	@Override
	public NodeinfoSchema getApplicationInformation() {
		LinkedHashMap<String,Long> users = new LinkedHashMap<>();
		schema.getUsage().put("users", users);
		users.put("total", getTotalUsers());
		users.put("activeHalfyear", getActiveUsersHalfyear());
		users.put("activeMonth", getActiveUsersMonth());
		
		//TODO add localPosts and localComments (we don't have a global repository to make that query)
		return schema;
	}
	
	

}
