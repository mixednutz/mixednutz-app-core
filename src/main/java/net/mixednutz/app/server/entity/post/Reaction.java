package net.mixednutz.app.server.entity.post;

import java.time.Instant;

public interface Reaction {

	Long getReactorId();
	
	Instant getDateCreated();
	
}
