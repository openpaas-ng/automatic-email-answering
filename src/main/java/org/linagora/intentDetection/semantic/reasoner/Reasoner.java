package org.linagora.intentDetection.semantic.reasoner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.linagora.intentDetection.Parameters;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.corenlp.Relation;
import org.linagora.intentDetection.corenlp.Stemmer;
import org.linagora.intentDetection.corenlp.TextCleaner;
import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.semantic.FrameElementInstance;
import org.linagora.intentDetection.semantic.Instance;
import org.linagora.intentDetection.semantic.InstanceOrigin;
import org.linagora.intentDetection.semantic.LexicalUnitInstance;
import org.linagora.intentDetection.semantic.MatchedIntent;
import org.linagora.intentDetection.semantic.ontology.DataType;
import org.linagora.intentDetection.semantic.ontology.FrameElement;
import org.linagora.intentDetection.semantic.ontology.FrameElementType;
import org.linagora.intentDetection.semantic.ontology.Intent;
import org.linagora.intentDetection.semantic.ontology.LexicalUnit;
import org.linagora.intentDetection.semantic.ontology.Ontology;
import org.linagora.intentDetection.semantic.ontology.RelatedIntent;
import org.linagora.intentDetection.semantic.ontology.ValueType;

public class Reasoner {

	private Ontology ontology = null;
	private List<Token> textTokens = null;
	private List<Relation> relations = null;
	private Language language = null;
	private List<Entity> entities = null;

	private StringBuffer originalText = new StringBuffer();
	private StringBuffer stemmedText = new StringBuffer();
	private String email = null;

	private HashMap<Integer, List<Token>> tokensBySentence = new HashMap<Integer, List<Token>>();

	public Reasoner(Ontology ontology, List<Token> textTokens, List<Relation> relations, Language language) {
		this.ontology = ontology;
		this.textTokens = textTokens;
		this.relations = relations;
		this.language = language;
		ontology.initLexicalUnitsData();
		ontology.initFrameElementsData();

		initializeManagerData();

	}
	
	public Reasoner(Ontology ontology, String email, List<Token> textTokens, List<Relation> relations, Language language) {
		this.ontology = ontology;
		this.textTokens = textTokens;
		this.relations = relations;
		this.email = email;
		this.language = language;
		ontology.initLexicalUnitsData();
		ontology.initFrameElementsData();

		initializeManagerData();

	}
	
	public Reasoner(Ontology ontology, String email, List<Token> textTokens, List<Relation> relations, List<Entity> entities, Language language) {
		this.ontology = ontology;
		this.textTokens = textTokens;
		this.relations = relations;
		this.email = email;
		this.language = language;
		this.entities = entities;
		ontology.initLexicalUnitsData();
		ontology.initFrameElementsData();

		initializeManagerData();

	}
	
	public Reasoner() {
		
	}

	private void initializeManagerData() {
		for (Token token : textTokens) {
			originalText.append(token.getText() + "\n");
			stemmedText.append(token.getStemm() + "_" + token.getPos() + "_" + token.getRank() + "\n");

			List<Token> tokensInSentence = tokensBySentence.get(token.getSentId());
			if (tokensInSentence == null) {
				tokensInSentence = new ArrayList<Token>();
				tokensBySentence.put(token.getSentId(), tokensInSentence);
			}
			tokensInSentence.add(token);

		}

	}

	private String tokenizeApostrophicText(String lexicalUnit) {
		String formattedLexicalUnit = "";
		switch (language.name()) {
		case "french": {
			formattedLexicalUnit = lexicalUnit.replaceAll("(s'|m'|n'|t'|d'|l'|qu'|c'|j')", "$1 ");
			formattedLexicalUnit = formattedLexicalUnit.replaceAll("(-ce|-je|-tu|-ils|-elles|-vous|-nous|-il|-elle)",
					" $1");
			return formattedLexicalUnit;
		}
		case "english": {
			formattedLexicalUnit = lexicalUnit.replaceAll("('s)", " $1");
		}
		}
		return lexicalUnit;

	}

	private String inverseTokenizeApostrophicText(String lexicalUnit) {
		String formattedLexicalUnit = "";
		switch (language.name()) {
		case "french": {
			formattedLexicalUnit = lexicalUnit.replaceAll("(s' |m' |n' |t' |d' |l' |qu' |c' |j' )", "$1");
			formattedLexicalUnit = formattedLexicalUnit
					.replaceAll("( -ce| -je| -tu| -ils| -elles| -vous| -nous| -il| -elle)", "$1");
			return formattedLexicalUnit;
		}
		case "english": {
			formattedLexicalUnit = lexicalUnit.replaceAll("( 's)", "$1");
		}
		}
		return lexicalUnit;

	}
	
