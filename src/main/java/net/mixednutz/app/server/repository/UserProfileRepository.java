package net.mixednutz.app.server.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.UserProfile;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {

	Optional<UserProfile> findOneByActivityPubActorUri(String activityPubActorUri);
	
}
