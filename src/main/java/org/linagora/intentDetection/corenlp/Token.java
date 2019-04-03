package org.linagora.intentDetection.corenlp;

public class Token implements Comparable<Token>{
	
	private int sentId = -1;
	private int rank = -1;
	private int startPosition = -1;
	private int endPosition = -1;
	
	private String text = null;
	private String lemma = null;
	private String stemm = null;
	private String pos = null;
	private String ner = null;
	
	public Token(){
		
	}
		
	public Token(int sentId, int rank, int startPosition, int endPosition, String text, String lemma, String stemm,
			String pos, String ner) {
		
		this.sentId = sentId;
		this.rank = rank;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.text = text;
		this.lemma = lemma;
		this.stemm = stemm;
		this.pos = pos;
		this.ner = ner;
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
	public String getId() {
		return sentId+"_"+rank+"_"+startPosition+"_"+endPosition;
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
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getNer() {
		return ner;
	}
	public void setNer(String ner) {
		this.ner = ner;
	}
	
	public String getStemm() {
		return stemm;
	}

	public void setStemm(String stemm) {
		this.stemm = stemm;
	}
	
	

	@Override
	public String toString() {
		return "Token [sentId=" + sentId + ", rank=" + rank + ", startPosition=" + startPosition + ", endPosition="
				+ endPosition + ", text=" + text + ", lemma=" + lemma + ", stemm=" + stemm + ", pos=" + pos + ", ner="
				+ ner + "]";
	}

	@Override
	public boolean equals(Object object2) {
		if(this.getStartPosition() == ((Token)object2).getStartPosition()
				&& this.getEndPosition() == ((Token)object2).getEndPosition()) {
			return true;
		}
		
		return false;
	}

	@Override
	public int compareTo(Token o) {
		if(this.startPosition > o.startPosition) return 1;
		if(this.startPosition < o.startPosition) return -1;
		return 0;
	}
	
	

}
