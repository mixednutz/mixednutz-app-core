/**
 * 
 */
package net.mixednutz.app.server.entity.post;

import java.time.ZonedDateTime;

import net.mixednutz.app.server.entity.User;

/**
 * @author Andy
 *
 */
public interface Comment {
	
	public String getBody();
	public void setBody(String body);
	
	public User getAuthor();
	public void setAuthor(User author);
	
	public ZonedDateTime getDateCreated();
	public void setDateCreated(ZonedDateTime timestamp);
	
	public ZonedDateTime getDateUpdated();
	public void setDateUpdated(ZonedDateTime timestamp);
	
	public <C extends Comment> void setParentComment(C parentComment);

}
