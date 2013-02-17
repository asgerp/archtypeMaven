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
import java.util.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.*;
import org.net4care.observation.*;
import org.net4care.serializer.Serializer;

/** An implementation of Serializer to be used
 * on the server side as it does not need to know
 * the exact class used to implement the ObservationSpecifics
 * interface on the client's side.
 * 
 * DO NOT USE THIS ON THE CLIENT SIDE - instead use
 * the JacksonJSONSerializer.
 * 
 * @author  Morten Larsson (MLN), Aarhus University
 *
 */
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
	           ) 
public class ServerJSONSerializer implements Serializer {
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
    // System.out.println(o);
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

    //Variables for holding the object references in the resulting STO. 
    DeviceDescription deviceDescription = null;
    String patientCPR, organizationUID, treatmentID, codeSystem = null;
    long time;

    Thread.currentThread().setContextClassLoader(getClass().getClassLoader()); // Jackson sets the wrong classloader for OSGi purposes

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Laissez faire for now: ignore properties that are not defined as beans on class
    try { 
      //Root node, i. e. whole STO tree.
      JsonNode stoNode = mapper.readValue(messagePayload, JsonNode.class);

      //Deserializing the simple variables from STO tree.
      patientCPR = stoNode.get("patientCPR").asText();
      organizationUID = stoNode.get("organizationUID").asText();
      treatmentID = stoNode.get("treatmentID").asText();
      codeSystem = stoNode.get("codeSystem").asText();
      String timeAsString = stoNode.get("time").asText();
      time = Long.valueOf(timeAsString);

      //Deserializing the DeviceDescription object from STO tree.
      JsonNode deviceDescriptionNode = stoNode.path("deviceDescription");
      deviceDescription = mapper.readValue(deviceDescriptionNode, DeviceDescription.class);

      //Deserializing each ClinicalQuantity object and add it to quantities ArrayList.
      ArrayList<ClinicalQuantity> quantities = new ArrayList<ClinicalQuantity>();
      JsonNode obsSpecNode = stoNode.path("observationSpecifics");
      JsonNode obsSpecFieldsNode = obsSpecNode.get(1);
      Iterator<JsonNode> fields;

      //Choosing if the ArrayList should be populated by traversal of over iterator variable in 
      //DefaultObservationSpecifics or by traversal of Client specific ObservationSpecifics instance.
      if(obsSpecNode.get(0).toString().equals("\"org.net4care.observation.DefaultObservationSpecifics\""))
        fields = obsSpecNode.get(1).path("quantities").get(1).getElements();
      else
        fields = obsSpecFieldsNode.getElements();

      //Iterating through the quantities
      JsonNode currentField = null;
      while(fields.hasNext()) {
        currentField = fields.next();
        if(isClinicalQuantity(currentField)) {
          ClinicalQuantity quantity = mapper.readValue(currentField, ClinicalQuantity.class);
          quantities.add(quantity);
        }
      }

      //Create the resulting STO.
      result = new StandardTeleObservation(
          patientCPR, 
          organizationUID, 
          treatmentID, 
          codeSystem, 
          deviceDescription, 
          new DefaultObservationSpecifics(
              quantities,
              makeObservationSpecificsHumanReadableText(quantities)));   
      //Setting the time according to the timestamp in the message.
      result.setTime(time);
    } catch (IOException e) {
      // Rethrow
      throw new RuntimeException(e);
    }

    return result;
  }

  private boolean isClinicalQuantity(JsonNode node) {
    return 
        node.has("value") && 
        node.has("unit") && 
        node.has("code") && 
        node.has("displayName");
  }

  private String makeObservationSpecificsHumanReadableText(List<ClinicalQuantity> quantities) {
    Iterator<ClinicalQuantity> qIter = quantities.iterator();
    StringBuilder result = new StringBuilder();  
    while(qIter.hasNext()) {
      result.append(qIter.next());
      if(qIter.hasNext())
        result.append(" / ");
    }
    if(result.length() > 0)
      result.insert(0, "Measured: ");
    return result.toString();
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