	private HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> optimizedLexicalUnitsMatching(){

		HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> sentenceIntents = new HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>>();
	
		List<Intent> activatedLeafIntents = new ArrayList<Intent>();
        List<Intent> intentsToExplore = new ArrayList<Intent>();
        int numberOfIntents = ontology.getIntents().size();
        Intent currentIntent = ontology.getCoreIntent();
 //       int level = 1;
//        System.out.println("Browse level "+ level);
//        System.out.println(currentIntent.getLocalName());
       
//        level ++;
        
        while(numberOfIntents > 0 && !currentIntent.getSubIntents().isEmpty()) {
//        	System.out.println("Browse level "+ level + " counter at " + numberOfIntents + " current node "+currentIntent.getLocalName());
//        	StringBuffer leaf = new StringBuffer("");
        	
        	for(Intent intent : currentIntent.getSubIntents()) {
        		
        		//System.out.print(intent.getLocalName()+" ");
        		numberOfIntents--;
        		//projeter les lu sur le texte
        		//project lu function to activate the node
        		boolean matchedLexicalUnits = matchLexicalUnits(intent, sentenceIntents);
        		//check activation and if it is a leaf
        		if(intent.isLeaf() && matchedLexicalUnits) {
        		//	leaf.append(intent.getLocalName() + " is an activated leaf"+"\n");
        			System.out.println("Leaf intent added: " + intent.getLocalName());
        			activatedLeafIntents.add(intent);        			
                }
        		if(!intent.isLeaf() && matchedLexicalUnits){
        			System.out.println("Intent to be explored: " + intent.getLocalName());
                	intentsToExplore.add(intent);
                }
        	        	
        	}
        	
        //	System.out.println();
        	if(intentsToExplore.isEmpty()) {
        		intentsToExplore = currentIntent.getSubIntents();
        	}
        	
        	//System.out.println(leaf);
        	if(!intentsToExplore.isEmpty()) {
        		currentIntent = intentsToExplore.get(0);
//        		System.out.println("TO EXPLORE " +intentsToExplore);
//        		System.out.println("Current Node to be explored "+currentIntent.getLocalName());
        		intentsToExplore.remove(0);
 //       		level ++;
        		        		
        	}
        	 
        	
        }
        
        for(Intent n: activatedLeafIntents) {
        	System.out.println("Leaf node activated " + n.getLocalName());
        }
        
//		 for (Integer sentId : sentenceIntents.keySet()) {
//		
//		 System.out.println("Intent detected on Sentence ID: " + sentId);
//		 HashMap<Intent, List<LexicalUnitInstance>> intentTokens =
//		 sentenceIntents.get(sentId);
//		 for (Intent intent : intentTokens.keySet()) {
//		 System.out.println(">>>Intent: " + intent.getLocalName());
//		 List<LexicalUnitInstance> instances = intentTokens.get(intent);
//		 for(Instance instance: instances) {
//		 System.out.println(instance.toString());
//		 }
//		 }
//		
//		 }

		return sentenceIntents;
	}
	
