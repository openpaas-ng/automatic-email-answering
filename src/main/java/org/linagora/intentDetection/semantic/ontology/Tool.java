package org.linagora.intentDetection.semantic.ontology;


public class Tool extends FrameElement {

	public Tool(String label, String value, ValueType valueType) {
		super(label, value, valueType);
		
	}

	public Tool() {
		super();
	}
	
	@Override
	public String toString() {
		return "Tool [label=" + getLabel() + ", intent=" + getIntent().getLocalName() + ", value=" + getValue() + ", valueType=" + getValueType()
				+ ", annotationType=" + getAnnotation() + "]";
	}
}
