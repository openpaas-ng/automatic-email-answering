package org.linagora.intentDetection.semantic.ontology;

public class Notification extends FrameElement {

	public Notification(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Notification() {
		super();
	}
	
	@Override
	public String toString() {
		return "Notification [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
