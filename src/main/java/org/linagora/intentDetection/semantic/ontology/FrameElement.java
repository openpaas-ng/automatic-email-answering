package org.linagora.intentDetection.semantic.ontology;


public class FrameElement {
	
	private String label = null;
	
	private Intent intent = null;
	
	private String value = null;
	private ValueType valueType = null;
	private DataType annotation = null;

	
	public FrameElement() {
	}
	
	public FrameElement(Intent intent) {
		
		this.intent = intent;

	}
	
	public FrameElement(String label, String value, ValueType valueType) {
		this.label = label;
		this.value = value;
		this.valueType = valueType;
		
	}
		
	public Object getEquivalentEntity() {
		return null;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public FrameElementType getFrameElementType() {
		if(value == null)
			return FrameElementType.CLASS;
		
		return FrameElementType.INDIVIDUAL;
	}
	
	public Intent getIntent() {
		return intent;
	}
	
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public DataType getAnnotation() {
		return annotation;
	}

	public void setAnnotation(DataType annotation) {
		this.annotation = annotation;
	}
	
	@Override
	public String toString() {
		return "FrameElement [label=" + label + ", intent=" + intent.getLocalName() + ", value=" + value + ", valueType=" + valueType
				+ ", annotation=" + annotation + "]";
	}
		
}
