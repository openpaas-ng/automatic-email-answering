package org.linagora.intentDetection.semantic;

import java.util.ArrayList;
import java.util.List;

import org.linagora.intentDetection.corenlp.Token;

public class Instance {
	
	private List<Token> tokens = null;
	private InstanceOrigin instanceOrigin = null;
	
	public Instance() {
		tokens = new ArrayList<Token>();
		
	}
	public Instance(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	public Instance(List<Token> tokens, InstanceOrigin instanceOrigin) {
		super();
		this.tokens = tokens;
		this.instanceOrigin = instanceOrigin;
	}
	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	
	public InstanceOrigin getInstanceOrigin() {
		return instanceOrigin;
	}
	public void setInstanceOrigin(InstanceOrigin instanceOrigin) {
		this.instanceOrigin = instanceOrigin;
	}
	public String getInstanceText() {
		String text = "";
		for(Token token: tokens) {
			text = text + token.getText() + " ";
		}
		text = text.trim();
		return text;
	}
	@Override
	public String toString() {
		String text = "";
		for(Token token: tokens) {
			text = text + token.getText() + " ";
		}
		text = text.trim();
		return "Instance [" + text + ", " + instanceOrigin + "]";
	}
	
	
	

}
