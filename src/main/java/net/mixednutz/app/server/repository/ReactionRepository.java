package net.mixednutz.app.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.post.AbstractReaction;

@Repository
public interface ReactionRepository extends CrudRepository<AbstractReaction, Long> {

}
