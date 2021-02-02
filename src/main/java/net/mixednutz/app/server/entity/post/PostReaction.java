package net.mixednutz.app.server.entity.post;

public interface PostReaction {
	
	Long getId();
	
	Long getReactorId();
	
	String getParentUri();
	
}
