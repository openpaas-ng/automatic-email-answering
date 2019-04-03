package org.linagora.intentDetection.corenlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextCleaner {
	private static String [] regexList = {"^>* *Le .+, .+ a écrit :",
			"^Le .+, .+ a écrit :",
			"^>* *On .+, .+ wrote:",
			"^.*, .+ a écrit :",
			"^.*On .+, .+ wrote:",
			"^On .+, .+ wrote:",
			
			"^>* *Le .+, .+ de [\\w.]+@[\\w]+.\\w+$",
			"^Le .+, .+ de [\\w.]+@[\\w]+.\\w+$",
			"^>* *On .+, .+ from [\\w.]+@[\\w]+.\\w+$",
			"^.*On .+, .+ from [\\w.]+@[\\w]+.\\w+$",
			"^On .+, .+ from [\\w.]+@[\\w]+.\\w+$",
			
			"^\\d\\d.+ \\d\\d:\\d\\d [\\w\\d\\W]+? <[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+>:",
			"^De : [\\w\\d\\W]+? <[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+>",
			"^From: [\\w\\d\\W]+? <[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+>",
			"(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Dimanche|Lundi|Mardi|Mercredi|Jeudi|Vendredi|Samedi),.+,.+ from [\\w\\d\\W]+? [a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+ *:",
			"-------- Message original --------",
			"-------- Courriel original --------",
			"-------- Forwarded Message --------",
			"------- Message transféré -------",
			"-------- Message transféré --------",
			"^\\*+"
			};
	
	public static String cleanReplyBlock(String mail) {
		
						
		for(String regex: loadCleanReplyBlockRegex()) {
			Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(mail);
			
			while(matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				mail = mail.substring(0, start);
				break;
			}
		}
		
		StringBuffer cleanedText = new StringBuffer();
		for(String line: mail.split("\n")) {
			if(!line.trim().startsWith(">")) {
				cleanedText.append((line+"\n"));
			}
		}
		
		return cleanedText.toString();
	}
	
	public static String getFirstDiscussionThread(String text) {
		StringBuffer discussionThread = new StringBuffer();
		String [] lines = text.split("[\n\r]");
		for(String line: lines) {
			if(!line.matches("^>{2,}.*")) {
				discussionThread.append(line+"\n");
			}
		}
		//System.out.println(discussionThread);
		return discussionThread.toString();
	}
	
	private static Set<String> loadCleanReplyBlockRegex() {
		String currentDir = System.getProperty("user.dir");
		String path = currentDir + "/TextCleaner.regex";
		Set<String> list = new HashSet<String>();
		try {
			
			File f = new File(path);

			BufferedReader b = new BufferedReader(new FileReader(f));

			String readLine = "";

			while ((readLine = b.readLine()) != null) {
				list.add(readLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static String formatText(String mail) {
		mail = mail.replace("Subject: ", "");
		mail = mail.replace("\nBody: ", " .\n");
		mail = mail.replace("?==?UTF-8?Q?", "");
		mail = mail.replaceAll("([\\.:\\?!]+( +|\\n{2,}))", "$1 .\n");
		mail = mail.replaceAll("(\n{2,})", " .$1");
		mail = mail.replaceAll("([<>\\{\\}\\|\\[\\]\\(\\)])", " $1 ");
		mail = mail.replaceAll(" +", " ").trim();
		mail = mail.replace(". . .", "... .\n");
	//	mail = mail.replaceAll("\n", " ");
		return mail;
	}
	
	public static String replaceNonBreakingWhiteSpace(String text) {
		text = text.replaceAll("[\u00a0\u202f\u2007]", " ");
		return text;
	}

	public static void main(String[] args) {
		
		
		String mail ="Hello,\n peut-tu m'envoyer une capture d'écran ?\n" + 
				"\n" + 
				"Le 2018-07-19 20:13, Michael BAILLY a Descrit :";
		System.out.println(cleanReplyBlock(mail));
		

	}

}

