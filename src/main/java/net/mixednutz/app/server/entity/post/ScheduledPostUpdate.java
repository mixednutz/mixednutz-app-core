package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

public interface ScheduledPostUpdate {
	
	/**
	 * Effective Date of this Post
	 * @return
	 */
	ZonedDateTime getEffectiveDate();

	void setEffectiveDate(ZonedDateTime publishDate);
	
	/**
	 * External Feed for crosposting this Post at the scheduled publishDate
	 * @return
	 */
	Long[] getExternalFeedId();

	void setExternalFeedId(Long[] externalFeedId);
	
}