	private boolean matchLexicalUnits(Intent intent, HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> sentenceIntents) {
		
		boolean intentIsActivated = false;
		String key = intent.getUri();
		
		HashMap<String, LexicalUnit> lexicalUnitsMap = ontology.getLexicalUnitsMap().get(key);
		HashMap<LexicalUnit, Intent> lexicalUnitIntentMap = ontology.getLexicalUnitIntentMap().get(key);
		Pattern pattern = ontology.getLexicalUnitIntentPattern().get(key);
		if(pattern == null) {
			return intentIsActivated;
		}
		Matcher matcher = pattern.matcher(stemmedText.toString());
		while (matcher.find()) {
//			 int startMatch = matcher.start();
//			 int endMatch = matcher.end();
			intentIsActivated = true;
			String stringMatch = matcher.group();
//			 System.out.println(intent.getLocalName() + " matches = " + stringMatch + " "
//			 + startMatch + " " + endMatch);
			List<Integer> tokensRank = new ArrayList<Integer>();
			String lexicalUnitKey = "";
			for (String matched : stringMatch.trim().split("\n")) {
				String rank = matched.substring(matched.lastIndexOf("_") + 1, matched.length());
				tokensRank.add(Integer.parseInt(rank));
				lexicalUnitKey = lexicalUnitKey + matched.substring(0, matched.indexOf("_")) + " ";
			}
			lexicalUnitKey = lexicalUnitKey.trim();

			lexicalUnitKey = inverseTokenizeApostrophicText(lexicalUnitKey);
			if (stringMatch.contains("\n")) {
				lexicalUnitKey = lexicalUnitKey + "_" + DataType.EXPR.name();
			} else {

				String pos = stringMatch.split("_")[1].trim();
				lexicalUnitKey = lexicalUnitKey + "_" + pos;
			}
			 //System.out.println("Match lexicalUnitkey "+lexicalUnitKey);

			LexicalUnit matchedLexicalUnit = lexicalUnitsMap.get(lexicalUnitKey.replace(" de le ", " du ")
					.replace(" à les ", " aux ").replace(" à le ", " au ").toUpperCase());
			if (matchedLexicalUnit == null) {
				for (String k : lexicalUnitsMap.keySet()) {
					boolean find = false;
					if(k.matches(".*[\\*\\+\\(\\)\\|\\[\\]].*")){
						find = lexicalUnitKey.replace(" de le ", " du ").replace(" à les ", " aux ")
								.replace(" à le ", " au ").toUpperCase().matches(k);
					}else {
						find = lexicalUnitKey.replace(" de le ", " du ").replace(" à les ", " aux ")
								.replace(" à le ", " au ").toUpperCase().matches(k.replace("?", "\\?"));
					}
					
					if (find) {
						matchedLexicalUnit = lexicalUnitsMap.get(k);
						break;

					}
				}
			}
			if (matchedLexicalUnit == null) {
				System.out.println("Problem with " + lexicalUnitKey);
				for (LexicalUnit lukey : lexicalUnitIntentMap.keySet()) {
					System.out.println(lukey + " >>> " + lexicalUnitIntentMap.get(lukey).getLocalName());
				}
				for (String lukey : lexicalUnitsMap.keySet()) {
					System.out.println(lukey + " >>> " + lexicalUnitsMap.get(lukey));
				}
				System.exit(1);
			}

			Intent intentOrigin = lexicalUnitIntentMap.get(matchedLexicalUnit);
			InstanceOrigin instanceOrigin = InstanceOrigin.From_Intent;
			if (intentOrigin != intent) {
				instanceOrigin = InstanceOrigin.From_Parent_Intent;
			}
			// System.out.println(tokensRank);
			List<Token> lexicalUnitAsToken = new ArrayList<Token>();
			for (Integer rank : tokensRank) {
				Token token = textTokens.get(rank);
				lexicalUnitAsToken.add(token);
			}

			HashMap<Intent, List<LexicalUnitInstance>> tokenOfIntent = sentenceIntents
					.get(lexicalUnitAsToken.get(0).getSentId());
			if (tokenOfIntent != null) {

				List<LexicalUnitInstance> tokens = tokenOfIntent.get(intent);
				if (tokens != null) {
					tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
				} else {
					tokens = new ArrayList<LexicalUnitInstance>();
					tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
					tokenOfIntent.put(intent, tokens);
				}

			} else {
				tokenOfIntent = new HashMap<Intent, List<LexicalUnitInstance>>();
				List<LexicalUnitInstance> tokens = new ArrayList<LexicalUnitInstance>();
				tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
				tokenOfIntent.put(intent, tokens);
				sentenceIntents.put(lexicalUnitAsToken.get(0).getSentId(), tokenOfIntent);
			}
		}
		
//		for(int sentId: sentenceIntents.keySet()) {
//			List<LexicalUnitInstance> lexicalUnitInstances = sentenceIntents.get(sentId).get(intent);
//			if(lexicalUnitInstances != null) {
//				HashMap<Intent, List<LexicalUnitInstance>> lexicalUnitInstancesMap = sentenceIntents.get(sentId);
//				for(Intent intentKey: lexicalUnitInstancesMap.keySet()) {
//					if(intent.getSuperIntents().contains(intentKey)) {
//						List<LexicalUnitInstance> lexicalUnitInstancesSupConcept = lexicalUnitInstancesMap.get(intentKey);
//						if(lexicalUnitInstancesSupConcept != null) {
//							lexicalUnitInstances.addAll(lexicalUnitInstancesSupConcept);
//						}
//						
//					}
//				}
//			}
//			
//		}
		
		return intentIsActivated;
		
	}
	
public List<String> findLexicalUnits(Intent intent, String text) {
		
		List<String> instances = new ArrayList<String>();
		
		String regex = "";
		
		for(LexicalUnit lexicalUnit: intent.getLexicalUnits()) {
			if(lexicalUnit.getType().equals(DataType.EXPR)) {
				if(lexicalUnit.getValue().matches(".*[\\*\\+\\(\\)\\|\\[\\]].*")) {
					regex = regex + lexicalUnit.getValue() + "|";
				}else {
					regex = regex + "\\b" + lexicalUnit.getValue() + "e*s*\\b|";
				}
			}else {
				regex = regex + "\\b" + lexicalUnit.getValue() + "e*s*\\b|";
			}
		}
		
		regex = "(" + regex.substring(0, regex.length() -1) + ")";
		//System.out.println(regex);
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(TextCleaner.getFirstDiscussionThread(text));
					
		while (matcher.find()) {
//			 int startMatch = matcher.start();
//			 int endMatch = matcher.end();
			
			String stringMatch = matcher.group();
			System.out.println(intent.getLocalName() + " matches = " + stringMatch);
			 instances.add(stringMatch);
//			
		}
		

		
		return instances;
		
	}


