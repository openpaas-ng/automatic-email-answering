package org.linagora.intentDetection.semantic.ontology;


public class Location extends FrameElement {
	
	public Location(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Location() {
		super();
	}
	
	@Override
	public String toString() {
		return "Location [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
