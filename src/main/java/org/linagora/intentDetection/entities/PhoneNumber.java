package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class PhoneNumber extends Entity {
	
	public PhoneNumber() {
		super();
	}
	
	public PhoneNumber(LinkedList<Token> tokens) {
		super(tokens);
	}
	
	@Override
	public Object getValue() {
		return getText();
	}

	@Override
	public void printValue() {
		System.out.println("PhoneNumber " + this.toString());
		
	}
	
	

}
