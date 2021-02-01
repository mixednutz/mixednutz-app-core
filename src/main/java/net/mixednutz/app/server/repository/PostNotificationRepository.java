package net.mixednutz.app.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.post.AbstractNotification;

@Repository
public interface PostNotificationRepository
	extends CrudRepository<AbstractNotification, Long>, PostNotificationCustomRepository {
	
}
