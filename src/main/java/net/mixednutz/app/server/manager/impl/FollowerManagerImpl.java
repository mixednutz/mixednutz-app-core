package net.mixednutz.app.server.manager.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mixednutz.app.server.entity.Follower;
import net.mixednutz.app.server.entity.Follower.FollowerPK;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.manager.FollowerManager;
import net.mixednutz.app.server.repository.FollowerRepository;

@Transactional
@Service
public class FollowerManagerImpl implements FollowerManager {
	
	@Autowired
	private FollowerRepository followerRepository;

	@Override
	public List<Follower> getFollowing(User user) {
		return StreamSupport.stream(followerRepository.getFollowing(user).spliterator(),false)
				.collect(Collectors.toList());
	}

	@Override
	public long countFollowing(User user) {
		return followerRepository.countFollowing(user);
	}

	@Override
	public List<Follower> getFollowers(User user) {
		return StreamSupport.stream(followerRepository.getFollowers(user).spliterator(),false)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Follower> getAllFollowers(User user) {
		return StreamSupport.stream(followerRepository.getAllFollowers(user).spliterator(),false)
				.collect(Collectors.toList());
	}
	
	@Override
	public long countFollowers(User user) {
		return followerRepository.countFollowers(user);
	}
	
	public Optional<Follower> get(FollowerPK id) {
		return followerRepository.findById(id);
	}
	
	protected Follower save(Follower follower) {
		return followerRepository.save(follower);
	}
	
	protected void delete(Follower follower) {
		followerRepository.delete(follower);
	}
	
	public Follower requestFollow(FollowerPK id, Consumer<Follower> onRequest) {
		Follower follower = new Follower(id);
		return save(follower);
	}
	
	public void acceptFollow(FollowerPK id, Consumer<Follower> onAccept) {
		get(id).ifPresent(follow->{
			follow.setPending(false);
			onAccept.accept(save(follow));
		});
	}

	@Override
	public Follower autoAcceptFollow(FollowerPK id) {
		Follower follower = new Follower(id);
		follower.setPending(false);
		return save(follower);
	}

	@Override
	public void unfollow(FollowerPK id, Consumer<Follower> onRequest) {
		get(id).ifPresent(follow->{
			delete(follow);
			onRequest.accept(follow);
		});
	}

}
