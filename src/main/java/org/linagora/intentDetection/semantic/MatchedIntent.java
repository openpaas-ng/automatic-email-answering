package org.linagora.intentDetection.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.semantic.ontology.DataType;
import org.linagora.intentDetection.semantic.ontology.Intent;

public class MatchedIntent implements Comparable<MatchedIntent>{
	
	private Intent intent = null;
	private List<Token> tokens = null;
	private int sentId = -1;
	private Double score = null;
	
	private List<LexicalUnitInstance> lexicalUnitInstances = null;
	private List<FrameElementInstance> frameElementInstances = null;
	
	public MatchedIntent() {
		
	}
	
	public MatchedIntent(Intent intent, List<Token> tokens, int sentId, Double score) {
		
		this.intent = intent;
		this.tokens = tokens;
		this.sentId = sentId;
		this.score = score;
	}
	
	public MatchedIntent(Intent intent, List<Token> tokens, int sentId, Double score,
			List<LexicalUnitInstance> lexicalUnitInstances, List<FrameElementInstance> frameElementInstances) {
		super();
		this.intent = intent;
		this.tokens = tokens;
		this.sentId = sentId;
		this.score = score;
		this.lexicalUnitInstances = lexicalUnitInstances;
		this.frameElementInstances = frameElementInstances;
	}

	public List<LexicalUnitInstance> getLexicalUnitInstances() {
		return lexicalUnitInstances;
	}

	public void setLexicalUnitInstances(List<LexicalUnitInstance> lexicalUnitInstances) {
		this.lexicalUnitInstances = lexicalUnitInstances;
	}

	public List<FrameElementInstance> getFrameElementInstances() {
		return frameElementInstances;
	}

	public void setFrameElementInstances(List<FrameElementInstance> frameElementInstances) {
		this.frameElementInstances = frameElementInstances;
	}

	public Intent getIntent() {
		return intent;
	}



	public void setIntent(Intent intent) {
		this.intent = intent;
	}



