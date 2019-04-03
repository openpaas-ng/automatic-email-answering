package org.linagora.intentDetection.semantic.ontology;


public class Change extends FrameElement {

	public Change(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Change() {
		super();
	}
	
	@Override
	public String toString() {
		return "Change [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
