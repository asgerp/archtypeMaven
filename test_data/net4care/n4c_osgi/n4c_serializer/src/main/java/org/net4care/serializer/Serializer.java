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
 
package org.net4care.serializer; 
 
import java.util.List; 
 
import org.net4care.observation.StandardTeleObservation; 
 
/** The serializer is responsible for marshalling and unmarshalling 
 * StandardTeleObservations (and lists of them)  
 * to and from the on-the-wire format that 
 * is used between a home device and the N4C server side. 
 *  
 * You should use the implementation(s) in subpackage delegate. 
 *  
 * The format is required to be a lightweight, compact format, 
 * see design decision XXX in Net4Care technical report 3. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public interface Serializer { 
 
  public String serialize(StandardTeleObservation sto); 
  public String serializeList(List<String> stoList); 
   
  public StandardTeleObservation deserialize(String messagePayload); 
  public List<StandardTeleObservation> deserializeList(String listOfSTOasString); 
  /** Deserialize a JSON message that just contains a list of strings. 
   * Used for PHMR documents. 
   * @param listOfStringsAsSingleString 
   * @return 
   */ 
  public List<String> deserializeStringList(String listOfStringsAsSingleString); 
} 
