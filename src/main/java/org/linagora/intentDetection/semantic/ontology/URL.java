package org.linagora.intentDetection.semantic.ontology;

public class URL extends FrameElement {

	public URL(String label, String value, ValueType valueType) {
		super(label, value, valueType);
	}

	public URL() {
		super();
	}

	@Override
	public String toString() {
		return "URL [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
