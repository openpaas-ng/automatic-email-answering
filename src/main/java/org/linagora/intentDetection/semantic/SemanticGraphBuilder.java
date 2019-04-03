package org.linagora.intentDetection.semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.linagora.intentDetection.corenlp.NLPData;
import org.linagora.intentDetection.corenlp.Relation;
import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.entities.Entity;

public class SemanticGraphBuilder {
	
	public SemanticGraphBuilder() {
		
	}
	
	public SemanticData convertToSemanticGraph(List<Token> tokens, List<Entity> entities, List<Relation> relations){
		List<Token> newTokens = new ArrayList<Token>();
		HashMap<Token, Token> tokensInEntity = new HashMap<Token, Token>();
		List<Relation> relsToRemove = new ArrayList<Relation>();
		List<Relation> relsToAdd = new ArrayList<Relation>();
		
		for(Entity entity: entities) {
			Token tokenFromEntity = entity.convertToToken();
			newTokens.add(tokenFromEntity);
			for(Token token: entity.getTokens()) {
				tokensInEntity.put(token, tokenFromEntity);
			}
		}
		
		for (Relation relation: relations) {
			Token entitySource = tokensInEntity.get(relation.getGovernor());
			Token entityTarget = tokensInEntity.get(relation.getDependent());
			
			if (entitySource != null && entityTarget != null && entitySource == entityTarget) {
				relsToRemove.add(relation);
			}else if (entitySource != null && entityTarget != null && entitySource != entityTarget) {
				Relation newRelation = new Relation();
				newRelation.setGovernor(entitySource);
				newRelation.setDependent(entityTarget);
				newRelation.setDirection(relation.getDirection());
				newRelation.setName(entitySource.getNer()+"_"+relation.getName());
				relsToAdd.add(newRelation);
				relsToRemove.add(relation);
			}else if(entitySource != null) {
				Relation newRelation = new Relation();
				newRelation.setGovernor(entitySource);
				newRelation.setDependent(relation.getDependent());
				newRelation.setDirection(relation.getDirection());
				newRelation.setName(entitySource.getNer()+"_"+relation.getName());
				relsToAdd.add(newRelation);
				relsToRemove.add(relation);
			}else if (entityTarget != null) {
				Relation newRelation = new Relation();
				newRelation.setGovernor(relation.getGovernor());
				newRelation.setDependent(entityTarget);
				newRelation.setDirection(relation.getDirection());
				newRelation.setName(entityTarget.getNer()+"_"+relation.getName());
				relsToAdd.add(newRelation);
				relsToRemove.add(relation);
			}
			
		}
		
		tokens.removeAll(tokensInEntity.keySet());
		tokens.addAll(newTokens);
		relations.removeAll(relsToRemove);
		relations.addAll(relsToAdd);
		relations =  new ArrayList(new HashSet<Relation>(relations));
		Collections.sort(tokens);
		
		for(int i = 0 ; i < tokens.size() ; i++) {
			tokens.get(i).setRank(i);
			
		}
		
		
		
		return new SemanticData(tokens, relations);
	}
		
}
