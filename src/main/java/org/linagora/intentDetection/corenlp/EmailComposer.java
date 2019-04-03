package org.linagora.intentDetection.corenlp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.linagora.intentDetection.Parameters;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.semantic.FrameElementInstance;
import org.linagora.intentDetection.semantic.MatchedIntent;
import org.linagora.intentDetection.semantic.ontology.DataType;
import org.linagora.intentDetection.semantic.ontology.Intent;
import org.linagora.intentDetection.semantic.ontology.LexicalUnit;
import org.linagora.intentDetection.semantic.ontology.Ontology;
import org.linagora.intentDetection.semantic.reasoner.OntologyBuilder;
import org.linagora.intentDetection.talismane.TalismaneWrapper;

import edu.stanford.nlp.util.StringUtils;

public class EmailComposer {

	public static final String GOODMORNING = "Bonjour";
	public static final String GOODEVENING = "Bonsoir";
	public static final String REGARDS = "Cordialement,";
	public static final String MADAM = "Madame";
	public static final String MISTER = "Monsieur";

	private static Set<String> FEMALE_LIST = new HashSet<String>();
	private static Set<String> MALE_LIST = new HashSet<String>();
	private static Set<String> MIX_LIST = new HashSet<String>();

	private static List<String> FRAME_ELEMENT_LIST = new ArrayList<String>();

	private static Set<String> getMaleFirstName() {

		if (MALE_LIST.isEmpty()) {
			String currentDir = System.getProperty("user.dir");
			MALE_LIST = loadList(currentDir + "/firstname_male.lst");
			MALE_LIST.removeAll(getCommonElements(MALE_LIST, getFemaleFirstName()));
		}

		return MALE_LIST;
	}

	private static Set<String> getFemaleFirstName() {

		if (FEMALE_LIST.isEmpty()) {
			String currentDir = System.getProperty("user.dir");
			FEMALE_LIST = loadList(currentDir + "/firstname_female.lst");
			FEMALE_LIST.removeAll(getCommonElements(FEMALE_LIST, getMaleFirstName()));
		}

		return FEMALE_LIST;
	}

	public static String getGender(String fullName) {
		try {
			String firstName = fullName.split(" ")[0].toLowerCase().trim();
			if (getFemaleFirstName().contains(firstName)) {
				return MADAM;
			} else if (getMaleFirstName().contains(firstName)) {
				return MISTER;
			} else {
				firstName = fullName.split("-")[0].toLowerCase().trim();
				if (getFemaleFirstName().contains(firstName)) {
					return MADAM;
				} else if (getMaleFirstName().contains(firstName)) {
					return MISTER;
				} else {
					return "";
				}
			}
		} catch (Exception e) {
			return "";
		}

	}

