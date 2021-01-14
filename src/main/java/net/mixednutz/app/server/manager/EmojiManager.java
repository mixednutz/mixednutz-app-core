package net.mixednutz.app.server.manager;

import java.util.List;
import java.util.Map;

import net.mixednutz.app.server.entity.Emoji;
import net.mixednutz.app.server.entity.EmojiCategory;

public interface EmojiManager {

	void load();
	
	Map<EmojiCategory, List<Emoji>> findOrganizeByCategory();
	
}
