package net.mixednutz.app.server.manager;

import java.util.Map;

import net.mixednutz.app.server.entity.UserEmailAddress;

public interface EmailMessageManager {
	
	void send(String templateName, EmailMessage message, Map<String, Object> model);
	
	public static class EmailMessage {
		Iterable<UserEmailAddress> to;
		UserEmailAddress from;
		String subject;
		
		public Iterable<UserEmailAddress> getTo() {
			return to;
		}
		public void setTo(Iterable<UserEmailAddress> to) {
			this.to = to;
		}
		public UserEmailAddress getFrom() {
			return from;
		}
		public void setFrom(UserEmailAddress from) {
			this.from = from;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
	}

}
