package org.linagora.intentDetection.corenlp;

import java.util.HashMap;

import org.apache.commons.lang3.text.WordUtils;
import org.tartarus.snowball.SnowballProgram;

public class Stemmer {
	
	private static HashMap<String, SnowballProgram> stemmers = new HashMap<String, SnowballProgram>();

	public static String stemmWord(String word, String language) {
		try {
			language =  WordUtils.capitalize(language);
			SnowballProgram stemmer = stemmers.get(language);
			if(stemmer == null) {
				stemmer = (SnowballProgram) Class.forName("org.tartarus.snowball.ext." + language + "Stemmer").getConstructor().newInstance();
				stemmers.put(language, stemmer);
			}
			stemmer.setCurrent(word);
			stemmer.stem();
			return stemmer.getCurrent();
			
		}catch(Exception e) {
			System.err.println(language+ " stemmer not exit");
			return word;
		}
		
	}
	
	public static String stemmText(String text, String language) {
		StringBuffer result = new StringBuffer();
		
		String [] words = text.split("\\s+");
		
		for(String word: words) {
			result.append(stemmWord(word, language)+" ");
		}
		
		return result.toString().trim();
		
	}



	public static void main(String[] args) {
		
		System.out.println(Stemmer.stemmWord("souhaiter", "french"));
		System.out.println(Stemmer.stemmWord("invitation", "French"));
		System.out.println(Stemmer.stemmWord("techniquement", "French"));
		System.out.println(Stemmer.stemmWord("actuellement", "French"));
		
		System.out.println(Stemmer.stemmText("si vous êtes disponibles", "french"));
	}
}
