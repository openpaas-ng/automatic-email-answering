package org.linagora.intentDetection.duckling;


import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.linagora.intentDetection.Parameters;
import org.linagora.intentDetection.entities.Email;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.entities.IntervalTime;
import org.linagora.intentDetection.entities.IntervalTimeType;
import org.linagora.intentDetection.entities.PhoneNumber;
import org.linagora.intentDetection.entities.Time;
import org.linagora.intentDetection.entities.Url;

import junit.framework.TestCase;


public class DucklingWrapperTest extends TestCase{
	protected DucklingLocale locale = DucklingLocale.fr_FR;
	protected DucklingWrapper wrapper = new DucklingWrapper(Parameters.DUCKLING_URL);
		
	public void testUnrecognizeEntities(){
		String jsonAsString = "[{\"body\":\"10 métres\",\"start\":18,\"value\":{\"value\":10,\"type\":\"value\",\"unit\":\"metre\"},\"end\":27,\"dim\":\"distance\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 0);
	}
	
	public void testRecognizePhoneNumber(){
		String jsonAsString = "[{\"body\":\"06.45.13.54.34\",\"start\":24,\"value\":{\"value\":\"0645135434\"},\"end\":38,\"dim\":\"phone-number\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
		assertTrue(entities.get(0) instanceof PhoneNumber);
	}
	
	public void testRecognizeTime(){
		String jsonAsString = "[{\"body\":\"à 15h00 aujourd'hui\",\"start\":24,\"value\":{\"values\":[{\"value\":\"2018-03-16T15:00:00.000-07:00\",\"grain\":\"minute\",\"type\":\"value\"}],\"value\":\"2018-03-16T15:00:00.000-07:00\",\"grain\":\"minute\",\"type\":\"value\"},\"end\":43,\"dim\":\"time\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
		assertTrue(entities.get(0) instanceof Time);
	}
	
	public void testRecognizeIntervalTime(){
		String jsonAsString = "[{\"body\":\"entre 10h et 12h\",\"start\":18,\"value\":{\"values\":[{\"to\":{\"value\":\"2018-03-16T13:00:00.000-07:00\",\"grain\":\"hour\"},\"from\":{\"value\":\"2018-03-15T22:00:00.000-07:00\",\"grain\":\"hour\"},\"type\":\"interval\"},{\"to\":{\"value\":\"2018-03-16T13:00:00.000-07:00\",\"grain\":\"hour\"},\"from\":{\"value\":\"2018-03-16T10:00:00.000-07:00\",\"grain\":\"hour\"},\"type\":\"interval\"},{\"to\":{\"value\":\"2018-03-17T13:00:00.000-07:00\",\"grain\":\"hour\"},\"from\":{\"value\":\"2018-03-16T22:00:00.000-07:00\",\"grain\":\"hour\"},\"type\":\"interval\"}],\"to\":{\"value\":\"2018-03-16T13:00:00.000-07:00\",\"grain\":\"hour\"},\"from\":{\"value\":\"2018-03-15T22:00:00.000-07:00\",\"grain\":\"hour\"},\"type\":\"interval\"},\"end\":34,\"dim\":\"time\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
		assertTrue(entities.get(0) instanceof IntervalTime);
	}
	
	public void testRecognizeEmail(){
		String jsonAsString = "[{\"body\":\"societe@test.org\",\"start\":14,\"value\":{\"value\":\"societe@test.org\"},\"end\":30,\"dim\":\"email\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
		assertTrue(entities.get(0) instanceof Email);
	}
	
	public void testRecognizeUrl(){
		String jsonAsString = "[{\"body\":\"doodle.org/planification.php\",\"start\":28,\"value\":{\"domain\":\"doodle.org\",\"value\":\"doodle.org/planification.php\"},\"end\":56,\"dim\":\"url\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
		assertTrue(entities.get(0) instanceof Url);
	}
	
	public void testRecognizeUrlAndEmail(){
		String jsonAsString = "[{\"body\":\"doodle.org/planification.php.\",\"start\":28,\"value\":{\"domain\":\"doodle.org\",\"value\":\"doodle.org/planification.php.\"},\"end\":57,\"dim\":\"url\"},{\"body\":\"societe@test.org\",\"start\":74,\"value\":{\"value\":\"societe@test.org\"},\"end\":90,\"dim\":\"email\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 2);
		assertTrue(entities.get(0) instanceof Url);
		assertTrue(entities.get(1) instanceof Email);
		
	}
	
	public void testFormalizeTimeValue(){
		String jsonAsString = "[{\"body\":\"le 10 septembre 2018 à 13h00\",\"start\":21,\"value\":{\"values\":[{\"value\":\"2018-09-10T13:00:00.000-07:00\",\"grain\":\"minute\",\"type\":\"value\"}],\"value\":\"2018-09-10T13:00:00.000-07:00\",\"grain\":\"minute\",\"type\":\"value\"},\"end\":49,\"dim\":\"time\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
	
		assertTrue(entities.get(0) instanceof Time);
		assertTrue(entities.get(0).getValue() instanceof DateTime);
		DateTime time = (DateTime)entities.get(0).getValue();
		assertTrue(time.getYear() == 2018);
		assertTrue(time.getMonthOfYear() == 9);
		assertTrue(time.getDayOfMonth() == 10);
		assertTrue(time.getHourOfDay() == 13);
		assertTrue(time.getMinuteOfHour() == 0);
		
	}
	