	private HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> lexicalUnitsMatching() {
		// System.out.println(stemmedText.toString());
		HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> sentenceIntents = new HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>>();

		for (String key : ontology.getIntents().keySet()) {
			Intent intent = ontology.getIntents().get(key);
			HashMap<String, LexicalUnit> lexicalUnitsMap = ontology.getLexicalUnitsMap().get(key);
			HashMap<LexicalUnit, Intent> lexicalUnitIntentMap = ontology.getLexicalUnitIntentMap().get(key);
			Pattern pattern = ontology.getLexicalUnitIntentPattern().get(key);
			if(pattern != null) {
				Matcher matcher = pattern.matcher(stemmedText.toString());
				//System.out.println(pattern.pattern());
				//System.out.println(stemmedText.toString());
				while (matcher.find()) {
//					 int startMatch = matcher.start();
//					 int endMatch = matcher.end();
					String stringMatch = matcher.group();
//					 System.out.println(intent.getLocalName() + " matches = " + stringMatch + " "
//					 + startMatch + " " + endMatch);
					List<Integer> tokensRank = new ArrayList<Integer>();
					String lexicalUnitKey = "";
					//System.out.println("matched expression ");
					for (String matched : stringMatch.trim().split("\n")) {
						//System.out.println(matched);
						String rank = matched.substring(matched.lastIndexOf("_") + 1, matched.length());
						tokensRank.add(Integer.parseInt(rank));
						lexicalUnitKey = lexicalUnitKey + matched.substring(0, matched.indexOf("_")) + " ";
					}
					
					lexicalUnitKey = lexicalUnitKey.trim();

					lexicalUnitKey = inverseTokenizeApostrophicText(lexicalUnitKey);
					
					if(lexicalUnitKey.endsWith("'")) {
						lexicalUnitKey = lexicalUnitKey + " ";
					}
					
					
					if (stringMatch.contains("\n")) {
						lexicalUnitKey = lexicalUnitKey + "_" + DataType.EXPR.name();
					} else {

						String pos = stringMatch.split("_")[1].trim();
						lexicalUnitKey = lexicalUnitKey + "_" + pos;
					}
					 //System.out.println("Match lexicalUnitkey "+lexicalUnitKey);

					LexicalUnit matchedLexicalUnit = lexicalUnitsMap.get(lexicalUnitKey.replace(" de le ", " du ")
							.replace(" à les ", " aux ").replace(" à le ", " au ").toUpperCase());
					if (matchedLexicalUnit == null) {
						for (String k : lexicalUnitsMap.keySet()) {
							boolean find = false;
							if(k.matches(".*[\\*\\+\\(\\)\\|\\[\\]].*")){
								find = lexicalUnitKey.replace(" de le ", " du ").replace(" à les ", " aux ")
										.replace(" à le ", " au ").toUpperCase().matches(k);
							}else {
								find = lexicalUnitKey.replace(" de le ", " du ").replace(" à les ", " aux ")
										.replace(" à le ", " au ").toUpperCase().matches(k.replace("?", "\\?"));
							}
							if (find) {
								matchedLexicalUnit = lexicalUnitsMap.get(k);
								break;

							}
						}
					}
					if (matchedLexicalUnit == null) {
						System.out.println("Problem with " + lexicalUnitKey);
						for (LexicalUnit lukey : lexicalUnitIntentMap.keySet()) {
							System.out.println(lukey + " >>> " + lexicalUnitIntentMap.get(lukey).getLocalName());
						}
						for (String lukey : lexicalUnitsMap.keySet()) {
							System.out.println(lukey + " >>> " + lexicalUnitsMap.get(lukey));
						}
						System.exit(1);
					}

					Intent intentOrigin = lexicalUnitIntentMap.get(matchedLexicalUnit);
					InstanceOrigin instanceOrigin = InstanceOrigin.From_Intent;
					if (intentOrigin != intent) {
						instanceOrigin = InstanceOrigin.From_Parent_Intent;
					}
					// System.out.println(tokensRank);
					List<Token> lexicalUnitAsToken = new ArrayList<Token>();
					for (Integer rank : tokensRank) {
						Token token = textTokens.get(rank);
						lexicalUnitAsToken.add(token);
					}

					HashMap<Intent, List<LexicalUnitInstance>> tokenOfIntent = sentenceIntents
							.get(lexicalUnitAsToken.get(0).getSentId());
					if (tokenOfIntent != null) {

						List<LexicalUnitInstance> tokens = tokenOfIntent.get(intent);
						if (tokens != null) {
							tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
						} else {
							tokens = new ArrayList<LexicalUnitInstance>();
							tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
							tokenOfIntent.put(intent, tokens);
						}

					} else {
						tokenOfIntent = new HashMap<Intent, List<LexicalUnitInstance>>();
						List<LexicalUnitInstance> tokens = new ArrayList<LexicalUnitInstance>();
						tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
						tokenOfIntent.put(intent, tokens);
						sentenceIntents.put(lexicalUnitAsToken.get(0).getSentId(), tokenOfIntent);
					}
				}
			}

			

		}

//		 for (Integer sentId : sentenceIntents.keySet()) {
//		
//		 System.out.println("Intent detected on Sentence ID: " + sentId);
//		 HashMap<Intent, List<LexicalUnitInstance>> intentTokens =
//		 sentenceIntents.get(sentId);
//		 for (Intent intent : intentTokens.keySet()) {
//		 System.out.println(">>>Intent: " + intent.getLocalName());
//		 List<LexicalUnitInstance> instances = intentTokens.get(intent);
//		 for(Instance instance: instances) {
//		 System.out.println(instance.toString());
//		 }
//		 }
//		
//		 }

		return sentenceIntents;
	}
	
