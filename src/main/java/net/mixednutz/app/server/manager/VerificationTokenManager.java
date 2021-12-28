package net.mixednutz.app.server.manager;

public interface VerificationTokenManager<Token, Subject> {

	void send(Token token);
	
	Token createVerificationToken(Subject subject);
	
	Token getVerificationToken(String tokenString);
	
	void delete(Token token);
	
}
