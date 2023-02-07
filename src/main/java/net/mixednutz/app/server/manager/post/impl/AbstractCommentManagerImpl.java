package net.mixednutz.app.server.manager.post.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import net.mixednutz.api.core.model.PageBuilder;
import net.mixednutz.api.model.IPage;
import net.mixednutz.api.model.IPageRequest;
import net.mixednutz.api.model.IPageRequest.Direction;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractPostComment;
import net.mixednutz.app.server.manager.ApiManager;
import net.mixednutz.app.server.manager.post.CommentManager;
import net.mixednutz.app.server.repository.CommentRepository;

public abstract class AbstractCommentManagerImpl<C extends AbstractPostComment> implements CommentManager<C> {

	protected CommentRepository<C> commentRepository;
	
	@Autowired
	protected ApiManager apiManager;
	
	protected abstract InternalTimelineElement toTimelineElement(C comment, User viewer);
	
	public IPage<InternalTimelineElement,Instant> getUserTimelineInternal(
			User owner, User viewer, IPageRequest<String> paging) {
			
		List<C> contents = null;
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
		for (C content: contents) {
			elements.add(toTimelineElement(content, viewer));
		}
		
		return new PageBuilder<InternalTimelineElement, Instant>()
			.setItems(elements)
			.setPageRequest(pageRequest)
			.setTokenCallback((item)->item.getPostedOnDate().toInstant())
			.build();
	}
		
	private List<C> getUsersPostsByDatePublishedLessThanEquals(User owner, User viewer,
			ZonedDateTime datePublished,
			PageRequest pageRequest) {
		return commentRepository.getAuthorPostsByDatePublishedLessThanEquals(owner, 
				viewer, datePublished, pageRequest);
	}
	
	private List<C> getUsersPostsByDatePublishedGreaterThan(User owner, User viewer,
			ZonedDateTime datePublished,
			PageRequest pageRequest) {
		return commentRepository.getAuthorPostsByDateCreatedGreaterThan(owner, 
				viewer, datePublished, pageRequest);
	}
	
	public IPage<InternalTimelineElement, Instant> getTimelineInternal(User owner, IPageRequest<String> paging) {
		final net.mixednutz.api.core.model.PageRequest<Instant> pageRequest = net.mixednutz.api.core.model.PageRequest
				.convert(paging, Instant.class, (str) -> {
					return ZonedDateTime.parse(str).toInstant();
				});
		
		return new PageBuilder<InternalTimelineElement, Instant>()
				.setItems(new ArrayList<>())
				.setPageRequest(pageRequest)
				.setTokenCallback((item)->item.getPostedOnDate().toInstant())
				.build();
	}

	public long countUserTimelineInteral(User owner, User viewer) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
}
