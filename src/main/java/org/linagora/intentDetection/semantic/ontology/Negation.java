package org.linagora.intentDetection.semantic.ontology;

public class Negation extends FrameElement {

	public Negation(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}
	
	public Negation() {
		super();
	}

	@Override
	public String toString() {
		return "Negation [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
