package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.joda.time.DateTime;
import org.linagora.intentDetection.corenlp.Token;

public class Time extends Entity {
	
	private DateTime time = null;
	private TimeUnit unit = null;
	
	public Time() {
		super();
	}
	
	public Time(DateTime time) {
		super();
		this.time = time;
	}
	
	public Time(DateTime time, TimeUnit unit) {
		super();
		this.time = time;
		this.unit = unit;
	}
	
	public Time(LinkedList<Token> tokens, DateTime time) {
		super(tokens);
		this.time = time;
	}
	
	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}
	
	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}

	@Override
	public Object getValue() {
		return time;
	}

	@Override
	public void printValue() {
		System.out.println("Time " + this.toString() + " " + unit);
		
	}


}
