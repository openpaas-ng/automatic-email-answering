package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class Person extends Entity {
	
	public Person() {
		super();
	}
	
	public Person(LinkedList<Token> tokens) {
		super(tokens);
	}

	@Override
	public Object getValue() {
		return getText();
	}

	@Override
	public void printValue() {
		System.out.println("Person " + this.toString());
		
	}

}
