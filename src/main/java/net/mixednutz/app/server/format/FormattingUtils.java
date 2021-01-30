package net.mixednutz.app.server.format;

import java.util.Date;

public interface FormattingUtils {
	
	String formatAbsoluteUrl(String relativeUrl);
	
	String formatAbsoluteUrl(String relativeUrl, boolean includeContextPath);
	
	String formatDateTodayYesterday(Date date, String datePattern);
	
	String formatDateTimeTodayYesterday(Date date, String datePattern, String timePattern);

	String formatTimeSince(long timestamp);
	
	String removeSpaces(String s);
	
}
