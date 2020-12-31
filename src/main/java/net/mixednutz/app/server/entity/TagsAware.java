package net.mixednutz.app.server.entity;

import java.util.Set;

import net.mixednutz.app.server.entity.post.AbstractTag;

public interface TagsAware<T extends AbstractTag> {

	Set<T> getTags();
	
	void setTags(Set<T> tags);
	
}
