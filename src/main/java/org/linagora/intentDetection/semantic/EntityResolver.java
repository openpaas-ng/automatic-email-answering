package org.linagora.intentDetection.semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.linagora.intentDetection.corenlp.NLPData;
import org.linagora.intentDetection.Parameters;
import org.linagora.intentDetection.corenlp.CoreNLPWrapper;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.duckling.DucklingLocale;
import org.linagora.intentDetection.duckling.DucklingWrapper;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.entities.IntervalTime;
import org.linagora.intentDetection.entities.Misc;
import org.linagora.intentDetection.entities.Time;

public class EntityResolver {
	
	private boolean entity1IsInEntity2(Entity entity1, Entity entity2) {
		return (entity1.getStartPosition() > entity2.getStartPosition() && entity1.getEndPosition() <= entity2.getEndPosition())
				|| (entity1.getStartPosition() >= entity2.getStartPosition() && entity1.getEndPosition() < entity2.getEndPosition());
	}
	
	private boolean entity1IsCollideEntity2(Entity entity1, Entity entity2) {
		return (entity1.getStartPosition() < entity2.getStartPosition() && entity1.getEndPosition() > entity2.getStartPosition()
				&& entity1.getEndPosition() < entity2.getEndPosition());
				
	}
	
	private Entity mergeSameEntityType(Entity entity1, Entity entity2) {
		try {
			String className = entity1.getClass().getName();
			Entity newEntity = (Entity)Class.forName(className).getConstructor().newInstance();
			
			//Merge the list of entities tokens, delete commons elements and sort the order
			Set<Token> mergedTokens = new HashSet<Token>();
			mergedTokens.addAll(entity1.getTokens());
			mergedTokens.addAll(entity2.getTokens());
			List<Token> newTokens = new ArrayList<Token>(mergedTokens);
			Collections.sort(newTokens);
			
			newEntity.setTokens(new LinkedList<Token>(newTokens));
			
			if(newEntity instanceof Time) {
				if(((Time)entity1).getTime() != null) {
					((Time) newEntity).setTime(((Time)entity1).getTime());
				}else {
					((Time) newEntity).setTime(((Time)entity2).getTime());
				}
				
				if(((Time)entity1).getUnit() != null) {
					((Time) newEntity).setUnit(((Time)entity1).getUnit());
				}else {
					((Time) newEntity).setUnit(((Time)entity2).getUnit());
				}
				
			}
			
			if(newEntity instanceof IntervalTime) {
				if(((IntervalTime)entity1).getStartTime() != null) {
					((IntervalTime) newEntity).setStartTime(((IntervalTime)entity1).getStartTime());
				}else {
					((IntervalTime) newEntity).setStartTime(((IntervalTime)entity2).getStartTime());
				}
				
				if(((IntervalTime)entity1).getEndTime() != null) {
					((IntervalTime) newEntity).setEndTime(((IntervalTime)entity1).getEndTime());
				}else {
					((IntervalTime) newEntity).setEndTime(((IntervalTime)entity2).getEndTime());
				}
				
				if(((IntervalTime)entity1).getUnit() != null) {
					((IntervalTime) newEntity).setUnit(((IntervalTime)entity1).getUnit());
				}else {
					((IntervalTime) newEntity).setUnit(((IntervalTime)entity2).getUnit());
				}
			}
			
			return newEntity;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Entity mergeDifferentEntityType(Entity entity1, Entity entity2) {
		try {
			String className = "org.linagora.intentDetection.entities.Misc";
			Entity newEntity = (Entity)Class.forName(className).getConstructor().newInstance();
			
			//Merge the list of entities tokens, delete commons elements and sort the order
			Set<Token> mergedTokens = new HashSet<Token>();
			mergedTokens.addAll(entity1.getTokens());
			mergedTokens.addAll(entity2.getTokens());
			List<Token> newTokens = new ArrayList<Token>(mergedTokens);
			Collections.sort(newTokens);
			
			newEntity.setTokens(new LinkedList<Token>(newTokens));
			
			return newEntity;
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/***
	 * Delete duckling entities error
	 * For instance 4 heures is Duration and Time value. Delete the time instance
	 * @param entities
	 * @return
	 */
	private List<Entity> resolveEntitiesConflict(List<Entity> entities){
		List<Entity> entitiesToRemove = new ArrayList<Entity>();
		List<Entity> entitiesToAdd = new ArrayList<Entity>();
		for(int i = 0; i < entities.size(); i++) {
			Entity e1 = entities.get(i);
			String className1 = e1.getClass().getSimpleName();
			for(int j = i+1; j < entities.size(); j++) {
				Entity e2 = entities.get(j);
				String className2 = e2.getClass().getSimpleName();
				if(e1.equals(e2) && e1 != e2) {
					if(className1.equals("Time") && className2.equals("Duration")) {
						entitiesToRemove.add(e1);
						entitiesToAdd.add(e2);
					}else if (className1.equals("Duration") && className2.equals("Time")) {
						entitiesToRemove.add(e2);
						entitiesToAdd.add(e1);
					}
				}
				
			}
			
		}
		entities.removeAll(entitiesToRemove);
		entities.addAll(entitiesToAdd);
		return entities;
	}
	
	public List<Entity> resolveEntities(List<Entity> entities1, List<Entity> entities2){
		entities1 = resolveEntitiesConflict(entities1);
		entities2 = resolveEntitiesConflict(entities2);
		List<Entity> resolvedEntitiesList = new ArrayList<Entity>();
		List<Entity> entitiesToRemove = new ArrayList<Entity>();
		List<Entity> commonEntities = new ArrayList<Entity>();
		List<Entity> newEntities = new ArrayList<Entity>();
		for(Entity entity1: entities1) {
			
			for(Entity entity2: entities2) {
				if(entity1.equals(entity2)) {
					//System.err.println("E1 and E2 are the same");
					// is the same entity
					// For instance 10:30 and 10:30
					if(entity1 instanceof Misc) {
						entitiesToRemove.add(entity1);
					}else {
						entitiesToRemove.add(entity2);
					}
					commonEntities.add(entity1);
				} else if (entity1IsInEntity2(entity1, entity2)) {
					//System.err.println("E1 include in E2");
					// is entity1 is included in entity2 but is not the same entity
					// for instance 10:30 and tomorrow à 10:30
					entitiesToRemove.add(entity1);					
				} else if (entity1IsInEntity2(entity2, entity1)) {
					//System.err.println("E2 include in E1");
					// is entity2 is included in entity1 but is not the same entity
					// for instance tomorrow à 10:30 and 10:30
					entitiesToRemove.add(entity2);					
				} else if (entity1IsCollideEntity2(entity1, entity2)) {
					//System.err.println("E1 collide in E2");
					// is entity1 collide with entity2
					// for instance Bruce Willis and Willis Man
					// Build a new Entity
					// Resolution strategy Same Type => Build an Entity with the same Type, Different type ==> Misc Entity
					if(entity1.getClass().getName().equals(entity2.getClass().getName())) {
						//System.err.println("Resolve collide same class");
						newEntities.add(mergeSameEntityType(entity1, entity2));						
					}else {
						//System.err.println("Resolve collide different class");
						newEntities.add(mergeDifferentEntityType(entity1, entity2));
					}
					entitiesToRemove.add(entity1);
					entitiesToRemove.add(entity2);
				}else if (entity1IsCollideEntity2(entity2, entity1)) {
					//System.err.println("E2 collide in E1");
					// is entity2 collide with entity1
					// for instance Bruce Willis and Isac Bruce
					// Build a new Entity
					// Resolution strategy Same Type => Build an Entity with the same Type, Different type ==> Misc Entity
					if(entity1.getClass().getName().equals(entity2.getClass().getName())) {
						//System.err.println("Resolve collide same class");
						newEntities.add(mergeSameEntityType(entity2, entity1));						
					}else {
						//System.err.println("Resolve collide different class");
						newEntities.add(mergeDifferentEntityType(entity2, entity1));
					}
					entitiesToRemove.add(entity1);
					entitiesToRemove.add(entity2);
				}
			}
		}
		
		resolvedEntitiesList.addAll(newEntities);
		
		entities1.removeAll(commonEntities);
		entities1.removeAll(entitiesToRemove);
		entities2.removeAll(commonEntities);
		entities2.removeAll(entitiesToRemove);
		
		resolvedEntitiesList.addAll(entities1);
		resolvedEntitiesList.addAll(entities2);
		resolvedEntitiesList.addAll(commonEntities);
		
		return resolvedEntitiesList;
	}
	
	public static void main(String [] args) {
		String text = "La réunion est prévu le 02/11/2018 entre 10:30 et 14:30. Elle durera 3 heures";
//		DucklingWrapper ducklingWrapper = new DucklingWrapper(Parameters.DUCKLING_URL);
//		ducklingWrapper.callDucklingService(text, DucklingLocale.fr_FR);
		CoreNLPWrapper coreNLPWrapper = new CoreNLPWrapper(Language.french);
		DucklingWrapper ducklingWrapper = new DucklingWrapper(Parameters.DUCKLING_URL);
		NLPData data = coreNLPWrapper.parseText(text);
		List<Entity> fromDuckling = ducklingWrapper.callDucklingService(text, DucklingLocale.fr_FR, data.getTokens());
		
		System.out.println("\nEntities from coreNlp");
		for(Entity entity: data.getEntities()) {
			entity.printValue();
		}
		System.out.println("\nEntities from duckling");
		for(Entity entity: fromDuckling) {
			entity.printValue();
		}
		
		EntityResolver resolver = new EntityResolver();
		List<Entity> resolvedEntities = resolver.resolveEntities(fromDuckling, data.getEntities());
		
		System.out.println("\nResolved Entities");
		for(Entity entity: resolvedEntities) {
			entity.printValue();
		}
		
	}


}
