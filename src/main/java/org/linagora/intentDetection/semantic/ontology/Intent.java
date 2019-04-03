package org.linagora.intentDetection.semantic.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Intent {

	private String uri = null;
	private String localName = null;
	private HashMap<String, String> labels = new HashMap<String, String>();
	private List<FrameElement> frameElements = null;
	private List<LexicalUnit> lexicalUnits = null;
	private Ontology ontology = null;

	private int countOfMandatoryFrameElements = -1;
	private int countOfMandatoryLexicalUnits = -1;

	private int countOfOptionalFrameElements = -1;
	private int countOfOptionalLexicalUnits = -1;

	private List<Intent> superIntents = null;
	private List<Intent> subIntents = null;
	
	private List<Answer> answers = null;

	public Intent() {
		this.labels = new HashMap<String, String>();
		this.frameElements = new ArrayList<FrameElement>();
		this.lexicalUnits = new ArrayList<LexicalUnit>();
		this.subIntents = new ArrayList<Intent>();
		this.superIntents = new ArrayList<Intent>();
		this.answers = new ArrayList<Answer>();
	}

	public Intent(String uri) {
		this.uri = uri;
	}

	public Intent(String uri, String localName) {
		this.uri = uri;
		this.localName = localName;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public HashMap<String, String> getLabels() {
		return labels;
	}

	public void setLabels(HashMap<String, String> labels) {
		this.labels = labels;
	}

	public List<FrameElement> getFrameElements() {
		return frameElements;
	}

	public void setFrameElements(List<FrameElement> frameElements) {
		this.frameElements = frameElements;
	}

	public List<LexicalUnit> getLexicalUnits() {
		return lexicalUnits;
	}

	public void setLexicalUnits(List<LexicalUnit> lexicalUnits) {
		this.lexicalUnits = lexicalUnits;
	}

	public List<Intent> getSuperIntents() {
		return superIntents;
	}

	public void setSuperIntents(List<Intent> superIntents) {
		this.superIntents = superIntents;
	}

	public List<Intent> getSubIntents() {
		return subIntents;
	}

	public void setSubIntents(List<Intent> subIntents) {
		this.subIntents = subIntents;
	}

	private List<String> getSuperIntentsLocalNames() {
		List<String> results = new ArrayList<String>();
		for (Intent intent : superIntents) {
			results.add(intent.localName);
		}
		return results;
	}

	private List<String> getSubIntentsLocalNames() {
		List<String> results = new ArrayList<String>();
		for (Intent intent : subIntents) {
			results.add(intent.localName);
		}
		return results;
	}
	
	

	public boolean isLeaf() {
		return subIntents.isEmpty();
	}

	public int countOfMandatoryFrameElements() {

		if (countOfMandatoryFrameElements == -1) {
			countOfMandatoryFrameElements = 0;
			for (FrameElement frameElement : frameElements) {

				if (frameElement.getAnnotation().equals(DataType.Mandatory)) {
					countOfMandatoryFrameElements++;
				}

			}
		}

		return countOfMandatoryFrameElements;
	}

	public int countOfOptionalFrameElements() {

		if (countOfOptionalFrameElements == -1) {
			countOfOptionalFrameElements = 0;
			for (FrameElement frameElement : frameElements) {

				if (frameElement.getAnnotation().equals(DataType.Optional)) {
					countOfOptionalFrameElements++;
				}

			}
		}

		return countOfOptionalFrameElements;
	}

	public int countOfMandatoryLexicalUnits() {
		if (countOfMandatoryLexicalUnits == -1) {
			countOfMandatoryLexicalUnits = 0;
			for (LexicalUnit lexicalUnit : lexicalUnits) {
				if (lexicalUnit.getAnnotation().equals(DataType.Mandatory)) {
					countOfMandatoryLexicalUnits++;
				}
			}
		}

		return countOfMandatoryLexicalUnits;
	}

	public int countOfOptionalLexicalUnits() {
		if (countOfOptionalLexicalUnits == -1) {
			countOfOptionalLexicalUnits = 0;
			for (LexicalUnit lexicalUnit : lexicalUnits) {
				if (lexicalUnit.getAnnotation().equals(DataType.Optional)) {
					countOfOptionalLexicalUnits++;
				}
			}
		}

		return countOfOptionalLexicalUnits;
	}
		
	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public List<LexicalUnit> getLexicalUnitByAnnotationType(DataType annotation){
		List<LexicalUnit> result = new ArrayList<LexicalUnit>();
		for(LexicalUnit lexicalUnit: lexicalUnits) {
			if(lexicalUnit.getAnnotation().equals(annotation)) {
				result.add(lexicalUnit);
			}
		}
		return result;
	}
	
	public List<FrameElement> getFrameElementByAnnotationType(DataType annotation){
		List<FrameElement> result = new ArrayList<FrameElement>();
		for(FrameElement frameElement: frameElements) {
			if(frameElement.getAnnotation().equals(annotation)) {
				result.add(frameElement);
			}
		}
		return result;
	}
	
	public List<FrameElement> getClassFrameElementByAnnotationType(DataType annotation){
		List<FrameElement> result = new ArrayList<FrameElement>();
		for(FrameElement frameElement: frameElements) {
			if(frameElement.getAnnotation().equals(annotation) && frameElement.getFrameElementType().equals(FrameElementType.CLASS)) {
				result.add(frameElement);
			}
		}
		return result;
	}
	
	public List<FrameElement> getIndividualFrameElementByAnnotationType(DataType annotation){
		List<FrameElement> result = new ArrayList<FrameElement>();
		for(FrameElement frameElement: frameElements) {
			if(frameElement.getAnnotation().equals(annotation) && frameElement.getFrameElementType().equals(FrameElementType.INDIVIDUAL)) {
				result.add(frameElement);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "Intent [uri=" + uri + ", localName=" + localName + ", labels=" + labels + ", frameElements="
				+ frameElements + ", lexicalUnits=" + lexicalUnits + ", superIntents=" + getSuperIntentsLocalNames()
				+ ", subIntents=" + getSubIntentsLocalNames() + "]";
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

}
