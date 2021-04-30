package net.mixednutz.app.server.manager;

import org.springframework.security.core.Authentication;

import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.Oembeds;
import net.mixednutz.app.server.entity.User;

public interface ApiElementConverter<Entity> {

	InternalTimelineElement toTimelineElement(
			InternalTimelineElement element, Entity entity, User viewer, String baseUrl);
		
	boolean canConvert(Class<?> entityClazz);
	
	Oembeds.Oembed toOembed(
			String path, Integer maxwidth, Integer maxheight, String format, 
			Authentication auth, String baseUrl);
	
	boolean canConvertOembed(String path);
	
}