	private HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> lexicalUnitsMatching(List<Intent> intentTypes) {
		// System.out.println(stemmedText.toString());
		HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> sentenceIntents = new HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>>();
		HashMap<String, Intent> filtredIntents = new HashMap<String, Intent>();
		for(Intent intent: ontology.getIntents().values()) {
			for(Intent type: intentTypes) {
				if(intent.getSuperIntents().contains(type)) {
					filtredIntents.put(intent.getUri(), intent);
					break;
				}
			}
		}

		for (String key : filtredIntents.keySet()) {
			Intent intent = filtredIntents.get(key);
			HashMap<String, LexicalUnit> lexicalUnitsMap = ontology.getLexicalUnitsMap().get(key);
			HashMap<LexicalUnit, Intent> lexicalUnitIntentMap = ontology.getLexicalUnitIntentMap().get(key);
			Pattern pattern = ontology.getLexicalUnitIntentPattern().get(key);
			if(pattern != null) {
				Matcher matcher = pattern.matcher(stemmedText.toString());
				while (matcher.find()) {
//					 int startMatch = matcher.start();
//					 int endMatch = matcher.end();
					String stringMatch = matcher.group();
//					 System.out.println(intent.getLocalName() + " matches = " + stringMatch + " "
//					 + startMatch + " " + endMatch);
					List<Integer> tokensRank = new ArrayList<Integer>();
					String lexicalUnitKey = "";
					for (String matched : stringMatch.trim().split("\n")) {
						String rank = matched.substring(matched.lastIndexOf("_") + 1, matched.length());
						tokensRank.add(Integer.parseInt(rank));
						lexicalUnitKey = lexicalUnitKey + matched.substring(0, matched.indexOf("_")) + " ";
					}
					
					lexicalUnitKey = lexicalUnitKey.trim();

					lexicalUnitKey = inverseTokenizeApostrophicText(lexicalUnitKey);
					
					if(lexicalUnitKey.endsWith("'")) {
						lexicalUnitKey = lexicalUnitKey + " ";
					}
					
					
					if (stringMatch.contains("\n")) {
						lexicalUnitKey = lexicalUnitKey + "_" + DataType.EXPR.name();
					} else {

						String pos = stringMatch.split("_")[1].trim();
						lexicalUnitKey = lexicalUnitKey + "_" + pos;
					}
					 //System.out.println("Match lexicalUnitkey "+lexicalUnitKey);

					LexicalUnit matchedLexicalUnit = lexicalUnitsMap.get(lexicalUnitKey.replace(" de le ", " du ")
							.replace(" à les ", " aux ").replace(" à le ", " au ").toUpperCase());
					if (matchedLexicalUnit == null) {
						for (String k : lexicalUnitsMap.keySet()) {
							boolean find = false;
							if(k.matches(".*[\\*\\+\\(\\)\\|\\[\\]].*")){
								find = lexicalUnitKey.replace(" de le ", " du ").replace(" à les ", " aux ")
										.replace(" à le ", " au ").toUpperCase().matches(k);
							}else {
								find = lexicalUnitKey.replace(" de le ", " du ").replace(" à les ", " aux ")
										.replace(" à le ", " au ").toUpperCase().matches(k.replace("?", "\\?"));
							}
							if (find) {
								matchedLexicalUnit = lexicalUnitsMap.get(k);
								break;

							}
						}
					}
					if (matchedLexicalUnit == null) {
						System.out.println("Problem with " + lexicalUnitKey);
						for (LexicalUnit lukey : lexicalUnitIntentMap.keySet()) {
							System.out.println(lukey + " >>> " + lexicalUnitIntentMap.get(lukey).getLocalName());
						}
						for (String lukey : lexicalUnitsMap.keySet()) {
							System.out.println(lukey + " >>> " + lexicalUnitsMap.get(lukey));
						}
						System.exit(1);
					}

					Intent intentOrigin = lexicalUnitIntentMap.get(matchedLexicalUnit);
					InstanceOrigin instanceOrigin = InstanceOrigin.From_Intent;
					if (intentOrigin != intent) {
						instanceOrigin = InstanceOrigin.From_Parent_Intent;
					}
					// System.out.println(tokensRank);
					List<Token> lexicalUnitAsToken = new ArrayList<Token>();
					for (Integer rank : tokensRank) {
						Token token = textTokens.get(rank);
						lexicalUnitAsToken.add(token);
					}

					HashMap<Intent, List<LexicalUnitInstance>> tokenOfIntent = sentenceIntents
							.get(lexicalUnitAsToken.get(0).getSentId());
					if (tokenOfIntent != null) {

						List<LexicalUnitInstance> tokens = tokenOfIntent.get(intent);
						if (tokens != null) {
							tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
						} else {
							tokens = new ArrayList<LexicalUnitInstance>();
							tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
							tokenOfIntent.put(intent, tokens);
						}

					} else {
						tokenOfIntent = new HashMap<Intent, List<LexicalUnitInstance>>();
						List<LexicalUnitInstance> tokens = new ArrayList<LexicalUnitInstance>();
						tokens.add(new LexicalUnitInstance(matchedLexicalUnit, lexicalUnitAsToken, instanceOrigin));
						tokenOfIntent.put(intent, tokens);
						sentenceIntents.put(lexicalUnitAsToken.get(0).getSentId(), tokenOfIntent);
					}
				}
			}

			

		}

//		 for (Integer sentId : sentenceIntents.keySet()) {
//		
//		 System.out.println("Intent detected on Sentence ID: " + sentId);
//		 HashMap<Intent, List<LexicalUnitInstance>> intentTokens =
//		 sentenceIntents.get(sentId);
//		 for (Intent intent : intentTokens.keySet()) {
//		 System.out.println(">>>Intent: " + intent.getLocalName());
//		 List<LexicalUnitInstance> instances = intentTokens.get(intent);
//		 for(Instance instance: instances) {
//		 System.out.println(instance.toString());
//		 }
//		 }
//		
//		 }

		return sentenceIntents;
	}

