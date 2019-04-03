package org.linagora.intentDetection.api;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.linagora.intentDetection.IntentDetector;
import org.linagora.intentDetection.Parameters;
import org.linagora.intentDetection.corenlp.EmailComposer;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.semantic.FrameElementInstance;
import org.linagora.intentDetection.semantic.LexicalUnitInstance;
import org.linagora.intentDetection.semantic.MatchedIntent;
import org.linagora.intentDetection.semantic.ontology.Answer;
import org.linagora.intentDetection.semantic.reasoner.Reasoner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;




@Path("detectintent")
public class WebService {
	Configuration config = null;
	IntentDetector intentDetector = null;
	private static final int LIMIT = 3;
	private static Random RANDOM = new Random();
	Logger logger = null;
    public WebService() { }
    
    public WebService(Configuration config) { 
    	
    	this.config = config;
    	logger = new Logger(new File(config.getTMPDirectory() + "/logging.csv"));
    }
    
 public WebService(Configuration config, IntentDetector intentDetector) { 
    	
    	this.config = config;
    	this.intentDetector = intentDetector;
    	logger = new Logger(new File(config.getTMPDirectory() + "/logging.csv"));
    }
 
 @GET
 @Path("help")
 public Response getHelp() {
	 String message = "How to use the API:\n"
			 +"Reload the ontology: curl -X GET http://localhost:9991/rest/detectintent/reloadontology\n"
			 +"Parse json email: curl -X POST http://localhost:9991/rest/detectintent/parsejson -F \"file=@path/to/json/file.json\"\n"
			 +"Parse text email: curl -X POST http://localhost:9991/rest/detectintent/parsetext -F \"file=@path/to/txt/file.text\"\n"
			 +"Parse eml email: curl -X POST http://localhost:9991/rest/detectintent/parseeml -F \"file=@path/to/txt/file.eml\"\n"
			 +"GET log file: curl -X GET http://localhost:9991/rest/detectintent/log\n";
				
	 return Response
 			.status(Status.OK)
 			.entity("[{\"HELP\": \""+ message +"\"}]\n")
 			.build();
 }
 
 @GET
 @Path("log")
 public Response getLog() {
	 
	return Response
 			.status(Status.OK)
 			.entity(logger.getLogFile())
 			.build();
 }
 
 @GET
 @Path("reloadontology")
 public Response reloadOntology() {
	 try {
		 this.intentDetector.reloadOntology(config.getOntologyPath());
	 }catch(Exception e) {
		 return Response
	    			.status(Status.BAD_REQUEST)
	    			.entity("[{\"error\": \"Enable to reload the ontology.\"}]\n")
	    			.build();
	 }
	 return Response
 			.status(Status.OK)
 			.entity("[{\"success\": \"Ontology reloaded.\"}]\n")
 			.build();
	
 }
    
