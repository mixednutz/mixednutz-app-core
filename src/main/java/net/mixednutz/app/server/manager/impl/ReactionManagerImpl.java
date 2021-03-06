package net.mixednutz.app.server.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mixednutz.app.server.entity.Emoji;
import net.mixednutz.app.server.entity.ReactionScore;
import net.mixednutz.app.server.entity.ReactionsAware;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractReaction;
import net.mixednutz.app.server.manager.ReactionManager;
import net.mixednutz.app.server.repository.UserRepository;

@Transactional
@Service
public class ReactionManagerImpl implements ReactionManager{
		
	@Autowired
	protected UserRepository userRepository;
	
	
	public void sendEmail(AbstractReaction reaction) {

	}
	
	protected User lookupAuthor(AbstractReaction reaction) {
		return userRepository.findById(reaction.getReactorId()).get();
	}
					
	@Override
	public <R extends AbstractReaction> Collection<R> addReaction(String emojiId, Set<R> reactions, User author,
			User currentUser, Function<String, R> callback) {
		List<R> addedReactions = new ArrayList<R>();
		AbstractReaction existingReaction = null;
		for (AbstractReaction reaction: reactions) {
			if (reaction.getEmoji().getId().equals(emojiId)
					&& userOwnsReaction(reaction, author, currentUser)) {
				existingReaction = reaction;
				break;
			}
		}
		if (existingReaction==null) {
			final R newreaction = callback.apply(emojiId);
			reactions.add(newreaction);
			addedReactions.add(newreaction);
		}
		return addedReactions;
	}

	@Override
	public <R extends AbstractReaction> R toggleReaction(String emojiId, Set<R> reactions, User author, User currentUser,
			Function<String, R> callback) {
		for (AbstractReaction reaction: reactions) {
			if (reaction.getEmoji().getId().equals(emojiId)
					&& userOwnsReaction(reaction, author, currentUser)) {
				reactions.remove(reaction);
				return null;
			}
		}
		R addedReaction = callback.apply(emojiId);
		reactions.add(addedReaction);
		return addedReaction;
	}

	@Override
	public <R extends AbstractReaction> List<ReactionScore> getReactionScores(Set<R> reactions, User author, User currentUser) {
		Map<Emoji, ReactionScore> reactionScores = new HashMap<Emoji, ReactionScore>();
		getReactionScores(reactionScores, reactions, author, currentUser);
		List<ReactionScore> list = new ArrayList<ReactionScore>(reactionScores.values());
		Collections.sort(list);
		return list;
	}
	
	public <R extends AbstractReaction> List<ReactionScore> rollupReactionScores(Iterable<? extends ReactionsAware<R>> iterableOfReactions, User author, User currentUser) {
		Map<Emoji, ReactionScore> reactionScores = new HashMap<Emoji, ReactionScore>();
		for (ReactionsAware<R> reactionAware: iterableOfReactions) {
			getReactionScores(reactionScores, reactionAware.getReactions(), author, currentUser);
		}

		List<ReactionScore> list = new ArrayList<ReactionScore>(reactionScores.values());
		Collections.sort(list);
		return list;
	}
	
	public <R extends AbstractReaction> void getReactionScores(Map<Emoji, ReactionScore> reactionScores, Set<R> reactions, User author, User currentUser) {
		for (R reaction : reactions) {
			if (!reactionScores.containsKey(reaction.getEmoji())) {
				reactionScores.put(reaction.getEmoji(), new ReactionScore(reaction.getEmoji()));
			}
			ReactionScore reactionScore = reactionScores.get(reaction.getEmoji());
			if (!reactionScore.isUserIncluded()) {
				//This eliminates duplicate votes too!
				reactionScore.setUserIncluded(userOwnsReaction(reaction, author, currentUser));
				reactionScore.incrementScore();
			}
		}
	}
	
	/**
	 * If current user is the Author the reactorId must be equal 0 or null.  
	 * Else reactorId must be current user's id.
	 * @param reaction
	 * @param author
	 * @param currentUser
	 * @return
	 */
	protected boolean userOwnsReaction(AbstractReaction reaction, User author, User currentUser) {
		return (author!=null && author.equals(currentUser) && (reaction.getReactorId()==null || reaction.getReactorId().equals(0L))) ||
				(currentUser!=null && currentUser.getUserId().equals(reaction.getReactorId()));
	}
}
