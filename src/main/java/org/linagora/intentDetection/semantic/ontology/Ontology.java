package org.linagora.intentDetection.semantic.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.corenlp.Stemmer;

public class Ontology {

	private HashMap<String, Intent> intents = null;
	private HashMap<String, Intent> intentsByLocalNames = null;
	private List<RelatedIntent> relatedIntents = null;
	private HashMap<String, RelatedIntent> relatedIntentsMap = null;
	private HashMap<String, Answer> answers = null;

	private HashMap<String, HashMap<String, LexicalUnit>> lexicalUnitsMap = new HashMap<String, HashMap<String, LexicalUnit>>();
	private HashMap<String, HashMap<LexicalUnit, Intent>> lexicalUnitIntentMap = new HashMap<String, HashMap<LexicalUnit, Intent>>();
	private HashMap<String, Pattern> lexicalUnitIntentPattern = new HashMap<String, Pattern>();

	private HashMap<Intent, List<FrameElement>> frameElements = new HashMap<Intent, List<FrameElement>>();
	private HashMap<Intent, HashMap<FrameElement, Intent>> frameElementIntentMap = new HashMap<Intent, HashMap<FrameElement, Intent>>();

	private Language language = Language.french;

	private Intent coreIntent = null;
	private List<String> frameElementNames =  null;

	public Ontology() {
		this.intents = new HashMap<String, Intent>();
		this.relatedIntents = new ArrayList<RelatedIntent>();
		this.setAnswers(new HashMap<String, Answer>());
	}

	public Ontology(HashMap<String, Intent> intents, List<RelatedIntent> relatedIntents) {
		
		this.intents = intents;
		this.relatedIntents = relatedIntents;
	}
	
	public Ontology(HashMap<String, Intent> intents, List<RelatedIntent> relatedIntents, HashMap<String, Answer> answers) {
		
		this.intents = intents;
		this.relatedIntents = relatedIntents;
		this.setAnswers(answers);
		
	}

	public Ontology(HashMap<String, Intent> intents, List<RelatedIntent> relatedIntents, Intent coreIntent) {
		
		this.intents = intents;
		this.relatedIntents = relatedIntents;
		this.coreIntent = coreIntent;
	}
	
	

	public Intent getCoreIntent() {
		return coreIntent;
	}

	public void setCoreIntent(Intent coreIntent) {
		this.coreIntent = coreIntent;
	}

	public HashMap<String, Intent> getIntents() {
		return intents;
	}

	public void setIntents(HashMap<String, Intent> intents) {
		this.intents = intents;
	}

	public List<RelatedIntent> getRelatedIntents() {
		return relatedIntents;
	}
	
	public HashMap<String, Intent> getIntentsByLocalName(){
		if(intentsByLocalNames == null) {
			intentsByLocalNames = new HashMap<String, Intent>();
			for(Intent intent: intents.values()){
				intentsByLocalNames.put(intent.getLocalName(), intent);
			}
		}
		
		return intentsByLocalNames;
	}
	

	public void setRelatedIntents(List<RelatedIntent> relatedIntents) {
		this.relatedIntents = relatedIntents;
	}

	public HashMap<String, RelatedIntent> getRelatedIntentsMap() {
		if (relatedIntentsMap == null) {
			relatedIntentsMap = new HashMap<String, RelatedIntent>();
			for (RelatedIntent relatedIntent : relatedIntents) {
				String key = relatedIntent.getDomain().getUri() + relatedIntent.getRange().getUri();

				relatedIntentsMap.put(key, relatedIntent);
				if (relatedIntent.getRelatedIntentCharacteristics()
						.contains(RelatedIntentCharacteristic.SymmetricProperty)) {
					String symmetrickey = relatedIntent.getRange().getUri() + relatedIntent.getDomain().getUri();

					relatedIntentsMap.put(symmetrickey, relatedIntent);
				}
			}
		}

		return relatedIntentsMap;
	}

	public int countFrameElements() {
		int count = 0;
		for (Intent intent : intents.values())
			count = count + intent.getFrameElements().size();
		return count;
	}

	public int countLexicalUnits() {
		int count = 0;
		for (Intent intent : intents.values())
			count = count + intent.getLexicalUnits().size();
		return count;
	}

