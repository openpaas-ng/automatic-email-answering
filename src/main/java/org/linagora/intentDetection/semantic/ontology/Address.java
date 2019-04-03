package org.linagora.intentDetection.semantic.ontology;


public class Address extends FrameElement {

	public Address(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Address() {
		super();
	}
	
	@Override
	public String toString() {
		return "Address [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
	
	
	
	
}