	public HashMap<Integer, HashMap<Intent, List<FrameElementInstance>>> frameElementsMatching(
			HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> sentenceWithLexicalUnits) {

		HashMap<Integer, HashMap<Intent, List<FrameElementInstance>>> sentenceIntentFEs = new HashMap<Integer, HashMap<Intent, List<FrameElementInstance>>>();
		int textSize = this.textTokens.get(textTokens.size() - 1).getEndPosition();

		for (Entry<Integer, HashMap<Intent, List<LexicalUnitInstance>>> entry : sentenceWithLexicalUnits.entrySet()) {
			// browse sentence
			StringBuffer sentenceText = new StringBuffer(textSize);
			for (int i = 0; i < textSize; i++) {
				sentenceText.append(" ");
			}
			Integer sentId = entry.getKey();
			List<Token> tokensInSentence = tokensBySentence.get(sentId);
			HashMap<String, List<Token>> tokensByType = new HashMap<String, List<Token>>();
			HashMap<Intent, List<FrameElementInstance>> frameElementInSentenceByIntent = new HashMap<Intent, List<FrameElementInstance>>();

			for (Token token : tokensInSentence) {
				sentenceText.replace(token.getStartPosition(), token.getEndPosition(), token.getText());
				List<Token> tokens = tokensByType.get(token.getNer());
				if (tokens == null) {
					tokens = new ArrayList<Token>();
					tokensByType.put(token.getNer(), tokens);
				}
				tokens.add(token);
			}
			// System.out.println(tokensByType);
			// System.out.println("ICI la phrase "+sentenceText.toString().replace("dle",
			// "du "));
			for (Entry<Intent, List<LexicalUnitInstance>> intentEntry : entry.getValue().entrySet()) {
				// browse intent in sentence
				Intent intent = intentEntry.getKey();
				// System.out.println("checking intent "+intent.getLocalName());
				List<FrameElementInstance> retrievedFrameElements = new ArrayList<FrameElementInstance>();
				// List<FrameElement> frameElements = intent.getFrameElements();
				// HashMap<FrameElement, Intent> frameElementIntentMap = new
				// HashMap<FrameElement, Intent>();
				// for(FrameElement frameElement: frameElements) {
				// frameElementIntentMap.put(frameElement, intent);
				// }
				// for (Intent superIntent: intent.getSuperIntents()) {
				// frameElements.addAll(superIntent.getFrameElements());
				// for(FrameElement frameElement: superIntent.getFrameElements()) {
				// frameElementIntentMap.put(frameElement, superIntent);
				// }
				// }

				List<FrameElement> frameElements = ontology.getFrameElements().get(intent);
				HashMap<FrameElement, Intent> frameElementIntentMap = ontology.getFrameElementIntentMap().get(intent);
				// filter frameElement retrieved in the text
				for (FrameElement frameElement : frameElements) {
					// System.out.println("checking frameElement "+frameElement.getLabel());
					if (frameElement.getFrameElementType().equals(FrameElementType.CLASS)) {
						String frameElementType = frameElement.getClass().getSimpleName();
						List<Token> retreivedTokens = tokensByType.get(frameElementType);
						if (retreivedTokens != null) {
							InstanceOrigin instanceOrigin = InstanceOrigin.From_Intent;
							if (intent != frameElementIntentMap.get(frameElement)) {
								instanceOrigin = InstanceOrigin.From_Parent_Intent;
							}
							for(Token t: retreivedTokens) {
								List<Token> tokenAsEntity = new ArrayList<Token>();
								tokenAsEntity.add(t);
								retrievedFrameElements
								.add(new FrameElementInstance(frameElement, tokenAsEntity, instanceOrigin));
							}
							
							// System.out.println("FE trouvé CLASS!!");
						}
					} else if (frameElement.getFrameElementType().equals(FrameElementType.INDIVIDUAL)) {
						boolean retreivedFEInstance = false;
						// for (Entry<String, ValueType> frameElementIndividual :
						// frameElement.getValues().entrySet()) {
						if (frameElement.getValueType().equals(ValueType.Sample_Value)) {

							for (Token token : tokensInSentence) {

								if (token.getText().equalsIgnoreCase(frameElement.getValue())) {
									retreivedFEInstance = true;
									List<Token> retreivedTokens = new ArrayList<Token>();
									retreivedTokens.add(token);
									InstanceOrigin instanceOrigin = InstanceOrigin.From_Intent;
									if (intent != frameElementIntentMap.get(frameElement)) {
										instanceOrigin = InstanceOrigin.From_Parent_Intent;
									}
									retrievedFrameElements.add(
											new FrameElementInstance(frameElement, retreivedTokens, instanceOrigin));
									// System.out.println("FE trouvé SAMPLE VALUE!!");
									// break;
								}
							}
						} else if (frameElement.getValueType().equals(ValueType.Regex)) {
							Pattern pattern = Pattern.compile(frameElement.getValue());
							Matcher matcher = pattern.matcher(sentenceText.toString().replace("dle", "du ")
									.replace("àles", "aux ").replace("àle", "au "));
							// System.out.println(frameElementInstance.getKey() + " >>> " +
							// sentenceText.toString());
							while (matcher.find()) {

								retreivedFEInstance = true;
								int start = matcher.start();
								int end = matcher.end();
								// System.out.println("FrameElement with regex "+frameElement.getLabel()+ "
								// "+matcher.group() + " " + start + " " + end);
								// System.out.println(sentenceText.toString().replace("dle", "du "));
								List<Token> retreivedTokens = new ArrayList<Token>();
								for (Token token : tokensInSentence) {
									if (token.getStartPosition() >= start && token.getEndPosition() <= end) {
										retreivedTokens.add(token);
										// System.out.println("rajouter!!!");
									}
								}
								InstanceOrigin instanceOrigin = InstanceOrigin.From_Intent;
								if (intent != frameElementIntentMap.get(frameElement)) {
									instanceOrigin = InstanceOrigin.From_Parent_Intent;
								}
								retrievedFrameElements
										.add(new FrameElementInstance(frameElement, retreivedTokens, instanceOrigin));
								// System.out.println("FE trouvé REGEX!!");

							}

							// }

						}
					}

				}
				frameElementInSentenceByIntent.put(intent, retrievedFrameElements);

			}
			sentenceIntentFEs.put(sentId, frameElementInSentenceByIntent);
		}

		// for (Integer sentId : sentenceIntentFEs.keySet()) {
		//
		// System.out.println("Intent detected on Sentence ID: " + sentId);
		// HashMap<Intent, List<FrameElementInstance>> intentFrameElements =
		// sentenceIntentFEs.get(sentId);
		// for (Intent intent : intentFrameElements.keySet()) {
		// System.out.println(">>>Intent: " + intent.getLocalName());
		// List<FrameElementInstance> instances = intentFrameElements.get(intent);
		// for(Instance instance: instances) {
		// System.out.println(instance.toString());
		// }
		// }
		//
		// }

		return sentenceIntentFEs;
	}

