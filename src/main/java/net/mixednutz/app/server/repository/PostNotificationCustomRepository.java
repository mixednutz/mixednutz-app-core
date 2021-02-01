package net.mixednutz.app.server.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.mixednutz.app.server.entity.post.AbstractNotification;

public interface PostNotificationCustomRepository {

	<N extends AbstractNotification> Iterable<N> loadNotifications(CriteriaBuilderCallback<N> callback, Class<N> entityType);
	
	public interface CriteriaBuilderCallback<N> {
		Predicate withCriteriaBuilder(CriteriaBuilder criteriaBuilder, Root<N> itemRoot);
	}
}
