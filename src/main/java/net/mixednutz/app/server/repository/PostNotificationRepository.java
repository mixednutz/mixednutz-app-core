package net.mixednutz.app.server.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.post.AbstractNotification;

@Repository
public interface PostNotificationRepository
	extends CrudRepository<AbstractNotification, Long>, PostNotificationCustomRepository {
	
	List<AbstractNotification> findByUserIdAndDateCreatedGreaterThanOrderByDateCreatedDesc(
			Long userId, ZonedDateTime dateCreated, Pageable pageRequest);
	
	default List<AbstractNotification> getMyNotificationsGreaterThan(Long userId, ZonedDateTime dateCreated, Pageable pageRequest) {
		return findByUserIdAndDateCreatedGreaterThanOrderByDateCreatedDesc(userId, dateCreated, pageRequest);
	}
	
	List<AbstractNotification> findByUserIdAndDateCreatedLessThanEqualOrderByDateCreatedDesc(
			Long userId, ZonedDateTime dateCreated, Pageable pageRequest);
	
	default List<AbstractNotification> getMyNotificationsLessThan(Long userId, ZonedDateTime dateCreated, Pageable pageRequest) {
		return findByUserIdAndDateCreatedLessThanEqualOrderByDateCreatedDesc(userId, dateCreated, pageRequest);
	}
	
	long deleteByUserId(Long userId); 
	
}
