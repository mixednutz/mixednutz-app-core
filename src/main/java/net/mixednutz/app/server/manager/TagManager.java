/**
 * 
 */
package net.mixednutz.app.server.manager;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.mixednutz.app.server.entity.TagScore;
import net.mixednutz.app.server.entity.TagsAware;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractTag;

/**
 * @author apfesta
 *
 */
public interface TagManager  {
	
	public String[] splitTags(String tagString);
	
	public String[] getTagsArray(Collection<? extends AbstractTag> tags);
	
	/**
	 * Creates a comma delimited string of the authors tags so they can edit it.
	 * @param tags
	 * @return
	 */
	public String getTagsString(Collection<? extends AbstractTag> tags);
	
	/**
	 * Merges (adds or deletes) tags from a set based on a tagArray
	 * 
	 * @param tagArray
	 * @param tags
	 * @param callback
	 */
	public <T extends AbstractTag> void mergeTags(String[] tagArray, 
			Set<T> tags, Function<String, T> callback);
	
	/**
	 * Adds a tagArray to a set of tags
	 * 
	 * @param tagArray
	 * @param tags
	 * @param author
	 * @param currentUser
	 * @param callback
	 * @return 
	 */
	public <T extends AbstractTag> Collection<T> addTags(String[] tagArray, Set<T> tags, 
			User author, User currentUser, Function<String, T> callback);
	
	/**
	 * Toggle Tag.  Either adds a tag, or removes it.  Returns the added tag or
	 * null if removed.
	 * 
	 * @param tagString
	 * @param tags
	 * @param author
	 * @param currentUser
	 * @param callback
	 * @return
	 */
	public <T extends AbstractTag> T toggleTag(String tagString, Set<T> tags, 
			User author, User currentUser, Function<String, T> callback);
		
	public <T extends AbstractTag> List<TagScore> getTagScores(Set<T> tags, User author, User currentUser);
	
	/**
	 * Totals all of the tags from tag enabled posts
	 * 
	 * @param <P>
	 * @param <T>
	 * @param posts
	 * @return
	 */
	public <P extends TagsAware<T>, T extends AbstractTag> List<TagScore> getTagScores(Set<P> posts);
	
}
