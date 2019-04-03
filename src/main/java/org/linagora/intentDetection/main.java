package org.linagora.intentDetection;



import org.linagora.intentDetection.corenlp.Language;

public class main {
	
	
	public static void main(String[] args) {
		String text ="Bonjour,\n merci de confirmer le RDV de demain à 10h.\n Cordialement";
		
				
		
		IntentDetector intentDetector = new IntentDetector(Language.french, Parameters.DUCKLING_URL, Parameters.ONTOLOGY_PATH);
		intentDetector.detectIntents(text);
		

	}
	

}
