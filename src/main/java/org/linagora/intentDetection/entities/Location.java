package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class Location extends Entity {
	
	public Location() {
		super();
	}
	
	public Location(LinkedList<Token> tokens) {
		super(tokens);
	}

	@Override
	public Object getValue() {
		return getText();
	}

	@Override
	public void printValue() {
		System.out.println("Location " + this.toString());
		
	}


}
