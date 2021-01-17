package net.mixednutz.app.server.repository;

import org.springframework.data.repository.CrudRepository;

import net.mixednutz.app.server.entity.post.AbstractReaction;

public interface ReactionRepository extends CrudRepository<AbstractReaction, Long> {

}
