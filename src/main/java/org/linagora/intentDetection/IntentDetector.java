package org.linagora.intentDetection;

import java.util.HashMap;
import java.util.List;

import org.apache.tika.language.LanguageIdentifier;
import org.linagora.intentDetection.corenlp.CoreNLPWrapper;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.corenlp.NLPData;
import org.linagora.intentDetection.corenlp.Relation;
import org.linagora.intentDetection.corenlp.TextCleaner;
import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.duckling.DucklingLocale;
import org.linagora.intentDetection.duckling.DucklingWrapper;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.semantic.EntityResolver;
import org.linagora.intentDetection.semantic.MatchedIntent;
import org.linagora.intentDetection.semantic.SemanticData;
import org.linagora.intentDetection.semantic.SemanticGraphBuilder;
import org.linagora.intentDetection.semantic.ontology.Ontology;
import org.linagora.intentDetection.semantic.reasoner.OntologyBuilder;
import org.linagora.intentDetection.semantic.reasoner.Reasoner;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

public class IntentDetector {
	
	private CoreNLPWrapper coreNLPWrapper = null;
	private DucklingWrapper ducklingWrapper = null;
	private SemanticGraphBuilder graphBuilder = null;
	private EntityResolver resolver = null;
	private Ontology ontology = null;
	private Language language = null;
	
	//language detection variables
	private List<LanguageProfile> languageProfiles = null;
	private LanguageDetector languageDetector = null;
	private TextObjectFactory textObjectFactory = null;
	
	
	public IntentDetector(Language language, String duckling_url, String ontology_path) {
		this.language = language;
		init(language, duckling_url, ontology_path);
	}
	
	private void init(Language language, String duckling_url, String ontology_path) {
		coreNLPWrapper = new CoreNLPWrapper(language);
		ducklingWrapper = new DucklingWrapper(duckling_url);
		resolver = new EntityResolver();
		graphBuilder = new SemanticGraphBuilder();
		ontology = OntologyBuilder.buildOntology(ontology_path);
		initLanguageDetection();
	}
	
