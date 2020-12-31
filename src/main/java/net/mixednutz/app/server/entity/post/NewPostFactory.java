package net.mixednutz.app.server.entity.post;

import org.springframework.ui.Model;

import net.mixednutz.app.server.entity.User;

public interface NewPostFactory<P> {
	
	/**
	 * Instantiates a new post and places it into the model to generate
	 * a user form.
	 * 
	 * @param model
	 * @param owner
	 * @return
	 */
	P newPostForm(Model model, User owner);

}
