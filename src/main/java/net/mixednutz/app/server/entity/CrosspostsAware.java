package net.mixednutz.app.server.entity;

import java.util.Set;

public interface CrosspostsAware {

	Set<ExternalFeedContent> getCrossposts();
	
	void setCrossposts(Set<ExternalFeedContent> crossposts);
	
}
