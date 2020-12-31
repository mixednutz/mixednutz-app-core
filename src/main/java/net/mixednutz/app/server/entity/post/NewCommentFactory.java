package net.mixednutz.app.server.entity.post;

import org.springframework.ui.Model;

public interface NewCommentFactory<C> {
	
	/**
	 * Instantiates a new post and places it into the model to generate
	 * a user form.
	 * 
	 * @param model
	 * @return
	 */
	C newCommentForm(Model model);

}
