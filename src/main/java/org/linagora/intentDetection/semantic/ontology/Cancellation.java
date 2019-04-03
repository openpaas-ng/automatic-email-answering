package org.linagora.intentDetection.semantic.ontology;


public class Cancellation extends FrameElement {

	public Cancellation(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Cancellation() {
		super();
	}

	@Override
	public String toString() {
		return "Cancellation [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}

}
