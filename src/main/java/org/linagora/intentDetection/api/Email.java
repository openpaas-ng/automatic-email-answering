package org.linagora.intentDetection.api;

import java.util.HashMap;

public class Email {
	
	private String messageId = "";
	private String body = "";
	private String subject = "";
	private String userName = "";
	private String userAddress = "";
	
	private HashMap<String, String> from = new HashMap<String, String>();
	private HashMap<String, String> recipients = new HashMap<String, String>();
	
	public Email() {
		
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String user) {
		this.userName = user;
	}

	public HashMap<String, String> getFrom() {
		return from;
	}

	public void setFrom(HashMap<String, String> from) {
		this.from = from;
	}

	public HashMap<String, String> getRecipients() {
		return recipients;
	}

	public void setRecipients(HashMap<String, String> recipients) {
		this.recipients = recipients;
	}
	
	@Override
	public String toString() {
		return "Email [messageId=" + messageId + ", body=" + body + ", subject=" + subject + ", user=" + userName
				+ ", from=" + from + ", recipients=" + recipients + "]";
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	

}
