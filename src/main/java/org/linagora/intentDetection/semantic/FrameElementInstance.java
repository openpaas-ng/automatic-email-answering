package org.linagora.intentDetection.semantic;

	import java.util.List;

	import org.linagora.intentDetection.corenlp.Token;
	import org.linagora.intentDetection.semantic.ontology.FrameElement;

	public class FrameElementInstance extends Instance{
		
		private FrameElement frameElement = null;
		
		public FrameElementInstance() {
			super();
		}
		
		public FrameElementInstance(FrameElement frameElement, List<Token> tokens) {
			super(tokens);
			this.frameElement = frameElement;
		}
		
		public FrameElementInstance(FrameElement frameElement, List<Token> tokens, InstanceOrigin instanceOrigin) {
			super(tokens, instanceOrigin);
			this.frameElement = frameElement;
		}
		

		public FrameElement getFrameElement() {
			return frameElement;
		}

		public void setFrameElement(FrameElement frameElement) {
			this.frameElement = frameElement;
		}

		@Override
		public String toString() {
			return "FrameElementInstance [frameElement=" + frameElement.getLabel() + "," + frameElement.getValue()+ ", "+ frameElement.getValueType()+", "+frameElement.getAnnotation()+", "+ super.toString()+"]";
		}
		
}
