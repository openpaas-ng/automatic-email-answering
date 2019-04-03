package org.linagora.intentDetection.corenlp;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.joda.time.DateTime;
import org.linagora.intentDetection.common.DateTimeUtil;
import org.linagora.intentDetection.entities.Duration;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.entities.TimeUnit;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.time.SUTime.Time;

public class CoreNLPWrapper {
	
	private StanfordCoreNLP pipeline = null;
	private Language language = null;
		
	public CoreNLPWrapper(Language language) {
			
		if(language.equals(Language.french)) {
			pipeline = getFrenchPipeline();
		}else if (language.equals(Language.english)) {
			pipeline = getEnglishPipeline();
		}
		this.language = language;
	}
	
	private StanfordCoreNLP getFrenchPipeline() {
		// set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	    
	    props.setProperty("tokenize.language", "fr");
	    
	    props.setProperty("pos.model","edu/stanford/nlp/models/pos-tagger/french/french-ud.tagger");
	    
	    //parse
	    //props.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");

	    //depparse
	    props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/UD_French.gz");
	    props.setProperty("depparse.language", "french");
	    
	   	    
	    // build and return french pipeline
	    return new StanfordCoreNLP(props);
	}
	
	private StanfordCoreNLP getEnglishPipeline() {
		// set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	      
	    // build and return english pipeline
	    return new StanfordCoreNLP(props);
	}
	
