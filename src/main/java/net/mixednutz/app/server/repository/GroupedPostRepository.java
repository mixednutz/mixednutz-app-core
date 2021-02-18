package net.mixednutz.app.server.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import net.mixednutz.app.server.entity.User;

public interface GroupedPostRepository {

	<P> List<P> queryByOwnerAndDatePublishedGreaterThanOrderByDatePublishedDesc(
			User owner, ZonedDateTime datePublished, Pageable pageRequest);
	
	default <P> List<P> getMyGroupedPostsGreaterThan(User owner, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryByOwnerAndDatePublishedGreaterThanOrderByDatePublishedDesc(owner, datePublished, pageRequest);
	}
	
	<P> List<P> queryByOwnerAndDatePublishedLessThanEqualOrderByDatePublishedDesc(
			User owner, ZonedDateTime datePublished, Pageable pageRequest);
	
	default <P> List<P> getMyGroupedPostsLessThan(User owner, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryByOwnerAndDatePublishedLessThanEqualOrderByDatePublishedDesc(owner, datePublished, pageRequest);
	}
	
	<P> List<P> queryByUsersGroupedPostsByDatePublishedGreaterThan(
			Long ownerId, 
			Long viewerId, 
			ZonedDateTime datePublished, Pageable pageRequest);
	
	default <P> List<P> getUsersGroupedPostsGreaterThan(User owner, User viewer, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryByUsersGroupedPostsByDatePublishedGreaterThan(owner.getUserId(), viewer!=null?viewer.getUserId():null, datePublished, pageRequest);
	}
	
	<P> List<P> queryByUsersGroupedPostsByDatePublishedLessThanEquals(
			Long ownerId, 
			Long viewerId, 
			ZonedDateTime datePublished, Pageable pageRequest);
	
	default <P> List<P> getUsersGroupedPostsLessThan(User owner, User viewer, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryByUsersGroupedPostsByDatePublishedLessThanEquals(owner.getUserId(), viewer!=null?viewer.getUserId():null, datePublished, pageRequest);
	}
	
	<P> List<P> queryByNoPostsOwnerAndDatePublishedGreaterThanOrderByDateCreatedDesc(
			User owner, ZonedDateTime dateCreated, Pageable pageRequest);
	
	default <P> List<P> getMyEmptyGroupedPostsGreaterThan(User owner, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryByNoPostsOwnerAndDatePublishedGreaterThanOrderByDateCreatedDesc(owner, datePublished, pageRequest);
	}
	
	<P> List<P> queryByNoPostsOwnerAndDatePublishedLessThanEqualOrderByDateCreatedDesc(
			User owner, ZonedDateTime dateCreated, Pageable pageRequest);
	
	default <P> List<P> getMyEmptyGroupedPostsLessThan(User owner, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryByNoPostsOwnerAndDatePublishedLessThanEqualOrderByDateCreatedDesc(owner, datePublished, pageRequest);
	}
	
}
