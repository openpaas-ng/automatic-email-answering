package org.linagora.intentDetection.semantic.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Answer {
	
	private String uri = null;
	private String localName = null;
	private HashMap<String, String> labels = new HashMap<String, String>();
	private List<String> text = new ArrayList<String>();
	private List<Intent> singleConcernedIntents = new ArrayList<Intent>();
	private List<List<Intent>> composedConcernedIntents = new ArrayList<List<Intent>>();
	
	public Answer() {
		
	}
	public Answer(String uri, String localName) {
		super();
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
	public List<String> getText() {
		return text;
	}
	public void setText(List<String> text) {
		this.text = text;
	}
	public List<Intent> getSingleConcernedIntents() {
		return singleConcernedIntents;
	}
	public void setSingleConcernedIntents(List<Intent> concernedIntents) {
		this.singleConcernedIntents = concernedIntents;
	}
	
	public List<List<Intent>> getComposedConcernedIntents() {
		return composedConcernedIntents;
	}
	public void setComposedConcernedIntents(List<List<Intent>> composedConcernedIntents) {
		this.composedConcernedIntents = composedConcernedIntents;
	}
	@Override
	public String toString() {
		return "Answer [uri=" + uri + ", localName=" + localName + ", labels=" + labels + ", text=" + text
				+ ", singleConcernedIntents=" + singleConcernedIntents + ", composedConcernedIntents="
				+ composedConcernedIntents + "]";
	}
		

}
