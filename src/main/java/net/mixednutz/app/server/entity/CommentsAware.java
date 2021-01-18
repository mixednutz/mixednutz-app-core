package net.mixednutz.app.server.entity;

import java.util.List;

import net.mixednutz.app.server.entity.post.PostComment;

public interface CommentsAware<C extends PostComment> {

	List<C> getComments();
	
	void setComments(List<C> comments);
	
}
