package net.mixednutz.app.server.manager;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import net.mixednutz.api.model.IUser;
import net.mixednutz.api.model.IUserSmall;
import net.mixednutz.api.model.IVisibility;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.Oembeds.Oembed;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.UserProfile;
import net.mixednutz.app.server.entity.Visibility;

public interface ApiManager {
	
	public IVisibility toVisibility(Visibility visibility);
	
	public String getAvatarUri(String avatarFilename);

	public <E> InternalTimelineElement toTimelineElement(E entity, User viewer);
	
	public <E> InternalTimelineElement toTimelineElement(E entity, User viewer, String baseUrl);
	
	public IUserSmall toUser(User entity);
	
	public IUser toUser(User entity, UserProfile profile);
	
	public Optional<Oembed> toOembed(String path, Integer maxwidth, Integer maxheight, String format, 
			Authentication auth, String baseUrl);
	
}
