package org.linagora.intentDetection.entities;

import java.util.LinkedList;

import org.linagora.intentDetection.corenlp.Token;

public class Entity {
	
	private LinkedList<Token> tokens = null;
	
	public Entity(LinkedList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public Entity() {
		this.tokens = new LinkedList<Token>();
	}
				
	public LinkedList<Token> getTokens() {
		return tokens;
	}
	
	public void setTokens(LinkedList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public int getStartPosition(){
		try {
			return tokens.getFirst().getStartPosition();
		}catch (Exception e){
			return -1;
		}
	}
	
	public int getEndPosition(){
		try {
			return tokens.getLast().getEndPosition();
		}catch (Exception e){
			return -1;
		}
	}
	
	public int getSentId() {
		try {
			return tokens.getFirst().getSentId();
		}catch (Exception e){
			return -1;
		}
	}
	
	public String getText() {
		
		try {
			int textSize = tokens.get(tokens.size()-1).getEndPosition();
			StringBuilder text = new StringBuilder(textSize);
			
			for(int i=0; i<textSize;i++) {
				text.append(" ");
			}
			
			for(Token token: tokens) {
				text.replace(token.getStartPosition(), token.getEndPosition(), token.getText());
			}
			
			return text.toString().replace("dle","du ").replace("àles","aux ").replace("àle","au ").trim();
						
		}catch(Exception e) {
			return null;
		}
		
	}
	
	public String getStemm() {
		try {
			int textSize = tokens.get(tokens.size()-1).getEndPosition();
			StringBuilder text = new StringBuilder(textSize);
			
			for(int i=0; i<textSize;i++) {
				text.append(" ");
			}
			
			for(Token token: tokens) {
				text.replace(token.getStartPosition(), token.getEndPosition(), token.getStemm());
			}
			
			return text.toString().replace("dle","du ").replace("àles","aux ").replace("àle","au ").trim();
		}catch(Exception e) {
			return null;
		}
		
	}
	
	private String getPos() {
		String text = "";
		for(Token token: tokens){
			text = text + token.getPos() + " ";
		}
		
		text = text.trim().replace(" ", "_");
		
		return text;
	}
	
	public Token convertToToken() {
		
		Token entityAsToken = new Token();
		
		entityAsToken.setRank(tokens.getFirst().getRank());
		entityAsToken.setSentId(tokens.getFirst().getSentId());
		entityAsToken.setStartPosition(getStartPosition());
		entityAsToken.setEndPosition(getEndPosition());
		
		entityAsToken.setText(getText());
		entityAsToken.setLemma(getText());
		entityAsToken.setStemm(getStemm());
		
		entityAsToken.setNer(this.getClass().getSimpleName());
		entityAsToken.setPos(getPos());		
		
		return entityAsToken;
	}
	
	public Token convertToToken(int rank) {
		
		Token entityAsToken = new Token();
		
		entityAsToken.setRank(rank);
		entityAsToken.setSentId(tokens.getFirst().getSentId());
		entityAsToken.setStartPosition(getStartPosition());
		entityAsToken.setEndPosition(getEndPosition());
		
		entityAsToken.setText(getText());
		entityAsToken.setLemma(getText());
		entityAsToken.setStemm(getStemm());
		
		entityAsToken.setNer(this.getClass().getSimpleName());
		entityAsToken.setPos(getPos());		
		
		return entityAsToken;
	}
	
	public Token convertToToken(String ner) {
		
		Token entityAsToken = new Token();
		
		entityAsToken.setRank(tokens.getFirst().getRank());
		entityAsToken.setSentId(tokens.getFirst().getSentId());
		entityAsToken.setStartPosition(getStartPosition());
		entityAsToken.setEndPosition(getEndPosition());
		
		entityAsToken.setText(getText());
		entityAsToken.setLemma(getText());
		entityAsToken.setStemm(getStemm());
		
		entityAsToken.setNer(ner);
		entityAsToken.setPos(getPos());		
		
		return entityAsToken;
	}
	
	public Token convertToToken(int rank, String ner) {
		
		Token entityAsToken = new Token();
		
		entityAsToken.setRank(rank);
		entityAsToken.setSentId(tokens.getFirst().getSentId());
		entityAsToken.setStartPosition(getStartPosition());
		entityAsToken.setEndPosition(getEndPosition());
		
		entityAsToken.setText(getText());
		entityAsToken.setLemma(getText());
		entityAsToken.setStemm(getStemm());
		
		entityAsToken.setNer(ner);
		entityAsToken.setPos(getPos());		
		
		return entityAsToken;
	}

	@Override
	public String toString() {
		return "[" + getStartPosition() + ", " + getEndPosition() + "]: " + getText() + " (Tokens size: " +tokens.size()+ ")";
	}
	
	@Override
	public boolean equals(Object object2) {
		if(this.getStartPosition() == ((Entity)object2).getStartPosition()
				&& this.getEndPosition() == ((Entity)object2).getEndPosition()) {
			return true;
		}
		
		return false;
	}

	
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void printValue() {
		// TODO Auto-generated method stub
		
	}
	
	
				
}
