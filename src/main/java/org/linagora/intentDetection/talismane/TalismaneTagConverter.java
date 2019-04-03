package org.linagora.intentDetection.talismane;

import org.linagora.intentDetection.semantic.ontology.DataType;

public class TalismaneTagConverter {
	
	public static DataType convertToUniversalTag(String posTag) {
		switch(posTag) {
		case "I" : /*Interjection*/ return DataType.INTJ;
		case "ADJ": /*adjectif*/ return DataType.ADJ;
		case "ADV": /*adverbe*/ return DataType.ADV;
		case "ADVWH": /*adverbe intérrogatif*/ return DataType.ADV;
		case "CC": /*conjonction de coordination*/ return DataType.CONJ;
		case "CS": /*conjonction de subordination*/ return DataType.SCONJ;
		case "DET":	/*déteriminant*/ return DataType.DET;
		case "DETWH": /*déterminant intérrogatif*/ return DataType.DET;
		case "ET":	/*mot étranger*/ return DataType.ET;
		case "NC":	/*nom commun*/	return DataType.NOUN;
		case "NPP":	/*nom propre*/	return DataType.PROPN;
		case "P": /*prépostion*/ return DataType.ADP;
		case "P+D":	/*préposition+déterminant*/ return DataType.ADP;
		case "P+PRO": /*préposition+pronom*/	return DataType.ADP;
		case "PONCT": /*ponctuation*/ return DataType.PUNCT;
		case "PRO": /*pronom*/ return DataType.PRON;
		case "PROREL": /*pronom rélatif*/ return DataType.PRON;
		case "PROWH": /*pronom intérrogatif*/ return DataType.PRON;	
		case "V":	/*verbe indicatif*/ return DataType.AUX;
		case "VIMP": /*verbe impératif*/ return DataType.VERB;
		case "VINF": /*verbe infinitif*/ return DataType.VERB;
		case "VPP": /*verbe participe passé*/ return DataType.VERB;
		case "VPR": /*verbe participe présent*/ return DataType.VERB;
		case "VS": /*verbe subjonctif*/ return DataType.VERB;
		case "CLO":	/*clitique objet*/ return DataType.PRON;
		case "CLR":	/*clitique réflechi*/ return DataType.PRON;
		case "CLS":	/*clitique sujet*/	return DataType.PRON;
		case "PREF": /*préfixe*/ return DataType.ET;
		default: return DataType.ET;
		}
	}

}
