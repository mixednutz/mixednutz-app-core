package net.mixednutz.app.server.entity.post;

import java.util.List;

public interface GroupedPosts<P extends Post<C>, C extends PostComment> {
	
	void setPosts(List<P> posts);
		
}
