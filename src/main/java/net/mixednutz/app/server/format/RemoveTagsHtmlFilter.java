package net.mixednutz.app.server.format;

public class RemoveTagsHtmlFilter implements HtmlFilter {
	
	private String replaceWith = "";

	@Override
	public String filter(String html) {
		return html.replaceAll("\\<.*?\\>", replaceWith);
	}

	public String getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}

}
