package net.mixednutz.app.server.manager.post;

import java.util.Map;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.Post;
import net.mixednutz.app.server.entity.post.PostComment;

public interface PostManager<P extends Post<C>, C extends PostComment> {
	
	public Map<Long, User> loadCommentAuthors(Iterable<C> comments);
	
}
