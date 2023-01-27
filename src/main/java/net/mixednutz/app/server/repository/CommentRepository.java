package net.mixednutz.app.server.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractPostComment;

@NoRepositoryBean
public interface CommentRepository<C extends AbstractPostComment> extends CrudRepository<C, Long> {

	List<C> findByAuthorAndDateCreatedGreaterThanOrderByDateCreatedDesc(User author, ZonedDateTime dateCreated, Pageable pageRequest);
	
	default List<C> getMyPostsGreaterThan(User author, ZonedDateTime dateCreated, Pageable pageRequest) {
		return findByAuthorAndDateCreatedGreaterThanOrderByDateCreatedDesc(author, dateCreated, pageRequest);
	}
	
	List<C> findByAuthorAndDateCreatedLessThanEqualOrderByDateCreatedDesc(User owner, ZonedDateTime datePublished, Pageable pageRequest);
	
	default List<C> getMyPostsLessThan(User author, ZonedDateTime dateCreated, Pageable pageRequest) {
		return findByAuthorAndDateCreatedLessThanEqualOrderByDateCreatedDesc(author, dateCreated, pageRequest);
	}
	/*
	 * SUBCLASS MUST DEFINE a @Query
	 */
	
	List<C> queryAuthorPostsByDateCreatedGreaterThan(
			Long authorId, 
			Long viewerId, 
			ZonedDateTime dateCreated, Pageable pageRequest);
	
	default List<C> getAuthorPostsByDateCreatedGreaterThan(User owner, User viewer, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryAuthorPostsByDateCreatedGreaterThan(owner.getUserId(), viewer!=null?viewer.getUserId():null, datePublished, pageRequest);
	}
	
	/*
	 * SUBCLASS MUST DEFINE a @Query
	 */
	List<C> queryAuthorPostsByDateCreatedLessThanEquals(
			@Param("authorId")Long authorId, 
			@Param("viewerId")Long viewerId, 
			@Param("dateCreated")ZonedDateTime dateCreated, Pageable pageRequest);
	
	default List<C> getAuthorPostsByDatePublishedLessThanEquals(User owner, User viewer, ZonedDateTime datePublished, Pageable pageRequest) {
		return queryAuthorPostsByDateCreatedLessThanEquals(owner.getUserId(), viewer!=null?viewer.getUserId():null, datePublished, pageRequest);
	}
}
