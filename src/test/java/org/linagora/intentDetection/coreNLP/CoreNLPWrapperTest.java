package org.linagora.intentDetection.coreNLP;


import java.util.List;

import org.linagora.intentDetection.corenlp.CoreNLPWrapper;
import org.linagora.intentDetection.corenlp.Direction;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.corenlp.NLPData;
import org.linagora.intentDetection.corenlp.Relation;
import org.linagora.intentDetection.entities.Duration;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.entities.Location;
import org.linagora.intentDetection.entities.Person;
import org.linagora.intentDetection.entities.TimeUnit;

import junit.framework.TestCase;


public class CoreNLPWrapperTest extends TestCase{
	
	protected CoreNLPWrapper wrapper = new CoreNLPWrapper(Language.french);
	protected String frenchText = "je voyage à Paris avec Patrick .";
	protected String multiwordsEntities = "je voyage à New York demain .";
	protected String frenchTextWithTime = "j'arrive au travail à 10:00 le 01/01/2019.";
	protected String frenchTextWithDuration = "La réunion va durer 40 minutes";
	
	public void testParseEmptyText() {
		NLPData data = wrapper.parseText("");
		assertTrue(data instanceof NLPData);
		
	}
	
	public void testParseNullText() {
		NLPData data = wrapper.parseText(null);
		assertTrue(data instanceof NLPData);
		
	}
	
	public void testSpaceText() {
		NLPData data = wrapper.parseText("   ");
		assertTrue(data instanceof NLPData);
		
	}
	
	public void testTokenExtraction() {
		NLPData data = wrapper.parseText(frenchText);
		assertTrue(data.getTokens().size() == 7);
		String [] words = frenchText.split(" ");
		boolean isSameToken = true;
		
		for(int i = 0 ; i < data.getTokens().size() ; i++) {
			isSameToken = isSameToken && (data.getTokens().get(i).getText().equals(words[i]));
		}
		
		assertTrue(isSameToken);
		
		
	}
	
	public void testEntityExtraction() {
		NLPData data = wrapper.parseText(frenchText);
		
		List<Entity> entities = data.getEntities();
				
		assertTrue(entities.size() == 2);
		
		assertTrue(entities.get(0) instanceof Location);
		
		assertTrue(entities.get(1) instanceof Person);
						
		assertTrue(entities.get(0).getText().equals("Paris"));
		
		assertTrue(entities.get(1).getText().equals("Patrick"));		
				
	}
	
//	public void testTimeExtraction() {
//		NLPData data = wrapper.parseText(frenchTextWithTime);
//		
//		List<Entity> entities = data.getEntities();
//				
//		assertTrue(entities.size() == 2);
//		
//		assertTrue(entities.get(0) instanceof Time);
//		
//		assertTrue(entities.get(1) instanceof Time);
//		
//		Time time = (Time)entities.get(0);
//		Time date = (Time)entities.get(1);
//						
//		assertTrue(time.getText().equals("10:00"));
//		
//		assertTrue(time.getTime().getHourOfDay() == 10 && time.getTime().getMinuteOfHour() == 0);
//		
//		assertTrue(date.getText().equals("01/01/2019"));
//		
//		assertTrue(date.getTime().getYear() == 2019 && date.getTime().getMonthOfYear() == 1 && date.getTime().getDayOfMonth() == 1);
//				
//	}
	
	public void testDurationExtraction() {
		NLPData data = wrapper.parseText(frenchTextWithDuration);
		
		List<Entity> entities = data.getEntities();
				
		assertTrue(entities.size() == 1);
		
		assertTrue(entities.get(0) instanceof Duration);
		
		Duration duration = (Duration)entities.get(0);
								
		assertTrue(duration.getText().equals("40 minutes"));
		
		assertTrue(duration.getUnit().equals(TimeUnit.minute));
						
	}
	
	public void testMultiWordsEntityExtraction() {
		NLPData data = wrapper.parseText(multiwordsEntities);
		
		List<Entity> entities = data.getEntities();
				
		assertTrue(entities.size() == 1);
		
		Entity newYork = entities.get(0);
		
		assertTrue(newYork instanceof Location);
		
		assertTrue(newYork.getText().equals("New York"));
		
		assertTrue(newYork.getTokens().size() == 2);
		
		assertTrue(newYork.getTokens().getFirst().getText().equals("New"));
		
		assertTrue(newYork.getTokens().getLast().getText().equals("York"));
				
	}
	
	public void testRelationExtraction() {
		NLPData data = wrapper.parseText(frenchText);
		
		List<Relation> relations = data.getRelations();
				
		boolean existNsubj = false;
		boolean existNmod = false;
		for(Relation rel: relations) {
			
			if(rel.getName().equals("nsubj") 
					&& rel.getGovernor().getText().equals("voyage")
					&& rel.getDependent().getText().equals("je")
					&& rel.getDirection().equals(Direction.left)) {
				existNsubj = true;
				
			}
			if(rel.getName().equals("nmod") 
					&& rel.getGovernor().getText().equals("voyage")
					&& rel.getDependent().getText().equals("Paris")
					&& rel.getDirection().equals(Direction.right)) {
				existNmod = true;
				
			}
		}
		
		assertTrue(relations.size() == 6);
		assertTrue(existNsubj);
		assertTrue(existNmod);
	}

}
