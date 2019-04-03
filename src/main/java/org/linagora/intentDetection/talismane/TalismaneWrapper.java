package org.linagora.intentDetection.talismane;

import java.io.BufferedReader;
import java.io.File;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.linagora.intentDetection.corenlp.Stemmer;
import org.linagora.intentDetection.corenlp.TextCleaner;
import org.linagora.intentDetection.corenlp.Token;

public class TalismaneWrapper {

	private static String hostName = null;
	private static int portNumber;
	private static String talismaneDir = null;
	private static String jarName = null;
	private static String confName = null;
	private static Process process = null;

	public static void initTalismaneWrapper(String hostName, int portNumber, String talismaneDir, String jarName, String confName) {
		TalismaneWrapper.hostName = hostName;
		TalismaneWrapper.portNumber = portNumber;
		TalismaneWrapper.talismaneDir = talismaneDir;
		TalismaneWrapper.jarName = jarName;
		TalismaneWrapper.confName = confName;
	}

	public static Process runTalismaneServer() {
		List<String> arg = new ArrayList<String>();
		arg.add("/usr/bin/java");
		arg.add("-Xmx2G");
		arg.add("-Dconfig.file=" + talismaneDir + "/" + confName);
		arg.add("-jar");
		arg.add(talismaneDir + "/" + jarName);
		arg.add("--analyse");
		arg.add("--endModule=posTagger");
		arg.add("--sessionId=fr");
		arg.add("--encoding=UTF8");
		arg.add("--mode=server");
		arg.add("--logConfigFile=" + talismaneDir + "/" + "logback.xml");
		arg.add("--template=" + talismaneDir + "/" + "NoMissingLemmas.ftl");
		arg.add("port=" + portNumber);
		arg.add("&");
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(arg);
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			pb.directory(new File(talismaneDir));
			System.out.println(pb.command());
			//System.exit(0);

			Process process = pb.start();
			
//			String line = null;
//			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			while ((line = input.readLine()) != null) {
//				System.out.println(line);
//			}
//			
//			
//			input.close();
			System.out.println("Starting Talismane Server...");
			//process.waitFor(5, TimeUnit.SECONDS);
			System.out.println("Talismane Server start.");
			TalismaneWrapper.process = process;

			return process;
		} catch (Exception e) {
			System.out.println("RUN TALISMANE SERVER ERROR\n" );
			e.printStackTrace(System.out);
			return null;
		}
	}

	public static List<TalismaneToken> callTalismane(String text) {
		if(process == null) {
			System.out.println("Init TalismaneWrapper before");
			return null;
		}else {
			List<TalismaneToken> talismaneResults = new ArrayList<TalismaneToken>();
			try {

				// open socket to server
				Socket socket = new Socket(hostName, portNumber);
				OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8"));
				BufferedReader in = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));

				String fromServer;
				String input = text;
				input += "\f\f\f";
				// System.out.println("Sending input to server: " + input);

				// Send user input to the server
				out.write(input);
				out.flush();

				int sentId = -1;
				int count = 1;

				// Display output from server
				while ((fromServer = in.readLine()) != null) {
					if (fromServer.equals("")) {
						sentId++;
						count = 1;
					} else {
						//System.out.println(fromServer);
						TalismaneToken talismaneResult = parseLine(text, fromServer, count, sentId);
//						String stemm = Stemmer.stemmWord(talismaneResult.getText().toLowerCase(), "french");
//						talismaneResult.setStemm(stemm);
						talismaneResults.add(talismaneResult);
						count++;

					}
				}

				Collections.sort(talismaneResults);
//				for (TalismaneToken t : talismaneResults) {
//					System.out.println(t.toString());
//				}

				socket.close();
				return talismaneResults;

			} catch (Exception e) {
				
				System.out.println("CALL TALISMANE SERVER ERROR\n");
				e.printStackTrace(System.out);
				return talismaneResults;

			}
		}
		

	}
	
	private static TalismaneToken parseLine(String text, String line, int count, int sentId) {
		TalismaneToken result = new TalismaneToken();
		String[] elements = line.split("\t");
		result.setSentId(sentId);
		result.setRank(count);
		try {
			result.setStartPosition(Integer.parseInt(elements[1]));
			result.setEndPosition(Integer.parseInt(elements[2]));
			result.setText(text.substring(result.getStartPosition(), result.getEndPosition()));
			
			result.setLemma(elements[4]);
			if(result.getLemma().startsWith("$") && result.getLemma().endsWith("$")) {
				result.setLemma(text.substring(result.getStartPosition(), result.getEndPosition()));
			}
			result.setCategory(elements[5]);
			result.setPostag(elements[6]);
			String[] morphology = elements[7].split("\\|");
			for (String m : morphology) {
				String[] values = m.split("=");
				result.getMorphology().put(values[0], values[1]);
			}

		} catch (Exception e) {
			// e.printStackTrace();
		}

		return result;
	}
	
public static String lemmatiseText(String text) {
		
		List<TalismaneToken> talismaneTokens = callTalismane(text);
		try {
			StringBuffer lemma = new StringBuffer("");
			for(TalismaneToken token: talismaneTokens) {
				lemma.append(token.getLemma()+" ");
			}
			
			return lemma.toString().trim();
						
		}catch(Exception e) {
			
			System.out.println("CALL LEMMATISE TEXT ERROR\n" );
			e.printStackTrace(System.out);
			return null;
		}
	}

public static String getGender(String text) {
	
	try {
		List<TalismaneToken> talismaneTokens = callTalismane(text);
		for(TalismaneToken token: talismaneTokens) {
			if(token.getMorphology().get("g") !=null) {
				return token.getMorphology().get("g");
			}
			
		}
		return "";
		
					
	}catch(Exception e) {
		System.out.println("CALL DETECT GENDER ERROR\n");
		e.printStackTrace(System.out);
		return "";
	}
}

	public static void main(String[] args) {
		String hostName = "localhost";
		int portNumber = 7272;
		String jarName = "talismane-core-5.1.2.jar";
		String confName = "talismane-fr-5.0.4.conf";
		String talismaneDir = "/home/zsellami/Téléchargements/talismane-distribution-5.1.2-bin";
		initTalismaneWrapper(hostName, portNumber, talismaneDir, jarName, confName);
		Process talismaneProcess = TalismaneWrapper.runTalismaneServer();

		String text = "l'adresse ip est 123.345.456.435. La soirée le 12-05-2016 à 6h30";

		
		text = TextCleaner.replaceNonBreakingWhiteSpace(text);
		
		text = TextCleaner.cleanReplyBlock(text);
		text = TextCleaner.formatText(text);
		TalismaneWrapper.callTalismane(text);
		System.out.println(text);
		System.out.println(TalismaneWrapper.lemmatiseText(text));
		System.out.println(TalismaneWrapper.lemmatiseText("réunion"));
		System.out.println(TalismaneWrapper.lemmatiseText("audience"));
		System.out.println(TalismaneWrapper.lemmatiseText("rendez-vous"));
		System.out.println(TalismaneWrapper.lemmatiseText("rdv"));
		System.out.println(TalismaneWrapper.lemmatiseText("réunion"));
		System.out.println(TalismaneWrapper.lemmatiseText("pourrai-t-il"));
		System.out.println(TalismaneWrapper.lemmatiseText("Pour notre réunion à Paris, je vous propose de faire le point à 10h."));
		
		System.out.println("Gender: "+TalismaneWrapper.getGender("réunion"));
		talismaneProcess.destroy();
		System.out.println("Gender: "+TalismaneWrapper.getGender("livrable"));

	}
	
	
	
}
