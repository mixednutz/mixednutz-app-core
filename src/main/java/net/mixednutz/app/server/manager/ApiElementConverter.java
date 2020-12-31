package net.mixednutz.app.server.manager;

import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.User;

public interface ApiElementConverter<Entity> {

	InternalTimelineElement toTimelineElement(
			InternalTimelineElement element, Entity entity, User viewer);
	
	boolean canConvert(Class<?> entityClazz);
	
}