	private void initLanguageDetection() {
		try {
			languageProfiles = new LanguageProfileReader().readAllBuiltIn();
			//build language detector:
			languageDetector = LanguageDetectorBuilder.create(NgramExtractors.backwards())
			        .withProfiles(languageProfiles)
			        .build();
			//create a text object factory
			textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
						
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String getLanguage(String text) {
		try {
			TextObject textObject = textObjectFactory.forText(text);
			Optional<LdLocale> lang = languageDetector.detect(textObject);
			return lang.get().getLanguage();
		}catch(Exception e) {
			LanguageIdentifier identifier = new LanguageIdentifier(text);
		    String language = identifier.getLanguage();
		    //System.out.println(language);
			return language ;
		}
	
	}
	
//	public HashMap<Integer, List<MatchedIntent>> detectIntents(Email email){
//		
//	}
	
	public Reasoner buildReasoner (String text) {
		text = TextCleaner.replaceNonBreakingWhiteSpace(text);
		String email = text;
		text = TextCleaner.cleanReplyBlock(text);
		text = TextCleaner.formatText(text);
		//System.out.println(text);
				
		long startTime = System.currentTimeMillis();
				
	    String lang = getLanguage(text);
	    System.out.println("Detected text language: "+lang);
//	    if(!lang.equalsIgnoreCase("fr")) {
//	    	return new HashMap<Integer,List<MatchedIntent>>();
//	    }
	      
		NLPData data = coreNLPWrapper.parseText(text);
		
		List<Entity> fromDuckling = null;
		
		if(language.equals(Language.french)) {
			fromDuckling = ducklingWrapper.callDucklingService(text, DucklingLocale.fr_FR, data.getTokens());
		}else if(language.equals(Language.english)) {
			fromDuckling = ducklingWrapper.callDucklingService(text, DucklingLocale.en_GB, data.getTokens());
		}
		
//		for(Token token: data.getTokens()) {
//			System.out.println(token.toString());
//		}
////
//		System.out.println("\n\nEntities from coreNlp");
//		for (Entity entity : data.getEntities()) {
//			entity.printValue();
//		}
//		System.out.println("\n_____________________________\nEntities from duckling\n");
//		for (Entity entity : fromDuckling) {
//			entity.printValue();
//		}

		List<Entity> resolvedEntities = resolver.resolveEntities(fromDuckling, data.getEntities());

//		System.out.println("\n_____________________________\nResolved Entities\n");
//		for (Entity entity : resolvedEntities) {
//			entity.printValue();
//			System.out.println(entity.getSentId());
//		}

		SemanticData sg = graphBuilder.convertToSemanticGraph(data.getTokens(), resolvedEntities, data.getRelations());

//		for (int i = 0; i < sg.getTokens().size(); i++) {
//			System.out.println(sg.getTokens().get(i).toString());
//
//		}
//
//		for (Relation rel : sg.getRelations()) {
//			System.out.println(rel.toString());
//		}
//
//		System.out.println("\n\n------------------------------\nMatching intents\n------------------------------\n\n");
		
		Reasoner manager = new Reasoner(ontology, email, sg.getTokens(), sg.getRelations(), resolvedEntities, Language.french);
		
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Time execution: " + elapsedTime+ "ms");
		
		return manager;
	}
	
	public HashMap<Integer,List<MatchedIntent>> detectIntents(String text) {
		text = TextCleaner.replaceNonBreakingWhiteSpace(text);
		String email = text;
		text = TextCleaner.cleanReplyBlock(text);
		text = TextCleaner.formatText(text);
		//System.out.println(text);
				
		long startTime = System.currentTimeMillis();
				
	    String lang = getLanguage(text);
	    System.out.println("Detected text language: "+lang);
//	    if(!lang.equalsIgnoreCase("fr")) {
//	    	return new HashMap<Integer,List<MatchedIntent>>();
//	    }
	      
		NLPData data = coreNLPWrapper.parseText(text);
		
		List<Entity> fromDuckling = null;
		
		if(language.equals(Language.french)) {
			fromDuckling = ducklingWrapper.callDucklingService(text, DucklingLocale.fr_FR, data.getTokens());
		}else if(language.equals(Language.english)) {
			fromDuckling = ducklingWrapper.callDucklingService(text, DucklingLocale.en_GB, data.getTokens());
		}
		
//		for(Token token: data.getTokens()) {
//			System.out.println(token.toString());
//		}
////
//		System.out.println("\n\nEntities from coreNlp");
//		for (Entity entity : data.getEntities()) {
//			entity.printValue();
//		}
//		System.out.println("\n_____________________________\nEntities from duckling\n");
//		for (Entity entity : fromDuckling) {
//			entity.printValue();
//		}

		List<Entity> resolvedEntities = resolver.resolveEntities(fromDuckling, data.getEntities());

//		System.out.println("\n_____________________________\nResolved Entities\n");
//		for (Entity entity : resolvedEntities) {
//			entity.printValue();
//			System.out.println(entity.getSentId());
//		}

		SemanticData sg = graphBuilder.convertToSemanticGraph(data.getTokens(), resolvedEntities, data.getRelations());

//		for (int i = 0; i < sg.getTokens().size(); i++) {
//			System.out.println(sg.getTokens().get(i).toString());
//
//		}
//
//		for (Relation rel : sg.getRelations()) {
//			System.out.println(rel.toString());
//		}
//
//		System.out.println("\n\n------------------------------\nMatching intents\n------------------------------\n\n");
		
		Reasoner manager = new Reasoner(ontology, email, sg.getTokens(), sg.getRelations(), Language.french);
//		System.out.println("\n\n------------------------------\nManager created\n------------------------------\n\n");
		HashMap<Integer,List<MatchedIntent>> matchedIntents = manager.intentsMatching();
		System.out.println("\n\n------------------------------\nMatched intents\n------------------------------\n\n");
//		for(Integer sentId: matchedIntents.keySet()) {
//			for(MatchedIntent matchedIntent: matchedIntents.get(sentId)) {
//				//System.out.println(matchedIntent);
//				matchedIntent.getScore();
//				matchedIntent.printMatchedIntentInstance();
//			}
//		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Time execution: " + elapsedTime+ "ms");
	    
	    return matchedIntents;
	}
	
	public void reloadOntology(String ontology_path) {
		ontology = OntologyBuilder.buildOntology(ontology_path);
	}

}
