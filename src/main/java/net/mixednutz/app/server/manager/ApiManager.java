package net.mixednutz.app.server.manager;

import net.mixednutz.api.model.IUser;
import net.mixednutz.api.model.IUserSmall;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.UserProfile;

public interface ApiManager {
	
	public String getAvatarUri(String avatarFilename);

	public <E> InternalTimelineElement toTimelineElement(E entity, User viewer);
	
	public IUserSmall toUser(User entity);
	
	public IUser toUser(User entity, UserProfile profile);
	
}
