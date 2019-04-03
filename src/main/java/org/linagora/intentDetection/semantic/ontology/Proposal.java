package org.linagora.intentDetection.semantic.ontology;

public class Proposal extends FrameElement {

	public Proposal(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Proposal() {
		super();
	}

	@Override
	public String toString() {
		return "Proposal [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
