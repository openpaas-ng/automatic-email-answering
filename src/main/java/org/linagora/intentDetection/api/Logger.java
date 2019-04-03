package org.linagora.intentDetection.api;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class Logger {
	private File logFile = null;
	
	public Logger(File logFile) {
		this.logFile = logFile;
		try {
			if(!logFile.exists()) {
				FileUtils.writeStringToFile(this.logFile, "Time\tCallingTimes");
			}
			
		}catch(Exception e) {
			System.out.println("Error when log smartReply action");
		}
	}
	
	public void logSmartReplyAction() {
		try {
			String currentTime = DateTime.now().toString(ISODateTimeFormat.dateHourMinute());
			
			List<String> fileContents = FileUtils.readLines(logFile);
			String endLine = fileContents.get(fileContents.size() - 1);
			if(!endLine.equalsIgnoreCase("Time\tCallingTimes")){
			String time = endLine.split("\t")[0];
			int value = Integer.parseInt(endLine.split("\t")[1]);
			//System.out.println("Time: " + time + ", " + value);
			if(time.equalsIgnoreCase(currentTime)) {
				value = value + 1;
				fileContents.remove(fileContents.size() - 1);
				//System.out.println("Meme valeur");
				
			}else {
				//System.out.println("Pas la Meme valeur");
				value = 1;
			}
			fileContents.add(currentTime+"\t"+value);
			}else {
				//System.out.println("Pas la Meme valeur");
				fileContents.add(currentTime+"\t1");
			}
			
			FileUtils.writeStringToFile(logFile, String.join("\n", fileContents));
			
		}catch(Exception e) {
			System.out.println("Error when log smartReply action");
		}
		
	}
	public File getLogFile() {
		return logFile;
	}

}
