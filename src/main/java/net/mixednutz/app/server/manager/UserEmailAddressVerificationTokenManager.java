package net.mixednutz.app.server.manager;

import net.mixednutz.app.server.entity.UserEmailAddress;
import net.mixednutz.app.server.entity.UserEmailAddressVerificationToken;

public interface UserEmailAddressVerificationTokenManager extends 
	VerificationTokenManager<UserEmailAddressVerificationToken, UserEmailAddress> {

}
