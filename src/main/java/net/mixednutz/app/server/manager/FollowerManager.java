package net.mixednutz.app.server.manager;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.mixednutz.app.server.entity.Follower;
import net.mixednutz.app.server.entity.Follower.FollowerPK;
import net.mixednutz.app.server.entity.User;

public interface FollowerManager {
	
	List<Follower> getFollowing(User user);

	long countFollowing(User user);
	
	List<Follower> getFollowers(User user);
	
	long countFollowers(User user);
	
	Optional<Follower> get(FollowerPK id);
	
	Follower requestFollow(FollowerPK id, Consumer<Follower> onRequest);
	
	void acceptFollow(FollowerPK id, Consumer<Follower> onAccept);
	
	Follower autoAcceptFollow(FollowerPK id);

}
