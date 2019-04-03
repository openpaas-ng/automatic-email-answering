package org.linagora.intentDetection.semantic.ontology;

public class Interrogation extends FrameElement {

	public Interrogation(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Interrogation() {
		super();
	}
	
	@Override
	public String toString() {
		return "Interrogation [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
