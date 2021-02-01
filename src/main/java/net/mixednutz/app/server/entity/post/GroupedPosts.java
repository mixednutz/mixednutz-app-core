package net.mixednutz.app.server.entity.post;

import java.util.List;

import net.mixednutz.app.server.entity.User;

public interface GroupedPosts<P extends Post<C>, C extends PostComment> {
	
	void setPosts(List<P> posts);
	
	User getAuthor();
		
}
