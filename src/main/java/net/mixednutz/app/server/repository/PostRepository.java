/**
 * 
 */
package net.mixednutz.app.server.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.Post;
import net.mixednutz.app.server.entity.post.PostComment;

/**
 * @author Andy
 *
 */
@NoRepositoryBean
public interface PostRepository<P extends Post<C>, C extends PostComment> extends CrudRepository<P, Long> {

	List<P> findByOwnerAndDatePublishedGreaterThanOrderByDatePublishedDesc(User owner, ZonedDateTime datePublished, Pageable pageRequest);
	
	default List<P> getMyPostsGreaterThan(User owner, ZonedDateTime datePublished, Pageable pageRequest) {
		return findByOwnerAndDatePublishedGreaterThanOrderByDatePublishedDesc(owner, datePublished, pageRequest);
	}
	
	List<P> findByOwnerAndDatePublishedLessThanEqualOrderByDatePublishedDesc(User owner, ZonedDateTime datePublished, Pageable pageRequest);
	
	default List<P> getMyPostsLessThan(User owner, ZonedDateTime datePublished, Pageable pageRequest) {
		return findByOwnerAndDatePublishedLessThanEqualOrderByDatePublishedDesc(owner, datePublished, pageRequest);
	}
	
	Optional<P> findByOwnerAndId(User owner, Long id);
	

	@Query("select p from #{#entityName} p"
			+" left join p.visibility.selectFollowers vsf"
			+" left join p.visibility.externalList vel"
			+ " where p.ownerId = :ownerId"+
			  " and (p.ownerId = :viewerId"
			  + " or p.authorId = :viewerId"
			  + " or p.visibility.visibilityType = 'WORLD'"
			  + " or (p.visibility.visibilityType = 'ALL_USERS' and :viewerId is not null)"
			  + " or (p.visibility.visibilityType = 'SELECT_FOLLOWERS' and vsf.userId = :viewerId)"
			  + " or (p.visibility.visibilityType = 'EXTERNAL_LIST' and vel.providerListId in :externalListIds))"
			  + " and p.datePublished > :datePublished"
			+ " order by p.datePublished desc")
	List<P> queryUsersPostsByDatePublishedGreaterThan(
			@Param("ownerId")Long ownerId, 
			@Param("viewerId")Long viewerId, 
			@Param("datePublished")ZonedDateTime datePublished, 
			@Param("externalListIds") List<String> externalListIds, Pageable pageRequest);
	
	default List<P> getUsersPostsByDatePublishedGreaterThan(User owner, User viewer, ZonedDateTime datePublished, List<String> externalListIds, Pageable pageRequest) {
		return queryUsersPostsByDatePublishedGreaterThan(owner.getUserId(), viewer!=null?viewer.getUserId():null, datePublished, externalListIds, pageRequest);
	}
	

	@Query("select p from #{#entityName} p"
			+" left join p.visibility.selectFollowers vsf"
			+" left join p.visibility.externalList vel"
			+ " where p.ownerId = :ownerId"+
			  " and (p.ownerId = :viewerId"
			  + " or p.authorId = :viewerId"
			  + " or p.visibility.visibilityType = 'WORLD'"
			  + " or (p.visibility.visibilityType = 'ALL_USERS' and :viewerId is not null)"
			  + " or (p.visibility.visibilityType = 'SELECT_FOLLOWERS' and vsf.userId = :viewerId)"
			  + " or (p.visibility.visibilityType = 'EXTERNAL_LIST' and vel.providerListId in :externalListIds))"
			  + " and p.datePublished <= :datePublished"
			+ " order by p.datePublished desc")
	List<P> queryUsersPostsByDatePublishedLessThanEquals(
			@Param("ownerId")Long ownerId, 
			@Param("viewerId")Long viewerId, 
			@Param("datePublished")ZonedDateTime datePublished, 
			@Param("externalListIds") List<String> externalListIds, Pageable pageRequest);
	
	default List<P> getUsersPostsByDatePublishedLessThanEquals(User owner, User viewer, ZonedDateTime datePublished, List<String> externalListIds, Pageable pageRequest) {
		return queryUsersPostsByDatePublishedLessThanEquals(owner.getUserId(), viewer!=null?viewer.getUserId():null, datePublished, externalListIds, pageRequest);
	}
	
	@Query("select count(p) from #{#entityName} p"
			+" left join p.visibility.selectFollowers vsf"
			+" left join p.visibility.externalList vel"
			+ " where p.ownerId = :ownerId"+
			  " and (p.ownerId = :viewerId"
			  + " or p.authorId = :viewerId"
			  + " or p.visibility.visibilityType = 'WORLD'"
			  + " or (p.visibility.visibilityType = 'ALL_USERS' and :viewerId is not null)"
			  + " or (p.visibility.visibilityType = 'SELECT_FOLLOWERS' and vsf.userId = :viewerId)"
			  + " or (p.visibility.visibilityType = 'EXTERNAL_LIST' and vel.providerListId in :externalListIds))")
	long countUsersPosts(
			@Param("ownerId")Long ownerId, 
			@Param("viewerId")Long viewerId, 
			@Param("externalListIds") List<String> externalListIds);
	
	default long countUsersPosts(User owner, User viewer, List<String> externalListIds) {
		return countUsersPosts(owner.getUserId(), viewer!=null?viewer.getUserId():null, externalListIds);
	}
		
}
