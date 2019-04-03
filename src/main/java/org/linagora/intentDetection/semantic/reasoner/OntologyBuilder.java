package org.linagora.intentDetection.semantic.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.linagora.intentDetection.Parameters;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.semantic.ontology.Answer;
import org.linagora.intentDetection.semantic.ontology.DataType;
import org.linagora.intentDetection.semantic.ontology.FrameElement;
import org.linagora.intentDetection.semantic.ontology.FrameElementType;
import org.linagora.intentDetection.semantic.ontology.Intent;
import org.linagora.intentDetection.semantic.ontology.LexicalUnit;
import org.linagora.intentDetection.semantic.ontology.Ontology;
import org.linagora.intentDetection.semantic.ontology.RelatedIntent;
import org.linagora.intentDetection.semantic.ontology.RelatedIntentCharacteristic;
import org.linagora.intentDetection.semantic.ontology.ValueType;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.UnionClass;

public class OntologyBuilder {

	private static final String ANNOTATION_PROPERTY_FRAME_ELEMENT = "frame_element";
	private static final String ANNOTATION_PROPERTY_LEXICAL_UNIT = "lexical_unit";

	private static final String ANNOTATION_PROPERTY_TYPE = "type";
	private static final String ANNOTATION_PROPERTY_VALUE = "value";
	private static final String ANNOTATION_PROPERTY_TEXT = "text";

	private static final String ANNOTATION_PROPERTY = "AnnotationProperty";

	private static final String DATA_TYPE_PROPERTY = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	private static final String CORE_INTENT = "Core_Intent";
	private static final String CORE_ANSWER = "Core_Answer";
	private static final String CORE_FRAME_ELEMENT = "Frame_Element_Class";

