package net.mixednutz.app.server.entity.post;

public interface PostComment extends Comment {
	
	<P extends Post<C>, C extends PostComment> void setPost(P post);
	
	<P extends Post<C>, C extends PostComment> P getPost();
		
	Long getCommentId();
	
	String getUri();
	
	<C extends PostComment> C getInReplyTo();
	
}
