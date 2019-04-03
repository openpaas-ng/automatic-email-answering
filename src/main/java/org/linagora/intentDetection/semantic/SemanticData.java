package org.linagora.intentDetection.semantic;

import java.util.List;

import org.linagora.intentDetection.corenlp.Relation;
import org.linagora.intentDetection.corenlp.Token;

public class SemanticData {

	private List<Token> tokens = null;
	private List<Relation> relations = null;

	public SemanticData() {

	}

	public SemanticData(List<Token> tokens, List<Relation> relations) {
		this.tokens = tokens;
		this.relations = relations;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

}
