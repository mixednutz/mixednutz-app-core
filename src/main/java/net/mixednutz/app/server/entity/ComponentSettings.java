package net.mixednutz.app.server.entity;

import java.util.Map;

public interface ComponentSettings {
	
	Map<String, ?> getSettings();
	
	boolean includeHtmlFragment();
	
	String includeHtmlFragmentName();
	
	boolean includeScriptFragment();
	
	String includeScriptFragmentName();

}
