package net.mixednutz.app.server.entity;

import java.util.Set;

import net.mixednutz.app.server.entity.post.AbstractReaction;

public interface ReactionsAware<R extends AbstractReaction> {

	Set<R> getReactions();
	
	void setReactions(Set<R> reactions);
	
}
