package org.linagora.intentDetection.semantic;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.entities.Location;
import org.linagora.intentDetection.entities.Misc;
import org.linagora.intentDetection.entities.Person;
import org.linagora.intentDetection.entities.Time;

import junit.framework.TestCase;


public class EntityResolverTest extends TestCase{
	
	public void testResolveIncludedEntities() {
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
		token2.setStartPosition(5);
		token2.setEndPosition(9);
		token2.setText("York");
		token2.setNer("LOCATION");
		token2.setPos("NNP");
		
		Token token3 = new Token();
		token3.setSentId(1);
		token3.setRank(2);
		token3.setStartPosition(11);
		token3.setEndPosition(15);
		token3.setText("City");
		token3.setNer("LOCATION");
		token3.setPos("NNP");
		
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(token1);
		tokens.add(token2);
		tokens.add(token3);
		
		Entity newYorkCity = new Location(tokens);
		
		LinkedList<Token> tokens2 = new LinkedList<Token>();
		tokens2.add(token1);
		tokens2.add(token2);
		
		
		Entity newYork = new Location(tokens2);
		
		List<Entity> entities1 = new ArrayList<Entity>();
		List<Entity> entities2 = new ArrayList<Entity>();
		
		entities1.add(newYork);
		entities2.add(newYorkCity);
		EntityResolver resolver = new EntityResolver();
		
		List<Entity> resolvedEntities = resolver.resolveEntities(entities1, entities2);
		
		assertTrue(resolvedEntities.size() == 1);
		assertTrue(resolvedEntities.get(0).getText().equals("New York City"));
					
	}
	
	public void testCollideSameEntities() {
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
		token2.setStartPosition(5);
		token2.setEndPosition(9);
		token2.setText("York");
		token2.setNer("LOCATION");
		token2.setPos("NNP");
		
		Token token3 = new Token();
		token3.setSentId(1);
		token3.setRank(3);
		token3.setStartPosition(11);
		token3.setEndPosition(15);
		token3.setText("City");
		token3.setNer("LOCATION");
		token3.setPos("NNP");
		
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(token1);
		tokens.add(token2);
		
		
		Entity newYork = new Location(tokens);
		
		LinkedList<Token> tokens2 = new LinkedList<Token>();
		tokens2.add(token2);
		tokens2.add(token3);
		
		
		Entity yorkCity = new Location(tokens2);
		
		List<Entity> entities1 = new ArrayList<Entity>();
		List<Entity> entities2 = new ArrayList<Entity>();
		
		entities1.add(newYork);
		entities2.add(yorkCity);
		EntityResolver resolver = new EntityResolver();
		
		List<Entity> resolvedEntities = resolver.resolveEntities(entities1, entities2);
						
		assertTrue(resolvedEntities.size() == 1);
		assertTrue(resolvedEntities.get(0).getText().equals("New York City"));
					
	}
	
	public void testCollideDifferentEntities() {
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
		token2.setStartPosition(5);
		token2.setEndPosition(9);
		token2.setText("York");
		token2.setNer("LOCATION");
		token2.setPos("NNP");
		
		Token token3 = new Token();
		token3.setSentId(1);
		token3.setRank(3);
		token3.setStartPosition(11);
		token3.setEndPosition(15);
		token3.setText("City");
		token3.setNer("LOCATION");
		token3.setPos("NNP");
		
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(token1);
		tokens.add(token2);
		
		
		Entity newYork = new Person(tokens);
		
		LinkedList<Token> tokens2 = new LinkedList<Token>();
		tokens2.add(token2);
		tokens2.add(token3);
		
		
		Entity yorkCity = new Location(tokens2);
		
		List<Entity> entities1 = new ArrayList<Entity>();
		List<Entity> entities2 = new ArrayList<Entity>();
		
		entities1.add(newYork);
		entities2.add(yorkCity);
		EntityResolver resolver = new EntityResolver();
		
		List<Entity> resolvedEntities = resolver.resolveEntities(entities1, entities2);
						
		assertTrue(resolvedEntities.size() == 1);
		assertTrue(resolvedEntities.get(0).getText().equals("New York City"));
		assertTrue(resolvedEntities.get(0) instanceof Misc);
					
	}
	
