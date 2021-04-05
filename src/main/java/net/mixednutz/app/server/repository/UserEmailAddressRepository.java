package net.mixednutz.app.server.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.UserEmailAddress;

@Repository
public interface UserEmailAddressRepository extends CrudRepository<UserEmailAddress, Long> {

	Optional<UserEmailAddress> findByUser(User user);
	
}