	public List<Token> getTokens() {
		return tokens;
	}



	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}



	public int getSentId() {
		return sentId;
	}



	public void setSentId(int sentId) {
		this.sentId = sentId;
	}

	/*public Double getScore() {
		if(score == null) {
			int numberOfInstance = 0;
			int specificLexicalUnitIntance = 0;
			int specificFrameElementIntance = 0;
			
			numberOfInstance = lexicalUnitInstances.size() + frameElementInstances.size();
			
			for(LexicalUnitInstance instance: this.lexicalUnitInstances) {
				if(instance.getInstanceOrigin().equals(InstanceOrigin.From_Intent)) {
					specificLexicalUnitIntance++;
				}
			}
			
			for(FrameElementInstance instance: this.frameElementInstances) {
				if(instance.getInstanceOrigin().equals(InstanceOrigin.From_Intent)) {
					specificFrameElementIntance++;
				}
			}
			int specificity = 1 * numberOfInstance  + 2 * specificFrameElementIntance +  3 * specificLexicalUnitIntance;
			
			score = new Double(specificity);
		}
		
		return score;
		
	}*/
	
	public double getSpecificityScore(List<LexicalUnitInstance> parentLexicalUnitInstances,
	List<FrameElementInstance> parentFrameElementInstances,
	List<LexicalUnitInstance> specificLexicalUnitInstances,
	List<FrameElementInstance> specificFrameElementInstances) {
	
		HashMap<Intent, HashMap<DataType, List<LexicalUnitInstance>>> pLexicalUnitsByIntent = new  HashMap<Intent, HashMap<DataType, List<LexicalUnitInstance>>>();
		HashMap<Intent, HashMap<DataType, List<FrameElementInstance>>> pFrameElementsByIntent = new  HashMap<Intent, HashMap<DataType, List<FrameElementInstance>>>();
		
		HashMap<Intent, HashMap<DataType, List<LexicalUnitInstance>>> sLexicalUnitsByIntent = new  HashMap<Intent, HashMap<DataType, List<LexicalUnitInstance>>>();
		HashMap<Intent, HashMap<DataType, List<FrameElementInstance>>> sFrameElementsByIntent = new  HashMap<Intent, HashMap<DataType, List<FrameElementInstance>>>();
		
		for(LexicalUnitInstance instance: specificLexicalUnitInstances) {
			 HashMap<DataType, List<LexicalUnitInstance>> slu = sLexicalUnitsByIntent.get(instance.getLexicalUnit().getIntent());
			 if(slu == null) {
				 slu = new HashMap<DataType, List<LexicalUnitInstance>>();
				 sLexicalUnitsByIntent.put(instance.getLexicalUnit().getIntent(), slu);
			 }
			 List<LexicalUnitInstance> lui = slu.get(instance.getLexicalUnit().getAnnotation());
			 if(lui == null) {
				 lui = new ArrayList<LexicalUnitInstance>();
				 slu.put(instance.getLexicalUnit().getAnnotation(), lui);
			 }
			 lui.add(instance);
						
		}
		
		for(LexicalUnitInstance instance: parentLexicalUnitInstances) {
			 HashMap<DataType, List<LexicalUnitInstance>> plu = pLexicalUnitsByIntent.get(instance.getLexicalUnit().getIntent());
			 if(plu == null) {
				 plu = new HashMap<DataType, List<LexicalUnitInstance>>();
				 pLexicalUnitsByIntent.put(instance.getLexicalUnit().getIntent(), plu);
			 }
			 List<LexicalUnitInstance> lui = plu.get(instance.getLexicalUnit().getAnnotation());
			 if(lui == null) {
				 lui = new ArrayList<LexicalUnitInstance>();
				 plu.put(instance.getLexicalUnit().getAnnotation(), lui);
			 }
			 lui.add(instance);
						
		}
		
		for(FrameElementInstance instance: specificFrameElementInstances) {
			HashMap<DataType, List<FrameElementInstance>> fFrameElementsByIntent =  sFrameElementsByIntent.get(instance.getFrameElement().getIntent());
			if (fFrameElementsByIntent == null) {
				fFrameElementsByIntent = new HashMap<DataType, List<FrameElementInstance>> ();
				sFrameElementsByIntent.put(instance.getFrameElement().getIntent(), fFrameElementsByIntent);
			}
			List<FrameElementInstance> fei = fFrameElementsByIntent.get(instance.getFrameElement().getAnnotation());
			if(fei == null) {
				fei = new ArrayList<FrameElementInstance>();
				fFrameElementsByIntent.put(instance.getFrameElement().getAnnotation(), fei);
			}
			fei.add(instance);
		}
		
		for(FrameElementInstance instance: parentFrameElementInstances) {
			HashMap<DataType, List<FrameElementInstance>> fFrameElementsByIntent =  pFrameElementsByIntent.get(instance.getFrameElement().getIntent());
			if (fFrameElementsByIntent == null) {
				fFrameElementsByIntent = new HashMap<DataType, List<FrameElementInstance>> ();
				pFrameElementsByIntent.put(instance.getFrameElement().getIntent(), fFrameElementsByIntent);
			}
			List<FrameElementInstance> fei = fFrameElementsByIntent.get(instance.getFrameElement().getAnnotation());
			if(fei == null) {
				fei = new ArrayList<FrameElementInstance>();
				fFrameElementsByIntent.put(instance.getFrameElement().getAnnotation(), fei);
			}
			fei.add(instance);
		}
		
		Double score1 = getParentLUAnnotationScore(pLexicalUnitsByIntent);
		Double score2 = getParentFEAnnotationScore(pFrameElementsByIntent);
		Double score3 = getSpecificLUAnnotationScore(sLexicalUnitsByIntent);
		Double score4 =getParentFEAnnotationScore(sFrameElementsByIntent);
		
//		System.out.println(this.getText() + " --> " + this.getIntent().getLocalName());
//		System.out.println("Parent lu score: "+score1);
//		System.out.println("Parent fe score: "+ score2);
//		
//		System.out.println("Specific lu socre: "+ score3);
//		System.out.println("Specific fe score: "+score4);
		Double finalScore = score1 + score2 + score3 + score4;
//		System.out.println("New Score: "+finalScore);
//		System.out.println();
//		System.out.println();
		
		return finalScore;
	}
	
	public Double getParentLUAnnotationScore(HashMap<Intent, HashMap<DataType, List<LexicalUnitInstance>>> instanceByIntent) {
		//ratio de parcours des branches
		//double ratio = new Double(instanceByIntent.size()) / new Double(intent.getSuperIntents().size());
		
		double mandatoryScore = 0;
		double optionalScore = 0;
		
		List<LexicalUnitInstance> mandatory = null;
		List<LexicalUnitInstance> optional = null;
		for(Intent parentIntent: intent.getSuperIntents()) {
			double lexicalUnitRatio = 1d / parentIntent.getLexicalUnits().size();
			if(parentIntent.getLexicalUnits().size() == 0) {
				lexicalUnitRatio = 1d;
			}
			if(parentIntent.countOfMandatoryLexicalUnits() > 0) {
				try {
					
					mandatory = instanceByIntent.get(parentIntent).get(DataType.Mandatory);
					mandatoryScore = mandatoryScore + lexicalUnitRatio * mandatory.size();
				}catch(Exception e) {
					mandatoryScore = mandatoryScore - lexicalUnitRatio;
				}
			}
			if(parentIntent.countOfOptionalLexicalUnits() > 0) {
				try {
					optional = instanceByIntent.get(parentIntent).get(DataType.Optional);
					optionalScore = optionalScore + lexicalUnitRatio * optional.size();
				}catch (Exception e){
					//optionalScore = optionalScore - lexicalUnitRatio;
				}
			}
					
		}
		
		int parentLexicalUnitSize = 0;
		for(Intent parent: intent.getSuperIntents()) {
			parentLexicalUnitSize = parentLexicalUnitSize + parent.getLexicalUnits().size();
		}
		return optionalScore + mandatoryScore ;
	}
	
	public Double getParentFEAnnotationScore(HashMap<Intent, HashMap<DataType, List<FrameElementInstance>>> instanceByIntent) {
		//ratio de parcours des branches
		//Double ratio = new Double(instanceByIntent.size()) / new Double(intent.getSuperIntents().size());
		
		double mandatoryScore = 0;
		double optionalScore = 0;
		
		List<FrameElementInstance> mandatory = null;
		List<FrameElementInstance> optional = null;
		for(Intent parentIntent: intent.getSuperIntents()) {
			double frameElementRatio = 1d / parentIntent.getFrameElements().size();
			if(parentIntent.getFrameElements().size() == 0) {
				frameElementRatio = 1d;
			}
			if(parentIntent.countOfMandatoryFrameElements() > 0) {
				try {
					mandatory = instanceByIntent.get(parentIntent).get(DataType.Mandatory);
					mandatoryScore = mandatoryScore +  frameElementRatio * mandatory.size();
				}catch(Exception e) {
					mandatoryScore = mandatoryScore - frameElementRatio;
				}
			}
			if(parentIntent.countOfOptionalFrameElements() > 0) {
				try {
					optional = instanceByIntent.get(parentIntent).get(DataType.Optional);
					optionalScore = optionalScore + frameElementRatio * optional.size();
				}catch (Exception e){
					//optionalScore = optionalScore - 1;
				}
			}
					
		}
		
		int parentFrameElementSize = 0;
		for(Intent parent: intent.getSuperIntents()) {
			parentFrameElementSize = parentFrameElementSize + parent.getFrameElements().size();
		}
		
		return (optionalScore + mandatoryScore); // new Double(Math.max(1,parentFrameElementSize)) ;
	}
	
	public Double getSpecificLUAnnotationScore(HashMap<Intent, HashMap<DataType, List<LexicalUnitInstance>>> instanceByIntent) {
				
		double mandatoryScore = 0;
		double optionalScore = 0;
		
		List<LexicalUnitInstance> mandatory = null;
		List<LexicalUnitInstance> optional = null;
		double lexicalUnitRatio = 1d / intent.getLexicalUnits().size();
		if(intent.getLexicalUnits().size() == 0) {
			lexicalUnitRatio = 1d;
		}
			if(intent.countOfMandatoryLexicalUnits() > 0) {
				try {
					mandatory = instanceByIntent.get(intent).get(DataType.Mandatory);
					mandatoryScore = mandatoryScore + lexicalUnitRatio * mandatory.size();
				}catch(Exception e) {
					mandatoryScore = mandatoryScore - lexicalUnitRatio;
				}
			}
			if(intent.countOfOptionalLexicalUnits() > 0) {
				try {
					optional = instanceByIntent.get(intent).get(DataType.Optional);
					optionalScore = optionalScore + lexicalUnitRatio * optional.size();
				}catch (Exception e){
					//optionalScore = optionalScore - 1;
				}
			}
					
		
		
		return  new Double(optionalScore + mandatoryScore) ;// new Double(Math.max(1, intent.getLexicalUnits().size())) ;
	}
	
	public Double getSpecificFEAnnotationScore(HashMap<Intent, HashMap<DataType, List<FrameElementInstance>>> instanceByIntent) {
		
		
		double mandatoryScore = 0;
		double optionalScore = 0;
		
		List<FrameElementInstance> mandatory = null;
		List<FrameElementInstance> optional = null;
		double frameElementRatio = 1d / intent.getFrameElements().size();
		if(intent.getFrameElements().size() == 0) {
			frameElementRatio = 1d;
		}
			if(intent.countOfMandatoryFrameElements() > 0) {
				try {
					mandatory = instanceByIntent.get(intent).get(DataType.Mandatory);
					mandatoryScore = mandatoryScore + frameElementRatio * mandatory.size();
				}catch(Exception e) {
					mandatoryScore = mandatoryScore - frameElementRatio;
				}
			}
			if(intent.countOfOptionalFrameElements() > 0) {
				try {
					optional = instanceByIntent.get(intent).get(DataType.Optional);
					optionalScore = optionalScore + frameElementRatio * optional.size();
				}catch (Exception e){
					//optionalScore = optionalScore - 1;
				}
			}
					
		
		
		return new Double (optionalScore + mandatoryScore) ;// new Double(Math.max(1, intent.getFrameElements().size())) ;
	}
	
	public Double getScore() {
		if(score == null) {
			
			int numberOfInstance = 0;
			int specificLexicalUnitIntance = 0;
			int specificFrameElementIntance = 0;
						
			int specificAnnotationScore = 0;
			int parentAnnotationScore = 0;
			double specificity = 0d;
									
			
			List<LexicalUnitInstance> parentLexicalUnitInstances = new ArrayList<LexicalUnitInstance>();
			List<FrameElementInstance> parentFrameElementInstances = new ArrayList<FrameElementInstance>();
			
			List<LexicalUnitInstance> specificLexicalUnitInstances = new ArrayList<LexicalUnitInstance>();
			List<FrameElementInstance> specificFrameElementInstances = new ArrayList<FrameElementInstance>();
			
			for(LexicalUnitInstance instance: this.lexicalUnitInstances) {
				if(instance.getInstanceOrigin().equals(InstanceOrigin.From_Intent)) {
					specificLexicalUnitInstances.add(instance);
					if(instance.getLexicalUnit().getAnnotation().equals(DataType.Mandatory)) specificAnnotationScore++;
				}else {
					parentLexicalUnitInstances.add(instance);
					if(instance.getLexicalUnit().getAnnotation().equals(DataType.Mandatory)) parentAnnotationScore++;
					
					
				}
				//if(instance.getLexicalUnit().getAnnotation().equals(DataType.Mandatory)) annotationScore++;
			}
			
			for(FrameElementInstance instance: this.frameElementInstances) {
				if(instance.getInstanceOrigin().equals(InstanceOrigin.From_Intent)) {
					specificFrameElementInstances.add(instance);
					if(instance.getFrameElement().getAnnotation().equals(DataType.Mandatory)) specificAnnotationScore++;
				}else {
					parentFrameElementInstances.add(instance);
					if(instance.getFrameElement().getAnnotation().equals(DataType.Mandatory)) parentAnnotationScore++;
					
				}
				
				//if(instance.getFrameElement().getAnnotation().equals(DataType.Mandatory)) annotationScore++;
			}
			
			specificity = getSpecificityScore(parentLexicalUnitInstances, parentFrameElementInstances, specificLexicalUnitInstances, specificFrameElementInstances);
						
			
			numberOfInstance = lexicalUnitInstances.size() + frameElementInstances.size();
			
			for(LexicalUnitInstance instance: this.lexicalUnitInstances) {
				if(instance.getInstanceOrigin().equals(InstanceOrigin.From_Intent)) {
					specificLexicalUnitIntance++;
				}
			}
			
			for(FrameElementInstance instance: this.frameElementInstances) {
				if(instance.getInstanceOrigin().equals(InstanceOrigin.From_Intent)) {
					specificFrameElementIntance++;
				}
			}
			//double specificity = 0.5d * numberOfInstance  + 1d * specificFrameElementIntance +  2d * specificLexicalUnitIntance + 3d * specificAnnotationScore + 1d * parentAnnotationScore;
			//double specificity =  specificFrameElementIntance + specificLexicalUnitIntance + specificAnnotationScore + parentAnnotationScore;
			double computedScore =  numberOfInstance + specificFrameElementIntance + 4 * specificLexicalUnitIntance +  4 * specificAnnotationScore  + parentAnnotationScore;
			
			score = specificity + computedScore ;
			
			
		}
		
		return score;
		
	}

	@Override
	public int compareTo(MatchedIntent o) {
		if(getScore() > o.getScore()) return -1;
		if(getScore() < o.getScore()) return 1;
		return 0;
	}

	@Override
	public String toString() {
		int textSize = tokens.get(tokens.size()-1).getEndPosition();
		StringBuilder sentence = new StringBuilder(textSize);
		String text = "";
		for(int i=0; i<textSize;i++) {
			sentence.append(" ");
		}
		
		for(Token token: tokens) {
			//System.out.println(token);
			sentence.replace(token.getStartPosition(), token.getEndPosition(), token.getText());
			//System.out.println(sentence.length());
		}
		
		text = "Sentence: " + sentence.toString().replace("dle","du ").replace("àles","aux ").replace("àle","au ").trim();
		text =  text + "\nMatchedIntent [" + "sentId=" + sentId + ", intent=" + intent.getLocalName() + ", score=" + getScore() + "]";
		
		return text;
	}
	
	public String getText() {
		int textSize = tokens.get(tokens.size()-1).getEndPosition();
		StringBuilder sentence = new StringBuilder(textSize);
		String text = "";
		for(int i=0; i<textSize;i++) {
			sentence.append(" ");
		}
		
		for(Token token: tokens) {
			sentence.replace(token.getStartPosition(), token.getEndPosition(), token.getText());
		}
		
		return sentence.toString().replace("dle","du ").replace("àles","aux ").replace("àle","au ").trim();
		
	}
	
	public void printMatchedIntentInstance() {
		StringBuffer instance = new StringBuffer(toString()+"\n");
		
		for(LexicalUnitInstance lexicalUnitInstance: lexicalUnitInstances) {
			instance.append(lexicalUnitInstance+"\n");
		}
		
		for(FrameElementInstance frameElementInstance: frameElementInstances) {
			instance.append(frameElementInstance+"\n");
		}
		
		System.out.println(instance.toString());
	}
	
	
	


}
