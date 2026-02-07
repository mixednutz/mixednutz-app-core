package net.mixednutz.app.server.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import net.mixednutz.app.server.entity.ExternalVisibility;

public interface ExternalVisibilityRepository extends CrudRepository<ExternalVisibility, Long> {
	
	public Optional<ExternalVisibility> findByProviderIdAndProviderListId(String providerId, String providerListId);
	
	public Optional<ExternalVisibility> findByProviderListId(String providerListId);

}
