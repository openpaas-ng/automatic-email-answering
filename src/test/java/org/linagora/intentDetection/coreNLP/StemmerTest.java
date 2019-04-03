package org.linagora.intentDetection.coreNLP;

import org.linagora.intentDetection.corenlp.Stemmer;

import junit.framework.TestCase;

public class StemmerTest extends TestCase{
	
	public void testStemmFrenchWord (){
		String word = "Actuellement";
		String stemm = Stemmer.stemmWord(word, "french");
		
		assertTrue(stemm.equals("Actuel"));
		
	}
	
	public void testStemmFrenchText() {
		String text = "ils débutent Actuellement";
		String stemm = Stemmer.stemmText(text, "french");
		
		assertTrue(stemm.equals("il débutent Actuel"));
	}

}
