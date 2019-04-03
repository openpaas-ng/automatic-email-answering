package org.linagora.intentDetection.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Configuration {
	
	private HashMap<String, String> parameters = new HashMap<String, String>();
	
	public Configuration(String path) {
		init(path);
	}
	
	private void init(String path) {
		if(parameters.isEmpty()) {
			try {

	            File f = new File(path);

	            BufferedReader b = new BufferedReader(new FileReader(f));

	            String readLine = "";
 
	            while ((readLine = b.readLine()) != null) {
	                //System.out.println(readLine);
	                String elements [] = readLine.split("=");
	                if(elements.length == 2) {
	                	parameters.put(elements[0].trim(), elements[1].trim());
	                }
	            }

	        } catch (IOException e) {
	            System.out.println("Enable to read configuration file. Service cannot start");
	            System.exit(0);
	        }

		}
	}
	
	public String getDucklingUrl() {
		return parameters.get("DUCKLING_URL");
	}
	
	public String getOntologyPath() {
		return parameters.get("ONTOLOGY_PATH");
	}
	
	public String getServiceUrl() {
		return parameters.get("SERVICE_URL");
	}
	
	public int getServicePort() {
		return Integer.parseInt(parameters.get("SERVICE_PORT"));
	}
	
	public String getTMPDirectory() {
		return parameters.get("TMP_DIRECTORY");
	}
	
	public String getDucklingDirectory() {
		return parameters.get("DUCKLING_DIRECTORY");
	}
	
	public String getTalismaneDir() {
		return parameters.get("TALISMANE_DIR");
	}
	
	public String getTalismaneHostName() {
		return parameters.get("TALISMANE_HOST_NAME");
	}
	
	public int getTalismanePortNumber() {
		return Integer.parseInt(parameters.get("TALISMANE_PORT_NUMBER"));
	}
	
	public String getTalismaneJarName() {
		return parameters.get("TALISMANE_JAR_NAME");
	}
	
	public String getTalismaneConfName() {
		return parameters.get("TALISMANE_CONF_NAME");
	}
	

	@Override
	public String toString() {
		return "Configuration [parameters=" + parameters + "]";
	}
	
	

}
