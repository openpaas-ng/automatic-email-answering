package org.linagora.intentDetection.api;

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import javax.mail.internet.MimeUtility;

import org.jsoup.Jsoup;

import javax.mail.internet.InternetAddress;
public class EmlReader {

	public static void main(String args[]) throws Exception {
		getEmail("/home/zsellami/Téléchargements/fwd.eml");

	}

	public static Email getEmail(String emlFilePath) {
		try {
			Email email = new Email();

			Properties props = System.getProperties();
			props.put("mail.host", "smtp.dummydomain.com");
			props.put("mail.transport.protocol", "smtp");

			Session mailSession = Session.getDefaultInstance(props, null);
			InputStream source = new FileInputStream(new File(emlFilePath));
			MimeMessage message = new MimeMessage(mailSession, source);
			
			String subject = MimeUtility.decodeText(message.getSubject());
			String user = message.getHeader("Delivered-To")[0];
			String fromString = MimeUtility.decodeText(message.getFrom()[0].toString());
			InternetAddress from = InternetAddress.parse(fromString)[0];
			String body = getTextFromMimeMultipart((MimeMultipart) message.getContent());
		//	String body = new HtmlToPlainText().getPlainText(Jsoup.parse(message.getContent().toString()).body());
			String messageId = message.getMessageID();
//			
//			System.out.println("Subject : " + subject);
//			System.out.println("Delivered-To: " + user);
//			System.out.println("From : " + from);
//			System.out.println("Body : " + body);
//			System.out.println("Delivered-To: " + user);
//			System.out.println("MessageId: " + messageId);
			
			HashMap<String, String> recipients = new HashMap<String, String>();
			recipients.putAll(getRecipient(message, RecipientType.TO));
			recipients.putAll(getRecipient(message, RecipientType.CC));
			recipients.putAll(getRecipient(message, RecipientType.BCC));
			
			email.setMessageId(messageId);
			email.setBody(body);
			email.setSubject(subject);
			email.setRecipients(recipients);
			
			if(recipients.get(user) != null) {
				email.setUserName(recipients.get(user));
			}else {
				email.setUserName(user);
			}
						
			if(from.getPersonal() != null) {
				email.getFrom().put(from.getAddress(), from.getPersonal());
			}else {
				email.getFrom().put(from.getAddress(), from.getAddress());
			}

			return email;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	private static HashMap<String, String> getRecipient(MimeMessage message, RecipientType type){
		HashMap<String, String> recipients = new HashMap<String, String>();
		try {
			Address[] address = message.getRecipients(type);
			if(address != null) {
				for(Address adr: address) {
					InternetAddress internetAdress = InternetAddress.parse(adr.toString())[0];
					//System.out.println(type.toString() + ": " + internetAdress.getAddress()+" "+ internetAdress.getPersonal());
					if(internetAdress.getPersonal() != null) {
						recipients.put(internetAdress.getAddress(), MimeUtility.decodeText(internetAdress.getPersonal()));
					}else {
						recipients.put(internetAdress.getAddress(), internetAdress.getAddress());
					}
				}
			}
			
		}catch(Exception e) {
			return recipients;
		}
		
		return recipients;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) {
		try {
			String result = "";
			int partCount = mimeMultipart.getCount();
			for (int i = 0; i < partCount; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					result = result + "\n" + bodyPart.getContent();
					break; // without break same text appears twice in my tests
				} else if (bodyPart.isMimeType("text/html")) {
					String html = (String) bodyPart.getContent();
					// result = result + "\n" + org.jsoup.Jsoup.parse(html).body().text();
					 result = result + "\n" + new HtmlToPlainText().getPlainText(Jsoup.parse(html).body());
					//result = html;
				} else if (bodyPart.getContent() instanceof MimeMultipart) {
					result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
				}
			}
			return result;

		} catch (Exception e) {
			return "";
		}

	}

}