	private String tokenizeApostrophicText(String lexicalUnit) {
		String formattedLexicalUnit = "";
		switch (language.name()) {
		case "french": {
			formattedLexicalUnit = lexicalUnit.replaceAll("(?i)(s'|m'|n'|t'|d'|l'|qu'|c'|j')", "$1 ");
			formattedLexicalUnit = formattedLexicalUnit
					.replaceAll("(?i)(-ce|-je|-tu|-ils|-elles|-vous|-nous|-il|-elle)", " $1");
			return formattedLexicalUnit;
		}
		case "english": {
			formattedLexicalUnit = lexicalUnit.replaceAll("(?i)('s)", " $1");
		}
		}
		return lexicalUnit;

	}

	public void initLexicalUnitsData() {
		if (lexicalUnitIntentPattern.isEmpty()) {
			for (String key : getIntents().keySet()) {
				Intent intent = getIntents().get(key);
				HashMap<String, LexicalUnit> lexicalUnitsMap = new HashMap<String, LexicalUnit>();
				HashMap<LexicalUnit, Intent> lexicalUnitIntentMap = new HashMap<LexicalUnit, Intent>();

				List<LexicalUnit> lexicalUnits = intent.getLexicalUnits();

				for (LexicalUnit lexicalUnit : lexicalUnits) {
					lexicalUnitIntentMap.put(lexicalUnit, intent);
				}
				
				if (intent.isLeaf()) {
					List<Intent> superIntents = intent.getSuperIntents();

					for (Intent superIntent : superIntents) {
						lexicalUnits.addAll(superIntent.getLexicalUnits());

						for (LexicalUnit lexicalUnit : superIntent.getLexicalUnits()) {
							lexicalUnitIntentMap.put(lexicalUnit, superIntent);
						}
					}
				}

				String regex = "";
				if (!lexicalUnits.isEmpty()) {
					for (LexicalUnit lexicalUnit : lexicalUnits) {
						if (lexicalUnit.getType().equals(DataType.EXPR)) {
							if (lexicalUnit.getValue().matches(".*[\\*\\+\\(\\)\\|\\[\\]].*")) {
								// System.out.println(lexicalUnit.getIntent().getLocalName() + " >>>
								// "+lexicalUnit.getValue());
								// Pattern.compile(lexicalUnit.getValue());
								// is a regex
								String lexicalUnitExpr = "";
								String formattedLexicalUnit = tokenizeApostrophicText(lexicalUnit.getValue());
								formattedLexicalUnit = formattedLexicalUnit.replace("(?i)", "");
								formattedLexicalUnit = formattedLexicalUnit.replace(" du ", " de le ")
										.replace(" aux ", " à les ").replace(" au ", " à le ");
								formattedLexicalUnit = formattedLexicalUnit.replaceAll(
										"(?i)([a-zA-Z0-9áàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ\\-']+)",
										"$1#\n");
								String lexicalUnitMapKey = "";
								for (String regexElement : formattedLexicalUnit.split("\n")) {
									String stemmedLUValue = "";
									if (regexElement.endsWith("#")) {
										regexElement = regexElement.substring(0, regexElement.length() - 1);
										stemmedLUValue = Stemmer.stemmWord(regexElement, language.toString());
										if (!stemmedLUValue.matches(
												".*[a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ']+")) {
											stemmedLUValue = regexElement;
										}
										regexElement = stemmedLUValue + "_" + "[A-Z]+_([0-9]+)";

									} else {
										stemmedLUValue = regexElement;
									}
									lexicalUnitMapKey = lexicalUnitMapKey + stemmedLUValue;
									lexicalUnitExpr = lexicalUnitExpr + regexElement;
								}
								lexicalUnitExpr = lexicalUnitExpr.replace(" ", "\n");
								// System.out.println(lexicalUnit.getIntent().getLocalName() + " >>>
								// "+lexicalUnitExpr);
								regex = regex + lexicalUnitExpr + "|";
								// regex = regex + lexicalUnit.getValue() + "|";
								lexicalUnitMapKey = lexicalUnitMapKey.trim();
								lexicalUnitsMap.put(lexicalUnitMapKey.toUpperCase() + "_" + DataType.EXPR.name(),
										lexicalUnit);
							} else {
								// is not a regex
								String lexicalUnitExpr = "";
								String formattedLexicalUnit = tokenizeApostrophicText(lexicalUnit.getValue());
								formattedLexicalUnit = formattedLexicalUnit.replace(" du ", " de le ")
										.replace(" aux ", " à les ").replace(" au ", " à le ");
								for (String element : formattedLexicalUnit.split(" ")) {
									String stemmedLUValue = Stemmer.stemmText(element, language.toString());
									lexicalUnitExpr = lexicalUnitExpr + stemmedLUValue + "_" + "[A-Z]+_([0-9]+)" + "\n";
								}

								regex = regex + lexicalUnitExpr + "|";

								String lexicalUnitMapKey = "";
								String lexicalUnitValue = lexicalUnit.getValue();
								lexicalUnitValue = tokenizeApostrophicText(lexicalUnitValue);
								for (String element : lexicalUnitValue.split(" ")) {
									String stemmedLUValue = Stemmer.stemmText(element, language.toString());
									lexicalUnitMapKey = lexicalUnitMapKey + stemmedLUValue + " ";
								}
								lexicalUnitMapKey = lexicalUnitMapKey.trim();
								lexicalUnitsMap.put(lexicalUnitMapKey.toUpperCase() + "_" + DataType.EXPR.name(),
										lexicalUnit);

							}

						} else {

							String stemmedLUValue = Stemmer.stemmWord(lexicalUnit.getValue(), language.toString());
							regex = regex + Pattern.quote(stemmedLUValue + "_" + lexicalUnit.getType().name())
									+ "_([0-9]+)|";

							lexicalUnitsMap.put(stemmedLUValue.toUpperCase() + "_" + lexicalUnit.getType().name(),
									lexicalUnit);
						}

					}
					regex = regex.substring(0, regex.length() - 1);
					this.lexicalUnitIntentPattern.put(key, Pattern.compile("(?i)" + regex));
					this.lexicalUnitsMap.put(key, lexicalUnitsMap);
					this.lexicalUnitIntentMap.put(key, lexicalUnitIntentMap);
				}
			}
		}

	}