	private static String getEquivalentClassName(String ner) {
		
		switch(ner) {
		case "PERSON": return "Person";
		case "LOCATION": return "Location";
		case "ORGANIZATION": return "Organization";
		//case "DATE": return "Time";
		//case "TIME": return "Time";
		case "DURATION": return "Duration";
		
		
		case "MONEY": return "Misc";
		case "NUMBER": return "Misc";
		case "ORDINAL": return "Misc";
		case "PERCENT": return "Misc";
		
		case "SET": return "Misc";
		case "MISC": return "Misc";
	
		}
		
		return null;
	}
	
		
	/***
	 * parseText parse a String text and return a CoreNLPDATA with tokens, relations extracted with coreNLP
	 * @param text
	 * @return
	 */
	public NLPData parseText(String text){
		
		List<Token> tokens = new ArrayList<Token>();
		List<Entity> entities = new ArrayList<Entity>();
		List<Relation> relations = new ArrayList<Relation>();
		HashMap<Integer, Token> tokenKeys = new HashMap<Integer, Token>();
		
		try {
			Annotation annotation = new Annotation(text);
		  	   
			CoreDocument document = new CoreDocument(annotation);
			
			pipeline.annotate(document);
			List<CoreSentence> sentences = document.sentences();
							
			for(int i = 0; i < sentences.size(); i++) {
				Entity currentEntity = null;
				CoreSentence sentence = sentences.get(i);
				List<CoreLabel> coreLabels = sentence.tokens();
				//create tokens from coreLabels
				for(int j = 0; j < coreLabels.size(); j++) {
					CoreLabel coreLabel = coreLabels.get(j);
					
					String originalText = coreLabel.originalText();
					String lemma = coreLabel.lemma();
					String pos = coreLabel.tag();
					String ner = coreLabel.ner();
					int rank = coreLabel.index();
					int startPosition = coreLabel.beginPosition();
					int endPosition = coreLabel.endPosition();
					int sentId = coreLabel.sentIndex();
					String id = sentId + "_" + rank + "_" + startPosition +"_" + endPosition;
					String stemm = Stemmer.stemmWord(originalText.toLowerCase(), this.language.toString());
					Token token = new Token(sentId, rank, startPosition, endPosition, originalText, lemma, stemm, pos, ner);
					tokenKeys.put(rank, token);
					tokens.add(token);
					
					//creating named entities object
					if(getEquivalentClassName(ner) != null) {
					
						String className = getEquivalentClassName(ner);
						
						if(currentEntity == null) {
							try {
								currentEntity = (Entity)Class.forName("org.linagora.intentDetection.entities."+className).getConstructor().newInstance();
								if(className.equals("Time")) {
									DateTime now = org.joda.time.DateTime.now();
									String currentDate = now.getYear() + "-" + now.getMonthOfYear() + "-" + now.getDayOfMonth();
									if(ner.equals("TIME")) {
										//System.out.println(currentDate + "T" + originalText);
										//Time t = SUTime.parseDateTime(currentDate + "T" + originalText);
										DateTime time = DateTimeUtil.parseDate(currentDate + "T" + originalText);
										((org.linagora.intentDetection.entities.Time)currentEntity).setTime(time);
									}else {
										System.out.println(originalText);
										
										DateTime time = DateTimeUtil.parseDate(originalText);
										
										((org.linagora.intentDetection.entities.Time)currentEntity).setTime(time);
									}
								}
								currentEntity.getTokens().add(token);
								
							}catch (Exception e){
								e.printStackTrace();
							}
						}else if (currentEntity.getClass().getSimpleName().equals(className)) {
							currentEntity.getTokens().add(token);
							if(className.equals("Duration")) {
								if(((Duration)currentEntity).getUnit() == null) {
									TimeUnit unit = TimeUnit.getEquivalentTimeUnit(token.getText());
									((Duration)currentEntity).setUnit(unit);
								}
							}
						}else if (!currentEntity.getClass().getSimpleName().equals(className)) {
							entities.add(currentEntity);
							try {
								currentEntity = (Entity)Class.forName("org.linagora.intentDetection.entities."+className).getConstructor().newInstance();
								if(className.equals("Time")) {
									DateTime now = org.joda.time.DateTime.now();
									String currentDate = now.getYear() + "-" + now.getMonthOfYear() + "-" + now.getDayOfMonth();
									if(ner.equals("TIME")) {
										Time suTime = SUTime.parseDateTime(currentDate + "T" + originalText);
										DateTime time = DateTime.parse(suTime.toISOString());
										((org.linagora.intentDetection.entities.Time)currentEntity).setTime(time);
									}else {
										Time suTime = SUTime.parseDateTime(originalText);
										DateTime time = null;
										if(suTime == null) {
											time = DateTime.parse(originalText);
										}else {
											time = DateTime.parse(suTime.toISOString());
										}
										((org.linagora.intentDetection.entities.Time)currentEntity).setTime(time);
									}
																
								}
								currentEntity.getTokens().add(token);
							}catch (Exception e){
								e.printStackTrace();
							}
							
						}
						
						if(j == coreLabels.size() - 1) {
							entities.add(currentEntity);
							currentEntity = null;
						}
					}else if(currentEntity != null){
						entities.add(currentEntity);
						currentEntity = null;
					}
									
				}
			
				//create relation from the semantic graph of the sentence
				SemanticGraph graph = sentence.dependencyParse();
				List<SemanticGraphEdge> edges = graph.edgeListSorted();
				
				for(SemanticGraphEdge edge: edges) {
					Token governor = tokenKeys.get(edge.getGovernor().index());
					Token dependent = tokenKeys.get(edge.getDependent().index());
					String name = edge.getRelation().getShortName();
					Direction direction = null;
					if(governor.getRank() > dependent.getRank()) {
						direction = Direction.left;
					}else {
						direction = Direction.right;
					}
					
					Relation relation = new Relation(governor, dependent, name, direction);
					relations.add(relation);
				}
				
			}
						
		}catch(Exception e) {
			System.err.println("Empty text for coreNLP.");
		}
		List<Entity> noEntities = new ArrayList<Entity>();
		for(Entity entity: entities) {
			if(entity.getTokens().getFirst().getText().matches("(?i)bonjour|bonsoir|salut|cordialement")) {
				entity.getTokens().remove(0);
			}
			if(entity.getTokens().isEmpty()) {
				noEntities.add(entity);
			}
		}
		
		entities.removeAll(noEntities);
		
		return new NLPData(tokens, entities, relations);
		
	}
	
	public static void main(String [] args) {
		String text = "La réunion va durer 40 minutes";
		CoreNLPWrapper wrapper = new CoreNLPWrapper(Language.french);
		NLPData data = wrapper.parseText(text);
		for(Token token : data.getTokens())
			System.out.println(token.toString());
		for(Relation relation: data.getRelations()){
			System.out.println(relation.toString());
		}
		for(Entity en: data.getEntities()) {
			en.printValue();
			System.out.println(en.getValue());
		}
			
		
	}
	
	

}
