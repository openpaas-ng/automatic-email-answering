package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class Misc extends Entity {
	
	public Misc() {
		super();
	}
	
	public Misc(LinkedList<Token> tokens) {
		super(tokens);
	}

	@Override
	public Object getValue() {
		return getText();
	}

	@Override
	public void printValue() {
		System.out.println("Misc " + this.toString());
		
	}

}
