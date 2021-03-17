package net.mixednutz.app.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.UserEmailAddressVerificationToken;

@Repository
public interface UserEmailAddressVerificationTokenRepository extends CrudRepository<UserEmailAddressVerificationToken, String> {

}
