package net.mixednutz.app.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import net.mixednutz.app.server.entity.post.AbstractPostComment;

@NoRepositoryBean
public interface CommentRepository<C extends AbstractPostComment> extends CrudRepository<C, Long> {

}
