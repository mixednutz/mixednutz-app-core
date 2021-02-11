package net.mixednutz.app.server.manager;

import net.mixednutz.api.core.model.Notification;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractCommentNotification;
import net.mixednutz.app.server.entity.post.AbstractPostComment;
import net.mixednutz.app.server.entity.post.AbstractReactionNotification;
import net.mixednutz.app.server.entity.post.GroupedPosts;
import net.mixednutz.app.server.entity.post.Post;
import net.mixednutz.app.server.entity.post.PostComment;
import net.mixednutz.app.server.entity.post.PostNotification;
import net.mixednutz.app.server.entity.post.PostReaction;

public interface NotificationManager {
	
	<P extends Post<C>, C extends PostComment> 
		void notifyNewComment(P replyTo, C comment);
	
	void notifyNewCommentReply(AbstractPostComment replyTo, AbstractPostComment comment);
	
	<P extends Post<C>, C extends PostComment, R extends PostReaction> 
		void notifyNewReaction(P reactedTo, R reaction);
	
	<P extends Post<C>, C extends PostComment> 
		void markAsRead(User user, P post);
	
	<G extends GroupedPosts<P,C>, P extends Post<C>, C extends PostComment> 
		void notifyNewAddition(G group, P post);
	
	public interface PostNotificationFactory<P extends Post<C>, 
		C extends PostComment, 
		R extends PostReaction> {
	
		boolean canConvert(Class<?> postEntityClazz);
		
		PostNotification createCommentNotification(P replyTo, C comment);
		
//		PostNotification createCommentReplyNotification(C replyTo, C comment);
		
		PostNotification createReactionNotification(P reactedTo, R reaction);
		
		Iterable<? extends AbstractCommentNotification<P,C>> lookupCommentNotifications(User user, P post);
		
		Iterable<? extends AbstractReactionNotification<P,C,R>> lookupReactionNotifications(User user, P reactedTo);
	}
	
	public interface GroupedPostNotificationFactory<G extends GroupedPosts<P, C>, 
		P extends Post<C>, 
		C extends PostComment> {
		
		Notification notifyNewAddition(G group, P post);

	}

}
