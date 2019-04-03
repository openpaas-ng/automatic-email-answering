package org.linagora.intentDetection.corenlp;

import java.util.List;

import org.linagora.intentDetection.entities.Entity;

public class NLPData {
	
	private List<Token> tokens = null;
	private List<Entity> entities = null;
	private List<Relation> relations = null;
	
	public NLPData() {
		
	}
	
	public NLPData(List<Token> tokens, List<Entity> entities, List<Relation> relations) {
		this.tokens = tokens;
		this.entities = entities;
		this.relations = relations;
	}
	
	public List<Token> getTokens() {
		return tokens;
	}
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	public List<Relation> getRelations() {
		return relations;
	}
	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}
	
	

}
