package net.mixednutz.app.server.manager.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractCommentReplyNotification;
import net.mixednutz.app.server.entity.post.AbstractPostComment;
import net.mixednutz.app.server.entity.post.CommentReplyNotification;
import net.mixednutz.app.server.repository.PostNotificationRepository;

public class BaseNotificationFactory {

	@Autowired
	protected PostNotificationRepository notificationRepository;

	protected Iterable<? extends AbstractCommentReplyNotification<? extends AbstractPostComment>> lookupCommentReplyNotifications(
			User user, List<? extends AbstractPostComment> comments) {
		
		List<Long> commentIds = comments.stream()
			.map((comment)->comment.getCommentId())
			.collect(Collectors.toList());

		if (!commentIds.isEmpty()) {
			return notificationRepository.loadNotifications((criteriaBuilder, itemRoot) ->{
				In<Long> inClause = criteriaBuilder.in(itemRoot.get("inReplyTo"));
				for (Long commentId: commentIds) {
					inClause.value(commentId);
				}
				return criteriaBuilder.and(
						inClause,
						criteriaBuilder.equal(itemRoot.get("userId"), user.getUserId()));
			}, CommentReplyNotification.class);
		}
		return Collections.emptyList();
	}

}
