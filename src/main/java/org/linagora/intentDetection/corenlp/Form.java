package org.linagora.intentDetection.corenlp;

public class Form {
	
	public Expression DET = null;
	public Expression WORD = null;
	
	@Override
	public String toString() {
		return "Form [DET=" + DET + ", WORD=" + WORD + "]";
	}
	
	public String asText() {
		String text = "{";
		if(DET != null) {
			text = text + DET.expressionString + " ";
		}
		text = text + WORD.expressionString + "}";
		
		return text;
	}
	

}
