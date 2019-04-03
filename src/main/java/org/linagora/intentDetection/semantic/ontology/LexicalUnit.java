package org.linagora.intentDetection.semantic.ontology;

public class LexicalUnit {
	
	private String value = null;
	private DataType type = null;
	private DataType annotation = null;
	private Intent intent = null;
	
	public LexicalUnit(String value, DataType type) {
		this.value = value;
		this.type = type;
		
	}
	
	public LexicalUnit(String value, DataType type, Intent intent) {
		this.value = value;
		this.type = type;
		this.intent = intent;
		
	}

	public LexicalUnit(String value, DataType type, DataType annotation) {
		this.value = value;
		this.type = type;
		this.annotation = annotation;
	}



	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public DataType getAnnotation() {
		return annotation;
	}


	public void setAnnotation(DataType annotation) {
		this.annotation = annotation;
	}

	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	
	public Intent getIntent() {
		return intent;
	}
	
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	
	@Override
	public String toString() {
		return "Lexical Unit [" + this.getValue() + ", " + this.getType() + ", " + this.getAnnotation() + "] ";
	}

}
