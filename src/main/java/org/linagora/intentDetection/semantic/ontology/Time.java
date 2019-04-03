package org.linagora.intentDetection.semantic.ontology;


public class Time extends FrameElement {

	public Time(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Time() {
		super();
	}
	
	@Override
	public String toString() {
		return "Time [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}

}
