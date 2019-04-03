package org.linagora.intentDetection.api;

import java.util.ArrayList;
import java.util.List;

public class PredictedIntent {
	
	private String intent;
	private double score;
	private List<String> hotwords = new ArrayList<String>();
	private List<String> answers = new ArrayList<String> ();
		
	public PredictedIntent() {
		
	}
	
	public PredictedIntent(String intent, double score) {
		
		this.intent = intent;
		this.score = score;
	}
	public PredictedIntent(String intent, double score, List<String> hotwords) {
		
		this.intent = intent;
		this.score = score;
		this.hotwords = hotwords;
	}
	public PredictedIntent(String intent, double score, List<String> hotwords, List<String> answers) {
		
		this.intent = intent;
		this.score = score;
		this.hotwords = hotwords;
		this.setAnswers(answers);
	}	
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	public List<String> getHotWords() {
		return hotwords;
	}

	public void setHotWords(List<String> hotwords) {
		this.hotwords = hotwords;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}
	
	

}
