package net.mixednutz.app.server.manager.post;

import java.util.Map;
import java.util.Optional;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.Post;
import net.mixednutz.app.server.entity.post.PostComment;

public interface PostManager<P extends Post<C>, C extends PostComment> {
	
	public enum NotVisibleType {
		NOT_PUBLISHED_YET,
		NOT_PUBLIC,
		NOT_IN_SELECT_FOLLOWERS,
		NOT_IN_EXTERNAL_LIST,
		PRIVATE
	}
	
	public Map<Long, User> loadCommentAuthors(Iterable<C> comments);
	
	Optional<NotVisibleType> assertVisible(P post);
	
	Optional<NotVisibleType> assertVisible(P post, User viewer);
	
	void delete(P post);
	
}
