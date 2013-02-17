/*
 * Copyright 2012 Net4Care, www.net4care.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
 
package org.net4care.serializer.delegate; 
 
import java.io.IOException; 
import java.util.List; 
 
import org.codehaus.jackson.map.*; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.serializer.Serializer; 
import org.w3c.dom.Document; 
 
/** An implementation of Serializer that uses 
 * JSON (as implemented by Jackson) as 'on the wire' format. 
 *  
 * @author Klaus Marius Hansen, University of Copenhagen 
 * 
 */ 
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
	           ) 
public class JacksonJSONSerializer implements Serializer { 
	@Override 
	public String serialize(StandardTeleObservation tm) { 
		return serializeObject(tm); 
	} 
 
	@Override 
	public String serializeList(java.util.List<String> stoList) { 
		return serializeObject(stoList); 
	} 
 
	private String serializeObject(Object o) { 
		String result = null; 
 
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.enableDefaultTyping();  // Enable polymorphic serialization/deserialization 
		try { 
			result = mapper.writeValueAsString(o); 
		} catch (IOException e) { 
			// Rethrow 
			throw new RuntimeException(e); 
		} 
 
		return result; 
	} 
 
	@Override 
	public StandardTeleObservation deserialize(String messagePayload) { 
		StandardTeleObservation result = null; 
 
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader()); // Jackson sets the wrong classloader for OSGi purposes 
 
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Laissez faire for now: ignore properties that are not defined as beans on class 
		mapper.enableDefaultTyping(); 
		try { 
			result = mapper.readValue(messagePayload, StandardTeleObservation.class); 
		} catch (IOException e) { 
			// Rethrow 
			throw new RuntimeException(e); 
		} 
 
		return result; 
	} 
 
	@Override 
	public java.util.List<StandardTeleObservation> deserializeList( 
			String listOfSTOasString) { 
		java.util.List<StandardTeleObservation> result = new java.util.LinkedList<StandardTeleObservation>(); 
		java.util.List<String> stringList = deserializeStringList(listOfSTOasString);  
 
		for (String string: stringList) { 
			result.add(deserialize(string)); 
		} 
 
		return result; 
	} 
 
	@Override 
	public java.util.List<String> deserializeStringList( 
			String listOfString) { 
		java.util.List<String> stringList = null; 
 
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader()); // Jackson sets the wrong classloader for OSGi purposes 
 
		ObjectMapper mapper = new ObjectMapper(); 
		try { 
			stringList = mapper.readValue(listOfString, java.util.List.class); 
		} catch (IOException e) { 
			// Rethrow 
			throw new RuntimeException(e); 
		} 
		return stringList; 
	} 
} 
 
