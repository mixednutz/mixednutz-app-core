package net.mixednutz.app.server.manager.post.impl;

import org.springframework.beans.factory.annotation.Autowired;

import net.mixednutz.api.core.model.NetworkInfo;
import net.mixednutz.app.server.entity.InternalTimelineElement;
import net.mixednutz.app.server.entity.InternalTimelineElement.Type;
import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.entity.post.AbstractPostComment;
import net.mixednutz.app.server.manager.ApiElementConverter;

public abstract class AbstractPostCommentEntityConverter<C extends AbstractPostComment> implements ApiElementConverter<C>{

	@Autowired
	protected NetworkInfo networkInfo;
	
	@Override
	public InternalTimelineElement toTimelineElement(InternalTimelineElement api, C entity, User viewer,
			String baseUrl) {
		api.setType(new Type("Comment",
				networkInfo.getHostName(),
				networkInfo.getId()+"_Comment"));
		api.setId(entity.getCommentId());
		api.setDescription(entity.getBody());
		
		//This overwrites the default setting. URL is left alone.
		api.setUri(entity.getUriNoAnchor());
		
		api.setInReplyToUri(entity.getPost().getUri());
		api.setInReplyToUrl(baseUrl+entity.getPost().getUri());
		
		populatePostProperties(api, entity);
		return api;
	}
	
	abstract protected void populatePostProperties(InternalTimelineElement api, C entity);

}