	public HashMap<String, HashMap<String, LexicalUnit>> getLexicalUnitsMap() {
		return lexicalUnitsMap;
	}

	public HashMap<String, HashMap<LexicalUnit, Intent>> getLexicalUnitIntentMap() {
		return lexicalUnitIntentMap;
	}

	public HashMap<String, Pattern> getLexicalUnitIntentPattern() {
		return lexicalUnitIntentPattern;
	}

	public void initFrameElementsData() {
		if (frameElements.isEmpty()) {
			for (Intent intent : intents.values()) {
				List<FrameElement> frameElements = intent.getFrameElements();
				HashMap<FrameElement, Intent> frameElementIntentMap = new HashMap<FrameElement, Intent>();
				for (FrameElement frameElement : frameElements) {
					frameElementIntentMap.put(frameElement, intent);
				}
				for (Intent superIntent : intent.getSuperIntents()) {
					frameElements.addAll(superIntent.getFrameElements());
					for (FrameElement frameElement : superIntent.getFrameElements()) {
						frameElementIntentMap.put(frameElement, superIntent);
					}
				}
				this.frameElements.put(intent, frameElements);
				this.frameElementIntentMap.put(intent, frameElementIntentMap);
			}

		}
	}

	public HashMap<Intent, List<FrameElement>> getFrameElements() {
		return frameElements;
	}

	public HashMap<Intent, HashMap<FrameElement, Intent>> getFrameElementIntentMap() {
		return frameElementIntentMap;
	}

	@Override
	public String toString() {
		return "Ontology [intents=" + intents + ", relatedIntents=" + relatedIntents + "]";
	}

	public HashMap<String, Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(HashMap<String, Answer> answers) {
		this.answers = answers;
	}

	public void setFrameElementNames(List<String> frameElementNames) {
		this.frameElementNames = frameElementNames;
		
	}
	
	public List<String> getFrameElementNames() {
		return frameElementNames;
		
	}

}
