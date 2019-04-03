package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class Url extends Entity {
	
	public Url() {
		super();
	}
	
	public Url(LinkedList<Token> tokens) {
		super(tokens);
	}

	@Override
	public Object getValue() {
		return getText();
	}

	@Override
	public void printValue() {
		System.out.println("URL " + this.toString());
		
	}

}
