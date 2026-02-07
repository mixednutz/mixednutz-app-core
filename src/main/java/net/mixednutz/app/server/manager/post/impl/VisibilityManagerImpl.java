package net.mixednutz.app.server.manager.post.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mixednutz.api.model.IExternalRole;
import net.mixednutz.api.model.IRole;
import net.mixednutz.app.server.entity.ExternalFeeds.AbstractFeed;
import net.mixednutz.app.server.entity.ExternalVisibility;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.Visibility;
import net.mixednutz.app.server.entity.VisibilityType;
import net.mixednutz.app.server.manager.ExternalFeedManager;
import net.mixednutz.app.server.manager.post.VisibilityManager;
import net.mixednutz.app.server.repository.ExternalFeedRepository;
import net.mixednutz.app.server.repository.ExternalVisibilityRepository;

@Transactional
@Service
public class VisibilityManagerImpl implements VisibilityManager {
	
	@Autowired
	private ExternalFeedRepository externalFeedRepository;
	@Autowired
	private ExternalFeedManager externalFeedManager;
	@Autowired
	private ExternalVisibilityRepository externalVisibilityRepository;
	
	
	/**
	 * Ensures the user select Visibility is prepared for database insertion
	 * 
	 * @param formVisiblity
	 * @param user
	 * @param externalListId
	 * @param groupId
	 * @return
	 */
	public Visibility parseVisibility(VisibilityType visibilityType, User user, 
//			Integer friendGroupId, 
			String[] externalListId,
			Long groupId,
			Long[] externalFeedId) {
		
		switch (visibilityType) {
			case EXTERNAL_LIST:
				List<AbstractFeed> feeds = Arrays.stream(externalFeedId)
					.map(id->externalFeedRepository.findById(id))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toList());
			
				//turn array of IDs to IRole to ensure its still an active external role, and part of selected feed
				Map<AbstractFeed, List<? extends IExternalRole>> v = externalFeedManager.getExternalVisibility(feeds, externalListId);
				
				Set<ExternalVisibility> externalVisibility = v.entrySet().stream()
					.flatMap(entry->{
						return entry.getValue().stream()
								.map(role->
								{
									return this.createOrUpdateExternalVisibility(role, entry.getKey());
								});
					})
					.collect(Collectors.toSet());
							
				return Visibility.toExternalVisibility(externalVisibility);
			case ALL_FOLLOWERS:
				return Visibility.toAllFollowers();
			case ALL_FRIENDS:
				return Visibility.toAllFriends();
			case ALL_USERS:
				return Visibility.toAllUsers();
			case PRIVATE:
				return Visibility.asPrivate();
			case WORLD:
				return Visibility.toWorld();		
			case SELECT_FOLLOWERS:
				//TODO
			case FRIEND_GROUPS:
				//TODO
		}
		return null;
	}
	
	@Override
	public void updateVisibility(Visibility existingVisibility, VisibilityType visibilityType, User user, String[] externalListId, Long groupId,
			Long[] externalFeedId) {
				
		List<String> existingExternalListId = existingVisibility.getExternalList()!=null 
				? existingVisibility.getExternalList().stream().map(ExternalVisibility::getProviderListId).collect(Collectors.toList()) 
				: Collections.emptyList();
		List<String> newExternalListId = externalListId!=null 
				? Arrays.asList(externalListId) 
				: Collections.emptyList();
		
		//if no change:
		if (existingExternalListId.containsAll(newExternalListId) && newExternalListId.containsAll(existingExternalListId)) {
			existingVisibility.setVisibilityType(visibilityType);
			return;
		}
		
		Visibility parsedResult = this.parseVisibility(visibilityType, user, externalListId, groupId, externalFeedId);
		existingVisibility.setVisibilityType(parsedResult.getVisibilityType());
		
		// Instantiate if needed because we'll be adding to it.
		if (existingVisibility.getExternalList()==null) {
			existingVisibility.setExternalList(new LinkedHashSet<>());
		}
		
		//update relationships
		updateList(existingVisibility.getExternalList(), 
				parsedResult.getExternalList()!=null?parsedResult.getExternalList():Collections.emptyList());
		
		return;
	}
	
	protected <T> void updateList(Collection<T> listToUpdate, Collection<T> newList) {
		//ADD
		List<T> addExternalList = newList.stream()
			.filter(Predicate.not(listToUpdate::contains))
			.collect(Collectors.toList());
		listToUpdate.addAll(addExternalList);
		//REMOVE
		List<T> removeExternalList = listToUpdate.stream()
			.filter(Predicate.not(newList::contains))
			.collect(Collectors.toList());
		listToUpdate.removeAll(removeExternalList);
	}

	protected boolean needsUpdate(Object existing, Object updated) {
		return ((existing==null && updated!=null) || !existing.equals(updated));
	}
	
	protected ExternalVisibility createOrUpdateExternalVisibility(IExternalRole role, AbstractFeed feed) {
		Optional<ExternalVisibility> existing = externalVisibilityRepository.findByProviderIdAndProviderListId(feed.getProviderId(), role.getId());
		if (existing.isPresent()) {
			//update
			boolean needUpdate = false;
			if (needsUpdate(existing.get().getName(), role.getName())) {
				existing.get().setName(role.getName());
				needUpdate = true;
			}
			if (needsUpdate(existing.get().getProviderUri(), role.getUrl())) {
				existing.get().setProviderUri(role.getUrl());
				needUpdate = true;
			}
			if (needUpdate) {
				return externalVisibilityRepository.save(existing.get());
			}
		}
		return existing.orElseGet(()->externalVisibilityRepository.save(ExternalVisibility.of(feed.getProviderId(), role.getId(), role.getName(), role.getUrl())));
	}
		
	public boolean isOnExternalList(Visibility visibility, User user) {
		List<String> roleIds = this.getExternalListIds(user);
		
		return visibility.getExternalList().stream()
			.map(ExternalVisibility::getProviderListId)
			.anyMatch(roleIds::contains);
	}
	
	public List<String> getExternalListIds(User user) {
		return externalFeedManager.feedsForUser(user).values().stream()
				.flatMap(List::stream)
				.map(externalFeedManager::getExternalListsUserIsOn)
				.flatMap(List::stream)
				.map(IRole::getId)
				.collect(Collectors.toList());
	}

}
