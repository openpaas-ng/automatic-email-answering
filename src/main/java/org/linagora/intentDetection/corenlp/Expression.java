package org.linagora.intentDetection.corenlp;


import java.util.List;

public class Expression {
	public String expressionString = null;
	public String localName = null;
	public String type = "";
	public List<String> values = null;
	public String defaultValue = null;
	@Override
	public String toString() {
		return "Expression [expressionString=" + expressionString + ", localName=" + localName + ", type=" + type
				+ ", values=" + values + ", defaultValue=" + defaultValue + "]";
	}
	
	
	
	
	

}
