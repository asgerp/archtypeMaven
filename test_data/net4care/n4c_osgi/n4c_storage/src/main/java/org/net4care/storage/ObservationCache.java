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
 
package org.net4care.storage; 
 
import java.util.List; 
 
import org.net4care.utility.Net4CareException; 
 
/** Interface to an server side cache of observation for 
 * fast retrieval in the serialized on-the-wire format 
 * of the stored StandardTeleObservations. 
 *  
 * This allows clients to retrieve a set of observations 
 * without the need to decode the PHMR format. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
 
public interface ObservationCache { 
 
  /** Provides the same behaviour as for the XDSRepository but 
   * stores the observation under the given registry entry. 
   * @param metadata 
   * @param serializedFormOfObservation 
   * @throws Net4CareException 
   */ 
  public void provideAndRegisterObservation(RegistryEntry metadata,  
      String serializedFormOfObservation) throws Net4CareException; 
   
  /** Retrieve a set of observations in their serialized form that 
   * satisfy a query. 
   * @param query 
   * @return 
   * @throws Net4CareException 
   */ 
  public List<String> retrieveObservationSet(XDSQuery query) throws Net4CareException; 
 
} 
