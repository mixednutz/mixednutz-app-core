package net.mixednutz.app.server.manager;

import java.time.Instant;

import net.mixednutz.api.model.IPage;
import net.mixednutz.api.model.IPageRequest;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.User;

public interface TimelineElementManager {

	IPage<InternalTimelineElement,Instant> getTimelineInternal(
			User owner, IPageRequest<String> paging);
	
	IPage<InternalTimelineElement,Instant> getUserTimelineInternal(
			User owner, User viewer, IPageRequest<String> paging);
	
}
