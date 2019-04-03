package org.linagora.intentDetection.semantic;

import java.util.List;

import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.semantic.ontology.LexicalUnit;

public class LexicalUnitInstance extends Instance{
	
	private LexicalUnit lexicalUnit = null;
	
	public LexicalUnitInstance() {
		super();
	}
	
	public LexicalUnitInstance(LexicalUnit lexicalUnit, List<Token> tokens) {
		super(tokens);
		this.lexicalUnit = lexicalUnit;
	}
	
	public LexicalUnitInstance(LexicalUnit lexicalUnit, List<Token> tokens, InstanceOrigin instanceOrigin) {
		super(tokens, instanceOrigin);
		this.lexicalUnit = lexicalUnit;
	}

	public LexicalUnit getLexicalUnit() {
		return lexicalUnit;
	}

	public void setLexicalUnit(LexicalUnit lexicalUnit) {
		this.lexicalUnit = lexicalUnit;
	}

	@Override
	public String toString() {
		return "LexicalUnitInstance [lexicalUnit=" + lexicalUnit + "," + super.toString()+"]";
	}
	
	
	
	

}
