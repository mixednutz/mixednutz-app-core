package net.mixednutz.app.server.entity.post;

public interface PostReaction extends Reaction {
	
	<P extends Post<?>> void setPost(P post);
	
	<P extends Post<?>> P getPost();
	
	Long getId();
	
	String getParentUri();
	
}
