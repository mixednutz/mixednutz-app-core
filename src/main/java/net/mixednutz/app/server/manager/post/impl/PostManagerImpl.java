package net.mixednutz.app.server.manager.post.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import net.mixednutz.api.core.model.PageBuilder;
import net.mixednutz.api.model.IPage;
import net.mixednutz.api.model.IPageRequest;
import net.mixednutz.api.model.IPageRequest.Direction;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractPost;
import net.mixednutz.app.server.entity.post.AbstractPostComment;
import net.mixednutz.app.server.entity.post.AbstractPostView;
import net.mixednutz.app.server.manager.ApiManager;
import net.mixednutz.app.server.manager.post.PostManager;
import net.mixednutz.app.server.manager.post.PostViewManager;
import net.mixednutz.app.server.repository.GroupedPostRepository;
import net.mixednutz.app.server.repository.PostRepository;

public abstract class PostManagerImpl<P extends AbstractPost<C>, C extends AbstractPostComment, V extends AbstractPostView> 
	implements PostManager<P,C> {
	
	protected PostRepository<P,C> postRepository;
	
	protected PostViewManager<P,C,V> postViewManager;
		
	@Autowired
	protected ApiManager apiManager;
	
	protected abstract InternalTimelineElement toTimelineElement(P post, User viewer);
		
	public IPage<InternalTimelineElement,Instant> getTimelineInternal(
			User owner, IPageRequest<String> paging) {
			
		List<P> contents = null;
		final net.mixednutz.api.core.model.PageRequest<Instant> pageRequest = net.mixednutz.api.core.model.PageRequest
				.convert(paging, Instant.class, (str) -> {
					return ZonedDateTime.parse(str).toInstant();
				});
		if (paging.getStart()==null) {
			contents = getMyPostsLessThan(owner, ZonedDateTime.now(), 
					PageRequest.of(0, paging.getPageSize()));
		} else {
			ZonedDateTime start = pageRequest.getStart().atZone(ZoneId.systemDefault());
			if (paging.getDirection()==Direction.LESS_THAN) {
				contents = getMyPostsLessThan(owner, start, 
						PageRequest.of(0, paging.getPageSize()));
			} else {
				contents = getMyPostsGreaterThan(owner, start, 
						PageRequest.of(0, paging.getPageSize()));
			}
		}
		List<InternalTimelineElement> elements = new ArrayList<>();
		for (P content: contents) {
			elements.add(toTimelineElement(content, null));
		}
		
		return new PageBuilder<InternalTimelineElement, Instant>()
			.setItems(elements)
			.setPageRequest(pageRequest)
			.setTokenCallback((item)->item.getPostedOnDate().toInstant())
			.build();
	}
	
	public IPage<InternalTimelineElement,Instant> getUserTimelineInternal(
			User owner, User viewer, IPageRequest<String> paging) {
			
		List<P> contents = null;
		final net.mixednutz.api.core.model.PageRequest<Instant> pageRequest = net.mixednutz.api.core.model.PageRequest
				.convert(paging, Instant.class, (str) -> {
					return ZonedDateTime.parse(str).toInstant();
				});
		if (paging.getStart()==null) {
			contents = getUsersPostsByDatePublishedLessThanEquals(
					owner, viewer, ZonedDateTime.now(), 
					PageRequest.of(0, paging.getPageSize()));
		} else {
			ZonedDateTime start = pageRequest.getStart().atZone(ZoneId.systemDefault());
			if (paging.getDirection()==Direction.LESS_THAN) {
				contents = getUsersPostsByDatePublishedLessThanEquals(
						owner, viewer, start, 
						PageRequest.of(0, paging.getPageSize()));
			} else {
				contents = getUsersPostsByDatePublishedGreaterThan(
						owner, viewer, start, 
						PageRequest.of(0, paging.getPageSize()));
			}
		}
		List<InternalTimelineElement> elements = new ArrayList<>();
		for (P content: contents) {
			elements.add(toTimelineElement(content, viewer));
		}
		
		return new PageBuilder<InternalTimelineElement, Instant>()
			.setItems(elements)
			.setPageRequest(pageRequest)
			.setTokenCallback((item)->item.getPostedOnDate().toInstant())
			.build();
	}
	
	public List<? extends ITimelineElement> getUserTimelineInternal(User user, User viewer, int pageSize) {
		return this.getUserTimelineInternal(user, viewer, 
				net.mixednutz.api.core.model.PageRequest.first(pageSize, Direction.LESS_THAN, String.class)).getItems();
	}
	
	public void incrementViewCount(P post, User viewer) {
		postViewManager.addView(post, viewer);
	}
	
	public Map<Long, User> loadCommentAuthors(Iterable<C> comments) {
		Map<Long, User> authorsById = new HashMap<>();
		for (C comment : comments) {
			if (comment.getAuthor()!=null) {
				authorsById.put(comment.getAuthor().getUserId(), comment.getAuthor());
			}
		}
		return authorsById;
	}	
	
	/**
	 * Is this post visible to the given user
	 * 
	 * @param chapter
	 * @param user Must not be null
	 * @return
	 */
	public boolean isVisible(P post, User user) {
		if (user==null) {
			//This check should be done before this method
			throw new IllegalArgumentException("User cannot be null.");
		}
		
		switch (post.getVisibility().getVisibilityType()) {
		case ALL_FOLLOWERS:
			//TODO
			break;
		case ALL_FRIENDS:
			//TODO
			break;
		case FRIEND_GROUPS:
			//TODO
			break;
		case SELECT_FOLLOWERS:
			return post.getVisibility().getSelectFollowers().contains(user);
		case PRIVATE:
			return (user.equals(post.getAuthor())||user.equals(post.getOwner()));
		case ALL_USERS:
		case WORLD:
			return true;
		}
		return true;
	}

	
	private List<P> getMyPostsLessThan(User owner, ZonedDateTime datePublished, 
			PageRequest pageRequest) {
		if (postRepository instanceof GroupedPostRepository) {
			GroupedPostRepository groupedPostRepository = (GroupedPostRepository) postRepository;
			List<P> results = new ArrayList<>();
			results.addAll(groupedPostRepository.getMyEmptyGroupedPostsLessThan(owner, datePublished, 
					pageRequest));
			results.addAll(groupedPostRepository.getMyGroupedPostsLessThan(owner, datePublished, 
					pageRequest));
			return results;
		}
		return postRepository.getMyPostsLessThan(owner, datePublished, 
				pageRequest);
	}
	
	private List<P> getMyPostsGreaterThan(User owner, ZonedDateTime datePublished, 
			PageRequest pageRequest) {
		if (postRepository instanceof GroupedPostRepository) {
			GroupedPostRepository groupedPostRepository = (GroupedPostRepository) postRepository;
			List<P> results = new ArrayList<>();
			results.addAll(groupedPostRepository.getMyEmptyGroupedPostsGreaterThan(owner, datePublished, 
					pageRequest));
			results.addAll(groupedPostRepository.getMyGroupedPostsGreaterThan(owner, datePublished, 
					pageRequest));
			return results;
		}
		return postRepository.getMyPostsGreaterThan(owner, datePublished, 
				pageRequest);
	}
	
	private List<P> getUsersPostsByDatePublishedLessThanEquals(User owner, User viewer,
			ZonedDateTime datePublished,
			PageRequest pageRequest) {
		if (postRepository instanceof GroupedPostRepository) {
			GroupedPostRepository groupedPostRepository = (GroupedPostRepository) postRepository;
			return groupedPostRepository.getUsersGroupedPostsLessThan(owner, viewer, 
					datePublished, pageRequest);
		}
		return postRepository.getUsersPostsByDatePublishedLessThanEquals(owner, 
				viewer, datePublished, pageRequest);
	}
	
	private List<P> getUsersPostsByDatePublishedGreaterThan(User owner, User viewer,
			ZonedDateTime datePublished,
			PageRequest pageRequest) {
		if (postRepository instanceof GroupedPostRepository) {
			GroupedPostRepository groupedPostRepository = (GroupedPostRepository) postRepository;
			return groupedPostRepository.getUsersGroupedPostsGreaterThan(owner, viewer, 
					datePublished, pageRequest);
		}
		return postRepository.getUsersPostsByDatePublishedGreaterThan(owner, 
				viewer, datePublished, pageRequest);
	}

}
