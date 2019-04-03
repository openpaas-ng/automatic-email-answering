package org.linagora.intentDetection.coreNLP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.linagora.intentDetection.corenlp.Token;

import junit.framework.TestCase;


public class TokenTest extends TestCase{
	
	public void testTokensEquality() {
		Token token1 = new Token();
		token1.setStartPosition(0);
		token1.setEndPosition(2);
		
		Token token2 = new Token();
		token2.setStartPosition(0);
		token2.setEndPosition(2);
		
		Token token3 = new Token();
		token3.setStartPosition(4);
		token3.setEndPosition(6);
		
		
		assertTrue(token1.equals(token2));
		assertFalse(token1.equals(token3));
		
	}
	
	public void testTokensEqualityInList() {
		Token token1 = new Token();
		token1.setStartPosition(0);
		token1.setEndPosition(2);
		
		Token token2 = new Token();
		token2.setStartPosition(0);
		token2.setEndPosition(2);
		
		Token token3 = new Token();
		token3.setStartPosition(4);
		token3.setEndPosition(6);
		
		List<Token> tokens = new ArrayList<Token>();
		tokens.add(token2);
		tokens.add(token3);
		
		assertTrue(tokens.contains(token1));
	}
	
	public void testTokensSort() {
		Token token1 = new Token();
		token1.setStartPosition(0);
		token1.setEndPosition(2);
		
		Token token2 = new Token();
		token2.setStartPosition(3);
		token2.setEndPosition(6);
		
		Token token3 = new Token();
		token3.setStartPosition(7);
		token3.setEndPosition(11);
		
		Token token4 = new Token();
		token4.setStartPosition(12);
		token4.setEndPosition(15);
		
		List<Token> tokens = new ArrayList<Token>();
		tokens.add(token3);
		tokens.add(token2);
		tokens.add(token1);
		tokens.add(token4);
		
		Collections.sort(tokens);
		
		boolean ordred = tokens.get(0) == token1 && tokens.get(1) == token2 && tokens.get(2) == token3 && tokens.get(3) == token4;
		
		assertTrue(ordred);
	}

}
