package org.linagora.intentDetection.semantic.ontology;

public class Unavailability extends FrameElement {

	public Unavailability(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Unavailability() {
		super();
	}

	@Override
	public String toString() {
		return "Unavailability [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
