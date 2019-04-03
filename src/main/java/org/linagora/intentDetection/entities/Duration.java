package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class Duration extends Entity {
	
	private TimeUnit unit = null;
	
	public Duration() {
		super();
	}
	
	public Duration(TimeUnit unit) {
		super();
		this.unit = unit;
	}
	
	public Duration(LinkedList<Token> tokens, TimeUnit unit) {
		super(tokens);
		this.unit = unit;
	}
	
	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
	
	@Override
	public Object getValue() {
		return Integer.parseInt(this.getTokens().getFirst().getText());
	}
	
	@Override
	public void printValue() {
		System.out.println("Duration: " + this.toString() + " " + unit);
		
	}

}
