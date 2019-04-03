package org.linagora.intentDetection.common;

import org.joda.time.DateTime;

import junit.framework.TestCase;

public class DateTimeUtilTest extends TestCase{
	
	public void testParseDateFromString1() {
		String dateStr = "12/05/2010";
		DateTime date = DateTimeUtil.parseDate(dateStr);
		
		assertTrue(date != null);
		assertTrue(date.getDayOfMonth() == 12 && date.getMonthOfYear() == 5 && date.getYear() == 2010);
	}
	
	public void testParseDateFromString2() {
		String dateStr = "2010";
		DateTime date = DateTimeUtil.parseDate(dateStr);
		
		assertTrue(date != null);
		assertTrue(date.getDayOfMonth() == 1 && date.getMonthOfYear() == 1 && date.getYear() == 2010);
	}
	
	public void testParseDateFromString3() {
		String dateStr = "06/2010";
		DateTime date = DateTimeUtil.parseDate(dateStr);
		
		assertTrue(date != null);
		assertTrue(date.getDayOfMonth() == 1 && date.getMonthOfYear() == 6 && date.getYear() == 2010);
	}
	
	public void testParseDateFromString4() {
		String dateStr = "2010-06";
		DateTime date = DateTimeUtil.parseDate(dateStr);
		//System.out.println(date);
		assertTrue(date != null);
		assertTrue(date.getDayOfMonth() == 1 && date.getMonthOfYear() == 6 && date.getYear() == 2010);
	}
	
	public void testParseDateFromString5() {
		String dateStr = "2010-06-13";
		DateTime date = DateTimeUtil.parseDate(dateStr);
		//System.out.println(date);
		assertTrue(date != null);
		assertTrue(date.getDayOfMonth() == 13 && date.getMonthOfYear() == 6 && date.getYear() == 2010);
	}
	
	public void testParseTimeFromString() {
		String time = "10:30";
		DateTime date = DateTimeUtil.parseDate("2018-05-03T"+time);
		assertTrue(date != null);
		assertTrue(date.getDayOfMonth() == 3 && date.getMonthOfYear() == 5 && date.getYear() == 2018);
		assertTrue(date.getHourOfDay() == 10 && date.getMinuteOfHour() == 30);
	}

}
