package net.mixednutz.api.hostmeta.server;

import org.ietf.hostmeta.server.HostmetaApplicationCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.mixednutz.api.core.model.NetworkInfo;

@Component
public class HostmetaApplicationCallbackImpl implements HostmetaApplicationCallback {

	@Autowired
	private NetworkInfo networkInfo;
	
	@Override
	public String getHostName() {
		return networkInfo.getHostName();
	}

}
