package org.linagora.intentDetection.semantic.ontology;

import java.util.List;

public class RelatedIntent {
	
	private String label = null;
	private Intent domain = null;
	private Intent range = null;
	private List<RelatedIntentCharacteristic> relatedIntentCharacteristics = null;

	public RelatedIntent(String label, Intent domain, Intent range,
			List<RelatedIntentCharacteristic> relatedIntentCharacteristics) {
		super();
		this.label = label;
		this.domain = domain;
		this.range = range;
		this.relatedIntentCharacteristics = relatedIntentCharacteristics;
	}
	
	public RelatedIntent() {
		super();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Intent getDomain() {
		return domain;
	}

	public void setDomain(Intent domain) {
		this.domain = domain;
	}

	public Intent getRange() {
		return range;
	}

	public void setRange(Intent range) {
		this.range = range;
	}

	public List<RelatedIntentCharacteristic> getRelatedIntentCharacteristics() {
		return relatedIntentCharacteristics;
	}

	public void setRelatedIntentCharacteristics(List<RelatedIntentCharacteristic> relatedIntentCharacteristics) {
		this.relatedIntentCharacteristics = relatedIntentCharacteristics;
	}

	@Override
	public String toString() {
		return "RelatedIntent [label=" + label + ", domain=" + domain + ", range=" + range
				+ ", relatedIntentCharacteristics=" + relatedIntentCharacteristics + "]";
	}

	
}
