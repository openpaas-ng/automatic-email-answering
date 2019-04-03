package org.linagora.intentDetection.entities;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.linagora.intentDetection.corenlp.Token;

import junit.framework.TestCase;


public class EntityTest extends TestCase{
	
	public void testEntitiesEquality() {
		Token token1 = new Token();
		token1.setStartPosition(0);
		token1.setEndPosition(2);
		
		Token token2 = new Token();
		token2.setStartPosition(4);
		token2.setEndPosition(8);
		
		LinkedList<Token> tokens1 = new LinkedList<Token>();
		tokens1.add(token1);
		tokens1.add(token2);
		Person person1 = new Person(tokens1);
		
		Token token3 = new Token();
		token3.setStartPosition(0);
		token3.setEndPosition(2);
		
		Token token4 = new Token();
		token4.setStartPosition(4);
		token4.setEndPosition(8);
		
		LinkedList<Token> tokens2 = new LinkedList<Token>();
		tokens2.add(token3);
		tokens2.add(token4);
		Person person2 = new Person(tokens2);
		
		assertTrue(person1.equals(person2));
		
	}
	
	public void testEntitiesEqualityInList() {
		Token token1 = new Token();
		token1.setStartPosition(0);
		token1.setEndPosition(2);
		
		Token token2 = new Token();
		token2.setStartPosition(4);
		token2.setEndPosition(8);
		
		LinkedList<Token> tokens1 = new LinkedList<Token>();
		tokens1.add(token1);
		tokens1.add(token2);
		Person person1 = new Person(tokens1);
		
		Token token3 = new Token();
		token3.setStartPosition(0);
		token3.setEndPosition(2);
		
		Token token4 = new Token();
		token4.setStartPosition(4);
		token4.setEndPosition(8);
		
		LinkedList<Token> tokens2 = new LinkedList<Token>();
		tokens2.add(token3);
		tokens2.add(token4);
		Person person2 = new Person(tokens2);
		
		List<Entity> persons = new ArrayList<Entity>();
		persons.add(person1);
		
		assertTrue(persons.contains(person2));
	}
	
	public void testConvertEntityToToken() {
		Token token1 = new Token();
		token1.setSentId(1);
		token1.setRank(1);
		token1.setStartPosition(0);
		token1.setEndPosition(3);
		token1.setText("New");
		token1.setNer("LOCATION");
		token1.setPos("NNP");
		
		Token token2 = new Token();
		token2.setSentId(1);
		token2.setRank(2);
		token2.setStartPosition(4);
		token2.setEndPosition(9);
		token2.setText("York");
		token2.setNer("LOCATION");
		token2.setPos("NNP");
		
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(token1);
		tokens.add(token2);
		
		Entity newYork = new Location(tokens);
		
		Token newYorkAsToken = newYork.convertToToken();
		
		assertTrue(newYork.convertToToken() != null);
		assertTrue(newYorkAsToken.getText().equals("New York"));
		assertTrue(newYorkAsToken.getStartPosition() == 0);
		assertTrue(newYorkAsToken.getEndPosition() == 9);
		assertTrue(newYorkAsToken.getRank() == 1);
		assertTrue(newYorkAsToken.getSentId() == 1);
		assertTrue(newYorkAsToken.getNer().equals("Location"));
		assertTrue(newYorkAsToken.getPos().equals("NNP_NNP"));
		
	}

}
