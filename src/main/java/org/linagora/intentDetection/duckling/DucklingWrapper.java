package org.linagora.intentDetection.duckling;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.*;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linagora.intentDetection.corenlp.NLPData;
import org.linagora.intentDetection.Parameters;
import org.linagora.intentDetection.corenlp.CoreNLPWrapper;
import org.linagora.intentDetection.corenlp.Language;
import org.linagora.intentDetection.corenlp.Token;
import org.linagora.intentDetection.entities.Duration;
import org.linagora.intentDetection.entities.Email;
import org.linagora.intentDetection.entities.Entity;
import org.linagora.intentDetection.entities.IntervalTime;
import org.linagora.intentDetection.entities.PhoneNumber;
import org.linagora.intentDetection.entities.Time;
import org.linagora.intentDetection.entities.TimeUnit;
import org.linagora.intentDetection.entities.Url;
import org.linagora.intentDetection.semantic.EntityResolver;


public class DucklingWrapper {
	
	private String URL = null;
	
	
	public DucklingWrapper(String URL) {
		this.URL = URL;
		
	}
	/***
	 * Running duckling service using POST call
	 * You need to install and run facebook duckling.
	 * Visit https://github.com/facebook/duckling for more details.
	 * @param locale
	 * @param text
	 * @return
	 */
	public List<Entity> callDucklingService(String text, DucklingLocale locale) {
		List<Entity> entities = new ArrayList<Entity>();
		
		try {
			HttpURLConnection connection = (HttpURLConnection) (new URL(URL)).openConnection();
			 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
						
			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.writeBytes("text=" + URLEncoder.encode(text) + "&locale=" + locale.name());
			writer.flush();
			writer.close();
	       
			
			BufferedReader reader = new BufferedReader (new InputStreamReader(connection.getInputStream()));
			StringBuffer jsonAsString = new StringBuffer();
			for (String line; (line = reader.readLine()) != null;) {
				jsonAsString.append(line);
			}
			System.out.println (jsonAsString);
			
			reader.close();
			entities = jsonResultToEntities(jsonAsString.toString(), locale);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return entities;
		
	}
	
	/***
	 * Running duckling service using POST call
	 * You need to install and run facebook duckling.
	 * Visit https://github.com/facebook/duckling for more details.
	 * @param locale
	 * @param text
	 * @param tokens
	 * @return
	 */
	public List<Entity> callDucklingService(String text, DucklingLocale locale, List<Token> tokens) {
		List<Entity> entities = new ArrayList<Entity>();
		
		try {
			HttpURLConnection connection = (HttpURLConnection) (new URL(URL)).openConnection();
			 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
						
			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.writeBytes("text=" + URLEncoder.encode(text) + "&locale=" + locale.name());
			writer.flush();
			writer.close();
	       
			
			BufferedReader reader = new BufferedReader (new InputStreamReader(connection.getInputStream()));
			StringBuffer jsonAsString = new StringBuffer();
			for (String line; (line = reader.readLine()) != null;) {
				jsonAsString.append(line);
			}
			System.out.println (jsonAsString);
			
			reader.close();
			entities = jsonResultToEntities(jsonAsString.toString(), locale, tokens);
			
			
		}catch(Exception e){
			e.printStackTrace();
			//System.exit(0);
		}
		
		return entities;
		
	}
	
	/**
	 * Duckling facebook list of entities types:
	 *  ['amount-of-money', 'distance', 'duration', 'email', 'numeral', 
	 * 'ordinal', 'phone-number', 'quantity', 'temperature', 'time', 'url', 'volume']
	 * Only duration, email, phone-number, time and url are processed.
	 * @param jsonAsString
	 * @return
	 */
	public List<Entity> jsonResultToEntities(String jsonAsString, DucklingLocale locale) {
		List<Entity> entities = new ArrayList<Entity>();
		JSONArray json = new JSONArray(jsonAsString);
		
		for(int i = 0; i < json.length(); i++) {
			JSONObject obj = json.getJSONObject(i);
			JSONObject value = obj.getJSONObject("value");
			
			String text = obj.getString("body");
			String dim = obj.getString("dim");
			int startPosition = obj.getInt("start");
			int endPosition = obj.getInt("end");
			
			//String formalizedValue = value.getString("value");
			
			//System.out.println(text+", "+startPosition+", "+endPosition+", "+dim+", "+formalizedValue);
			Entity entity = null;
			switch (dim) {
			
			case "duration": {
				String grain = value.getString("unit");
				entity = new Duration(TimeUnit.valueOf(grain));
				break;
			}
			case "email": {
				entity = new Email();
				break;
			}
			case "phone-number": {
				entity = new PhoneNumber();
				break;
			}
			case "url": {
				entity = new Url();
				break;
			}
			case "time": {
				String type = value.getString("type");
				switch (type){
				case "interval":{
					if(locale.equals(DucklingLocale.fr_FR)) {
						entity = intervalTimeResolverFrench(text, value.getJSONArray("values"));
						break;
					}else {
						entity = intervalTimeResolverXX(text,value);
						break;
					}
			
				}
				case "value":{
					String formalizedTime = value.getString("value");
					String grain = value.getString("grain");				
					entity = new Time(DateTime.parse(formalizedTime), TimeUnit.valueOf(grain));
					break;
				}
				}
				break;
			}
			}
			
			if(entity != null) {
				entities.add(entity);
			//	entity.printValue();
			//	System.out.println(entity.getValue());
				//Pair<DateTime, DateTime> v = (Pair<DateTime, DateTime>)entity.getValue();
				//System.out.println(v.getLeft()+", "+v.getRight());
				
			}
			
		}
		
		
		return entities;
	}
	/**
	 * Retrieve tokens from startPostion to endPostion
	 * @param tokens
	 * @param startPosition
	 * @param endPostion
	 * @return
	 */
	private LinkedList<Token> getTokensInPosition(List<Token> tokens, int startPosition, int endPosition){
		LinkedList<Token> result = new LinkedList<Token>();
		for(Token token: tokens) {
			if((token.getStartPosition() >= startPosition && token.getStartPosition() <= endPosition)
				||
				(startPosition >= token.getStartPosition() && startPosition <= token.getEndPosition()
					&& endPosition >= token.getStartPosition() && endPosition <= token.getEndPosition())) {
			result.add(token);
		}
//			if(token.getStartPosition() >= startPosition && token.getStartPosition() <= endPosition) {
//				result.add(token);
//			}
		}
		return result;
	}
	
	/**
	 * Duckling facebook list of entities types:
	 *  ['amount-of-money', 'distance', 'duration', 'email', 'numeral', 
	 * 'ordinal', 'phone-number', 'quantity', 'temperature', 'time', 'url', 'volume']
	 * Only duration, email, phone-number, time and url are processed.
	 * @param jsonAsString
	 * @param locale
	 * @param tokens
	 * @return
	 */
	public List<Entity> jsonResultToEntities(String jsonAsString, DucklingLocale locale, List<Token> tokens) {
		List<Entity> entities = new ArrayList<Entity>();
		JSONArray json = new JSONArray(jsonAsString);
		
		for(int i = 0; i < json.length(); i++) {
			JSONObject obj = json.getJSONObject(i);
			JSONObject value = obj.getJSONObject("value");
			
			String text = obj.getString("body");
			String dim = obj.getString("dim");
			int startPosition = obj.getInt("start");
			int endPosition = obj.getInt("end");
			LinkedList<Token> concernedTokens = getTokensInPosition(tokens, startPosition, endPosition);
			//String formalizedValue = value.getString("value");
			
			//System.out.println(text+", "+startPosition+", "+endPosition+", "+dim+", "+formalizedValue);
			Entity entity = null;
			switch (dim) {
			
			case "duration": {
				String grain = value.getString("unit");
				entity = new Duration(concernedTokens, TimeUnit.valueOf(grain));
				break;
			}
			case "email": {
				entity = new Email(concernedTokens);
				break;
			}
			case "phone-number": {
				entity = new PhoneNumber(concernedTokens);
				break;
			}
			case "url": {
				entity = new Url(concernedTokens);
				break;
			}
			case "time": {
				String type = value.getString("type");
				switch (type){
				case "interval":{
					if(locale.equals(DucklingLocale.fr_FR)) {
						//System.out.println(text);
						entity = intervalTimeResolverFrench(text, value.getJSONArray("values"));
						//System.out.println(entity);
						entity.setTokens(concernedTokens);
						break;
					}else {
						entity = intervalTimeResolverXX(text,value);
						entity.setTokens(concernedTokens);
						break;
					}
			
				}
				case "value":{
					String formalizedTime = value.getString("value");
					String grain = value.getString("grain");
					entity = new Time(DateTime.parse(formalizedTime), TimeUnit.valueOf(grain));
					entity.setTokens(concernedTokens);
					break;
				}
				}
				break;
			}
			}
			
			if(entity != null) {
				entities.add(entity);
			//	entity.printValue();
			//	System.out.println(entity.getValue());
				//Pair<DateTime, DateTime> v = (Pair<DateTime, DateTime>)entity.getValue();
				//System.out.println(v.getLeft()+", "+v.getRight());
				
			}
			
		}
		
		
		return entities;
	}
		
	/**
	 * duckling resolving timeInterval problem with french locale
	 * @param text
	 * @param values
	 * @return
	 */
	private IntervalTime intervalTimeResolverFrench(String text, JSONArray values) {
		//if there is one interval time retrieved by duckling facebook
		
				if(values.length() == 1) {
					JSONObject value = values.getJSONObject(0);
					
					JSONObject from = null;
					JSONObject to = null;
					DateTime startTime = null;
					DateTime endTime = null;
					try {
						from = value.getJSONObject("from");
						startTime = new DateTime(DateTime.parse(from.getString("value")));
					}catch(JSONException e) {
						//continue
						//e.printStackTrace();
						startTime = null;
					}
					
					try {
						to = value.getJSONObject("to");
						String grain = to.getString("grain");
						//deleting one (second, minute, hour, day, year) (added by duckling)
						//endTime = resolveEndDateTime(grain, to.getString("value"));
						endTime = new DateTime(DateTime.parse(to.getString("value")));
					
					}catch(JSONException e) {
						//continue
						//e.printStackTrace();
						endTime = null;
					}
					String grain = null;
					if(from != null) {
						grain = from.getString("grain");
					}else {
						grain = to.getString("grain");
					}
					
					return new IntervalTime(startTime, endTime, TimeUnit.valueOf(grain));
				
				}else {
					//choose the best value based on compared time sequence in the text
					Pattern pattern = Pattern.compile("([0-9]+) ?[hH\\-:]{1} ?([0-9]+)?");
				    Matcher matcher = pattern.matcher(text);
				    int startHour = -1;
				    int endHour = -1;
				    List<Integer> hours = new ArrayList<Integer>();
				    
				    while(matcher.find()) {
				    	hours.add(Integer.parseInt(matcher.group(1)));
				    	//System.out.println(matcher.group(1));
				    	
				    }
				    
				    if(hours.size() == 2) {
				    	startHour = hours.get(0);
				    	endHour = hours.get(1);
				    	
				    	for(int i = 0; i < values.length() ; i++) {
				    		JSONObject value = values.getJSONObject(i);
				    		
				    		JSONObject from = null;
							JSONObject to = null;
							DateTime startTime = null;
							DateTime endTime = null;
							try {
								from = value.getJSONObject("from");
								startTime = new DateTime(DateTime.parse(from.getString("value")));
							}catch(JSONException e) {
								//continue
								startTime = null;
							}
							
							try {
								to = value.getJSONObject("to");
								String grain = to.getString("grain");
								//deleting one (second, minute, hour, day, year) (added by duckling)
								//endTime = resolveEndDateTime(grain, to.getString("value"), endHour);
								endTime = new DateTime(DateTime.parse(to.getString("value")));
							}catch(JSONException e) {
								//continue
								//e.printStackTrace();
								endTime = null;
							}

							//if(startTime.getHourOfDay() == startHour && endTime.getHourOfDay() == endHour) {
								String grain = from.getString("grain");
								return new IntervalTime(startTime, endTime, TimeUnit.valueOf(grain));
						//	}
								
				    	}
				    }else if(hours.size() == 1) {
				    	for(int i = 0; i < values.length() ; i++) {
				    		JSONObject value = values.getJSONObject(i);
				    		JSONObject from = null;
							JSONObject to = null;
							DateTime startTime = null;
							DateTime endTime = null;
							try {
								from = value.getJSONObject("from");
								startTime = new DateTime(DateTime.parse(from.getString("value")));
							}catch(JSONException e) {
								//continue
								//e.printStackTrace();
								startTime = null;
							}
							
							try {
								to = value.getJSONObject("to");
								String grain = to.getString("grain");
								//deleting one (second, minute, hour, day, year) (added by duckling)
								//endTime = resolveEndDateTime(grain, to.getString("value"), endHour);
								endTime = new DateTime(DateTime.parse(to.getString("value")));
							}catch(JSONException e) {
								//continue
								//e.printStackTrace();
								endTime = null;
							}
							if((from != null /*&& startTime.getHourOfDay() == hours.get(0)*/) ||
									(to != null /*&& endTime.getHourOfDay() == hours.get(0)) */)) {
								String grain = null;
								if(from != null) {
									grain = from.getString("grain");
								}else {
									grain = to.getString("grain");
								}
								
								return new IntervalTime(startTime, endTime, TimeUnit.valueOf(grain));
							}
								
				    	}
				    }else {
				    	//System.out.println("je passe ici aussi");
				JSONObject value = values.getJSONObject(0);
				JSONObject from = null;
				JSONObject to = null;
				DateTime startTime = null;
				DateTime endTime = null;
				try {
					from = value.getJSONObject("from");
					startTime = new DateTime(DateTime.parse(from.getString("value")));
				} catch (JSONException e) {
					// continue
					startTime = null;
				}

				try {
					to = value.getJSONObject("to");
					String grain = to.getString("grain");
					// deleting one (second, minute, hour, day, year) (added by duckling)
					//endTime = resolveEndDateTime(grain, to.getString("value"));
					endTime = new DateTime(DateTime.parse(to.getString("value")));
				} catch (JSONException e) {
					// continue
					// e.printStackTrace();
					endTime = null;
				}

				String grain = null;
				if (from != null) {
					grain = from.getString("grain");
				} else {
					grain = to.getString("grain");
				}

				return new IntervalTime(startTime, endTime, TimeUnit.valueOf(grain));
									    	
				    }
					
				}
				
				return null;
	}
	
	/**
	 * duckling return timeInterval for other locale
	 * 
	 * @param text
	 * @param value
	 * @return
	 */
	private IntervalTime intervalTimeResolverXX(String text, JSONObject value) {
		// if there is one interval time retrieved by duckling facebook

		JSONObject from = null;
		JSONObject to = null;
		DateTime startTime = null;
		DateTime endTime = null;
		try {
			from = value.getJSONObject("from");
			startTime = new DateTime(DateTime.parse(from.getString("value")));
		} catch (JSONException e) {
			// continue
			startTime = null;
		}

		try {
			to = value.getJSONObject("to");
			String grain = to.getString("grain");
			// deleting one (second, minute, hour, day, year) (added by duckling)
			//endTime = resolveEndDateTime(grain, to.getString("value"));
			endTime = new DateTime(DateTime.parse(to.getString("value")));
		} catch (JSONException e) {
			// continue
			//e.printStackTrace();
			endTime = null;
		}
		String grain = null;
		if(from != null) {
			grain = from.getString("grain");
		}else {
			grain = to.getString("grain");
		}
		
		return new IntervalTime(startTime, endTime, TimeUnit.valueOf(grain));
		
	}
	
	
//	private DateTime resolveEndDateTime(String grain, String dateTimeAsString) {
//		System.out.println(grain + " " + dateTimeAsString);
//		if(grain.equals("second")) {
//			return new DateTime(DateTime.parse(dateTimeAsString).minusSeconds(1));
//		}else if(grain.equals("minute")) {
//			return new DateTime(DateTime.parse(dateTimeAsString).minusMinutes(1));
//		}else if(grain.equals("hour")) {
//			return new DateTime(DateTime.parse(dateTimeAsString).minusHours(1));
//		}else if(grain.equals("day")) {
//			return new DateTime(DateTime.parse(dateTimeAsString).minusDays(1));
//		}else if(grain.equals("month")) {
//			return new DateTime(DateTime.parse(dateTimeAsString).minusMonths(1));
//		}else if(grain.equals("year")) {
//			return new DateTime(DateTime.parse(dateTimeAsString).minusYears(1));
//		}else {
//			return new DateTime(DateTime.parse(dateTimeAsString));
//		}
//	}
	
//	private DateTime resolveEndDateTime(String grain, String dateTimeAsString, int endHour) {
//		System.out.println(grain + " " + dateTimeAsString);
//		DateTime parsedDate = DateTime.parse(dateTimeAsString);
//		if(grain.equals("second")) {
//			return new DateTime(parsedDate.minusSeconds(1));
//		}else if(grain.equals("minute")) {
//			return new DateTime(parsedDate.minusMinutes(1));
//		}else if(grain.equals("hour")) {
//			return new DateTime(parsedDate.minusHours(endHour - parsedDate.getHourOfDay()));
//		}else if(grain.equals("day")) {
//			return new DateTime(parsedDate.minusDays(1));
//		}else if(grain.equals("month")) {
//			return new DateTime(parsedDate.minusMonths(1));
//		}else if(grain.equals("year")) {
//			return new DateTime(parsedDate.minusYears(1));
//		}else {
//			return parsedDate;
//		}
//	}
		
	public static void main(String [] args) {
		String text = "Bonjour,\n" + 
				"\n" + 
				"je souhaite annuler la réunion de demain et refixer une autre le mois prochain.\n" + 
				"\n" + 
				"Est-ce que vous pouvez me donner vos disponibilités du mois prochain ?\n" + 
				"\n" + 
				"Cordialement,\n" + 
				"\n" + 
				"";
		DucklingWrapper ducklingWrapper = new DucklingWrapper(Parameters.DUCKLING_URL);
		//System.out.println(ducklingWrapper.resolveEndDateTime("hour", "2018-04-24T16:00:00.000-07:00"));
		
		//ducklingWrapper.callDucklingService(text, DucklingLocale.fr_FR);
		CoreNLPWrapper coreNLPWrapper = new CoreNLPWrapper(Language.french);
		//DucklingWrapper ducklingWrapper = new DucklingWrapper(Parameters.DUCKLING_URL);
		NLPData data = coreNLPWrapper.parseText(text);
		
		List<Entity> en = ducklingWrapper.callDucklingService(text, DucklingLocale.fr_FR, data.getTokens());
		List<Entity> resolvedEntities = new EntityResolver().resolveEntities(en, data.getEntities());
		for(Entity e: resolvedEntities) {
			e.printValue();
		}
		
	}

}
