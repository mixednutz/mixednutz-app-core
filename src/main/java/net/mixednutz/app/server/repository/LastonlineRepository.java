package net.mixednutz.app.server.repository;

import java.time.ZonedDateTime;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.Lastonline;

@Repository
public interface LastonlineRepository extends CrudRepository<Lastonline, Long> {

	long countByTimestampGreaterThan(ZonedDateTime timestamp);
	
}
