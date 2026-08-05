package net.mixednutz.app.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.mixednutz.app.server.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	public Optional<User> findByUsername(String username);
	
	public Optional<User> findByAvatarFilename(String avatarFilename);
	
	/**
	 * All REAL users
	 * 
	 * @return
	 */
	@Query("SELECT u FROM User u JOIN u.lastonline ORDER BY u.username ASC")
    List<User> findAllWhereLastonlineExistsOrderByUsername();
		
}
