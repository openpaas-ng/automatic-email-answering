package org.linagora.intentDetection.semantic.ontology;


public class Confirmation extends FrameElement {

	public Confirmation(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Confirmation() {
		super();
	}
	
	@Override
	public String toString() {
		return "Confirmation [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
	
}
