/**
 * 
 */
package net.mixednutz.app.server.manager;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.mixednutz.app.server.entity.ReactionScore;
import net.mixednutz.app.server.entity.ReactionsAware;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractReaction;


/**
 * @author apfesta
 *
 */
public interface ReactionManager {
	
	public void sendEmail(AbstractReaction reaction);
		
	/**
	 * Adds a emoji to a set of reactions
	 * 
	 * @param tagArray
	 * @param tags
	 * @param author
	 * @param currentUser
	 * @param callback
	 * @return 
	 */
	public <R extends AbstractReaction> Collection<R> addReaction(String emojiId, Set<R> reactions, 
			User author, User currentUser, Function<String, R> callback);
		
	/**
	 * Toggle Reaction.  Either adds a reaction, or removes it.  Returns the added reaction or
	 * null if removed.
	 * 
	 * @param emoji
	 * @param reactions
	 * @param author
	 * @param currentUser
	 * @param callback
	 * @return
	 */
	public <R extends AbstractReaction> R toggleReaction(String emojiId, Set<R> reactions, 
			User author, User currentUser, Function<String, R> callback);
		
	public <R extends AbstractReaction> List<ReactionScore> getReactionScores(Set<R> reactions, User author, User currentUser);
	
	public <R extends AbstractReaction> List<ReactionScore> rollupReactionScores(Iterable<? extends ReactionsAware<R>> iterableOfReactions, User author, User currentUser);
	
}
