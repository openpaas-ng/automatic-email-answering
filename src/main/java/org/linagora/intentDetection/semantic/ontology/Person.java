package org.linagora.intentDetection.semantic.ontology;

public class Person extends FrameElement {

	public Person(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Person() {
		super();
	}
	
	@Override
	public String toString() {
		return "Person [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