	public void testFormalizeIntervalTimeValue(){
		String jsonAsString = "[{\"body\":\"le 10 septembre 2018 de 10h00 à 12h00\",\"start\":21,\"value\":{\"values\":[{\"to\":{\"value\":\"2018-09-10T12:01:00.000-07:00\",\"grain\":\"minute\"},\"from\":{\"value\":\"2018-09-10T10:00:00.000-07:00\",\"grain\":\"minute\"},\"type\":\"interval\"}],\"to\":{\"value\":\"2018-09-10T12:01:00.000-07:00\",\"grain\":\"minute\"},\"from\":{\"value\":\"2018-09-10T10:00:00.000-07:00\",\"grain\":\"minute\"},\"type\":\"interval\"},\"end\":58,\"dim\":\"time\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
	
		assertTrue(entities.get(0) instanceof IntervalTime);
		assertTrue(entities.get(0).getValue() instanceof Pair<?, ?>);
		Pair<DateTime, DateTime> intervalTime = (Pair<DateTime, DateTime>)entities.get(0).getValue();
		
		assertTrue(intervalTime.getLeft().equals(DateTime.parse("2018-09-10T10:00:00.000-07:00")));
		assertTrue(intervalTime.getRight().equals(DateTime.parse("2018-09-10T12:01:00.000-07:00")));
		assertTrue(((IntervalTime)entities.get(0)).getIntervalType() == IntervalTimeType.closed);
		
	}
	
	public void testFormalizeIntervalTimeValueFrom(){
		String jsonAsString = "[{\"body\":\"le 10 septembre 2018 à partir de 10h00\",\"start\":21,\"value\":{\"values\":[{\"from\":{\"value\":\"2018-09-10T10:00:00.000-07:00\",\"grain\":\"minute\"},\"type\":\"interval\"},{\"from\":{\"value\":\"2018-09-10T22:00:00.000-07:00\",\"grain\":\"minute\"},\"type\":\"interval\"}],\"from\":{\"value\":\"2018-09-10T10:00:00.000-07:00\",\"grain\":\"minute\"},\"type\":\"interval\"},\"end\":59,\"dim\":\"time\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
	
		assertTrue(entities.get(0) instanceof IntervalTime);
		assertTrue(entities.get(0).getValue() instanceof Pair<?, ?>);
		Pair<DateTime, DateTime> intervalTime = (Pair<DateTime, DateTime>)entities.get(0).getValue();
		
		assertTrue(intervalTime.getLeft().equals(DateTime.parse("2018-09-10T10:00:00.000-07:00")));
		assertNull(intervalTime.getRight());
		assertTrue(((IntervalTime)entities.get(0)).getIntervalType() == IntervalTimeType.withoutEnd);
		
	}
	
	public void testFormalizeFrenchIntervalTime(){
		String jsonAsString = "[{\"body\":\"le 10 mars 2018 de 9h30 à 11h30\",\"start\":22,\"value\":{\"values\":[{\"to\":{\"value\":\"2018-03-10T11:31:00.000-08:00\",\"grain\":\"minute\"},\"from\":{\"value\":\"2018-03-10T09:30:00.000-08:00\",\"grain\":\"minute\"},\"type\":\"interval\"},{\"to\":{\"value\":\"2018-03-10T23:31:00.000-08:00\",\"grain\":\"minute\"},\"from\":{\"value\":\"2018-03-10T21:30:00.000-08:00\",\"grain\":\"minute\"},\"type\":\"interval\"}],\"to\":{\"value\":\"2018-03-10T11:31:00.000-08:00\",\"grain\":\"minute\"},\"from\":{\"value\":\"2018-03-10T09:30:00.000-08:00\",\"grain\":\"minute\"},\"type\":\"interval\"},\"end\":53,\"dim\":\"time\"}]";
		List<Entity> entities = wrapper.jsonResultToEntities(jsonAsString, locale);
		assertTrue(entities.size() == 1);
	
		assertTrue(entities.get(0) instanceof IntervalTime);
		assertTrue(entities.get(0).getValue() instanceof Pair<?, ?>);
		Pair<DateTime, DateTime> intervalTime = (Pair<DateTime, DateTime>)entities.get(0).getValue();
		
		assertTrue(intervalTime.getLeft().equals(DateTime.parse("2018-03-10T09:30:00.000-08:00")));
		assertTrue(intervalTime.getRight().equals(DateTime.parse("2018-03-10T11:31:00.000-08:00")));
		assertTrue(((IntervalTime)entities.get(0)).getIntervalType() == IntervalTimeType.closed);
		
	}
	
	

}
