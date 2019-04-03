package org.linagora.intentDetection.api;

import java.util.ArrayList;
import java.util.List;

public class Result {
	
	private List<IntentResult> intents = new ArrayList<IntentResult>();
	private List<ProposedAnswer> answers = new ArrayList<ProposedAnswer>();
	
	
	public Result() {
		
	}
	public Result(List<IntentResult> intents, List<ProposedAnswer> answers) {
		this.intents = intents;
		this.answers = answers;
	}
	
	public List<IntentResult> getIntents() {
		return intents;
	}
	public void setIntents(List<IntentResult> intents) {
		this.intents = intents;
	}
	public List<ProposedAnswer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<ProposedAnswer> answers) {
		this.answers = answers;
	}
		

}
