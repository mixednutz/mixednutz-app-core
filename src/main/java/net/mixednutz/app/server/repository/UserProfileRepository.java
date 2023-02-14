package net.mixednutz.app.server.repository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.UserProfile;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {

	Optional<UserProfile> findOneByActivityPubActorUri(URI activityPubActorUri);
	
}
