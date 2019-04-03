package org.linagora.intentDetection.api;

import java.util.ArrayList;
import java.util.List;

public class IntentResult {
	
	private String text;
	private List<PredictedIntent> predictedIntents = new ArrayList<PredictedIntent>();

	public IntentResult() {
		
	}
	public IntentResult(String text, List<PredictedIntent> intentScores) {
		
		this.text = text;
		this.predictedIntents = intentScores;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<PredictedIntent> getPredictedIntents() {
		return predictedIntents;
	}
	public void setPredictedIntents(List<PredictedIntent> predictedIntents) {
		this.predictedIntents = predictedIntents;
	}
	
	

}