	public static Ontology buildOntology(String owlPath) {

		HashMap<String, Intent> intents = new HashMap<String, Intent>();
		HashMap<String, Answer> answers = new HashMap<String, Answer>();
		List<RelatedIntent> relatedIntents = new ArrayList<RelatedIntent>();
		Ontology ontology = new Ontology();
		Intent coreIntent = null;
		List<String> frameElementNames = new ArrayList<String>();

		try {

			// load owl file on a Jena model
			OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
			model.read(owlPath);

			// load annotation properties
			String annotationBaseUri = model.getNsPrefixMap().get(ANNOTATION_PROPERTY);

			Property frame_element = model.getAnnotationProperty(annotationBaseUri + ANNOTATION_PROPERTY_FRAME_ELEMENT);
			Property lexical_unit = model.getAnnotationProperty(annotationBaseUri + ANNOTATION_PROPERTY_LEXICAL_UNIT);

			Property value = model.getAnnotationProperty(annotationBaseUri + ANNOTATION_PROPERTY_VALUE);
			Property type = model.getAnnotationProperty(annotationBaseUri + ANNOTATION_PROPERTY_TYPE);
			Property text_annotation_property = model
					.getAnnotationProperty(annotationBaseUri + ANNOTATION_PROPERTY_TEXT);

			// load data type property
			Property dataTypeProperty = model.getProperty(DATA_TYPE_PROPERTY);
			System.out.println("######################################");
			System.out.println("Load properties");
			System.out.println("######################################");

			System.out.println(frame_element);
			System.out.println(lexical_unit);
			System.out.println(value);
			System.out.println(type);
			System.out.println(dataTypeProperty);
			System.out.println(text_annotation_property);

			// loop on ontology classes to build Intent Java class
			System.out.println("######################################");
			System.out.println("Loading ontology concepts");
			System.out.println("######################################");
			ExtendedIterator<OntClass> owlClasses = model.listClasses();
			OntClass CORE_ANSWER_CLS = null;
			

			while (owlClasses.hasNext()) {
				OntClass owlClass = owlClasses.next();
				
				Intent intent = new Intent();

				intent.setOntology(ontology);

				String uri = owlClass.getURI();
				String localName = owlClass.getLocalName();
				String frenchLabel = owlClass.getLabel("fr");
				String englishLabel = owlClass.getLabel("en");

				if (!owlClass.isRestriction() && !owlClass.isIntersectionClass()) {
					
					if (owlClass.getLocalName().equals(CORE_FRAME_ELEMENT)) {
						
						List<OntClass> frameElementClass = owlClass.listSubClasses(true).toList();
						for(OntClass cls: frameElementClass) {
							frameElementNames.add(cls.getLocalName());
						}
					}
					if (localName.equals(CORE_INTENT)) {
						coreIntent = intent;
						ontology.setCoreIntent(coreIntent);
					}

					if (localName.equals(CORE_ANSWER)) {
						CORE_ANSWER_CLS = owlClass;
					}
										
					intent.setUri(uri);
					intent.setLocalName(localName);
					System.out.println(intent.getLocalName());

					// load labels
					if (frenchLabel != null) {
						intent.getLabels().put(Language.french.name(), frenchLabel);
					}
					if (englishLabel != null) {
						intent.getLabels().put(Language.english.name(), englishLabel);
					}

					// load lexical units
					StmtIterator lexicalUnits = owlClass.listProperties(lexical_unit);
					while (lexicalUnits.hasNext()) {

						Statement lexicalUnit = lexicalUnits.next();
						String text = lexicalUnit.getLiteral().getString();
						String pos = model.getProperty(lexicalUnit.getLiteral().getDatatypeURI()).getLocalName();
						intent.getLexicalUnits().add(new LexicalUnit(text, DataType.valueOf(pos), intent));
					}

					// load frame elements
					StmtIterator frameElements = owlClass.listProperties(frame_element);
					while (frameElements.hasNext()) {
						Statement frameElement = frameElements.next();
						frameElement.getResource().getURI();
						String frameElementName = frameElement.getResource().getLocalName();
						// StmtIterator properties = frameElement.getResource().listProperties();

						List<Statement> propertiesValue = frameElement.getResource().listProperties(value).toList();
						// System.out.println("Properties values "+propertiesValue.size() );
						if (propertiesValue.isEmpty()) {
							try {
								FrameElement frameElementAsClass = (FrameElement) Class
										.forName("org.linagora.intentDetection.semantic.ontology." + frameElementName)
										.getConstructor().newInstance();

								frameElementAsClass.setLabel(frameElement.getResource().getLocalName());
								frameElementAsClass.setIntent(intent);
								intent.getFrameElements().add(frameElementAsClass);

							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							HashMap<String, ValueType> valueTypes = new HashMap<String, ValueType>();
							// System.out.println("je passe ici "+propertiesValue.size());
							for (Statement nextValue : propertiesValue) {

								// System.out.println("Valuetypes "+nextValue);
								String text = nextValue.getLiteral().getString();
								String valueType = model.getProperty(nextValue.getLiteral().getDatatypeURI())
										.getLocalName();
								valueTypes.put(text, ValueType.valueOf(valueType));
							}

							try {
								for (Entry<String, ValueType> individual : valueTypes.entrySet()) {
									String frameElementClassName = frameElement.getResource()
											.getProperty(dataTypeProperty).getResource().getLocalName();
									// System.out.println(frameElement.getProperty(dataTypeProperty));
									FrameElement frameElementAsClass = (FrameElement) Class.forName(
											"org.linagora.intentDetection.semantic.ontology." + frameElementClassName)
											.getConstructor().newInstance();
									frameElementAsClass.setIntent(intent);
									frameElementAsClass.setLabel(frameElement.getResource().getLocalName());
									frameElementAsClass.setValue(individual.getKey());
									frameElementAsClass.setValueType(individual.getValue());
									intent.getFrameElements().add(frameElementAsClass);
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}

					intents.put(intent.getUri(), intent);
				}
			}

			// create father and childs relations on intent object;

			owlClasses = model.listClasses();
			System.out.println("######################################");
			System.out.println("Loading subClass and superClass relations");
			System.out.println("######################################");

			while (owlClasses.hasNext()) {

				OntClass owlClass = owlClasses.next();
				// System.out.println("Processing " + owlClass.getURI());
				Intent currentIntent = intents.get(owlClass.getURI());
				if (currentIntent != null) {

					List<OntClass> superClasses = owlClass.listSuperClasses(false).toList();
					List<OntClass> subClasses = owlClass.listSubClasses(true).toList();
					// System.out.println("Father size "+superClasses.size());
					List<Intent> superIntents = new ArrayList<Intent>();
					List<Intent> subIntents = new ArrayList<Intent>();
					for (OntClass superClass : superClasses) {
						// System.out.println("Processing father");
						Intent superIntent = intents.get(superClass.getURI());
						if (superIntent != null) {
							superIntents.add(superIntent);
						}
					}
					for (OntClass subClass : subClasses) {
						// System.out.println("Processing childs");

						Intent subIntent = intents.get(subClass.getURI());
						if (subIntent != null) {
							subIntents.add(subIntent);
						}
					}
					currentIntent.setSuperIntents(superIntents);
					currentIntent.setSubIntents(subIntents);
				}

			}

			// delete a non intent class from intents hashmap
			List<String> notIntents = new ArrayList<String>();
			for (Intent intent : intents.values()) {
				if (!intent.getSuperIntents().contains(coreIntent)) {
					notIntents.add(intent.getUri());
				}
			}

			for (String key : notIntents) {
				intents.remove(key);
			}

			// load objectProperties
			System.out.println("######################################");
			System.out.println("Loading object property relations");
			System.out.println("######################################");
			ExtendedIterator<ObjectProperty> objectPropertiesIterator = model.listObjectProperties();
			while (objectPropertiesIterator.hasNext()) {
				RelatedIntent relatedIntent = new RelatedIntent();
				List<RelatedIntentCharacteristic> intentCharacteristics = new ArrayList<RelatedIntentCharacteristic>();
				ObjectProperty property = (ObjectProperty) objectPropertiesIterator.next();
				System.out.println(property.getLocalName());

				StmtIterator iter = property.listProperties(dataTypeProperty);
				while (iter.hasNext()) {
					Statement characteristic = iter.next();
					try {
						RelatedIntentCharacteristic relatedCharacteristic = RelatedIntentCharacteristic
								.valueOf(characteristic.getResource().getLocalName());
						intentCharacteristics.add(relatedCharacteristic);
					}

					catch (Exception e) {
						// e.printStackTrace();
					}
				}
				String relatedIntentName = property.getLocalName();
				String domain = "";
				String range = "";
				if (property.getDomain() != null)
					domain = property.getDomain().getURI();
				if (property.getRange() != null)
					range = property.getRange().getURI();
				// System.out.println("URI Domain: "+domain);
				// System.out.println("URI Range: "+range);
				Intent domainIntent = intents.get(domain);
				Intent rangeIntent = intents.get(range);
				relatedIntent.setDomain(domainIntent);
				relatedIntent.setRange(rangeIntent);
				relatedIntent.setLabel(relatedIntentName);
				relatedIntent.setRelatedIntentCharacteristics(intentCharacteristics);
				relatedIntents.add(relatedIntent);
				// System.out.println(relatedIntentName + " " + domainIntent.getLocalName() + "
				// " + rangeIntent.getLocalName() + " " + intentCharacteristics);

			}

			// load annotations axioms
			System.out.println("######################################");
			System.out.println("Loading annotations axioms");
			System.out.println("######################################");

			// owlAnnotationProperties are the properties used to represent
			// annotated axioms in RDF/XML.
			Set<Property> owlAnnotationProperties = new HashSet<Property>() {
				{
					add(RDF.type);
					add(OWL2.annotatedProperty);
					add(OWL2.annotatedSource);
					add(OWL2.annotatedTarget);
				}
			};

			// Find the axioms in the model.
			ResIterator axioms = model.listSubjectsWithProperty(RDF.type, OWL2.Axiom);
			while (axioms.hasNext()) {
				Resource axiom = axioms.next();
				StmtIterator stmts = axiom.listProperties();
				while (stmts.hasNext()) {
					Statement stmt = stmts.next();
					if (!owlAnnotationProperties.contains(stmt.getPredicate())) {
						Statement intent = stmt.getSubject().getProperty(OWL2.annotatedSource);
						Statement annotationType = stmt.getSubject().getProperty(OWL2.annotatedProperty);
						Statement annotationText = stmt.getSubject().getProperty(OWL2.annotatedTarget);
						String annotationValue = stmt.getObject().asResource().getLocalName();
						// System.out.println("Concerned Intent: " + intent.getResource().getURI());
						// System.out.println("Type of the annotation property:
						// "+annotationType.getResource().getLocalName());
						// System.out.println("Concerned annotation property: "+annotationText);
						// System.out.println("Is mandatory or Optional: "+ annotationValue );

						Intent intentWithAxiom = intents.get(intent.getResource().getURI());
						if (annotationType.getResource().getLocalName().equals(ANNOTATION_PROPERTY_LEXICAL_UNIT)) {
							for (LexicalUnit lexicalUnit : intentWithAxiom.getLexicalUnits()) {
								String lexicalUnitValue = annotationText.getLiteral().getString();
								String pos = model.getProperty(annotationText.getLiteral().getDatatypeURI())
										.getLocalName();
								if (lexicalUnit.getValue().equals(lexicalUnitValue)
										&& lexicalUnit.getType().toString().equals(pos)) {
									lexicalUnit.setAnnotation(DataType.valueOf(annotationValue));
									break;
									// System.out.println("Type of '" + lexicalUnitValue + "' : " + lexicalUnit);
								}
							}

						} else if (annotationType.getResource().getLocalName()
								.equals(ANNOTATION_PROPERTY_FRAME_ELEMENT)) {

							for (FrameElement frameElement : intentWithAxiom.getFrameElements()) {
								String frameElementName = annotationText.getResource().getLocalName();

								if (frameElement.getLabel().equals(frameElementName)) {
									if (frameElement.getFrameElementType().equals(FrameElementType.INDIVIDUAL)) {
										Statement individualValue = annotationText.getObject().asResource()
												.getProperty(value);
										String individualTargetValue = individualValue.getLiteral().getString();
										if (frameElement.getValue().equals(individualTargetValue)) {
											frameElement.setAnnotation(DataType.valueOf(annotationValue));
											break;
											// System.out.println("Current FE Value : " + frameElementValue);
											// System.out.println("Searched FE Value : " + individualTargetValue);
										}

									} else if (frameElement.getFrameElementType().equals(FrameElementType.CLASS)) {
										frameElement.setAnnotation(DataType.valueOf(annotationValue));
									}
								}
							}
						}

					}
				}
			}

			if (CORE_ANSWER_CLS != null) {
				// load answers concepts
				System.out.println("######################################");
				System.out.println("Loading answers concepts");
				System.out.println("######################################");

				ExtendedIterator<OntClass> answersClass = CORE_ANSWER_CLS.listSubClasses(false);
				while (answersClass.hasNext()) {
					OntClass answerClass = answersClass.next();
					if (!answerClass.isRestriction() && answerClass.listSubClasses().toList().isEmpty()) {
						System.out.println(answerClass.getLocalName());
						String uri = answerClass.getURI();
						String localName = answerClass.getLocalName();
						String frenchLabel = answerClass.getLabel("fr");
						String englishLabel = answerClass.getLabel("en");
						Answer answer = new Answer();

						answer.setUri(uri);
						answer.setLocalName(localName);

						// load labels
						if (frenchLabel != null) {
							answer.getLabels().put(Language.french.name(), frenchLabel);
						}
						if (englishLabel != null) {
							answer.getLabels().put(Language.english.name(), englishLabel);
						}

						// load lexical units
						StmtIterator lexicalUnits = answerClass.listProperties(text_annotation_property);
						while (lexicalUnits.hasNext()) {
							Statement lexicalUnit = lexicalUnits.next();
							String answerText = lexicalUnit.getLiteral().getString();
							answer.getText().add(answerText);
						}

						// loading restriction answer_for
						for (Iterator<OntClass> supersClass = answerClass.listSuperClasses(); supersClass.hasNext();) {
							OntClass superClass = supersClass.next();
							if (superClass.isRestriction()) {
								Restriction restriction = superClass.asRestriction();
								if (restriction.isAllValuesFromRestriction()) {
									loadAnswerFor(answer, restriction.asAllValuesFromRestriction().getAllValuesFrom(),
											intents);
								} else if (restriction.isSomeValuesFromRestriction()) {
									loadAnswerFor(answer, restriction.asSomeValuesFromRestriction().getSomeValuesFrom(),
											intents);
								}
							}
						}
						answers.put(answer.getUri(), answer);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// for (Intent intent : intents.values()) {
		// System.out.println("\n" + intent.getLocalName() + " FE mandatory " +
		// intent.countOfMandatoryFrameElements()
		// + " FE optional " + intent.countOfOptionalFrameElements() + " LU mandatory: "
		// + intent.countOfMandatoryLexicalUnits() + " LU optional: " +
		// intent.countOfOptionalLexicalUnits());
		//
		// for (LexicalUnit lu : intent.getLexicalUnits()) {
		// System.out.println("Lexical unit: " + lu);
		// }
		// }
//		System.out.println("************************************************");
//		System.out.println("************************************************");
//		System.out.println("************************************************");
//		System.out.println("************************************************");
//		
//		for(Answer answer: answers.values()) {
//			System.out.println("ANSWER: "+answer.getLocalName()+ " "+answer.getLabels().get(Language.french.name()));
//			for(Intent intent: answer.getSingleConcernedIntents()) {
//				System.out.println(intent.getLocalName());
//			}
//			System.out.println();
//		}
		
		for(Answer answer: answers.values()) {
			for(Intent intent: answer.getSingleConcernedIntents()) {
				intent.getAnswers().add(answer);
			}
		}
//		System.out.println("************************************************");
//		System.out.println("************************************************");
//		System.out.println("************************************************");
//		System.out.println("************************************************");
//		for(Intent intent: intents.values()) {
//			System.out.println("Intent: "+intent.getLocalName());
//			for(Answer answer: intent.getAnswers()) {
//				System.out.println(answer.getLocalName());
//			}
//			System.out.println();
//		}

		ontology.setIntents(intents);
		ontology.setRelatedIntents(relatedIntents);
		ontology.setAnswers(answers);
		ontology.setFrameElementNames(frameElementNames);

		return ontology;

	}

	private static void loadAnswerFor(Answer answer, Resource constraint, HashMap<String, Intent> intents) {
		if (constraint.canAs(UnionClass.class)) {
			UnionClass uc = constraint.as(UnionClass.class);
			List<Intent> forIntents = new ArrayList<Intent>();
			for (Iterator<? extends OntClass> i = uc.listOperands(); i.hasNext();) {
				Intent intent = intents.get(i.next().getURI());
				forIntents.add(intent);
				//intent.getAnswers().add(answer);
			}
			answer.getComposedConcernedIntents().add(forIntents);
		} else if (constraint.canAs(IntersectionClass.class)) {
			IntersectionClass uc = constraint.as(IntersectionClass.class);
			// this would be so much easier in ruby ...
			List<Intent> forIntents = new ArrayList<Intent>();
			for (Iterator<? extends OntClass> i = uc.listOperands(); i.hasNext();) {
				Intent intent = intents.get(i.next().getURI());
				forIntents.add(intent);
				//intent.getAnswers().add(answer);
			}
			answer.getComposedConcernedIntents().add(forIntents);
		} else {
			Intent intent = intents.get(constraint.getURI());
			answer.getSingleConcernedIntents().add(intent);
			//intent.getAnswers().add(answer);
		}

	}

	public static void main(String[] args) {
		// System.out.println(OntologyBuilder.buildOntology(Parameters.ONTOLOGY_PATH));
		Ontology onto = OntologyBuilder.buildOntology(Parameters.ONTOLOGY_PATH);
System.out.println(onto.getFrameElementNames());
		// Reasoner r = new Reasoner();
		// String text = "Je serai en réunion la semaine prochaine.";
		// System.out.println(r.findLexicalUnits(onto.getCoreIntent().getSubIntents().get(0),
		// text));
		// System.out.println(intent.getSuperIntents().get(0).getUri());
		// System.out.println(onto.getIntents().get(intent.getSuperIntents().get(0).getUri()).getLocalName());
	}

}