	public void testIncludeTimeEntities() {
		Token token1 = new Token();
		token1.setSentId(1);
		token1.setRank(1);
		token1.setStartPosition(0);
		token1.setEndPosition(1);
		token1.setText("le");
		token1.setNer("");
		token1.setPos("");
		
		Token token2 = new Token();
		token2.setSentId(1);
		token2.setRank(2);
		token2.setStartPosition(3);
		token2.setEndPosition(12);
		token2.setText("12/05/2018");
		token2.setNer("Date");
		token2.setPos("NNP");
		
		Token token3 = new Token();
		token3.setSentId(1);
		token3.setRank(3);
		token3.setStartPosition(14);
		token3.setEndPosition(15);
		token3.setText("à");
		token3.setNer("o");
		token3.setPos("Sym");
		
		Token token4 = new Token();
		token4.setSentId(1);
		token4.setRank(4);
		token4.setStartPosition(16);
		token4.setEndPosition(20);
		token4.setText("11:30");
		token4.setNer("Date");
		token4.setPos("Sym");
		
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(token1);
		tokens.add(token2);
		tokens.add(token3);
		tokens.add(token4);
		
		
		Entity dateTime = new Time(tokens,null);
		
		LinkedList<Token> tokens2 = new LinkedList<Token>();
		tokens2.add(token4);
		
		
		Entity time = new Time(tokens2, null);
		
		List<Entity> entities1 = new ArrayList<Entity>();
		List<Entity> entities2 = new ArrayList<Entity>();
		
		entities1.add(dateTime);
		entities2.add(time);
		EntityResolver resolver = new EntityResolver();
		
		List<Entity> resolvedEntities = resolver.resolveEntities(entities1, entities2);
		System.out.println("Time resolution "+resolvedEntities.get(0).getText());
						
		assertTrue(resolvedEntities.size() == 1);
		assertTrue(resolvedEntities.get(0).getText().equals("le 12/05/2018 à 11:30"));
		assertTrue(resolvedEntities.get(0) instanceof Time);
					
	}
	
	public void testCollideTimeEntities() {
		Token token1 = new Token();
		token1.setSentId(1);
		token1.setRank(1);
		token1.setStartPosition(0);
		token1.setEndPosition(2);
		token1.setText("le");
		token1.setNer("");
		token1.setPos("");
		
		Token token2 = new Token();
		token2.setSentId(1);
		token2.setRank(2);
		token2.setStartPosition(3);
		token2.setEndPosition(12);
		token2.setText("12/05/2018");
		token2.setNer("Date");
		token2.setPos("NNP");
		
		Token token3 = new Token();
		token3.setSentId(1);
		token3.setRank(3);
		token3.setStartPosition(14);
		token3.setEndPosition(15);
		token3.setText("à");
		token3.setNer("o");
		token3.setPos("Sym");
		
		Token token4 = new Token();
		token4.setSentId(1);
		token4.setRank(4);
		token4.setStartPosition(16);
		token4.setEndPosition(20);
		token4.setText("11:30");
		token4.setNer("Date");
		token4.setPos("Sym");
		
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(token2);
		tokens.add(token3);
		tokens.add(token4);
		
		
		Entity dateTime = new Time(tokens,null);
		
		LinkedList<Token> tokens2 = new LinkedList<Token>();
		tokens2.add(token1);
		tokens2.add(token2);
		
		
		Entity time = new Time(tokens2, null);
		
		List<Entity> entities1 = new ArrayList<Entity>();
		List<Entity> entities2 = new ArrayList<Entity>();
		
		entities1.add(dateTime);
		entities2.add(time);
		EntityResolver resolver = new EntityResolver();
		
		List<Entity> resolvedEntities = resolver.resolveEntities(entities1, entities2);
		System.out.println("Time resolution "+resolvedEntities.get(0).getText());
						
		assertTrue(resolvedEntities.size() == 1);
		assertTrue(resolvedEntities.get(0).getText().equals("le 12/05/2018 à 11:30"));
		assertTrue(resolvedEntities.get(0) instanceof Time);
					
	}


}
