package net.mixednutz.app.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.Follower;
import net.mixednutz.app.server.entity.Follower.FollowerPK;
import net.mixednutz.app.server.entity.User;

@Repository
public interface FollowerRepository extends CrudRepository<Follower, FollowerPK> {

	/**
	 * Who am I following?
	 * 
	 * @param user
	 * @return
	 */
	default Iterable<Follower> getFollowing(User user) {
		return findByFollowerAndPendingAndUser_LastonlineIsNotNullOrderByUser_Lastonline_TimestampDesc(user, false);
	}
	default long countFollowing(User user) {
		return countByFollowerAndPending(user, false);
	}
	
	/**
	 * Who's following me?
	 * 
	 * @param user
	 * @return
	 */
	default Iterable<Follower> getFollowers(User user) {
		return findByUserAndPendingAndFollower_LastonlineIsNotNullOrderByFollower_Lastonline_TimestampDesc(user, false);
	}
	default long countFollowers(User user) {
		return countByUserAndPending(user, false);
	}
	
	/**
	 * Who wants to follow me?
	 * 
	 * @param user
	 * @return
	 */
	default Iterable<Follower> getPendingFollowers(User user) {
		return findByUserAndPendingAndFollower_LastonlineIsNotNullOrderByFollower_Lastonline_TimestampDesc(user, true);
	}
	
	/**
	 * Includes Remote Users
	 * 
	 * @param user
	 * @return
	 */
	default Iterable<Follower> getAllFollowers(User user) {
		return findByUserAndPending(user, false);
	}
	
	Iterable<Follower> findByFollowerAndPendingAndUser_LastonlineIsNotNullOrderByUser_Lastonline_TimestampDesc(User follower, boolean pending);
	
	long countByFollowerAndPending(User follower, boolean pending);
	
	Iterable<Follower> findByUserAndPendingAndFollower_LastonlineIsNotNullOrderByFollower_Lastonline_TimestampDesc(User user, boolean pending);
	
	Iterable<Follower> findByUserAndPending(User user, boolean pending);
		
	long countByUserAndPending(User user, boolean pending);
	
}
