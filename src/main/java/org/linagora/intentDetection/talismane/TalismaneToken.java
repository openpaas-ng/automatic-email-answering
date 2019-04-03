package org.linagora.intentDetection.talismane;

import java.util.HashMap;

import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.semantic.ontology.DataType;

public class TalismaneToken implements Comparable<TalismaneToken>{
	
	private int sentId = -1;
	private int rank = -1;
	private int startPosition = -1;
	private int endPosition = -1;
	private String text = null;
	private String lemma = null;
	private String stemm = null;
	private String category = null;
	private String posTag = null;
	private HashMap<String,String> morphology = new HashMap<String, String>();
	
	
	public TalismaneToken() {
		
	}
		
	public int getSentId() {
		return sentId;
	}
	public void setSentId(int sentId) {
		this.sentId = sentId;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPostag() {
		return posTag;
	}
	public void setPostag(String postag) {
		this.posTag = postag;
	}
	public HashMap<String,String> getMorphology() {
		return morphology;
	}
	public void setMorphology(HashMap<String,String> morphology) {
		this.morphology = morphology;
	}
	public int getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	public int getEndPosition() {
		return endPosition;
	}
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
	
	public DataType getUniversalPOS() {
		return TalismaneTagConverter.convertToUniversalTag(posTag);
	}
	@Override
	public int compareTo(TalismaneToken o) {
		if(this.startPosition > o.startPosition) return 1;
		if(this.startPosition < o.startPosition) return -1;
		return 0;
	}
	@Override
	public String toString() {
		return "TalismaneToken [sentId=" + sentId + ", rank=" + rank + ", startPosition=" + startPosition + ", endPosition="
				+ endPosition + ", text=" + text + ", lemma=" + lemma + ", category=" + category + ", posTag=" + posTag
				+ ", morphology=" + morphology + ", universalPosTag=" + getUniversalPOS()+  "]";
	}
	
	public Token toToken() {
		Token token = new Token();
		token.setText(text);
		token.setLemma(lemma);
		token.setStemm(stemm);
		token.setStartPosition(startPosition);
		token.setEndPosition(endPosition);
		token.setSentId(sentId);
		token.setRank(rank);
		token.setPos(getUniversalPOS().name());
		token.setNer("O");
		
		return token;
	}

	public String getStemm() {
		return stemm;
	}

	public void setStemm(String stemm) {
		this.stemm = stemm;
	}
	

}
