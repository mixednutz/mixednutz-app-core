package net.mixednutz.app.server.entity;

import java.util.Map;

public interface ComponentSettings {
	
	Map<String, ?> getSettings();
	
	// CSS
	boolean css();
	
	String cssHref();
	
	// TIMELINE
	
	boolean includeTimelineTemplateHtmlFragment();
	
	String includeTimelineTemplateHtmlFragmentName();
	
	//SETTINGS
	
	boolean includeHtmlFragment();
	
	String includeHtmlFragmentName();
	
	boolean includeScriptFragment();
	
	String includeScriptFragmentName();
	
	// FORMS
	
	boolean includeNewFormModal();
	
	String includeNewFormModalContentFragmentName();
	
	String newFormModalId();

}