	private boolean containsMandatoryFromIntent(List<LexicalUnitInstance> lexicalUnitInstances) {
		for (LexicalUnitInstance lexicalUnitInstance : lexicalUnitInstances) {
			// System.out.println(lexicalUnitInstance.getLexicalUnit().getIntent().getLocalName()+",
			// "+lexicalUnitInstance.getLexicalUnit().getAnnotation() + ", " +
			// lexicalUnitInstance.getInstanceOrigin());
			if (lexicalUnitInstance.getLexicalUnit().getAnnotation().equals(DataType.Mandatory)
			/*
			 * && lexicalUnitInstance.getInstanceOrigin().equals(InstanceOrigin.From_Intent)
			 */) {
				return true;
			}
		}
		return false;
	}
	public boolean isTheSameInstance(List<LexicalUnitInstance> lexicalUnitInstances, List<FrameElementInstance> frameElementInstances) {
		if(lexicalUnitInstances.size() == 1 && frameElementInstances.size() == 1) {
			List<Token> tokens1 = lexicalUnitInstances.get(0).getTokens();
			List<Token> tokens2 = frameElementInstances.get(0).getTokens();
			String instance1 = "";
			for(Token token: tokens1) {
				instance1 = instance1 + token.getText() + " ";
			}
			instance1 = instance1.trim().toLowerCase();
			String instance2 = "";
			for(Token token: tokens2) {
				instance2= instance2 + token.getText() + " ";
			}
			instance2 = instance2.trim().toLowerCase();
			if(instance1.equalsIgnoreCase(instance2)) {
				return true;
			}
		}
		return false;
	}

	public HashMap<Integer, List<MatchedIntent>> intentsMatching() {
		List<Intent> intentTypes = new ArrayList<Intent>();
		HashMap<Integer, List<MatchedIntent>> matchedIntents = new HashMap<Integer, List<MatchedIntent>>();
//		//find the type of email (meeting, etc.) based on the core intent first childs.
//		
		for(Intent intent: ontology.getCoreIntent().getSubIntents()) {
			if(!findLexicalUnits(intent, email).isEmpty()) {
				intentTypes.add(intent);
			}
		}
		System.out.println("Number of core Intent instance retrieved: " + intentTypes.size());
		if(intentTypes.isEmpty()) {
			return matchedIntents;
		}
		
		HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> lexicalUnitInstancesByIntent = lexicalUnitsMatching(intentTypes);
		//HashMap<Integer, HashMap<Intent, List<LexicalUnitInstance>>> lexicalUnitInstancesByIntent = optimizedLexicalUnitsMatching();
		HashMap<Integer, HashMap<Intent, List<FrameElementInstance>>> frameElementInstancesByIntent = frameElementsMatching(
				lexicalUnitInstancesByIntent);

		for (Integer sentId : lexicalUnitInstancesByIntent.keySet()) {
			HashMap<Intent, List<LexicalUnitInstance>> intentLexicalUnits = lexicalUnitInstancesByIntent.get(sentId);
			for (Intent intent : intentLexicalUnits.keySet()) {
				if (intent.isLeaf() && containsMandatoryFromIntent(intentLexicalUnits.get(intent))) {
					List<LexicalUnitInstance> lexicalUnitInstances = intentLexicalUnits.get(intent);
					List<FrameElementInstance> frameElementInstances = frameElementInstancesByIntent.get(sentId)
							.get(intent);
					if((lexicalUnitInstances.size() + frameElementInstances.size() >= 2) && !isTheSameInstance(lexicalUnitInstances, frameElementInstances)) {
						MatchedIntent matchedIntent = new MatchedIntent();
						matchedIntent.setIntent(intent);
						// matchedIntent.setScore(score);
						matchedIntent.setSentId(sentId);
						matchedIntent.setTokens(tokensBySentence.get(sentId));
						matchedIntent.setFrameElementInstances(frameElementInstances);
						matchedIntent.setLexicalUnitInstances(lexicalUnitInstances);
						List<MatchedIntent> list = matchedIntents.get(sentId);
						if (list == null) {
							list = new ArrayList<MatchedIntent>();
							matchedIntents.put(sentId, list);
						}
						list.add(matchedIntent);

					}
					
				}

			}
		}

//		 HashMap<Integer, List<MatchedIntent>> filtredMatchedIntents =
//		 filterIntentWithObjectProperties(matchedIntents);
//		 for(Integer key: filtredMatchedIntents.keySet()) {
//		 Collections.sort(filtredMatchedIntents.get(key));
//		 }
//		//
//		//
//		 return filtredMatchedIntents;

		for (Integer key : matchedIntents.keySet()) {
			Collections.sort(matchedIntents.get(key));
		}
		//
		return matchedIntents;
	}

