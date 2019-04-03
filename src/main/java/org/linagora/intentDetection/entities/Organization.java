package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class Organization extends Entity {
	
	public Organization() {
		super();
	}
	
	public Organization(LinkedList<Token> tokens) {
		super(tokens);
	}

	@Override
	public Object getValue() {
		return getText();
	}

	@Override
	public void printValue() {
		System.out.println("Organization " + this.toString());
		
	}

}
