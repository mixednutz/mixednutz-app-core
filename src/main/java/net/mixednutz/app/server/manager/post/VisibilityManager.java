package net.mixednutz.app.server.manager.post;

import java.util.List;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.Visibility;
import net.mixednutz.app.server.entity.VisibilityType;

public interface VisibilityManager {
	
	/**
	 * Ensures the user select Visibility is prepared for database insertion
	 * 
	 * @param visibilityType
	 * @param user
	 * @param externalListId
	 * @param groupId
	 * @param externalFeedId
	 * @return
	 */
	Visibility parseVisibility(VisibilityType visibilityType, User user, 
//			Integer friendGroupId, 
			String[] externalListId,
			Long groupId,
			Long[] externalFeedId);
	
	/**
	 * Updates the existing Visibility given the new selections
	 * 
	 * @param existingVisibility
	 * @param user
	 * @param externalListId
	 * @param groupId
	 * @param externalFeedId
	 * @return
	 */
	void updateVisibility(Visibility existingVisibility, VisibilityType visibilityType, User user, 
//			Integer friendGroupId, 
			String[] externalListId,
			Long groupId,
			Long[] externalFeedId);
	
	/**
	 * Checks whether a user is granted visibility via an Exeternal List
	 * 
	 * @param visibility
	 * @param user
	 * @return
	 */
	boolean isOnExternalList(Visibility visibility, User user);
	
	/**
	 * Gets the list of user granted externalListIds. (maps to the provider's list id)
	 * 
	 * @param user
	 * @return
	 */
	List<String> getExternalListIds(User user);

}
