package net.mixednutz.app.server.manager;

import net.mixednutz.app.server.entity.ResetPasswordToken;
import net.mixednutz.app.server.entity.User;

public interface ResetPasswordTokenManager extends 
	VerificationTokenManager<ResetPasswordToken, User> {

}
