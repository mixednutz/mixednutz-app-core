package net.mixednutz.app.server.repository;

import java.time.ZonedDateTime;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.post.AbstractScheduledPost;

@Repository
public interface ScheduledPostRepository extends CrudRepository<AbstractScheduledPost, Long> {

	Iterable<? extends AbstractScheduledPost> findByPublishDateLessThanEqualAndPublishedFalse(ZonedDateTime date);
	
}
