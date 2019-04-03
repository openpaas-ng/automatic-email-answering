package org.linagora.intentDetection.semantic.ontology;

public class Subject extends FrameElement {
	
	public Subject(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}
	
	public Subject() {
		super();
	}

	@Override
	public String toString() {
		return "Subject [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
