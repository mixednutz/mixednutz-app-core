package net.mixednutz.app.server.repository.activitypub;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.activitypub.Inbox;

@Repository
public interface InboxRepository extends CrudRepository<Inbox, Long>{

}
