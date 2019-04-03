package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.linagora.intentDetection.corenlp.Token;

public class IntervalTime extends Entity {
	
	private DateTime startTime = null;
	private DateTime endTime = null;
	private TimeUnit unit = null;
	
	public IntervalTime() {
		super();
	}
	
	public IntervalTime(DateTime startTime, DateTime endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public IntervalTime(DateTime startTime, DateTime endTime, TimeUnit unit) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.unit = unit;
	}
	
	public IntervalTime(LinkedList<Token> tokens, DateTime startTime, DateTime endTime) {
		super(tokens);
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}
	
	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
	
	public IntervalTimeType getIntervalType() {
		if (startTime != null && endTime != null)
			return IntervalTimeType.closed;
		
		if(startTime == null && endTime != null)
			return IntervalTimeType.withoutStart;
		
		if(startTime != null && endTime == null)
			return IntervalTimeType.withoutEnd;
		
		return null;
		
	}

	@Override
	public Object getValue() {
		Pair<DateTime, DateTime> pair = Pair.of(startTime, endTime);
		return pair;
	}

	@Override
	public void printValue() {
		System.out.println("IntervalTime " + this.toString() + " " + unit);
		
	}

}
