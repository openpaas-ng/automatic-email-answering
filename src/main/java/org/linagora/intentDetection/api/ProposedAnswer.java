package org.linagora.intentDetection.api;

public class ProposedAnswer implements Comparable<ProposedAnswer>{
	
	//private String id = "";
	private String label = "";
	private String email = "";
	private double score = 0d;
	
	public ProposedAnswer() {
		
	}
	public ProposedAnswer(String label, String email) {
		this.label = label;
		this.email = email;
		//this.setId(id);
	}
	
	public ProposedAnswer(String label, String email, double score) {
		this.label = label;
		this.email = email;
		//this.setId(id);
		this.setScore(score);
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	@Override
	public int compareTo(ProposedAnswer o) {
		if(this.score > o.score) return -1;
		if(this.score < o.score) return 1;
		return 0;
	}
	
	

}
