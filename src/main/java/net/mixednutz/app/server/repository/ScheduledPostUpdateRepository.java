package net.mixednutz.app.server.repository;

import java.time.ZonedDateTime;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.post.AbstractScheduledPostUpdate;

@Repository
public interface ScheduledPostUpdateRepository extends CrudRepository<AbstractScheduledPostUpdate, Long> {

	Iterable<? extends AbstractScheduledPostUpdate> findByEffectiveDateLessThanEqualAndExecutedFalse(ZonedDateTime date);
	
}
