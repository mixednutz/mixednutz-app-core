package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

public interface ScheduledPost {
	
	/**
	 * Publish Date of this Post
	 * @return
	 */
	ZonedDateTime getPublishDate();

	void setPublishDate(ZonedDateTime publishDate);

	/**
	 * External Feed for crosposting this Post at the scheduled publishDate
	 * @return
	 */
	Long[] getExternalFeedId();

	void setExternalFeedId(Long[] externalFeedId);

	/**
	 * Email Friend group at the scheduled publishDate
	 * @return
	 */
	boolean isEmailFriendGroup();

	void setEmailFriendGroup(boolean emailFriendGroup);
	
}
