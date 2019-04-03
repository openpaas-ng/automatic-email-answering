package org.linagora.intentDetection;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.semantic.MatchedIntent;
import org.linagora.intentDetection.semantic.ontology.Intent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class IntentDetectorTester {
	
	
	private static PrintWriter output;
	
	private static String readEmail(Path path) {
		FileInputStream inputStream = null;
		 String everything = null;
		try {
			inputStream = new FileInputStream(path.toString());
		    everything = IOUtils.toString(inputStream);
		} catch(Exception e) {
		    return "";
		}
		
		return everything;
	}
	private static void writeText(String text) {
		try {
			output.print(text);
		}catch(Exception e) {
			e.printStackTrace();
			output.close();
		}
	}
	private static void evaluateIntentDetection(Path path, IntentDetector intentDetector) {
				try {
					FileWriter fw = new FileWriter("statistic.txt", true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    output = new PrintWriter(bw);
		String email = readEmail(path);
		String intentOnEmail = path.getParent().getFileName().toString();
		System.out.println("File: "+path.toString() + " >> " + intentOnEmail);
		HashMap<Integer, List<MatchedIntent>> matchedIntents = intentDetector.detectIntents(email);
		boolean exactIntentMatched = false;
		String method = "";
		if(matchedIntents.size() == 1) {
			//only one intent in the email
			
			method = "BEST FIRST 3 Intents";
			List<MatchedIntent> detectedIntents = matchedIntents.values().iterator().next();
						
			int counter = 0;
			for(int i = 0; i< detectedIntents.size(); i++) {
				if(detectedIntents.get(i).getIntent().getLocalName().equalsIgnoreCase(intentOnEmail) && counter < 3) {
					exactIntentMatched = true;
					break;
				}else {
					counter ++;
				}
			}
		}else if(matchedIntents.size() > 1){
			method = "BEST FIRST Intent from " + matchedIntents.keySet().size();
			int counter = 0;
			for(Integer key: matchedIntents.keySet()) {
				counter = 0;
				List<MatchedIntent> detectedIntents = matchedIntents.get(key);
				for(int i = 0; i< detectedIntents.size(); i++) {
					if(detectedIntents.get(i).getIntent().getLocalName().equalsIgnoreCase(intentOnEmail) && counter < 3) {
						exactIntentMatched = true;
						break;
					}else {
						counter ++;
					}
				}
			}
		}else {
			method = "NO INTENT MATCHED";
		}
		
		if(exactIntentMatched) {
			writeText(path.toString() + "\t" + method + "\tYES\n");
			
		}else {
			writeText(path.toString()+ "\t" + method + "\tNO\n");
		}
		output.close();
				}catch(Exception e) {
					output.close();
					e.printStackTrace();
				}
	}

	public static void main(String[] args) {
		
		
		IntentDetector intentDetector = new IntentDetector(Language.french, Parameters.DUCKLING_URL, Parameters.ONTOLOGY_PATH);
		System.out.println("browser corpus");
		//String path = "/home/zsellami/dev/corpus/Intent Corpus Test/";
		//String path = "/home/zsellami/Téléchargements/Corpus_Intents";
		//String path = "/home/zsellami/Téléchargements/Corpus_test";
		String path = "/home/zsellami/Téléchargements/Corpus_test/Corpus_test_avec_fils_de_messages2";
		try {
			
			
			Files.walk(Paths.get(path))
	        .filter(Files::isRegularFile)
	        .forEach(x -> evaluateIntentDetection(x, intentDetector));
			

		}catch(Exception e) {
			e.printStackTrace();
			output.close();
			
		}
	
	}

}