	private static Set<String> loadList(String path) {
		Set<String> list = new HashSet<String>();
		try {

			File f = new File(path);

			BufferedReader b = new BufferedReader(new FileReader(f));

			String readLine = "";

			while ((readLine = b.readLine()) != null) {
				list.add(readLine.toLowerCase());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static Set<String> getCommonElements(Set<String> list1, Set<String> list2) {
		if (MIX_LIST.isEmpty()) {
			for (String element : list1) {
				if (list2.contains(element))
					MIX_LIST.add(element);
			}
		}

		return MIX_LIST;
	}

	public static String composeEmail(String recipient, String signature, List<String> sentences) {
		String email = "";
		DateTime currentTime = DateTime.now().toDateTimeISO();

		if (currentTime.getHourOfDay() >= 0 && currentTime.getHourOfDay() <= 16) {
			email = GOODMORNING + " " + recipient.split(" ")[0] + ",\n\n";
		} else {
			email = GOODEVENING + " " + recipient.split(" ")[0] + ",\n\n";
		}

		for (String sentence : sentences) {
			email = email + sentence + "\n";
		}

		email = email + "\n";

		email = email + REGARDS + "\n";

		email = email + signature;

		return email;
	}

	private static String getCompangyFromEmail(String email) {

		String domain = email.split("@")[1];
		String company = domain.substring(0, domain.lastIndexOf("."));
		return company;
	}

	private static String composeGreeting(String recipient, String recipientEmail, String sender, String senderEmail) {
		// System.out.println("recipient: "+recipient);
		// System.out.println("recipientEmail: "+recipientEmail);
		// System.out.println("sender: " + sender);
		// System.out.println("senderEmail: " + senderEmail);
		String greeting = "";

		String gender = getGender(recipient);
		boolean sameCompany = getCompangyFromEmail(recipientEmail).equals(getCompangyFromEmail(senderEmail));
		DateTime currentTime = DateTime.now().toDateTimeISO();

		if (currentTime.getHourOfDay() >= 0 && currentTime.getHourOfDay() <= 16) {
			greeting = GOODMORNING;
		} else {
			greeting = GOODEVENING;
		}
		if (sameCompany) {
			greeting = greeting + " " + recipient.split(" ")[0] + ",\n\n";
		} else {
			if (gender.equals("")) {
				greeting = greeting + " " + recipient + ",\n\n";
			} else {
				greeting = greeting + " " + gender + recipient.substring(recipient.indexOf(" ")) + ",\n\n";
			}

		}

		return greeting;
	}

	public static String composeEmail(String recipient, String recipientEmail, String sender, String senderEmail,
			List<String> sentences) {
		String email = "";

		email = composeGreeting(recipient, recipientEmail, sender, senderEmail);

		for (String sentence : sentences) {
			email = email + sentence + "\n";
		}

		email = email + "\n";

		email = email + REGARDS + "\n";

		email = email + sender;

		return email;
	}
	
	public static String composeAnswer(String answer, String originalText, Ontology ontology,
			List<MatchedIntent> matchedIntents, List<Entity> entities) {
try {
	List<Form> grammars = parseAnswerCaneva(answer, ontology);
	for (Form grammar : grammars) {
		if (grammar.WORD.type.equals("FrameElement")) {
			answer = parseEntitiesGrammar(grammar, answer, entities);
		} else {
			answer = parseConceptGrammar(grammar, answer, originalText, ontology);
		}
	}
	
	for(Form grammar: grammars) {
		if(grammar.WORD.type.equals("FrameElement")) {
			
		String instanceText = "";
		if(grammar.DET != null) {
			instanceText = grammar.DET.defaultValue + " ";
		}
		instanceText = instanceText + grammar.WORD.defaultValue;
		answer = answer.replace(grammar.asText(), instanceText);
		
		
	}
	}

	// answer = answer.replaceAll("\\[[a-zA-Z]+\\]", "");
	answer = answer.replace("...", " ");
	answer = answer.replaceAll(" +", " ");
	return answer;
}catch(Exception e) {
	e.printStackTrace();
	return null;
}
		
	}

	public static String composeAnswer(String answer, String originalText, Ontology ontology,
			List<MatchedIntent> matchedIntents) {
try {
	List<Form> grammars = parseAnswerCaneva(answer, ontology);
	for (Form grammar : grammars) {
		if (grammar.WORD.type.equals("FrameElement")) {
			answer = parseFramElementGrammar(grammar, answer, matchedIntents);
		} else {
			answer = parseConceptGrammar(grammar, answer, originalText, ontology);
		}
	}
	
	for(Form grammar: grammars) {
		if(grammar.WORD.type.equals("FrameElement")) {
			
		String instanceText = "";
		if(grammar.DET != null) {
			instanceText = grammar.DET.defaultValue + " ";
		}
		instanceText = instanceText + grammar.WORD.defaultValue;
		answer = answer.replace(grammar.asText(), instanceText);
		
		
	}
	}

	// answer = answer.replaceAll("\\[[a-zA-Z]+\\]", "");
	answer = answer.replaceAll(" +", " ");
	return answer;
}catch(Exception e) {
	e.printStackTrace();
	return null;
}
		
	}

	private static String parseConceptGrammar(Form grammar, String answer, String originalText, Ontology ontology) {
		HashMap<String, String> instances = new HashMap<String, String>();
		
		//for (Intent intent : ontology.getCoreIntent().getSubIntents()) {
			instances.putAll(findLexicalUnits(ontology.getIntentsByLocalName().get(grammar.WORD.localName), originalText));
		//}
		
		//System.out.println(originalText);
		//System.out.println(instances);
		//System.out.println(grammar.WORD.localName);
		String instance = instances.get(grammar.WORD.localName);
		if (instance == null) {
			//System.out.println("GRAMMAR: " + grammar);
			String answerInstance = grammar.DET.defaultValue + " " + grammar.WORD.defaultValue;
			answer = answer.replace(grammar.asText(), answerInstance);
			return answer;
		}
		if (instance.matches("(?i)[aeyuioùéèàêû].+")) {
			if (grammar.DET.values.size() == 3) {
				String answerInstance = grammar.DET.values.get(2) + instance;
				answer = answer.replace(grammar.asText(), answerInstance);
				return answer;
			}
		}
		if(StringUtils.find(originalText, "(?i)(des|les|ces) +" + instance)){
			if(grammar.DET.values.contains("le") | grammar.DET.values.contains("la")) {
				String answerInstance = "les " + instance;
				answer = answer.replace(grammar.asText(), answerInstance);
				return answer;
			}else {
				String answerInstance = "des " + instance;
				answer = answer.replace(grammar.asText(), answerInstance);
				return answer;
			}
		}
		else if (StringUtils.find(originalText, "(?i)(une|la|ma|ta|cette|ça) +" + instance) 
				|| TalismaneWrapper.getGender(instance).equalsIgnoreCase("f")) {
			String answerInstance = grammar.DET.values.get(1) + " " + instance;
			answer = answer.replace(grammar.asText(), answerInstance);
			return answer;
		} else {
			String answerInstance = grammar.DET.values.get(0) + " " + instance;
			answer = answer.replace(grammar.asText(), answerInstance);
			return answer;
		}

	}

	private static String parseFramElementGrammar(Form grammar, String answer, List<MatchedIntent> matchedIntents) {
		//System.out.println("Parse frameElement Grammar: " + answer + " GRAMMAR " + grammar);
		for (MatchedIntent mi : matchedIntents) {
			answer = replaceFETagsWithInstance(grammar, answer, mi);
		}
		answer = answer.replace(grammar.asText(), "");
		return answer;
	}

	private static String replaceFETagsWithInstance(Form grammar, String text, MatchedIntent matchedIntent) {
		// System.out.println(text);
		for (FrameElementInstance instance : matchedIntent.getFrameElementInstances()) {
			// System.out.println(instance);
			if(instance.getFrameElement().getClass().getSimpleName().equalsIgnoreCase(grammar.WORD.localName)) {
				String instanceText = "";
				if(grammar.DET != null) {
					instanceText = grammar.DET.defaultValue + " ";
				}
				instanceText = instanceText + instance.getInstanceText();
				text = text.replace(grammar.asText(), instanceText);
				break;
			}
			
		}
		return text;
	}
	
	private static String parseEntitiesGrammar(Form grammar, String answer, List<Entity> entities) {
		//System.out.println("Parse frameElement Grammar: " + answer + " GRAMMAR " + grammar);
		for (Entity entity : entities) {
			answer = replaceFETagsWithInstance(grammar, answer, entity);
		}
		answer = answer.replace(grammar.asText(), "");
		return answer;
	}

	private static String replaceFETagsWithInstance(Form grammar, String text, Entity entity) {
		// System.out.println(text);
		
			// System.out.println(instance);
			if(entity.getClass().getSimpleName().equalsIgnoreCase(grammar.WORD.localName)) {
				String instanceText = "";
				if(grammar.DET != null) {
					instanceText = grammar.DET.defaultValue + " ";
				}
				instanceText = instanceText + entity.getText();
				text = text.replace(grammar.asText(), instanceText);
				
			}
			
		
		return text;
	}

	private static List<Form> parseAnswerCaneva(String text, Ontology ontology) {
		List<Form> forms = new ArrayList<Form>();

		Pattern pattern = Pattern.compile("\\{[^\\{.]+\\}", Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {

			String formText = matcher.group();
			//System.out.println(formText);

			Pattern patternExpr = Pattern.compile("\\[([^\\[.]+)\\]", Pattern.CASE_INSENSITIVE);

			Matcher matcherExpr = patternExpr.matcher(formText);
			Form form = new Form();
			while (matcherExpr.find()) {
				String exprText = matcherExpr.group(1);
				//System.out.println(exprText);
				Expression expression = new Expression();
				expression.expressionString = "[" + exprText + "]";
				String[] elements = exprText.split(";");
				// String type = null;
				List<String> values = new ArrayList<String>();
				// String defaultValue = null;
				for (String element : elements) {
					if (element.startsWith("default=")) {
						expression.defaultValue = element.replace("default=", "");
					} else if (element.startsWith("values=")) {
						for (String value : elements[1].replace("values=", "").split(",")) {
							values.add(value);
						}
					} else {
						expression.localName = element;
					}
				}
				expression.values = values;
				if (expression.localName.equals("DET")) {
					form.DET = expression;
				} else {
					form.WORD = expression;
				}
				if (form.WORD != null) {
					if (ontology.getFrameElementNames().contains(form.WORD.localName)) {
						form.WORD.type = "FrameElement";
					} else {
						form.WORD.type = "Concept";
						
					}
				}

			}

			forms.add(form);

		}
//		for (Form f : forms) {
//			System.out.println(f.asText());
//		}

		return forms;

	}

//	private static List<String> getFrameElementNameList() {
//		if (FRAME_ELEMENT_LIST.isEmpty()) {
//			String path = "org/linagora/intentDetection/semantic/ontology/";
//			try {
//				Enumeration<URL> resources = EmailComposer.class.getClassLoader().getResources(path);
//				while (resources.hasMoreElements()) {
//					URL next = resources.nextElement();
//					// System.out.println(next.getContent());
//					DataInputStream dis = new DataInputStream((InputStream) next.getContent());
//					String line = null;
//					while ((line = dis.readLine()) != null) {
//						if (line.endsWith(".class")) {
//							System.out.println("FrameElement name: "+line.substring(0, line.lastIndexOf('.')));
//							FRAME_ELEMENT_LIST.add(line.substring(0, line.lastIndexOf('.')));
//						}
//					}
//				}
//
//			} catch (Exception e) {
//				System.out.println("EmailComposer.getFrameElementNameList(): Enable to load frame element list.");
//
//			}
//		}
//		return FRAME_ELEMENT_LIST;
//
//	}

	public static HashMap<String, String> findLexicalUnits(Intent intent, String text) {

		HashMap<String, String> instances = new HashMap<String, String>();

		String regex = "";

		for (LexicalUnit lexicalUnit : intent.getLexicalUnits()) {
			if (lexicalUnit.getType().equals(DataType.EXPR)) {
				if (lexicalUnit.getValue().matches(".*[\\*\\+\\(\\)\\|\\[\\]].*")) {
					regex = regex + lexicalUnit.getValue() + "|";
				} else {
					regex = regex + "\\b" + lexicalUnit.getValue() + "e*s*\\b|";
				}
			} else {
				regex = regex + "\\b" + lexicalUnit.getValue() + "e*s*\\b|";
			}
		}

		regex = "(" + regex.substring(0, regex.length() - 1) + ")";
		// System.out.println(regex);
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			// int startMatch = matcher.start();
			// int endMatch = matcher.end();

			String stringMatch = matcher.group();
			// System.out.println(intent.getLocalName() + " matches = " + stringMatch);
			instances.put(intent.getLocalName(), stringMatch);
			//
		}

		return instances;

	}

	public static void main(String args[]) {
		Ontology onto = OntologyBuilder.buildOntology(Parameters.ONTOLOGY_PATH);
		EmailComposer.parseAnswerCaneva("Merci, j'ai bien reçu {[DET;values=le,la,l';default=le] [Share_Document;default=document]}.", onto);

	}

}
