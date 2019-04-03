package org.linagora.intentDetection.semantic.ontology;

public class IntervalTime extends FrameElement {

	public IntervalTime(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public IntervalTime() {
		super();
	}

	@Override
	public String toString() {
		return "IntervalTime [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
