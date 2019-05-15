package org.linagora.intentDetection.api;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.linagora.intentDetection.IntentDetector;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.talismane.TalismaneWrapper;

public class WebServiceMain {
	
	private static URI getBaseURI(String baseURI, int port) {
        return UriBuilder.fromUri(baseURI).port(port).build();
    }
    
    private static Process runDucklingService(String duckling_dir) {
		List<String> arg = new ArrayList<String>();
		arg.add ("stack"); // command name
		arg.add ("exec");
		arg.add("duckling-example-exe");
		
		try {
			ProcessBuilder pb = new ProcessBuilder (arg);
			pb.directory(new File(duckling_dir));
			Process process = pb.start();
			return process;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
    
    public static void main(String[] args) {

    if(args.length ==1) {
    		Thread app = new Thread() {
    			public void run() {
    			
    				Configuration config = new Configuration(args[0]);
    				
    				//running Talismane service
    				String hostName = config.getTalismaneHostName();
    				int portNumber = config.getTalismanePortNumber();
    				String jarName = config.getTalismaneJarName();
    				String confName = config.getTalismaneConfName();
    				String talismaneDir = config.getTalismaneDir();
    				TalismaneWrapper.initTalismaneWrapper(hostName, portNumber, talismaneDir, jarName, confName);
    				Process talismaneProcess = TalismaneWrapper.runTalismaneServer();
    				
    				//running Duckling service
    	    		Process ducklingProcess = runDucklingService(config.getDucklingDirectory());
    	    		IntentDetector intentDetector = new IntentDetector(Language.french, config.getDucklingUrl(), config.getOntologyPath());
    	    		
    	    		ResourceConfig rc = new ResourceConfig();
    	    		rc.setApplicationName("intentionDetection");
    	    		
    	            rc.packages("org.linagora.intentDetection.api").register(MultiPartFeature.class).register(new WebService(config, intentDetector));
    	            try {
    	            	URI serviceURI = getBaseURI(config.getServiceUrl(), config.getServicePort());
    	            	HttpServer server = GrizzlyHttpServerFactory.createHttpServer(serviceURI, rc);
    	            	
    	                server.start();
    	                
    	                System.out.println(String.format("Intent detection app started with WADL available at "
    	                        + "%sapplication.wadl", serviceURI, serviceURI));
    	                while(server.isStarted()) {
    	                	sleep(10000);
    	                	
    	                }
    	               
    	            } catch (Exception e) {
    	                e.printStackTrace();
    	            }
    			}
    			
    		};
    		
    		app.setDaemon(false);
    		app.start();
    		
    		try {
				app.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    	}else {
    		System.out.println("Please set config argument. Usage: WebServiceMain configFilePath");
    		System.exit(0);
    		
    	}
        
    }
}