	public HashMap<Integer, List<MatchedIntent>> filterIntentWithObjectProperties(
			HashMap<Integer, List<MatchedIntent>> matchedIntents) {

		HashMap<Integer, List<MatchedIntent>> filtredMatchedIntentsMap = new HashMap<Integer, List<MatchedIntent>>();
		HashMap<Integer, Integer> adjacentSentence = new HashMap<Integer, Integer>();
		HashMap<Integer, Set<MatchedIntent>> concervedMatchedIntents = new HashMap<Integer, Set<MatchedIntent>>();

		int sentenceWindows = 1;
		List<Integer> sentIds = new ArrayList<Integer>(matchedIntents.keySet());

		// filter matchedIntents with maxScore
		for (Integer sentId : sentIds) {
			List<MatchedIntent> matchedIntentsInSentId = matchedIntents.get(sentId);
			filtredMatchedIntentsMap.put(sentId, new ArrayList<MatchedIntent>());
			List<MatchedIntent> filtredMatchedIntents = filtredMatchedIntentsMap.get(sentId);
			Double maxScore = matchedIntentsInSentId.get(0).getScore();
			for (MatchedIntent matchedIntent : matchedIntentsInSentId) {
				// if(matchedIntent.getScore().doubleValue() == maxScore.doubleValue()) {
				filtredMatchedIntents.add(matchedIntent);
				// }
			}

		}

		// retrieve adjacent sentences with matchedIntents
		for (int i = 0; i < sentIds.size() - 1; i++) {
			for (int j = i + 1; j < sentIds.size(); j++) {
				Integer sentId1 = sentIds.get(i);
				Integer sentId2 = sentIds.get(j);
				if (sentId2 == sentId1 + sentenceWindows) {
					adjacentSentence.put(sentId1, sentId2);
					// System.out.println("Adjacent sentences " + sentId1 + " >>> " + sentId2);
				}

			}
		}

		// retreive matchedIntents thats form a domain and a range in an ontological
		// objectproperty
		for (Entry<Integer, Integer> adjacent : adjacentSentence.entrySet()) {
			List<MatchedIntent> matchedIntents1 = filtredMatchedIntentsMap.get(adjacent.getKey());
			List<MatchedIntent> matchedIntents2 = filtredMatchedIntentsMap.get(adjacent.getValue());
			for (MatchedIntent matchedIntent1 : matchedIntents1) {
				for (MatchedIntent matchedIntent2 : matchedIntents2) {
					String key = matchedIntent1.getIntent().getUri() + matchedIntent2.getIntent().getUri();
					RelatedIntent relatedIntent = ontology.getRelatedIntentsMap().get(key);
					if (relatedIntent != null) {
						// System.out.println("A conserver !!! ");
						//
						// System.out.println("--- " + matchedIntent1.getSentId() +
						// matchedIntent1.getIntent().getLocalName());
						// System.out.println("--- " + matchedIntent2.getSentId() +
						// matchedIntent2.getIntent().getLocalName());

						Set<MatchedIntent> concervedMatchedIntent1 = concervedMatchedIntents.get(adjacent.getKey());
						Set<MatchedIntent> concervedMatchedIntent2 = concervedMatchedIntents.get(adjacent.getValue());
						if (concervedMatchedIntent1 == null) {
							concervedMatchedIntent1 = new HashSet<MatchedIntent>();
							concervedMatchedIntents.put(adjacent.getKey(), concervedMatchedIntent1);
						}
						if (concervedMatchedIntent2 == null) {
							concervedMatchedIntent2 = new HashSet<MatchedIntent>();
							concervedMatchedIntents.put(adjacent.getValue(), concervedMatchedIntent2);
						}
						concervedMatchedIntent1.add(matchedIntent1);
						concervedMatchedIntent2.add(matchedIntent2);

					}

				}
			}
		}

		// concerve only correct matchedIntent
		for (Integer sentId : sentIds) {
			if (adjacentSentence.containsKey(sentId) || adjacentSentence.containsValue(sentId)) {
				Set<MatchedIntent> matchedIntentsInSentId = concervedMatchedIntents.get(sentId);
				if (matchedIntentsInSentId != null) {
					filtredMatchedIntentsMap.remove(sentId);
					filtredMatchedIntentsMap.put(sentId, new ArrayList<MatchedIntent>(matchedIntentsInSentId));
				}
			}

		}

		return filtredMatchedIntentsMap;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

}