   //https://stackoverflow.com/questions/30653012/multipart-form-data-no-injection-source-found-for-a-parameter-of-type-public-ja?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
   //curl -X POST http://localhost:9991/rest/detectintent/parsetext -F "file=@/home/zsellami/email.txt"
   @POST
   @Path("parsetext")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIntentsForEmail(@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
    	System.out.println("Calling intent detection service...");
    	
    	String fileName = fileDetail.getFileName();
    	
    	if(!fileName.endsWith(".txt")) {
    		return Response
        			.status(Status.BAD_REQUEST)
        			.entity("File must be txt format")
        			.build();
    	}
    	
    	// save it
    	String uuid = UUID.randomUUID().toString();
    	Long time = System.currentTimeMillis();
    	String randomFileName = "email_" + uuid + "_" + time + ".txt";
    	String uploadedFileLocation = config.getTMPDirectory() + "/" + randomFileName;
        writeToFile(fileInputStream, uploadedFileLocation);
        String emailContent = readEmail(uploadedFileLocation);
                
        try {
        	Files.delete(Paths.get(uploadedFileLocation));
        }catch(Exception e) {
        	System.out.println("File " + uploadedFileLocation + "not deleted.");
        }
        
        if(emailContent.trim().isEmpty()) {
			return Response
	    			.status(Status.OK)
	    			.entity("[{\"empty\": \"The email file is empty.\"}]\n")
	    			.build();
		}
                
        HashMap<Integer,List<MatchedIntent>> matchedIntents = intentDetector.detectIntents(emailContent);
        String recipient = "";
        String signature = "";
		List<IntentResult> intentResults = toIntentResult(matchedIntents, LIMIT);
		List<ProposedAnswer> answerResults = toAnswerResult("", "", matchedIntents, LIMIT);
		
		if(intentResults.isEmpty()) {
			return Response
	    			.status(Status.OK)
	    			.entity("[{\"empty\": \"Meeting intents not found.\"}]\n")
	    			.build();
		}else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	    	try {
	    		Result result = new Result(intentResults, answerResults);
	    		String json = mapper.writeValueAsString(result);
				return Response
		    			.status(Status.OK)
		    			.entity(json+"\n")
		    			.build();

			}catch(Exception e) {
				//e.printStackTrace();
				return Response
		    			.status(Status.INTERNAL_SERVER_ERROR)
		    			.entity("[]\n")
		    			.build();
				
			}
		}

        
    	
    }
   
 //https://stackoverflow.com/questions/30653012/multipart-form-data-no-injection-source-found-for-a-parameter-of-type-public-ja?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
   //curl -X POST http://localhost:9991/rest/detectintent/parsetext -F "file=@/home/zsellami/email.txt"
   @POST
   @Path("parsejson")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIntentsForJsonEmail(@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
    	System.out.println("Calling intent detection service...");
    	logger.logSmartReplyAction();
    	String fileName = fileDetail.getFileName();
        	
    	if(!fileName.endsWith(".json")) {
    		
    		return Response
        			.status(Status.OK)
        			.entity("[]")
        			.build();
    	}
    	
    	// save it
    	String uuid = UUID.randomUUID().toString();
    	Long time = System.currentTimeMillis();
    	String randomFileName = "email_" + uuid + "_" + time + ".json";
    	String uploadedFileLocation = config.getTMPDirectory() + "/" + randomFileName;
        writeToFile(fileInputStream, uploadedFileLocation);
        Email email = readJsonEmail(uploadedFileLocation);
        if(email == null) {
        	System.out.println("Error when reading json file. Email is null.");
        	return Response
	    			.status(Status.OK)
	    			.entity("[]")
	    			.build();
        }else if(email.getFrom().containsKey(email.getUserAddress())){
        	System.out.println("Sender and Recipient are the same. No intent detection");
        	return Response
	    			.status(Status.OK)
	    			.entity("[]")
	    			.build();
        }
        String emailContent = email.getSubject() + " . \n" + email.getBody();
        //String emailContent = email.getBody();
        //writeLog(emailContent, uploadedFileLocation+".log");
                        
        try {
        	Files.delete(Paths.get(uploadedFileLocation));
        }catch(Exception e) {
        	System.out.println("File " + uploadedFileLocation + "not deleted.");
        }
        
        if(email.getBody().equals("") || email.getBody() == null) {
        	System.out.println("No text in the email body.");
        }
        
        if(email.getSubject().equals("") || email.getSubject() == null) {
        	System.out.println("No text in the email subject.");
        }
        
        if(emailContent.trim().isEmpty()) {
        	System.out.println("No text in the email to analyse");
			return Response
	    			.status(Status.OK)
	    			.entity("[]")
	    			.build();
		}
        //System.out.println("Email content:\n"+emailContent);
        
        Reasoner reasoner = intentDetector.buildReasoner(emailContent);
        
        HashMap<Integer,List<MatchedIntent>> matchedIntents = reasoner.intentsMatching();
        //HashMap<Integer,List<MatchedIntent>> matchedIntents = intentDetector.detectIntents(emailContent);
        //System.out.println("Entities: " + reasoner.getEntities());
        String recipient = email.getFrom().values().iterator().next();
        String recipientEmail = email.getFrom().keySet().iterator().next();
        String sender = email.getUserName();
        String senderEmail = "";
        for(Entry<String, String> entry: email.getRecipients().entrySet()) {
        	if(entry.getValue().equals(sender)) {
        		senderEmail = entry.getKey();
        		break;
        	}
        }
        if(senderEmail == "") {
        	senderEmail = sender;
        }
				
		List<IntentResult> intentResults = toIntentResult(matchedIntents, LIMIT);
		List<ProposedAnswer> answerResults = toAnswerResult(recipient, recipientEmail, sender, senderEmail, matchedIntents, reasoner.getEntities(), LIMIT);
		
//		if(intentResults.isEmpty()) {
//			return Response
//	    			.status(Status.OK)
//	    			.entity("[{\"empty\": \"Meeting intents not found.\"}]\n")
//	    			.build();
//		}else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	    	try {
	    		System.out.println(answerResults.size() + " Detected intent in the email.");
	    		Result result = new Result(intentResults, answerResults);
	    		String json = mapper.writeValueAsString(answerResults);
	    		//String json = mapper.writeValueAsString(result);
	    		return Response
		    			.status(Status.OK)
		    			.entity(json)
		    			.build();

			}catch(Exception e) {
				//e.printStackTrace();
				System.out.println("No intents in the email.");
				return Response
		    			.status(Status.OK)
		    			.entity("[]")
		    			.build();
				
			}
		}

      
   
   private Email readJsonEmail(String uploadedFileLocation) {
	
	try {
		String jsonAsString = readEmail(uploadedFileLocation);
		JSONObject json = new JSONObject(jsonAsString);
		Email email = new Email();
		
		String subject = "";
		String textBody = "";
		
		try {
			textBody = json.getString("textBody");
		}catch(Exception e) {
			System.out.println("No body on email.");
			textBody = "";
		}
		
		try {
			subject = json.getJSONArray("subject").getString(0);
		}catch (Exception e){
			System.out.println("No Subject on email.");
			subject = "";
		}
		
		JSONObject recipients = json.getJSONObject("recipients");
		Iterator<Object> to = (Iterator<Object>)recipients.getJSONArray("to").iterator();
		Iterator<Object> cc = (Iterator<Object>)recipients.getJSONArray("cc").iterator();
		Iterator<Object> bcc = (Iterator<Object>)recipients.getJSONArray("bcc").iterator();
		JSONObject from = json.getJSONArray("from").getJSONObject(0);
		String userAddress = json.getJSONArray("users").getString(0);
		
		//email.setMessageId(messageId);
		email.setSubject(subject);
		email.setBody(textBody);
		
		if(from.getString("name") == "" || from.getString("name") == null) {
			email.getFrom().put(from.getString("address"), from.getString("address"));
		}else {
			email.getFrom().put(from.getString("address"), from.getString("name"));
		}
		
		
		while(to.hasNext()) {
			JSONObject recipient = (JSONObject)to.next();
			String name = recipient.getString("name");
			String address = recipient.getString("address");
			if(name == "" || name == null) {
				name = address;
			}
			email.getRecipients().put(address, name);
		}
		
		while(cc.hasNext()) {
			JSONObject recipient = (JSONObject)cc.next();
			String name = recipient.getString("name");
			String address = recipient.getString("address");
			if(name == "" || name == null) {
				name = address;
			}
			email.getRecipients().put(address, name);
		}
		
		while(bcc.hasNext()) {
			JSONObject recipient = (JSONObject)bcc.next();
			String name = recipient.getString("name");
			String address = recipient.getString("address");
			if(name == "" || name == null) {
				name = address;
			}
			email.getRecipients().put(address, name);
		}
		if(email.getRecipients().get(userAddress) != null) {
			email.setUserName(email.getRecipients().get(userAddress));
		}else {
			email.setUserName(userAddress);
		}
		email.setUserAddress(userAddress);
		
		return email;
	}catch(Exception e) {
		e.printStackTrace();
		return null;
	}
		
}
   
   @POST
   @Path("parseeml")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIntentsForEmlEmail(@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
    	System.out.println("Calling intent detection service...");
    	
    	String fileName = fileDetail.getFileName();
    	
    	if(!fileName.endsWith(".eml")) {
    		return Response
        			.status(Status.BAD_REQUEST)
        			.entity("File must be eml format")
        			.build();
    	}
    	
    	// save it
    	String uuid = UUID.randomUUID().toString();
    	Long time = System.currentTimeMillis();
    	String randomFileName = "email_" + uuid + "_" + time + ".eml";
    	String uploadedFileLocation = config.getTMPDirectory() + "/" + randomFileName;
        writeToFile(fileInputStream, uploadedFileLocation);
        Email email = EmlReader.getEmail(uploadedFileLocation);
        if(email == null) {
        	return Response
	    			.status(Status.INTERNAL_SERVER_ERROR)
	    			.entity("[{\"error\": \"Error when reading the email file.\"}]")
	    			.build();
        }
        String emailContent = email.getSubject() + " . \n" + email.getBody();
                
        try {
        	Files.delete(Paths.get(uploadedFileLocation));
        }catch(Exception e) {
        	System.out.println("File " + uploadedFileLocation + "not deleted.");
        }
        
        if(emailContent.trim().isEmpty()) {
			return Response
	    			.status(Status.OK)
	    			.entity("[]")
	    			.build();
		}
                
        HashMap<Integer,List<MatchedIntent>> matchedIntents = intentDetector.detectIntents(emailContent);
        
        String recipient = email.getFrom().values().iterator().next();
        String recipientEmail = email.getFrom().keySet().iterator().next();
        String sender = email.getUserName();
        String senderEmail = "";
        for(Entry<String, String> entry: email.getRecipients().entrySet()) {
        	if(entry.getValue().equals(sender)) {
        		senderEmail = entry.getKey();
        		break;
        	}
        }
        if(senderEmail == "") {
        	senderEmail = sender;
        }
				
		List<IntentResult> intentResults = toIntentResult(matchedIntents, LIMIT);
		List<ProposedAnswer> answerResults = toAnswerResult(recipient, recipientEmail, sender, senderEmail, matchedIntents, LIMIT);
		
//		if(intentResults.isEmpty()) {
//			return Response
//	    			.status(Status.OK)
//	    			.entity("[{\"empty\": \"Meeting intents not found.\"}]\n")
//	    			.build();
//		}else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	    	try {
	    		Result result = new Result(intentResults, answerResults);
	    		String json = mapper.writeValueAsString(answerResults);
				return Response
		    			.status(Status.OK)
		    			.entity(json)
		    			.build();

			}catch(Exception e) {
				//e.printStackTrace();
				return Response
		    			.status(Status.INTERNAL_SERVER_ERROR)
		    			.entity("[{\"error\": Error when converting results to json format.\"\"}]")
		    			.build();
				
			}
		}

@POST
   @Path("emailtype")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmailType(@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
    	System.out.println("Calling email identification type...");
    	
    	String fileName = fileDetail.getFileName();
    	
    	if(!fileName.endsWith(".txt")) {
    		return Response
        			.status(Status.BAD_REQUEST)
        			.entity("File must be txt format")
        			.build();
    	}
    	
    	// save it
    	String uuid = UUID.randomUUID().toString();
    	Long time = System.currentTimeMillis();
    	String randomFileName = "email_" + uuid + "_" + time + ".txt";
    	String uploadedFileLocation = config.getTMPDirectory() + "/" + randomFileName;
        writeToFile(fileInputStream, uploadedFileLocation);
        String emailContent = readEmail(uploadedFileLocation);
                
        try {
        	Files.delete(Paths.get(uploadedFileLocation));
        }catch(Exception e) {
        	System.out.println("File " + uploadedFileLocation + "not deleted.");
        }
        
        if(emailContent.trim().isEmpty()) {
			return Response
	    			.status(Status.OK)
	    			.entity("[{\"emailtype\": \"other\"}]\n")
	    			.build();
		}
                
        HashMap<Integer,List<MatchedIntent>> matchedIntents = intentDetector.detectIntents(emailContent);
				
		List<IntentResult> intentResults = toIntentResult(matchedIntents, LIMIT);
		
		if(intentResults.isEmpty()) {
			return Response
	    			.status(Status.OK)
	    			.entity("[{\"emailtype\": \"other\"}]\n")
	    			.build();
		}else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	    	try {
//	    		Result result = new Result();
//	    		result.setIntents(intentResults);
//				String json = mapper.writeValueAsString(result);
				return Response
		    			.status(Status.OK)
		    			.entity("[{\"emailtype\": \"meeting\"}]\n")
		    			.build();

			}catch(Exception e) {
				//e.printStackTrace();
				return Response
		    			.status(Status.INTERNAL_SERVER_ERROR)
		    			.entity("[{\"error\": Error when converting "+intentResults.size() +" results\"\"}]\n")
		    			.build();
				
			}
		}

        
    	
    }
    
    private String readEmail(String path) {
		FileInputStream inputStream = null;
		 String content = null;
		try {
			inputStream = new FileInputStream(path.toString());
			content = IOUtils.toString(inputStream, Parameters.UTF_8_ENCODING);
		} catch(Exception e) {
		    return "";
		}
		
		return content;
	}
    
    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
        String uploadedFileLocation) {

        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
    
    //save Log
    private void writeLog(String content, String fileLocation) {
    	try {
    		FileUtils.writeStringToFile(new File(fileLocation), content, Charset.forName("UTF-8"));
    	}catch(Exception e) {
    		
    	}
    	
    }
    private List<ProposedAnswer> toAnswerResult(String recipient, String sender, HashMap<Integer,List<MatchedIntent>> matchedIntents, int limit){
    	Set<Answer> answers = new HashSet<Answer>();
    	HashMap<Answer, Double> answerScores = new HashMap<Answer, Double>();
    	HashMap<Answer, List<MatchedIntent>> answerMatchedIntents = new HashMap<Answer, List<MatchedIntent>>();
    	List<ProposedAnswer> proposedAnswers = new ArrayList<ProposedAnswer>();
    	HashMap<Answer, String> originalTextMap = new HashMap<Answer, String>();
    	for(Integer sentId: matchedIntents.keySet()) {

			int count = 0;
    		for(MatchedIntent matchedIntent: matchedIntents.get(sentId)) {
    			if(count < limit) {
    				//System.out.println(matchedIntent);
    				answers.addAll(matchedIntent.getIntent().getAnswers());
    				for(Answer answer: matchedIntent.getIntent().getAnswers()) {
    					Double score = answerScores.get(answer);
    					if(score == null) {
    						answerScores.put(answer, matchedIntent.getScore());
    						originalTextMap.put(answer, matchedIntent.getText());
    					}else {
    						Double newScore = new Double(score+matchedIntent.getScore());
    						answerScores.remove(answer);
    						answerScores.put(answer, newScore);
    						String text = originalTextMap.get(answer);
    						originalTextMap.remove(answer);
    						originalTextMap.put(answer, matchedIntent.getText() + "\n" + text);
    					}
    					List<MatchedIntent> matchedIntentsForAnswer = answerMatchedIntents.get(answer);
    					if(matchedIntentsForAnswer == null) {
    						matchedIntentsForAnswer = new ArrayList<MatchedIntent>();
    						answerMatchedIntents.put(answer, matchedIntentsForAnswer);
    					}
    					matchedIntentsForAnswer.add(matchedIntent);
    					
    				}
    				count++;
    				break;
    			}
				
				
			}
			
		}
    	for(Answer answer: answers) {
    		System.out.println("Original text: " + originalTextMap.get(answer));
    		ProposedAnswer proposedAnswer = new ProposedAnswer();
    		//proposedAnswer.setId(answer.getLocalName());
    		proposedAnswer.setScore(answerScores.get(answer));
    		proposedAnswer.setLabel(answer.getLabels().get(Language.french.name()));
    		String sentence = answer.getText().get(0);
    		for(MatchedIntent mi: answerMatchedIntents.get(answer)) {
    			sentence = replaceTagsWithInstance(sentence, mi);
    		}
    		sentence = sentence.replaceAll("\\[[a-zA-Z]+\\]", "");
    		
    		List<String> sentences = new ArrayList<String>();
    		sentences.add(sentence);
    		proposedAnswer.setEmail(EmailComposer.composeEmail(recipient, sender, sentences));
    		proposedAnswers.add(proposedAnswer);
    	}
    	
    	Collections.sort(proposedAnswers);
    	
    	List<ProposedAnswer> filtredProposedAnswers = new ArrayList<ProposedAnswer>();
    	int count = 0;
    	for(ProposedAnswer answer: proposedAnswers) {
    		if(count < limit) {
    			filtredProposedAnswers.add(answer);
    		}
    		count++;
    	}
    	
    	return filtredProposedAnswers;
    }
    
    private List<ProposedAnswer> toAnswerResult(String recipient, String recipientEmail, String sender, String senderEmail, HashMap<Integer,List<MatchedIntent>> matchedIntents, int limit){
    	Set<Answer> answers = new HashSet<Answer>();
    	HashMap<Answer, Double> answerScores = new HashMap<Answer, Double>();
    	HashMap<Answer, List<MatchedIntent>> answerMatchedIntents = new HashMap<Answer, List<MatchedIntent>>();
    	List<ProposedAnswer> proposedAnswers = new ArrayList<ProposedAnswer>();
    	StringBuffer originalTextBuffer = new StringBuffer();
    	for(Integer sentId: matchedIntents.keySet()) {
			int count = 0;
    		for(MatchedIntent matchedIntent: matchedIntents.get(sentId)) {
    			if(count < limit) {
    				originalTextBuffer.append(matchedIntent.getText()+"\n");
    				//System.out.println(matchedIntent);
    				answers.addAll(matchedIntent.getIntent().getAnswers());
    				for(Answer answer: matchedIntent.getIntent().getAnswers()) {
    					Double score = answerScores.get(answer);
    					if(score == null) {
    						answerScores.put(answer, matchedIntent.getScore());
    					}else {
    						Double newScore = new Double(score+matchedIntent.getScore());
    						answerScores.remove(answer);
    						answerScores.put(answer, newScore);    						 						
    					}
    					List<MatchedIntent> matchedIntentsForAnswer = answerMatchedIntents.get(answer);
    					if(matchedIntentsForAnswer == null) {
    						matchedIntentsForAnswer = new ArrayList<MatchedIntent>();
    						answerMatchedIntents.put(answer, matchedIntentsForAnswer);
    					}
    					matchedIntentsForAnswer.add(matchedIntent);
    					
    				}
    				count++;
    				break;
    			}
				
				
			}
			
		}
    	for(Answer answer: answers) {
    		ProposedAnswer proposedAnswer = new ProposedAnswer();
    		//System.out.println("Original text: " + originalTextBuffer);
    		//proposedAnswer.setId(answer.getLocalName());
    		proposedAnswer.setScore(answerScores.get(answer));
    		proposedAnswer.setLabel(answer.getLabels().get(Language.french.name()));
    		int canevaSize = answer.getText().size();
    		String sentence = answer.getText().get(RANDOM.nextInt(canevaSize));
    		String proposedStringAnswer = null;
    		try {
    			proposedStringAnswer = EmailComposer.composeAnswer(sentence, originalTextBuffer.toString(), answerMatchedIntents.get(answer).get(0).getIntent().getOntology(), answerMatchedIntents.get(answer));
    			System.out.println("Poposed answer: " + proposedStringAnswer);
        		
    		}catch(Exception e) {
    			e.printStackTrace();
    			proposedStringAnswer = sentence;
    		}
    		
    		List<String> sentences = new ArrayList<String>();
    		sentences.add(proposedStringAnswer);
    		proposedAnswer.setEmail(EmailComposer.composeEmail(recipient, recipientEmail, sender, senderEmail, sentences));
    		proposedAnswers.add(proposedAnswer);
    	}
    	
    	Collections.sort(proposedAnswers);
    	
    	List<ProposedAnswer> filtredProposedAnswers = new ArrayList<ProposedAnswer>();
    	int count = 0;
    	for(ProposedAnswer answer: proposedAnswers) {
    		if(count < limit) {
    			filtredProposedAnswers.add(answer);
    		}
    		count++;
    	}
    	
    	return filtredProposedAnswers;
    }
    
    private List<ProposedAnswer> toAnswerResult(String recipient, String recipientEmail, String sender, String senderEmail, HashMap<Integer,List<MatchedIntent>> matchedIntents, List<Entity> entities, int limit){
    	Set<Answer> answers = new HashSet<Answer>();
    	HashMap<Integer, List<Entity>> entitiesBySentence = new HashMap<Integer, List<Entity>>();
    	for(Entity entity: entities) {
    		List<Entity> entitiesBySent = entitiesBySentence.get(entity.getSentId());
    		if(entitiesBySent == null) {
    			entitiesBySent = new ArrayList<Entity>();
    			entitiesBySentence.put(entity.getSentId(), entitiesBySent);
    		}
    		entitiesBySent.add(entity);
    	}
    	HashMap<Answer, Double> answerScores = new HashMap<Answer, Double>();
    	HashMap<Answer, List<MatchedIntent>> answerMatchedIntents = new HashMap<Answer, List<MatchedIntent>>();
    	List<ProposedAnswer> proposedAnswers = new ArrayList<ProposedAnswer>();
    	StringBuffer originalTextBuffer = new StringBuffer();
    	for(Integer sentId: matchedIntents.keySet()) {
			int count = 0;
    		for(MatchedIntent matchedIntent: matchedIntents.get(sentId)) {
    			if(count < limit) {
    				originalTextBuffer.append(matchedIntent.getText()+"\n");
    				//System.out.println(matchedIntent);
    				answers.addAll(matchedIntent.getIntent().getAnswers());
    				for(Answer answer: matchedIntent.getIntent().getAnswers()) {
    					Double score = answerScores.get(answer);
    					if(score == null) {
    						answerScores.put(answer, matchedIntent.getScore());
    					}else {
    						Double newScore = new Double(score+matchedIntent.getScore());
    						answerScores.remove(answer);
    						answerScores.put(answer, newScore);    						 						
    					}
    					List<MatchedIntent> matchedIntentsForAnswer = answerMatchedIntents.get(answer);
    					if(matchedIntentsForAnswer == null) {
    						matchedIntentsForAnswer = new ArrayList<MatchedIntent>();
    						answerMatchedIntents.put(answer, matchedIntentsForAnswer);
    					}
    					matchedIntentsForAnswer.add(matchedIntent);
    					
    				}
    				count++;
    				break;
    			}
				
				
			}
			
		}
    	for(Answer answer: answers) {
    		ProposedAnswer proposedAnswer = new ProposedAnswer();
    		//System.out.println("Original text: " + originalTextBuffer);
    		//proposedAnswer.setId(answer.getLocalName());
    		proposedAnswer.setScore(answerScores.get(answer));
    		proposedAnswer.setLabel(answer.getLabels().get(Language.french.name()));
    		int canevaSize = answer.getText().size();
    		String sentence = answer.getText().get(RANDOM.nextInt(canevaSize));
    		String proposedStringAnswer = null;
    		try {
    			List<Entity> entitiesInSentence = entitiesBySentence.get(answerMatchedIntents.get(answer).get(0).getSentId());
    			if(entitiesInSentence == null) {
    				entitiesInSentence = new ArrayList<Entity>();
    			}
    			proposedStringAnswer = EmailComposer.composeAnswer(sentence, originalTextBuffer.toString(), answerMatchedIntents.get(answer).get(0).getIntent().getOntology(), answerMatchedIntents.get(answer), entitiesInSentence);
    			System.out.println("Poposed answer: " + proposedStringAnswer);
        		
    		}catch(Exception e) {
    			e.printStackTrace();
    			proposedStringAnswer = sentence;
    		}
    		
    		List<String> sentences = new ArrayList<String>();
    		sentences.add(proposedStringAnswer);
    		proposedAnswer.setEmail(EmailComposer.composeEmail(recipient, recipientEmail, sender, senderEmail, sentences));
    		proposedAnswers.add(proposedAnswer);
    	}
    	
    	Collections.sort(proposedAnswers);
    	
    	List<ProposedAnswer> filtredProposedAnswers = new ArrayList<ProposedAnswer>();
    	int count = 0;
    	for(ProposedAnswer answer: proposedAnswers) {
    		if(count < limit) {
    			filtredProposedAnswers.add(answer);
    		}
    		count++;
    	}
    	
    	return filtredProposedAnswers;
    }
    
    private String replaceTagsWithInstance(String text, MatchedIntent matchedIntent) {
    	//System.out.println(text);
    	for(FrameElementInstance instance: matchedIntent.getFrameElementInstances()) {
    		//System.out.println(instance);
    		
    		String tag = "[" + instance.getFrameElement().getClass().getSimpleName() + "]";
    		if(text.contains(tag)) {
    			text = text.replace(tag, instance.getInstanceText());
    		}
    	
    	}
    	return text;
    }
    
    private List<IntentResult> toIntentResult(HashMap<Integer,List<MatchedIntent>> matchedIntents, int limit) {
    	
    	List<IntentResult> results = new ArrayList<IntentResult>();
    	for(Integer sentId: matchedIntents.keySet()) {
    		List<PredictedIntent> predictedIntents = new ArrayList<PredictedIntent>();
			String text = null;
			int count = 0;
    		for(MatchedIntent matchedIntent: matchedIntents.get(sentId)) {
    			if(count < limit) {
    				//System.out.println(matchedIntent);
    				//matchedIntent.getScore();
    				//matchedIntent.printMatchedIntentInstance();
    				predictedIntents.add(new PredictedIntent(matchedIntent.getIntent().getLocalName(), matchedIntent.getScore(), getHotWords(matchedIntent), getAnswers(matchedIntent)));
    				if(text == null) {
    					text = matchedIntent.getText();
    				}
    				count++;
    			}
				
				
			}
			IntentResult intentResult = new IntentResult();
			intentResult.setPredictedIntents(predictedIntents);
			intentResult.setText(text);
			results.add(intentResult);
		}
    	
    	return results;
    	
    	
    }
    private String toString(List<Token> tokens) {
    	StringBuffer string = new StringBuffer();
    	for(Token token: tokens) {
    		string.append(token.getText()+" ");
    	}
    	return string.toString().trim();
    }
    private List<String> getHotWords(MatchedIntent matchedIntent){
    	List<String> hotWords = new ArrayList<String>();
    	for(LexicalUnitInstance instance: matchedIntent.getLexicalUnitInstances()) {
    		hotWords.add(toString(instance.getTokens()));
    	}
    	for(FrameElementInstance instance: matchedIntent.getFrameElementInstances()) {
    		hotWords.add(toString(instance.getTokens()));
    	}
    	return new ArrayList<String>(new HashSet<String>(hotWords));
    }
    
    private List<String> getAnswers(MatchedIntent matchedIntent){
    	List<String> answers = new ArrayList<String>();
    	for(Answer answer: matchedIntent.getIntent().getAnswers()) {
    		answers.add(answer.getLabels().get(Language.french.name()));
    	}
    	return answers;
    }
}
